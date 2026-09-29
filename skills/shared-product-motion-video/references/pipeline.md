# Production pipeline, end to end

```
brief -> storyboard -> capture -> build scenes -> stills QA -> render -> audio -> mux -> final QA -> deliver
```

Commands below use the starter kit (`assets/starter-kit`). Other stacks: see `other-stacks.md`.

## 1. Brief (10 minutes, before anything else)

Write down, and get the product owner to agree:

- **Audience and channel**: launch post, landing page, internal demo, onboarding, release notes.
- **Kind**: promo (at most 40 s, sells one idea) or tutorial (50 to 120 s, teaches one task).
- **The one sentence** the film must leave behind. It becomes the hook or the title card.
- **The proof points**: three to six things the product really does, each one showable in one scene.
- **Real facts** you will put on screen (counts, supported platforms) and where they come from.
- **Brand inputs**: product name exactly as written, icon (vector or 1024 px PNG), brand colour, the
  call to action (URL, command, store).

## 2. Storyboard

Fill a scene table before writing any code. Start from `storyboard-promo.md` or
`storyboard-tutorial.md` and keep their structure. One row per scene:

| # | Scene | Start | Length | Tag (colour) | Title | Detail line | Product shown | Action (cursor, typing, morph) | Doodles | Sounds |
| - | ----- | ----- | ------ | ------------ | ----- | ----------- | ------------- | ------------------------------ | ------- | ------ |
| 1 | | 0.0 | 4.4 | | | | | | | |

Check the table against the pacing rules in `motion.md` (scene lengths, one idea per scene, total
length) before moving on. Read every title and detail line aloud: if it takes longer to read than the
scene holds still, cut words.

## 3. Capture

Follow `capture.md`. Output: PNGs in `assets/`, plus a facts file for any number shown on screen.
With the kit and Compose UI on the classpath:

```bash
gradle run --args="capture"
```

Open every capture at 100%. Reject any with placeholder text, spinners, a wrong status bar, personal
data or a debug banner.

## 4. Build the scenes

In the kit, one `Scene(name, length, cues = { ... }) { t -> ... }` per storyboard row, in
`ExampleFilm.kt` (rename it). Inside a scene:

- Time is local (0 = the scene starts). Start nothing before 0.1 s.
- Build with the pieces (`Caption`, `Device`, `Footage`, `Words`, `Cta`, cards) and the doodles.
- Aim the cursor and doodles at `Marks[...]` or footage fractions, never at guessed coordinates.
- Declare each sound in `cues` at the same local time as its picture: every click, landing, typed
  string, doodle and success.

## 5. Stills QA (the cheap loop)

Render stills at the key moments of each scene (its settled state, each click, each result):

```bash
gradle run --args="stills 2.0 3.9 6.5 11.5 16.0"
```

Look at every still at full size. Fix and repeat until each one would pass as a poster. This loop
takes seconds; a full render takes about 20 ms a frame plus encoding, so do not skip it.

## 6. Render

```bash
gradle run --args="render 8 14"   # preview one range while tuning motion
gradle run --args="render"        # the whole silent film: out/<name>-silent.mp4
```

What the kit runs (and what the reference films used): Compose renders each frame off screen at
1920 x 1080 (a 960 x 540 dp page at density 2), each frame is rendered twice so measured positions are
current, and raw BGRA pixels are piped into:

```bash
ffmpeg -y -loglevel error \
  -f rawvideo -pix_fmt bgra -s 1920x1080 -r 30 -i - \
  -c:v libx264 -preset slow -crf 14 -pix_fmt yuv420p -movflags +faststart \
  out/film-silent.mp4
```

- 30 fps, constant frame rate, 1080p, H.264 High, 4:2:0.
- CRF 14 is visually lossless for flat paper and text. The delivered films came out at 1.5 to
  2.1 Mbps because flat backgrounds compress very well.

## 7. Audio

```bash
gradle run --args="audio"         # out/<name>.wav, 16-bit 44.1 kHz stereo
```

Listen once with headphones against the picture: every click on its frame, no sound without a
picture, nothing louder than the pops. See `sound.md` for the recipes and levels.

## 8. Mux

```bash
ffmpeg -y -i out/film-silent.mp4 -i out/film.wav \
  -map 0:v:0 -map 1:a:0 -c:v copy -c:a aac -b:a 192k -shortest -movflags +faststart \
  out/film.mp4
```

The delivered reference files match this: H.264 High 1920 x 1080 30 fps yuv420p, AAC-LC 44.1 kHz
stereo about 192 kbps, `+faststart`. (The exact mux command was not in the reference source; this one
reproduces the delivered streams.)

## 9. Final QA checklist

Make a contact sheet (one frame a second) and read it top to bottom:

```bash
ffmpeg -i out/film.mp4 -vf "fps=1,scale=384:-1,tile=6x7" out/sheet.png
```

Picture:

- [ ] Every readable string is at least 11 sp (22 px) and on screen long enough to read aloud.
- [ ] Nothing readable within 40 dp (80 px) of an edge.
- [ ] No placeholder text anywhere: grep the film source for `lorem`, `TODO`, `REPLACE`, `Product`,
      `example`, `test` and read every string literal once.
- [ ] Every product image is a real capture with a clean status bar, crisp at its largest zoom.
- [ ] Every number on screen came from the facts file, and is true today.
- [ ] Doodles land on their targets in every frame of their scene (check the stills at the start and
      the end of camera moves).
- [ ] One idea per scene; each scene's final state holds still for at least 0.8 s.
- [ ] Promo 40 s or less; tutorial steps match the step dots.
- [ ] A poster frame is exported (below): the first frame is blank paper by design, so players need one.

Brand:

- [ ] Product name spelled and cased exactly as the brand does, every time.
- [ ] The real icon, never a placeholder square.
- [ ] Brand colour only in the icon and at most one accent; the rest is the house palette.
- [ ] The call to action is real and works (open the URL, run the command).

Sound:

- [ ] Loudness about -16 LUFS integrated, true peak -1 dBTP or lower (`ebur128`, see `sound.md`).
- [ ] Every click, pop and chime sits on its frame; the music fades out by the last frame.
- [ ] The film makes complete sense muted.

Technical:

- [ ] `ffprobe` shows 1920 x 1080, 30/1 fps, h264 High, yuv420p, aac 44100 Hz stereo, the expected
      duration.

## 10. Deliver

| Deliverable | How |
| --- | --- |
| Master | `film.mp4`, 1080p30 H.264 + AAC, `+faststart` |
| Poster frame | `ffmpeg -ss <lockup time> -i film.mp4 -frames:v 1 poster.png` (the end card or the lockup) |
| Web-light copy | `ffmpeg -i film.mp4 -c:v libx264 -crf 22 -preset slow -c:a copy -movflags +faststart film-web.mp4` |
| Silent loop for a landing page | the silent mp4, or `-an` on the master |
| GIF preview (short, for chat) | `ffmpeg -ss 0 -t 6 -i film.mp4 -vf "fps=15,scale=960:-1:flags=lanczos,split[a][b];[a]palettegen[p];[b][p]paletteuse" preview.gif` |
| Square or vertical (social) | re-lay out the scenes on a 540 x 540 or 540 x 960 dp page. Do not crop the 16:9 film: the grid puts captions and product side by side |
| Accessibility | a short text description of the film; if a voiceover exists, an `.srt` |

Name files `<Product> <kind> - <topic>.mp4` (for example `Acme promo.mp4`,
`Acme tutorial - New project.mp4`). Keep the source project (not the renders) in version control, so
the film can be re-rendered when the product changes.
