# Starter kit: product motion video in Compose Desktop

A small, product-neutral Kotlin project that renders films in the house style: warm paper, black ink,
hand-drawn doodles, real product UI, a cursor that clicks, and a synthesised soundtrack. Copy this
folder out of the skill and adapt it. It is an example to change, not a library to depend on.

It needs no product classpath. The original films were built against a product's own runtime so they
could render its UI live. This kit is plain Compose Desktop: product UI arrives either as PNGs in
`assets/` or as composables you add as a dependency and render with `Capture.kt`.

## Needs

| Tool | Version used | Notes |
| --- | --- | --- |
| JDK | 17 or newer (tested on 21) | |
| Gradle | 8.10 or newer (tested on 9.5.1) | No wrapper is bundled. Run `gradle wrapper` once if you want one |
| Kotlin | 2.4.0 | Plugins in `build.gradle.kts` |
| Compose Multiplatform | 1.11.1 | `compose.desktop.currentOs` |
| ffmpeg | any build with `libx264` and the native `aac` encoder | `brew install ffmpeg`. Set `FFMPEG=/path/to/ffmpeg` if it is not on PATH |

No network is used at render time and nothing calls a paid service.

## Run it

```bash
cp -R <skill>/assets/starter-kit ~/work/my-film && cd ~/work/my-film
./scripts/fetch-fonts.sh          # optional: open-licence fonts into ./fonts (system fallback otherwise)
gradle run --args="all"           # capture stand-in UI, render, synthesise audio, mux: out/example.mp4
```

The whole example (23.4 s, 702 frames at 1080p30) renders in about 15 s on an Apple Silicon laptop.

The loop you will actually use:

```bash
gradle run --args="stills 1.2 6.5 11.5"   # PNGs at those film times: check them before anything else
gradle run --args="render 8 14"           # a preview clip of one range
gradle run --args="render"                # the whole silent film
gradle run --args="audio"                 # the soundtrack WAV
```

## Files

| File | What it holds |
| --- | --- |
| `Style.kt` | Palette (`Ink`), font families (`Type`), `display` / `body` / `note` / `code` text styles |
| `Motion.kt` | `window`, the easing curves, `spring`, `bob`, `pressScale`, typing helpers and speeds |
| `Doodles.kt` | Pen strokes: underline, loop, strike, arrow, sparkle, tick, star, the cursor, the marker sweep |
| `Pieces.kt` | `Marks` (find words on screen), `enter`, `pop`, `Picture`, `Device`, `MacWindow`, `Pill`, `card`, `Cta`, `Words`, `Caption`, `Paper` |
| `Footage.kt` | Tutorial kit: `Footage` (camera, cursor, loops over a capture), `StepDots`, `TitleCard`, `TypingField`, `PromptCard`, `Morph`, `CodeCard`, `ChecklistCard` |
| `Timeline.kt` | `Scene` and `Film` (the dissolves between scenes) plus `Cues`, the sound vocabulary |
| `Audio.kt` | Synthesised beds (`PROMO`, `TUTORIAL`), sound effects, mastering, WAV writer |
| `Render.kt` | The frame pump into ffmpeg, stills, the mux |
| `Capture.kt` | Render any composable to a PNG at 2x or 3x |
| `SampleProduct.kt` | A stand-in product so the example runs. Delete it |
| `ExampleFilm.kt` | The example film. Start your storyboard from it |

## Adapting it

1. Replace `Ink.brand`, and the `PRODUCT`, `TAGLINE`, `TAGLINE_WORD`, `CTA`, `CTA_LINE` constants.
2. Capture your real UI into `assets/` (see the skill's `references/capture.md`) and delete `SampleProduct.kt`.
3. Write one `Scene` per storyboard row, with its sounds in the same `Scene` so they cannot drift.
4. Check stills, then render, then listen.
