---
name: shared-perf-comparison-video
description: 'Turn two screen recordings into a polished side-by-side before/after performance video — both launches aligned on one clock, a per-side timer that stops the moment each app is ready, and a summary card of the numbers. Use whenever someone wants to SHOW a performance improvement rather than describe it: a before/after or side-by-side app video, a launch-time or startup-time demo, "how much faster is the new version", proving out a migration or rewrite, or a clip for a team demo, release note or stakeholder update. Also use when someone simply hands over two screen recordings of the same flow and wants them cut together, even if they never say the word "video".'
---

# Before/after performance video

Produce a clip in the style people recognise from platform migration announcements:
two phones on a white ground, **Before** on the left and **After** on the right, both
apps launching on the same frame, a running clock under each that freezes green the
instant that app is usable, and a card at the end with the numbers.

The rendering is the easy part and the bundled scripts do it. The work worth your
attention is deciding **when each app is "ready"**, because that single judgement is
the entire claim the video makes. Get it wrong and you have produced a confident,
beautiful, wrong artefact that someone will show to their CTO.

## What you need before starting

- **Two screen recordings of the same journey**, one per version/app. They do not need
  to be the same length, start at the same moment, or even be the same device — all of
  that is handled. They do need to be the *same journey* on comparable hardware.
- **ffmpeg** on PATH. `brew install ffmpeg` if missing.
- If the person has not recorded yet, read `references/capture.md` first and tell them
  how to capture fairly. A biased recording cannot be rescued in the edit.

Set up the tooling once per working directory:

```bash
eval "$(/path/to/skill/scripts/bootstrap.sh)"   # creates a venv, exports PERF_PY
```

## Pipeline

### 1. Find the instants

Never scrub a timeline by eye and never trust the two files to start at the same
moment — they never do. Let the pixels tell you:

```bash
"$PERF_PY" scripts/probe_moments.py "before.mov"
"$PERF_PY" scripts/probe_moments.py "after.mov"
```

This prints the clip's **bursts of change**: a launcher sitting still, then a spike when
the window transition fires, then quiet, then content landing, then quiet again. The
first burst's `start` is almost always the launch instant. A burst's `settle` is when the
picture stopped moving, which is your candidate for "ready".

Narrow in on anything ambiguous with `--from/--to` for per-frame detail, and export the
frames so you can actually look at them:

```bash
"$PERF_PY" scripts/probe_moments.py "before.mov" --dump 8.5,8.58,8.63 --out-dir frames/
```

**Look at the dumped frames with your own eyes before believing any number.** The curve
tells you when pixels changed; only the picture tells you whether what appeared was real
content or another skeleton.

### 2. Choose the moments — the part that matters

Read `references/measurement.md` before settling on anything. It covers picking a
"ready" rule that is symmetric across both sides, spotting human reaction time
contaminating a measurement, and what to do when the two recordings diverge.

The short version: pick one rule, write it down, apply it identically to both sides, and
prefer the reading that is *less* flattering to the new version when it is a close call.

### 3. Describe the film

Write a `film.json` next to the recordings. Schema and a worked example in
`references/config.md`; a starting point in `assets/film.example.json`.

If the two recordings diverge after the first milestone — because a human scrolled and
tapped at different moments in each — **do not** try to force one continuous clock over
the whole thing. Use a chapter per milestone, each re-aligned at its own `t0`. That is
honest and it reads better.

### 4. Check before you render

```bash
"$PERF_PY" scripts/make_video.py film.json --check
```

This composites one still per side per chapter, at the exact frame where that side's
clock stops, and skips the expensive render. **Open the stills and look at them.** If a
screen still shows a shimmer, a spinner or a grey placeholder block where content
belongs, the `ready` value is wrong — fix it and check again. Two rounds here cost
seconds and save you from shipping a false claim.

### 5. Render and hand over

```bash
"$PERF_PY" scripts/make_video.py film.json
```

Out comes the `.mp4` and a `-summary.png` card. The script prints the durations and the
ratio it computed; use those numbers in your write-up rather than recomputing them.

## What to tell the person afterwards

The video is a claim. Hand it over with the reasoning attached, briefly:

- **The rule you used** for "ready", in one sentence, and that it was applied to both.
- **Anything that flatters or penalises one side** that you noticed and could not remove:
  a cache that was warm on one run, a human pause mid-load, a promo image that arrived
  late on one side only. Say it plainly. If you had to choose between two defensible
  readings, say which you chose and what the other one would have shown.
- **Run count.** A single run per side is fine for a demo clip, but stage and production
  backends vary run to run, so say so rather than letting a single sample read as a
  benchmark.

Do not bury these in a footnote and do not skip them because the numbers look good.
The person will repeat whatever you tell them to someone who was not in the room.

## Traps that produce wrong numbers

- **Variable frame rate.** Emulator and simulator recorders write VFR files. ffmpeg's
  `select` filter counts decoded frames, `-ss` seeks by timestamp, and on a VFR source
  those two disagree — frame 116 and t=116/24 can be completely different pictures. Work
  in timestamps throughout; the scripts force a constant-rate grid before measuring.
- **ffmpeg without drawtext.** Many builds ship without freetype, so `drawtext` does not
  exist and text overlays fail. The bundled renderer draws the whole overlay with Pillow
  and composites it, so this never comes up — do not "simplify" it back to drawtext.
- **Debug builds.** A debug build is not the app anyone ships. On Android a debug build
  also skips the baseline profile, which penalises exactly the kind of startup work a
  migration usually improves. Compare release-shaped builds or say loudly that you did not.
- **The status bar clock** changes every second and will defeat any naive whole-frame
  diff. Already cropped out by the scripts.
- **A human touching the screen mid-load.** The most common way a comparison silently
  becomes a measurement of someone's reaction time. See `references/measurement.md`.

## Adjusting the look

Layout constants live at the top of `scripts/make_video.py` — canvas, colours, bezel
radius, label column width, the y positions of the title, words, timers and badges. The
phone windows size themselves from each recording's own aspect ratio and share a common
height, so an Android next to an iPhone still looks deliberate. Change the constants
rather than post-processing the output.

## Bundled files

| Path | Use |
| --- | --- |
| `scripts/bootstrap.sh` | Checks ffmpeg, creates the venv, prints `PERF_PY` |
| `scripts/probe_moments.py` | Change curve, burst detection, frame dumps, contact sheets |
| `scripts/make_video.py` | Config → overlay → chapters → final film + summary card |
| `references/capture.md` | Recording fairly on Android and iOS |
| `references/measurement.md` | Choosing the moments, symmetry, contamination, honesty |
| `references/config.md` | `film.json` schema, worked example |
| `assets/film.example.json` | Two-chapter starting point |
