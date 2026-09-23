#!/usr/bin/env python3
"""Find the interesting instants in a screen recording.

Screen recordings of app launches are mostly dead-still punctuated by bursts of
change: the launcher sits motionless, the window transition explodes, a skeleton
shimmers, content lands, the screen goes quiet again. Measuring that pattern is
far more reliable than eyeballing a scrubber, and it is the only way to get the
two sides of a comparison onto one clock.

Two things this guards against, both of which silently produce wrong numbers:

1. VARIABLE FRAME RATE. Emulator and simulator recorders emit VFR files. ffmpeg's
   `select` filter counts decoded frames while `-ss` seeks by timestamp, so frame
   17 and t=17/fps are NOT the same picture. Everything here runs through an
   `fps=` filter first, which resamples to a constant grid where index i always
   means t = i/fps. Quote any instant you find as a TIMESTAMP, and extract frames
   with `-ss`, never with `select`.

2. THE STATUS BAR. Its clock and indicators tick independently of the app, so a
   whole-frame diff never settles. It is cropped out before measuring.

Usage:
    probe_moments.py VIDEO                        # summary + suggested moments
    probe_moments.py VIDEO --from 7 --to 9        # per-frame detail in a window
    probe_moments.py VIDEO --sheet out.png        # contact sheet of the whole clip
    probe_moments.py VIDEO --dump 4.7,5.0 --out-dir frames/
"""
import argparse
import json
import subprocess
import sys

try:
    import numpy as np
except ImportError:
    sys.exit("numpy missing. Run the skill's scripts/bootstrap.sh and use the venv it prints.")

GRID_W = 168          # downscale before diffing: fast, and immune to compression noise
STATUS_BAR_FRAC = 0.043   # top slice to drop, as a fraction of height (~130px on a 2992px phone)


def probe(path):
    out = subprocess.run(
        ["ffprobe", "-v", "error", "-select_streams", "v:0",
         "-show_entries", "stream=width,height", "-of", "json", path],
        capture_output=True, text=True, check=True).stdout
    s = json.loads(out)["streams"][0]
    return int(s["width"]), int(s["height"])


def load_curve(path, fps):
    """Mean absolute frame-to-frame luma change, on a constant-rate grid."""
    w, h = probe(path)
    top = int(h * STATUS_BAR_FRAC)
    gh = max(2, int(round(GRID_W * (h - top) / w)) // 2 * 2)
    vf = f"fps={fps},crop={w}:{h - top}:0:{top},scale={GRID_W}:{gh},format=gray"
    raw = subprocess.run(
        ["ffmpeg", "-v", "error", "-i", path, "-vf", vf,
         "-f", "rawvideo", "-pix_fmt", "gray", "-"],
        capture_output=True, check=True).stdout
    n = len(raw) // (GRID_W * gh)
    if n < 2:
        sys.exit(f"decoded only {n} frames from {path}")
    f = np.frombuffer(raw[: n * GRID_W * gh], dtype=np.uint8).reshape(n, gh, GRID_W).astype(np.int16)
    d = np.concatenate([[0.0], np.abs(np.diff(f, axis=0)).mean(axis=(1, 2))])
    return d, (w, h)


def suggest(d, fps, busy=2.0, quiet=0.6, settle_ms=250):
    """Segment the clip into bursts of change separated by stillness.

    A burst starts at the first frame that clearly moves after a still stretch,
    and ends once the picture holds steady for `settle_ms`. For a launch clip the
    first burst is the app opening and its end is roughly "first frame drawn";
    later bursts are content landing, and the end of the last one before the user
    touches anything is usually the honest "ready" instant.
    """
    n = len(d)
    hold = max(1, int(fps * settle_ms / 1000))
    events, i = [], 1
    while i < n:
        if d[i] > busy:
            start = i
            last_busy = i
            j = i + 1
            while j < n:
                if d[j] > busy:
                    last_busy = j
                elif d[j] <= quiet and j - last_busy >= hold:
                    break
                j += 1
            events.append({"start": start, "start_t": round(start / fps, 3),
                           "settle": last_busy + 1, "settle_t": round((last_busy + 1) / fps, 3),
                           "peak": round(float(d[start:j + 1].max()), 2)})
            i = j + 1
        else:
            i += 1
    return events


def contact_sheet(path, out, step=0.5, cols=8, tile_w=126):
    dur = float(subprocess.run(
        ["ffprobe", "-v", "error", "-show_entries", "format=duration", "-of", "csv=p=0", path],
        capture_output=True, text=True, check=True).stdout.strip())
    rows = max(1, int((dur / step) // cols) + 1)
    subprocess.run(["ffmpeg", "-v", "error", "-y", "-i", path, "-vf",
                    f"fps={1/step},scale={tile_w}:-1,tile={cols}x{rows}:margin=4:padding=3:color=0x202020",
                    "-frames:v", "1", out], check=True)
    print(f"contact sheet -> {out}   ({step}s per tile, {cols} per row, row-major from t=0)")


def dump(path, stamps, out_dir, width=320):
    import os
    os.makedirs(out_dir, exist_ok=True)
    for t in stamps:
        o = os.path.join(out_dir, f"t{t:0>8.3f}.png")
        subprocess.run(["ffmpeg", "-v", "error", "-y", "-ss", f"{t:.4f}", "-i", path,
                        "-frames:v", "1", "-vf", f"scale={width}:-2", o], check=True)
        print(f"  {t:8.3f}s -> {o}")


def main():
    p = argparse.ArgumentParser()
    p.add_argument("video")
    p.add_argument("--fps", type=float, default=0, help="grid rate; default = the file's own rate")
    p.add_argument("--from", dest="lo", type=float)
    p.add_argument("--to", dest="hi", type=float)
    p.add_argument("--busy", type=float, default=2.0)
    p.add_argument("--quiet", type=float, default=0.6)
    p.add_argument("--sheet")
    p.add_argument("--dump", help="comma-separated timestamps to export as PNGs")
    p.add_argument("--out-dir", default="frames")
    p.add_argument("--json", action="store_true")
    a = p.parse_args()

    if not a.fps:
        r = subprocess.run(["ffprobe", "-v", "error", "-select_streams", "v:0",
                            "-show_entries", "stream=r_frame_rate", "-of", "csv=p=0", a.video],
                           capture_output=True, text=True, check=True).stdout.strip()
        num, den = (r.split("/") + ["1"])[:2]
        a.fps = float(num) / float(den)

    d, (w, h) = load_curve(a.video, a.fps)
    events = suggest(d, a.fps, a.busy, a.quiet)

    if a.json:
        print(json.dumps({"file": a.video, "fps": a.fps, "size": [w, h],
                          "frames": len(d), "duration": round(len(d) / a.fps, 3),
                          "events": events}))
        return

    print(f"{a.video}")
    print(f"  {w}x{h}, grid {a.fps:g} fps, {len(d)} frames, {len(d)/a.fps:.2f}s")
    print(f"\n  bursts of change (start = something moved, settle = picture held still again)")
    print(f"  {'#':>3} {'start':>9} {'settle':>9} {'peak':>7}")
    for k, e in enumerate(events):
        print(f"  {k:>3} {e['start_t']:>8.3f}s {e['settle_t']:>8.3f}s {e['peak']:>7.1f}")
    if events:
        print(f"\n  likely launch instant: {events[0]['start_t']:.3f}s")
        print(f"  confirm every instant visually before using it:")
        print(f"    probe_moments.py '{a.video}' --dump "
              f"{events[0]['start_t']:.3f},{events[-1]['settle_t']:.3f} --out-dir frames/")

    if a.lo is not None:
        hi = a.hi if a.hi is not None else a.lo + 1.0
        print(f"\n  per-frame detail {a.lo}s..{hi}s")
        for i in range(int(a.lo * a.fps), min(int(hi * a.fps) + 1, len(d))):
            print(f"  {i:>5} t={i/a.fps:>8.3f} d={d[i]:>7.2f} {'#' * min(50, int(d[i]))}")

    if a.sheet:
        contact_sheet(a.video, a.sheet)
    if a.dump:
        dump(a.video, [float(x) for x in a.dump.split(",")], a.out_dir)


if __name__ == "__main__":
    main()
