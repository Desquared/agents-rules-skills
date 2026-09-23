#!/usr/bin/env python3
"""Cut two screen recordings into a before/after performance film.

THIS SCRIPT IS THE LOOK. Everything recognisable about the output — the white
ground, the Before/After columns, the phone bodies with the screens punched
through them, the clocks that freeze green, the summary card — lives here and
nowhere else. Reaching for raw ffmpeg instead produces a different video that
happens to show two phones, which is not the same deliverable. If this script
will not run, fix the environment or say so; do not reimplement it.

Everything is driven by one config file so the arithmetic that is easy to get
wrong by hand — chapter durations, where each clip is trimmed so both launches
share a clock, which side won and by how much — is computed once here.

    make_video.py film.json                # render the film
    make_video.py film.json --check        # just the verification stills (fast)
    make_video.py film.json --chapter 1    # render one chapter only

The overlay is drawn with Pillow rather than ffmpeg's drawtext because many
ffmpeg builds (including Homebrew's default) ship without freetype, so drawtext
does not exist at all. Drawing it here also means the phone bodies are an opaque
layer with transparent holes: the recordings composite underneath, which gives
them rounded corners for free.

See references/config.md for the schema.
"""
import argparse
import json
import os
import shutil
import subprocess
import sys
import tempfile

try:
    from PIL import Image, ImageDraw, ImageFont
except ImportError:
    sys.exit("Pillow missing. Run the skill's scripts/bootstrap.sh and use the venv it "
             "prints. Do not fall back to hand-written ffmpeg — see the note at the top "
             "of this file.")

WHITE = (255, 255, 255, 255)
INK = (16, 16, 18, 255)
MUTED = (135, 135, 142, 255)
FAINT = (176, 176, 183, 255)
DONE = (13, 145, 80, 255)
BEZEL = (22, 22, 25, 255)
EDGE = (58, 58, 64, 255)
HAIR = (231, 231, 235, 255)

CANVAS_PRESETS = {"landscape": (1920, 1080), "portrait": (1080, 1350), "square": (1080, 1080)}

FONT_DIRS = ["/System/Library/Fonts/Supplemental", "/System/Library/Fonts",
             "/Library/Fonts", "/usr/share/fonts/truetype/dejavu"]
SANS = ["Arial.ttf", "Helvetica.ttc", "DejaVuSans.ttf"]
SANS_BOLD = ["Arial Bold.ttf", "Helvetica.ttc", "DejaVuSans-Bold.ttf"]
MONO_BOLD = [("Menlo.ttc", 1), ("Courier New Bold.ttf", 0), ("DejaVuSansMono-Bold.ttf", 0)]


def _font(names, size, index=0):
    for n in names:
        for d in FONT_DIRS:
            p = os.path.join(d, n)
            if os.path.exists(p):
                try:
                    return ImageFont.truetype(p, size, index=index)
                except Exception:
                    continue
    return ImageFont.load_default()


def _mono(size):
    for n, idx in MONO_BOLD:
        for d in FONT_DIRS:
            p = os.path.join(d, n)
            if os.path.exists(p):
                try:
                    return ImageFont.truetype(p, size, index=idx)
                except Exception:
                    continue
    return _font(SANS_BOLD, size)


def probe_size(path):
    out = subprocess.run(["ffprobe", "-v", "error", "-select_streams", "v:0",
                          "-show_entries", "stream=width,height", "-of", "json", path],
                         capture_output=True, text=True, check=True).stdout
    s = json.loads(out)["streams"][0]
    return int(s["width"]), int(s["height"])


class Theme:
    """Canvas geometry for one of two arrangements.

    `sides` puts the big Before/After words in outer columns, which needs a wide
    frame — it is the default and the one the house style was designed around.
    `stacked` puts them above each phone so the film also fits a portrait or
    square frame for chat and social, where a 16:9 letterbox wastes most of the
    screen. Wanting a tall frame is a legitimate need and used to be a reason to
    abandon this script and hand-roll something; now it is a config value.
    """

    def __init__(self, canvas, left_size, right_size):
        if isinstance(canvas, str):
            if canvas not in CANVAS_PRESETS:
                sys.exit(f"unknown canvas {canvas!r}; use {list(CANVAS_PRESETS)} or "
                         f'{{"width": W, "height": H}}')
            self.W, self.H = CANVAS_PRESETS[canvas]
        else:
            self.W, self.H = int(canvas["width"]), int(canvas["height"])
        self.mode = "sides" if self.W >= self.H * 1.4 else "stacked"

        if self.mode == "sides":
            sx, sy = self.W / 1920, self.H / 1080
            self.pad = round(14 * sy)
            self.gap = round(120 * sx)
            self.side = round(300 * sx)
            self.bez_y = round(110 * sy)
            self.radius = round(42 * sy)
            screen_h = round(801 * sy)
            self.y_title, self.f_title = round(38 * sy), round(28 * sy)
            self.y_word, self.f_word = round(470 * sy), round(66 * sy)
            self.y_wordsub, self.f_wordsub = round(556 * sy), round(21 * sy)
            self.y_timer, self.f_timer = round(962 * sy), round(54 * sy)
            self.y_badge, self.f_badge = round(1032 * sy), round(24 * sy)
            self.f_verdict = round(34 * sy)
        else:
            self.pad = max(8, round(0.011 * self.W))
            self.gap = round(0.040 * self.W)
            self.side = 0
            self.radius = round(0.034 * self.W)
            self.y_title, self.f_title = round(0.023 * self.H), round(0.023 * self.H)
            self.y_word, self.f_word = round(0.060 * self.H), round(0.036 * self.H)
            self.y_wordsub, self.f_wordsub = round(0.101 * self.H), round(0.016 * self.H)
            self.y_timer, self.f_timer = self.H - round(0.105 * self.H), round(0.040 * self.H)
            self.y_badge, self.f_badge = self.H - round(0.045 * self.H), round(0.019 * self.H)
            self.f_verdict = round(0.029 * self.H)
            self.bez_y = round(0.130 * self.H)
            screen_h = self.y_timer - round(0.022 * self.H) - self.bez_y - 2 * self.pad

        # Each window keeps its own source's aspect and both share a height, so an
        # Android beside an iPhone reads as deliberate instead of letterboxed.
        def widths(h):
            return (int(round(h * left_size[0] / left_size[1] / 2)) * 2,
                    int(round(h * right_size[0] / right_size[1] / 2)) * 2)

        lw, rw = widths(screen_h)
        room = self.W - 2 * self.side - self.gap - round(0.025 * self.W)
        if lw + rw + 4 * self.pad > room:
            screen_h = int(screen_h * room / (lw + rw + 4 * self.pad))
            lw, rw = widths(screen_h)
        self.h, self.lw, self.rw = screen_h, lw, rw
        self.lbez, self.rbez = lw + 2 * self.pad, rw + 2 * self.pad

        total = 2 * self.side + self.lbez + self.gap + self.rbez
        self.margin = (self.W - total) // 2
        self.xl = self.margin + self.side
        self.xr = self.xl + self.lbez + self.gap
        if self.mode == "sides":
            self.cx_left = self.margin + self.side // 2
            self.cx_right = self.xr + self.rbez + self.side // 2
        else:
            self.cx_left = self.xl + self.lbez // 2
            self.cx_right = self.xr + self.rbez // 2

    def screen(self, side):
        x = self.xl if side == "left" else self.xr
        w = self.lw if side == "left" else self.rw
        return x + self.pad, self.bez_y + self.pad, w, self.h

    def bez(self, side):
        x = self.xl if side == "left" else self.xr
        w = self.lbez if side == "left" else self.rbez
        return x, self.bez_y, w, self.h + 2 * self.pad

    def cx(self, side):
        """Centre of the Before/After wording: its own column when wide, the phone when tall."""
        return self.cx_left if side == "left" else self.cx_right

    def cx_phone(self, side):
        """Centre of the phone itself. Clocks belong under the device they time, which in
        the wide arrangement is NOT the same x as the wording beside it."""
        x, _, w, _ = self.bez(side)
        return x + w // 2


def mid(d, text, f, cx, y, fill):
    x0, y0, x1, y1 = d.textbbox((0, 0), text, font=f)
    d.text((cx - (x1 - x0) / 2 - x0, y - y0), text, font=f, fill=fill)
    return x1 - x0


def check_mark(d, cx, cy, colour, r):
    d.ellipse([cx - r, cy - r, cx + r, cy + r], fill=colour)
    d.line([(cx - r * .45, cy), (cx - r * .1, cy + r * .4), (cx + r * .5, cy - r * .42)],
           fill=WHITE, width=max(2, r // 3), joint="curve")


def verdict_text(slow, fast):
    """Say what actually happened, including when the 'after' side lost."""
    if min(slow, fast) <= 0:
        return ""
    ratio = slow / fast
    if abs(ratio - 1) < 0.05:
        return "about the same"
    return f"{ratio:.1f}× faster" if ratio > 1 else f"{1/ratio:.1f}× slower"


def chapter_base(cfg, th, ch):
    img = Image.new("RGBA", (th.W, th.H), WHITE)
    d = ImageDraw.Draw(img)
    mid(d, ch.get("title", ""), _font(SANS, th.f_title), th.W // 2, th.y_title, FAINT)
    for side in ("left", "right"):
        x, y, w, h = th.bez(side)
        d.rounded_rectangle([x, y, x + w, y + h], radius=th.radius, fill=BEZEL)
        d.rounded_rectangle([x, y, x + w, y + h], radius=th.radius, outline=EDGE, width=2)
        mid(d, cfg[side].get("word", side.title()), _font(SANS_BOLD, th.f_word),
            th.cx(side), th.y_word, INK)
        sub = cfg[side].get("sub", "")
        for i, line in enumerate([sub] if isinstance(sub, str) else sub):
            if line:
                mid(d, line, _font(SANS, th.f_wordsub), th.cx(side),
                    th.y_wordsub + i * round(th.f_wordsub * 1.4), MUTED)
    return img


def punch(img, th):
    hole = Image.new("L", (th.W, th.H), 255)
    hd = ImageDraw.Draw(hole)
    for side in ("left", "right"):
        x, y, w, h = th.screen(side)
        hd.rounded_rectangle([x, y, x + w, y + h], radius=max(2, th.radius - th.pad), fill=0)
    img.putalpha(Image.composite(img.getchannel("A"), Image.new("L", (th.W, th.H), 0), hole))
    return img


def draw_timer(d, th, side, elapsed, finish, ch):
    cx = th.cx_phone(side)
    done = elapsed >= finish
    shown = min(max(elapsed, 0.0), finish)
    mid(d, f"{shown:0.2f}s", _mono(th.f_timer), cx, th.y_timer, DONE if done else INK)
    caption = ch.get("badge") if done else ch.get("running_badge")
    if caption:
        colour = DONE if done else FAINT
        off = round(th.f_badge * 0.62)
        bw = mid(d, caption, _font(SANS_BOLD, th.f_badge), cx + off, th.y_badge, colour)
        if done:
            r = max(6, round(th.f_badge * 0.42))
            check_mark(d, cx + off - bw / 2 - r * 2, th.y_badge + round(th.f_badge * 0.5), colour, r)


def render_chapter_frames(cfg, th, ch, outdir):
    os.makedirs(outdir, exist_ok=True)
    fps = cfg["fps"]
    pre, tail = ch.get("preroll", 0.75), ch.get("tail", 1.6)
    lt = ch["left"]["ready"] - ch["left"]["t0"]
    rt = ch["right"]["ready"] - ch["right"]["t0"]
    if lt <= 0 or rt <= 0:
        sys.exit(f"chapter {ch.get('name','?')}: ready must be after t0 on both sides")
    dur = pre + max(lt, rt) + tail
    v = verdict_text(lt, rt)
    v_at = pre + max(lt, rt) + 0.45
    base = chapter_base(cfg, th, ch)
    n = int(round(dur * fps))
    for i in range(n):
        t = i / fps
        e = t - pre
        img = base.copy()
        d = ImageDraw.Draw(img)
        draw_timer(d, th, "left", e, lt, ch)
        draw_timer(d, th, "right", e, rt, ch)
        if v and t >= v_at:
            d.rectangle([0, 0, th.W, th.y_title + round(th.f_title * 1.6)], fill=WHITE)
            mid(d, v, _font(SANS_BOLD, th.f_verdict), th.W // 2, th.y_title - 4, INK)
        punch(img, th).save(f"{outdir}/f{i:05d}.png")
    return {"frames": n, "duration": dur, "left": lt, "right": rt, "verdict": v}


def fit_font(d, text, names, size, max_w, bold=False):
    """Shrink until the text fits. A headline written for a 1920 frame is simply too
    wide at 1080, and a clipped headline looks like a broken render."""
    while size > 8:
        f = _font(names, size)
        x0, _, x1, _ = d.textbbox((0, 0), text, font=f)
        if x1 - x0 <= max_w:
            return f
        size -= 1
    return _font(names, 8)


def render_outro(cfg, th, rows, path):
    o = cfg.get("outro", {})
    W, H = th.W, th.H
    narrow = W < 1400
    img = Image.new("RGB", (W, H), (255, 255, 255))
    d = ImageDraw.Draw(img)
    f_row = _font(SANS_BOLD, round(0.0370 * H))
    f_sm = _font(SANS, round(0.0231 * H))
    f_delta = _font(SANS, round((0.019 if narrow else 0.0231) * H))
    f_big = _font(SANS_BOLD, round(0.0556 * H))

    if narrow:
        x_label = round(0.075 * W)
        x_before, x_arrow, x_after = round(0.530 * W), round(0.605 * W), round(0.685 * W)
        x_big, x_delta = round(0.875 * W), round(0.605 * W)
        rule = (round(0.060 * W), round(0.940 * W))
    else:
        x_label = round(0.230 * W)
        x_before, x_arrow, x_after = round(0.560 * W), round(0.605 * W), round(0.655 * W)
        x_big, x_delta = round(0.740 * W), round(0.740 * W)
        rule = (round(0.224 * W), round(0.776 * W))

    inner = rule[1] - rule[0]
    mid(d, o.get("headline", "Before and after"),
        fit_font(d, o.get("headline", "Before and after"), SANS_BOLD, round(0.0481 * H), inner),
        W // 2, round(0.198 * H), INK)
    if o.get("sub"):
        mid(d, o["sub"], fit_font(d, o["sub"], SANS, round(0.0231 * H), inner),
            W // 2, round(0.270 * H), MUTED)

    y, step = round(0.398 * H), round(0.122 * H)
    label_room = x_before - x_label - round(0.09 * W)
    for r in rows:
        d.line([(rule[0], y - round(0.031 * H)), (rule[1], y - round(0.031 * H))], fill=HAIR, width=2)
        d.text((x_label, y), r["what"],
               font=fit_font(d, r["what"], SANS_BOLD, round(0.0370 * H), label_room), fill=INK)
        mid(d, f"{r['before']:.2f}s", f_row, x_before, y, MUTED)
        mid(d, "→", f_row, x_arrow, y, FAINT)
        mid(d, f"{r['after']:.2f}s", f_row, x_after, y, DONE)
        mid(d, r["verdict"].replace(" faster", "").replace(" slower", ""), f_big, x_big,
            y - round(0.0074 * H), INK)
        # Signed as a change in duration: getting faster is negative seconds and a
        # negative percentage. The other convention reads as "2.29s slower".
        delta = r["after"] - r["before"]
        if abs(r["before"]) > 1e-6:
            mid(d, f"{delta:+.2f}s ({delta / r['before'] * 100:+.0f}%)", f_delta, x_delta,
                y + round(0.052 * H), MUTED)
        y += step
    d.line([(rule[0], y - round(0.031 * H)), (rule[1], y - round(0.031 * H))], fill=HAIR, width=2)
    if o.get("foot"):
        mid(d, o["foot"], fit_font(d, o["foot"], SANS, round(0.0231 * H), inner),
            W // 2, y + round(0.022 * H), FAINT)
    img.save(path)


def ff(args):
    subprocess.run(["ffmpeg", "-hide_banner", "-v", "warning", "-y"] + args, check=True)


def build_chapter(cfg, th, ch, ovl, info, out):
    fps = cfg["fps"]
    pre = ch.get("preroll", 0.75)
    seek_l = max(0.0, ch["left"]["t0"] - pre)
    seek_r = max(0.0, ch["right"]["t0"] - pre)
    lx, ly, lw, lh = th.screen("left")
    rx, ry, rw, rh = th.screen("right")
    dur = info["duration"]
    ff(["-i", cfg["left"]["file"], "-i", cfg["right"]["file"],
        "-framerate", str(fps), "-i", f"{ovl}/f%05d.png",
        "-filter_complex",
        f"color=c=white:s={th.W}x{th.H}:r={fps}:d={dur}[bg];"
        f"[0:v]fps={fps},setpts=PTS-STARTPTS,trim=start={seek_l},setpts=PTS-STARTPTS,"
        f"scale={lw}:{lh}:flags=lanczos[l];"
        f"[1:v]fps={fps},setpts=PTS-STARTPTS,trim=start={seek_r},setpts=PTS-STARTPTS,"
        f"scale={rw}:{rh}:flags=lanczos[r];"
        f"[bg][l]overlay={lx}:{ly}:eof_action=repeat[a];"
        f"[a][r]overlay={rx}:{ry}:eof_action=repeat[b];"
        f"[b][2:v]overlay=0:0:eof_action=repeat[v]",
        "-map", "[v]", "-an", "-t", f"{dur}", "-r", str(fps),
        "-c:v", "libx264", "-preset", "slow", "-crf", "17",
        "-pix_fmt", "yuv420p", out])


def concat(parts, fade, out):
    if len(parts) == 1:
        shutil.copy(parts[0][0], out)
        return
    inputs, filt, prev, offset = [], [], None, 0.0
    for i, (path, dur) in enumerate(parts):
        inputs += ["-i", path]
        if i == 0:
            prev, offset = "0:v", dur - fade
            continue
        label = f"x{i}" if i < len(parts) - 1 else "v"
        filt.append(f"[{prev}][{i}:v]xfade=transition=fade:duration={fade}:offset={offset:.3f}[{label}]")
        prev = label
        offset += dur - fade
    ff(inputs + ["-filter_complex", ";".join(filt), "-map", "[v]", "-an",
                 "-c:v", "libx264", "-preset", "slow", "-crf", "17",
                 "-pix_fmt", "yuv420p", "-movflags", "+faststart", out])


def still(cfg, th, ch, info, side, out):
    """One composited frame at the moment `side`'s clock stops — the thing to eyeball.

    If that screen still shows a skeleton or a spinner, the `ready` instant in the
    config is wrong, and no amount of rendering will fix the claim the film makes.
    """
    elapsed = info[side]
    tmp = tempfile.mkdtemp(prefix="perfvideo-still-")
    try:
        img = chapter_base(cfg, th, ch)
        d = ImageDraw.Draw(img)
        draw_timer(d, th, "left", elapsed, info["left"], ch)
        draw_timer(d, th, "right", elapsed, info["right"], ch)
        layer = os.path.join(tmp, "layer.png")
        punch(img, th).save(layer)
        lx, ly, lw, lh = th.screen("left")
        rx, ry, rw, rh = th.screen("right")
        # Each source is seeked, then clamped to one frame whose PTS is reset to 0.
        # Without the reset the decoded frame keeps its original timestamp, lands
        # nowhere near the background's t=0, and overlay composites nothing.
        ff(["-ss", f"{ch['left']['t0'] + elapsed:.4f}", "-i", cfg["left"]["file"],
            "-ss", f"{ch['right']['t0'] + elapsed:.4f}", "-i", cfg["right"]["file"],
            "-i", layer,
            "-filter_complex",
            f"color=c=white:s={th.W}x{th.H}:d=0.04[bg];"
            f"[0:v]trim=end_frame=1,setpts=PTS-STARTPTS,scale={lw}:{lh}:flags=lanczos[l];"
            f"[1:v]trim=end_frame=1,setpts=PTS-STARTPTS,scale={rw}:{rh}:flags=lanczos[r];"
            f"[bg][l]overlay={lx}:{ly}[a];[a][r]overlay={rx}:{ry}[b];[b][2:v]overlay=0:0[v]",
            "-map", "[v]", "-frames:v", "1", "-update", "1", out])
    finally:
        shutil.rmtree(tmp, ignore_errors=True)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("config")
    ap.add_argument("--check", action="store_true",
                    help="render only the stop-moment stills, skip the film")
    ap.add_argument("--chapter", type=int, help="render a single chapter (0-based)")
    ap.add_argument("--work", default=None)
    a = ap.parse_args()

    cfg = json.load(open(a.config))
    cfg.setdefault("fps", 48)
    base_dir = os.path.dirname(os.path.abspath(a.config))
    for s in ("left", "right"):
        if not os.path.isabs(cfg[s]["file"]):
            cfg[s]["file"] = os.path.join(base_dir, cfg[s]["file"])
        if not os.path.exists(cfg[s]["file"]):
            sys.exit(f"missing {s} recording: {cfg[s]['file']}")

    out = cfg.get("out", "before-after.mp4")
    if not os.path.isabs(out):
        out = os.path.join(base_dir, out)
    work = a.work or tempfile.mkdtemp(prefix="perfvideo-")
    os.makedirs(work, exist_ok=True)

    th = Theme(cfg.get("canvas", "landscape"),
               probe_size(cfg["left"]["file"]), probe_size(cfg["right"]["file"]))
    chapters = cfg["chapters"]
    if a.chapter is not None:
        chapters = [chapters[a.chapter]]

    print(f"canvas {th.W}x{th.H} ({th.mode}) @ {cfg['fps']}fps   "
          f"left window {th.lw}x{th.h}   right {th.rw}x{th.h}")
    rows, parts = [], []
    for k, ch in enumerate(chapters):
        ovl = os.path.join(work, f"ovl{k}")
        info = render_chapter_frames(cfg, th, ch, ovl)
        rows.append({"what": ch.get("name", ch.get("title", f"Chapter {k+1}")),
                     "before": info["left"], "after": info["right"],
                     "verdict": info["verdict"]})
        print(f"  [{k}] {ch.get('name', '')}: before {info['left']:.2f}s  "
              f"after {info['right']:.2f}s  -> {info['verdict']}  ({info['duration']:.2f}s of film)")

        if a.check:
            for side in ("left", "right"):
                p = os.path.join(os.path.dirname(out), f"check-ch{k}-{side}-stop.png")
                still(cfg, th, ch, info, side, p)
                print(f"      stop-frame check ({side}) -> {p}")
            continue

        part = os.path.join(work, f"ch{k}.mp4")
        build_chapter(cfg, th, ch, ovl, info, part)
        parts.append((part, info["duration"]))

    if a.check:
        print("\ncheck mode: open the stills above and look at them. Each is the exact frame "
              "where that side's clock stops. If a screen still looks mid-load, the 'ready' "
              "instant is wrong.")
        return

    if cfg.get("outro", {}).get("enabled", True) and rows and a.chapter is None:
        card = os.path.join(work, "outro.png")
        render_outro(cfg, th, rows, card)
        secs = cfg.get("outro", {}).get("seconds", 3.0)
        mp4 = os.path.join(work, "outro.mp4")
        ff(["-loop", "1", "-framerate", str(cfg["fps"]), "-i", card, "-t", str(secs),
            "-r", str(cfg["fps"]), "-c:v", "libx264", "-preset", "slow", "-crf", "17",
            "-pix_fmt", "yuv420p", "-vf", f"fade=t=out:st={secs-0.5}:d=0.5", mp4])
        parts.append((mp4, secs))
        summary = os.path.splitext(out)[0] + "-summary.png"
        shutil.copy(card, summary)
        print(f"summary card -> {summary}")

    concat(parts, cfg.get("crossfade", 0.35), out)
    w, h = probe_size(out)
    dur = subprocess.run(["ffprobe", "-v", "error", "-show_entries", "format=duration",
                          "-of", "csv=p=0", out], capture_output=True, text=True).stdout.strip()
    print(f"\nfilm -> {out}  ({float(dur):.2f}s, {w}x{h}, {os.path.getsize(out)/1048576:.1f} MB)")
    print(f"sanity check: a film from this script is {th.W}x{th.H}. Anything else did not "
          f"come from here.")
    print("work dir:", work)


if __name__ == "__main__":
    main()
