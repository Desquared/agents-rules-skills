#!/usr/bin/env python3
"""Cut two screen recordings into a before/after performance film.

Everything is driven by one config file so the arithmetic that is easy to get
wrong by hand — chapter durations, where each clip must be trimmed so both
launches share a clock, which side won and by how much — is computed once here
rather than re-derived per project.

    make_video.py film.json                # render the film
    make_video.py film.json --check        # just the verification stills (fast)
    make_video.py film.json --chapter 1    # render one chapter only

The overlay is drawn with Pillow rather than ffmpeg's drawtext because a lot of
ffmpeg builds (including Homebrew's default at time of writing) ship without
freetype, so drawtext simply does not exist. Drawing it ourselves also means the
phone bodies are opaque PNG with the screens punched out as transparent holes:
the recordings are composited underneath, which gives them rounded corners for
free and keeps every pixel of chrome under our control.

See references/config.md for the schema.
"""
import argparse
import json
import math
import os
import shutil
import subprocess
import sys
import tempfile

try:
    from PIL import Image, ImageDraw, ImageFont
except ImportError:
    sys.exit("Pillow missing. Run the skill's scripts/bootstrap.sh and use the venv it prints.")

# ---------------------------------------------------------------- appearance
W, H = 1920, 1080
WHITE = (255, 255, 255, 255)
INK = (16, 16, 18, 255)
MUTED = (135, 135, 142, 255)
FAINT = (176, 176, 183, 255)
DONE = (13, 145, 80, 255)
BEZEL = (22, 22, 25, 255)
EDGE = (58, 58, 64, 255)
HAIR = (231, 231, 235, 255)

PAD = 14          # bezel thickness around the screen
GAP = 120         # between the two phones
SIDE = 300        # width of the Before / After label column
BEZ_Y = 110       # top of the phone bodies
RADIUS = 42
SCREEN_H = 801

Y_TITLE, Y_WORD, Y_WORDSUB = 38, 470, 556
Y_TIMER, Y_BADGE = 962, 1032

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


# ------------------------------------------------------------------ helpers
def probe_size(path):
    out = subprocess.run(["ffprobe", "-v", "error", "-select_streams", "v:0",
                          "-show_entries", "stream=width,height", "-of", "json", path],
                         capture_output=True, text=True, check=True).stdout
    s = json.loads(out)["streams"][0]
    return int(s["width"]), int(s["height"])


def mid(d, text, f, cx, y, fill):
    x0, y0, x1, y1 = d.textbbox((0, 0), text, font=f)
    d.text((cx - (x1 - x0) / 2 - x0, y - y0), text, font=f, fill=fill)
    return x1 - x0


def check_mark(d, cx, cy, colour, r=10):
    d.ellipse([cx - r, cy - r, cx + r, cy + r], fill=colour)
    d.line([(cx - r * .45, cy), (cx - r * .1, cy + r * .4), (cx + r * .5, cy - r * .42)],
           fill=WHITE, width=3, joint="curve")


def verdict_text(slow, fast):
    """Say what actually happened, including when the 'after' side lost."""
    if min(slow, fast) <= 0:
        return ""
    ratio = slow / fast
    if abs(ratio - 1) < 0.05:
        return "about the same"
    return f"{ratio:.1f}× faster" if ratio > 1 else f"{1/ratio:.1f}× slower"


class Layout:
    """Phone windows sized from each source's own aspect, sharing one height.

    The two sides are often different devices (a tall Android next to an iPhone),
    and forcing one window size on both letterboxes whichever loses.
    """

    def __init__(self, left_size, right_size, screen_h=SCREEN_H):
        self.h = screen_h
        self.lw = int(round(screen_h * left_size[0] / left_size[1] / 2)) * 2
        self.rw = int(round(screen_h * right_size[0] / right_size[1] / 2)) * 2
        self.lbez = self.lw + 2 * PAD
        self.rbez = self.rw + 2 * PAD
        total = SIDE + self.lbez + GAP + self.rbez + SIDE
        self.margin = (W - total) // 2
        self.xl = self.margin + SIDE
        self.xr = self.xl + self.lbez + GAP
        self.cx_left = self.margin + SIDE // 2
        self.cx_right = self.xr + self.rbez + SIDE // 2

    def screen(self, side):
        x = self.xl if side == "left" else self.xr
        w = self.lw if side == "left" else self.rw
        return x + PAD, BEZ_Y + PAD, w, self.h

    def bez(self, side):
        x = self.xl if side == "left" else self.xr
        w = self.lbez if side == "left" else self.rbez
        return x, BEZ_Y, w, self.h + 2 * PAD


# ------------------------------------------------------------------ drawing
def chapter_base(cfg, lay, ch):
    img = Image.new("RGBA", (W, H), WHITE)
    d = ImageDraw.Draw(img)
    mid(d, ch.get("title", ""), _font(SANS, 28), W // 2, Y_TITLE, FAINT)
    for side in ("left", "right"):
        x, y, w, h = lay.bez(side)
        cx = lay.cx_left if side == "left" else lay.cx_right
        d.rounded_rectangle([x, y, x + w, y + h], radius=RADIUS, fill=BEZEL)
        d.rounded_rectangle([x, y, x + w, y + h], radius=RADIUS, outline=EDGE, width=2)
        mid(d, cfg[side].get("word", side.title()), _font(SANS_BOLD, 66), cx, Y_WORD, INK)
        sub = cfg[side].get("sub", "")
        for i, line in enumerate([sub] if isinstance(sub, str) else sub):
            if line:
                mid(d, line, _font(SANS, 21), cx, Y_WORDSUB + i * 30, MUTED)
    return img


def punch(img, lay):
    hole = Image.new("L", (W, H), 255)
    hd = ImageDraw.Draw(hole)
    for side in ("left", "right"):
        x, y, w, h = lay.screen(side)
        hd.rounded_rectangle([x, y, x + w, y + h], radius=RADIUS - PAD, fill=0)
    img.putalpha(Image.composite(img.getchannel("A"), Image.new("L", (W, H), 0), hole))
    return img


def draw_timer(d, lay, side, elapsed, finish, badge):
    x, _, w, _ = lay.bez(side)
    cx = x + w // 2
    done = elapsed >= finish
    shown = min(max(elapsed, 0.0), finish)
    mid(d, f"{shown:0.2f}s", _mono(54), cx, Y_TIMER, DONE if done else INK)
    if done and badge:
        bw = mid(d, badge, _font(SANS_BOLD, 24), cx + 15, Y_BADGE, DONE)
        check_mark(d, cx + 15 - bw / 2 - 20, Y_BADGE + 12, DONE)


def render_chapter_frames(cfg, lay, ch, outdir):
    os.makedirs(outdir, exist_ok=True)
    fps = cfg["fps"]
    pre, tail = ch.get("preroll", 0.75), ch.get("tail", 1.6)
    lt = ch["left"]["ready"] - ch["left"]["t0"]
    rt = ch["right"]["ready"] - ch["right"]["t0"]
    dur = pre + max(lt, rt) + tail
    v = verdict_text(lt, rt)
    v_at = pre + max(lt, rt) + 0.45
    base = chapter_base(cfg, lay, ch)
    n = int(round(dur * fps))
    for i in range(n):
        t = i / fps
        e = t - pre
        img = base.copy()
        d = ImageDraw.Draw(img)
        draw_timer(d, lay, "left", e, lt, ch.get("badge"))
        draw_timer(d, lay, "right", e, rt, ch.get("badge"))
        if v and t >= v_at:
            d.rectangle([0, 0, W, Y_TITLE + 44], fill=WHITE)
            mid(d, v, _font(SANS_BOLD, 34), W // 2, Y_TITLE - 4, INK)
        punch(img, lay).save(f"{outdir}/f{i:05d}.png")
    return {"frames": n, "duration": dur, "left": lt, "right": rt, "verdict": v}


def render_outro(cfg, rows, path):
    o = cfg.get("outro", {})
    img = Image.new("RGB", (W, H), (255, 255, 255))
    d = ImageDraw.Draw(img)
    f_h, f_row, f_sm, f_big = (_font(SANS_BOLD, 52), _font(SANS_BOLD, 40),
                               _font(SANS, 25), _font(SANS_BOLD, 60))
    mid(d, o.get("headline", "Before and after"), f_h, W // 2, 214, INK)
    if o.get("sub"):
        mid(d, o["sub"], f_sm, W // 2, 292, MUTED)
    y = 430
    for r in rows:
        d.line([(430, y - 34), (1490, y - 34)], fill=HAIR, width=2)
        d.text((440, y), r["what"], font=f_row, fill=INK)
        mid(d, f"{r['before']:.2f}s", f_row, 1075, y, MUTED)
        mid(d, "→", f_row, 1165, y, FAINT)
        mid(d, f"{r['after']:.2f}s", f_row, 1258, y, DONE)
        mid(d, r["verdict"].replace(" faster", "").replace(" slower", ""), f_big, 1420, y - 8, INK)
        y += 132
    d.line([(430, y - 34), (1490, y - 34)], fill=HAIR, width=2)
    if o.get("foot"):
        mid(d, o["foot"], f_sm, W // 2, y + 24, FAINT)
    img.save(path)


# ----------------------------------------------------------------- ffmpeg
def ff(args):
    subprocess.run(["ffmpeg", "-hide_banner", "-v", "warning", "-y"] + args, check=True)


def build_chapter(cfg, lay, ch, ovl, info, out):
    fps = cfg["fps"]
    pre = ch.get("preroll", 0.75)
    seek_l = max(0.0, ch["left"]["t0"] - pre)
    seek_r = max(0.0, ch["right"]["t0"] - pre)
    lx, ly, lw, lh = lay.screen("left")
    rx, ry, rw, rh = lay.screen("right")
    dur = info["duration"]
    ff(["-i", cfg["left"]["file"], "-i", cfg["right"]["file"],
        "-framerate", str(fps), "-i", f"{ovl}/f%05d.png",
        "-filter_complex",
        f"color=c=white:s={W}x{H}:r={fps}:d={dur}[bg];"
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


def still(cfg, lay, ch, info, side, out):
    """One composited frame at the moment `side`'s clock stops — the thing to eyeball.

    If that screen still shows a skeleton or a spinner, the `ready` instant in the
    config is wrong, and no amount of rendering will fix the claim the film makes.
    """
    elapsed = info[side]
    tmp = tempfile.mkdtemp(prefix="perfvideo-still-")
    try:
        img = chapter_base(cfg, lay, ch)
        d = ImageDraw.Draw(img)
        draw_timer(d, lay, "left", elapsed, info["left"], ch.get("badge"))
        draw_timer(d, lay, "right", elapsed, info["right"], ch.get("badge"))
        layer = os.path.join(tmp, "layer.png")
        punch(img, lay).save(layer)
        lx, ly, lw, lh = lay.screen("left")
        rx, ry, rw, rh = lay.screen("right")
        # Each source is seeked, then clamped to a single frame whose PTS is reset to 0.
        # Without the reset the decoded frame keeps its original timestamp, lands nowhere
        # near the background's t=0, and overlay composites nothing — a blank phone.
        ff(["-ss", f"{ch['left']['t0'] + elapsed:.4f}", "-i", cfg["left"]["file"],
            "-ss", f"{ch['right']['t0'] + elapsed:.4f}", "-i", cfg["right"]["file"],
            "-i", layer,
            "-filter_complex",
            f"color=c=white:s={W}x{H}:d=0.04[bg];"
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

    lay = Layout(probe_size(cfg["left"]["file"]), probe_size(cfg["right"]["file"]))
    chapters = cfg["chapters"]
    if a.chapter is not None:
        chapters = [chapters[a.chapter]]

    print(f"canvas {W}x{H} @ {cfg['fps']}fps   left window {lay.lw}x{lay.h}   right {lay.rw}x{lay.h}")
    rows, parts = [], []
    for k, ch in enumerate(chapters):
        ovl = os.path.join(work, f"ovl{k}")
        info = render_chapter_frames(cfg, lay, ch, ovl)
        rows.append({"what": ch.get("name", ch.get("title", f"Chapter {k+1}")),
                     "before": info["left"], "after": info["right"],
                     "verdict": info["verdict"]})
        print(f"  [{k}] {ch.get('name', '')}: before {info['left']:.2f}s  "
              f"after {info['right']:.2f}s  -> {info['verdict']}  ({info['duration']:.2f}s of film)")

        if a.check:
            for side in ("left", "right"):
                p = os.path.join(os.path.dirname(out), f"check-ch{k}-{side}-stop.png")
                still(cfg, lay, ch, info, side, p)
                print(f"      stop-frame check ({side}) -> {p}")
            continue

        part = os.path.join(work, f"ch{k}.mp4")
        build_chapter(cfg, lay, ch, ovl, info, part)
        parts.append((part, info["duration"]))

    if a.check:
        print("\ncheck mode: look at the stills above. Each shows the exact frame where that "
              "side's clock stops. If a screen still looks mid-load, the 'ready' instant is wrong.")
        return

    if cfg.get("outro", {}).get("enabled", True) and len(rows) and a.chapter is None:
        card = os.path.join(work, "outro.png")
        render_outro(cfg, rows, card)
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
    dur = subprocess.run(["ffprobe", "-v", "error", "-show_entries", "format=duration",
                          "-of", "csv=p=0", out], capture_output=True, text=True).stdout.strip()
    print(f"\nfilm -> {out}  ({float(dur):.2f}s, {os.path.getsize(out)/1048576:.1f} MB)")
    print("work dir:", work)


if __name__ == "__main__":
    main()
