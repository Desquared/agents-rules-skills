# The same style on another stack

The style is a set of numbers, not a Kotlin feature. Any renderer that draws a frame as a function of
time, and can pipe or export frames to ffmpeg, can reproduce it. Two good fits for teams not on Kotlin:

- **Remotion** (React, TypeScript): JSX scenes, `useCurrentFrame()`, renders with headless Chrome and
  ffmpeg. Best when the product is a web app (you can render its real components). Check Remotion's
  licence first: companies above a small headcount need a paid company licence, so ask before
  adopting it.
- **Motion Canvas** (TypeScript, MIT): generator-based timelines drawn on a canvas, with an editor
  and an ffmpeg exporter. Best for diagram-heavy films; product UI comes in as PNG captures.

## Mapping

| House rule | Kotlin kit | Remotion | Motion Canvas |
| --- | --- | --- | --- |
| Time | `t` seconds, pure functions | `const t = useCurrentFrame() / fps` | `yield* waitFor(s)` timeline; or signals of time |
| `window(t, start, d)` | `Motion.kt` | `interpolate(frame, [start*fps, (start+d)*fps], [0, 1], { extrapolateLeft: 'clamp', extrapolateRight: 'clamp' })` | `tween(d, v => ...)` from `start` |
| easeOutCubic | `easeOutCubic` | `Easing.out(Easing.cubic)` | `easeOutCubic` |
| easeInOutCubic | `easeInOutCubic` | `Easing.inOut(Easing.cubic)` | `easeInOutCubic` |
| easeInCubic | `easeInCubic` | `Easing.in(Easing.cubic)` | `easeInCubic` |
| easeOutBack (s) | `easeOutBack(x, s)` | `Easing.out(Easing.back(s))` | `easeOutBack` (default s 1.70158; write your own for 1.4 / 1.8 / 2) |
| spring (d, f) | `spring(x, d, f)` | write the formula `1 - exp(-d x) cos(2 pi f x)`; Remotion's physical `spring()` feels different | write the formula |
| Page | 960 x 540 dp at density 2 | `<Composition width={1920} height={1080} fps={30}>` and `transform: scale(2)` on a 960 x 540 root | `size: [1920, 1080]`, a 960 x 540 view scaled 2x |
| Scene dissolve (0.3 s overlap, 0.35 s fade + 16 dp lift) | `Film` | `<Sequence from durationInFrames>` per scene with overlapping ranges and the same alpha / translateY math | two scenes with overlapping `all()` tweens |
| Word pops | `Words` | split into `<span>`s, each with its own start `start + i * 0.09` | `Txt` per word with staggered `sequence(0.09, ...)` |
| Pen doodles | `PathMeasure` segments | SVG `<path>` with `strokeDasharray = length`, `strokeDashoffset = length * (1 - p)` (`@remotion/paths` `evolvePath`) | `Path` / `Line` with `end` signal 0 to 1 |
| Marks (aim at measured positions) | `Modifier.mark` | measure with `ref.getBoundingClientRect()` in a layout effect, or lay out with known boxes | node `.position()` / `.size()` signals |
| Footage camera | `Footage` + `Cam` keys | an `<Img>` inside an overflow-hidden window, transform `scale(s) translate(...)` from interpolated keys | `Img` with animated `scale` and `position` |
| Cursor | a path in the doodle layer | an SVG arrow in the top layer, position from interpolated keys | a `Path` node on top |
| Frames to video | ffmpeg rawvideo pipe, `libx264 -preset slow -crf 14 -pix_fmt yuv420p -r 30` | `npx remotion render <id> out.mp4 --codec h264 --crf 14 --pixel-format yuv420p` | ffmpeg exporter, or PNG sequence then `ffmpeg -framerate 30 -i %06d.png -c:v libx264 -preset slow -crf 14 -pix_fmt yuv420p out.mp4` |
| Soundtrack | synthesised WAV from cues | generate the WAV with the same recipes (a small Node or Python script over the cue list), then `<Audio src>`; or mux after render | same: generate the WAV offline and mux with ffmpeg |
| Real UI | `capture()` off screen | render the product's real React components with fixture props directly in the composition | PNG captures (see `capture.md`) |

## What must not change when you port

- The palette, the type roles and sizes, the 960 x 540 dp grid.
- The easing curves and the durations table in `motion.md`. Library "ease" defaults are not the same
  curves; always name the curve explicitly.
- 30 fps constant frame rate, 1080p, H.264 yuv420p, AAC 192 kbps, `+faststart`.
- Stills-first QA and a single timing source for picture and sound.
