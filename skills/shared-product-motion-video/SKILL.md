---
name: shared-product-motion-video
description: 'Make product marketing and tutorial motion videos in the Desquared house style (warm paper, heavy ink headlines, hand-drawn doodles, the real product UI with a clicking cursor, a synthesised soundtrack), from storyboard to encoded mp4. Use whenever someone wants a promo, launch, teaser, explainer, feature walkthrough, how-to, onboarding or tutorial video for a product or feature, or asks to "make a video", "animate a demo" or "show how X works" for a launch post, landing page, release notes or team demo. Includes 40 s promo and step-by-step tutorial storyboard templates, exact colour, type, motion and sound values, ffmpeg commands, a QA checklist and a Compose Desktop starter kit, with a mapping to Remotion and Motion Canvas. Not for before/after performance comparisons (use shared-perf-comparison-video).'
---

# Product motion video (house style)

Produce short product films that look like a person annotating the real product on a warm notebook
page: off-white paper with a faint dot grid, heavy black headlines that pop in word by word, pastel
highlighter tags, pen marks that draw themselves around the thing that matters, the product's real
UI inside device frames and desktop windows, a cursor that visibly clicks, and a quiet synthesised
soundtrack whose clicks and chimes land on the frame.

Two formats:

- **Promo**: at most 40 s, sells one idea through a hook, a brand lockup, one proof per scene, an
  outcome and an end card.
- **Tutorial**: 50 to 120 s, teaches one task in numbered steps on real footage, no voice.

The style was extracted from three shipped Desquared films (a 40 s promo and two tutorials of 51 s
and 57 s). Every number in this skill comes from their source code, not from taste.

## Non-negotiables

1. **Real product UI only.** Render it from code or capture it clean (`references/capture.md`).
   Never a mock-up, a redraw or lorem ipsum. Every number on screen is computed from the product.
2. **Everything is a function of time.** Each frame is computed from `t` through a window and a named
   easing curve, so any still can be rendered on its own. Check stills before rendering the film.
3. **One timing source for picture and sound.** A sound is declared next to the picture it belongs
   to, in the same scene-local time, never typed a second time.
4. **The film works muted.** Captions carry the story; there is no voiceover by default.
5. **Nothing paid by default.** Music and effects are synthesised; fonts are open licence. Ask before
   adding any paid text-to-speech, stock music or render service.

## The style in one screen

Full values and rationale: `references/house-style.md` (look), `references/motion.md` (motion,
pacing, cursor), `references/sound.md` (sound).

| Area | Rule |
| --- | --- |
| Canvas | 1920 x 1080, 30 fps. Laid out on a **960 x 540 dp page at density 2** (1 dp = 2 px) |
| Paper | `#FBFAF7`; dots `#E3E1DB`, radius 0.9 dp, every 24 dp, drifting 3 dp/s |
| Ink | text `#191919`, body `#6F6E69`, faint `#A5A39E`, hairline `#E6E4DF`, cards white |
| Highlighters | yellow `#FBE7A1`, blue `#D6E8F5`, green `#DDEEDC`, pink `#F7DDE6`, purple `#E9E1F5`, orange `#FBE3CF`: one per section tag, rotating by scene |
| UI inks | `#2F6FDE` selection, focus, ripples; `#2E8B57` ticks and success |
| Brand colour | only the app icon and at most one accent |
| Type | Display (Inter Display, or SF Pro Display) Black/Bold, tracking -0.02em, line 1.06; Text (Inter) line 1.4; one word per scene in italic serif (Newsreader); mono (JetBrains Mono) for tags, paths and code |
| Sizes (sp) | hook 66 to 74 Black; wordmark 70; tutorial title 62; caption title 36 to 40 Bold; detail 15; note 16 italic; tags 11 mono; nothing to read under 11 |
| Grid | margin 60 dp; caption column x 60, y 150, 300 wide; product area x 372, 560 wide; step dots at y 24 right-aligned to 936 |
| Entrances | rise 18 dp + fade over 0.4 to 0.6 s, easeOutCubic; words 0.5 s each, 0.09 s apart, easeOutBack 1.4 |
| Landings | cards, phones, chips pop with easeOutBack 1.8 to 2 over 0.5 to 0.55 s, siblings 0.12 to 0.28 s apart |
| Moves | camera and cursor on easeInOutCubic; camera 1 to 1.6 s per key, max zoom 2.6x; cursor 0.4 to 0.6 s per hop |
| State changes | damped spring bump from 0.86 to 0.9 up to 1 (damping 7, frequency 2.2 to 2.4) |
| Scene change | the only transition: 0.3 s overlap, outgoing fades over 0.35 s (easeInCubic) while lifting 16 dp, with a whoosh |
| Click | 0.3 s: cursor shrinks 12%, blue ripple 6 to 24 dp, the button dips 6 to 8%, click sound, then a 0.6 s rest |
| Typing | 0.045 s/char in forms, 0.038 in a hero prompt, 0.032 in an assistant; block caret blinking at 1 Hz; one key tick per character |
| Doodles | round-capped ink pen strokes 2.2 to 2.8 dp (strike 5 dp) that draw on: strike, underline, loop, arrow, sparkle, tick, star, yellow marker sweep. Two or three per scene, each aimed at a measured word or UI element, each with a scribble sound |
| Pacing | promo scenes 3.6 to 5.6 s, tutorial steps 6.5 to 13 s; one idea per scene; every final state holds still at least 0.8 s |
| Sound | 112 BPM maj9 bed with a groove for promos, 96 BPM calm bed for tutorials; whoosh, click, pop, key tick, scribble, chime on the picture; soft clip, -1 dBFS peak, aim -16 LUFS |

## Pipeline

Details, commands and the checklist: `references/pipeline.md`.

1. **Brief.** Audience, channel, promo or tutorial, the one sentence, three to six proof points, the
   real facts to show, brand inputs (exact name, icon, colour, call to action).
2. **Storyboard.** Fill the scene table from `references/storyboard-promo.md` (40 s, ten scenes) or
   `references/storyboard-tutorial.md` (title, steps from six patterns, end card). Check it against
   the pacing rules before any code.
3. **Capture** the real UI into `assets/` at 3x (phones) or 2x (desktop, web), with clean status bars
   and fixture data. `references/capture.md` covers render-from-code, snapshot tests, headless
   browsers and simulators.
4. **Build scenes.** One `Scene` per storyboard row, built from the kit's pieces, with its sound cues
   in the same place.
5. **Stills QA.** Render the settled state and every click of every scene as PNGs and look at each at
   full size. Repeat until each would pass as a poster.
6. **Render** the silent film: frames piped into
   `ffmpeg -f rawvideo -pix_fmt bgra -s 1920x1080 -r 30 -i - -c:v libx264 -preset slow -crf 14 -pix_fmt yuv420p -movflags +faststart`.
7. **Audio.** Synthesise the WAV from the same cue list (16-bit, 44.1 kHz, stereo).
8. **Mux** without re-encoding video: `-c:v copy -c:a aac -b:a 192k -shortest -movflags +faststart`.
9. **Final QA** with a one-frame-a-second contact sheet and the checklist: legibility, no placeholder
   text, real captures, true numbers, brand spelled right, working call to action, loudness, muted
   comprehension, `ffprobe` specs.
10. **Deliver** the 1080p master, a poster frame, a lighter web copy, and a re-laid-out square or
    vertical cut if social needs one (never a crop of the 16:9 film).

## Starter kit

`assets/starter-kit/` is a small Compose Desktop project (Kotlin 2.4, Compose Multiplatform 1.11,
JDK 17+, Gradle 8.10+, ffmpeg with libx264) holding the reusable pieces: palette and type, easing,
doodles, cursor, captions, device and window frames, the footage camera, tutorial cards, the scene
timeline with sound cues, the synthesiser, the frame pump and the capture helper. It needs no product
classpath: product UI comes in as PNGs or as composables you add as a dependency. Its example film
(23 s) renders with `gradle run --args="all"` in about 15 s.

Copy the folder out, replace the placeholder constants and the stand-in product, and write your
scenes. Read its `README.md` for the file map.

Not on Kotlin? `references/other-stacks.md` maps every rule to Remotion and Motion Canvas.

## Traps

- **Guessed coordinates.** Doodles and the cursor that aim at hand-typed positions drift the first
  time copy or layout changes. Aim at measured positions (the kit's `Marks`) or footage fractions.
  Measured positions come from layout, so render each frame twice (the kit does).
- **Soft footage.** A camera push at 2.6x on a 1x capture is mush. Capture wide enough:
  source px at least displayed px times the maximum zoom.
- **A spinner in a capture.** Async images need a second render after the loader warms up.
- **Default easing.** Library "ease" presets are not these curves. Name the curve every time.
- **Two timelines.** Sound times typed separately from picture times drift on the first edit.
- **Rushing.** Shortening every scene to fit more proofs makes the film unreadable. Cut a scene.
- **Fonts.** SF Pro and New York look right on a Mac but are licensed for Apple-platform UI
  mock-ups; published films use the open-licence set unless the licence is cleared.
- **A crop for vertical.** The grid puts captions beside the product; cropping loses one of them.

## Bundled files

| Path | Use |
| --- | --- |
| `references/house-style.md` | Palette, type scale, grid, components, doodles, how the product appears |
| `references/motion.md` | Easing formulas, durations, pacing, cursor and camera choreography, transitions |
| `references/sound.md` | Sound vocabulary, synth recipes, beds, mastering, loudness |
| `references/capture.md` | Getting real, clean product UI into the film |
| `references/pipeline.md` | Every step with commands, the QA checklist, delivery formats |
| `references/storyboard-promo.md` | 40 s promo template, ten scenes with beats and sounds |
| `references/storyboard-tutorial.md` | Tutorial template, six step patterns, the 51 s worked example |
| `references/other-stacks.md` | The same rules in Remotion and Motion Canvas |
| `assets/starter-kit/` | Compose Desktop starter kit and example film |
