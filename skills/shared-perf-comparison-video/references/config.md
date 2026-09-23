# `film.json` schema

One file describes the whole cut. Paths are relative to the config file.

```jsonc
{
  "out": "before-after.mp4",     // optional, default before-after.mp4
  "fps": 48,                     // optional, default 48. 48 maps 24fps sources 1:2 with no judder
  "crossfade": 0.35,             // optional, seconds between chapters

  "left":  { "file": "before.mov", "word": "Before", "sub": "Native Android" },
  "right": { "file": "after.mov",  "word": "After",  "sub": "Compose Multiplatform" },

  "chapters": [
    {
      "name":  "Cold start to home",                       // row label on the summary card
      "title": "Cold start  ·  tap the icon → home ready", // line above the phones
      "badge": "home ready",                               // caption under a stopped clock
      "preroll": 0.75,                                     // still frames shown before t0
      "tail": 1.6,                                         // held after the slower side stops
      "left":  { "t0": 3.583, "ready": 8.583 },
      "right": { "t0": 2.042, "ready": 4.708 }
    }
  ],

  "outro": {
    "enabled": true,
    "seconds": 3.0,
    "headline": "Same phone. Same account. Same journey.",
    "sub": "Old client  →  new client",
    "foot": "iPhone 17 Pro · iOS 26.2 · release builds"
  }
}
```

## Fields that need explaining

**`left` is always the "before" side.** The verdict is computed as left ÷ right, and the
script will honestly print `1.4× slower` if the new version lost. Do not swap the sides to
make the arrow point the right way.

**`t0` and `ready` are timestamps in that side's own file, in seconds.** They come from
`probe_moments.py`. Each chapter's clock measures `ready - t0`, so the two files never
need to be trimmed or aligned beforehand.

**`preroll`** shows a beat of the pre-launch screen so viewers see the starting state and
the clocks sitting at `0.00s`. 0.6–0.8s reads well. It is also what makes the two launches
visibly simultaneous.

**`tail`** is how long the film keeps playing after the *slower* side stops, so the viewer
sees the loser arrive rather than cutting on the verdict. ~1.5s.

**Chapter duration is computed**, not configured: `preroll + max(left, right) + tail`.

**`badge`** should describe the milestone, not the app — "home ready", "shop open",
"results shown". It appears under both clocks, which is part of showing the same rule was
applied to both.

## Chapters

One chapter per milestone, each re-aligned at its own `t0`. Add a second chapter when the
recordings diverge after the first milestone — see `measurement.md`.

```jsonc
{
  "name":  "Opening a detail screen",
  "title": "Open a shop  ·  tap a card → page ready",
  "badge": "page ready",
  "preroll": 0.6,
  "tail": 1.5,
  "left":  { "t0": 15.667, "ready": 18.125 },
  "right": { "t0":  7.917, "ready":  8.750 }
}
```

## Sources of different sizes

Nothing to configure. Each phone window is sized from its own recording's aspect ratio and
both share one height, so an Android beside an iPhone looks intentional rather than
letterboxed.

## Checking before rendering

```bash
"$PERF_PY" scripts/make_video.py film.json --check     # stop-moment stills only
"$PERF_PY" scripts/make_video.py film.json --chapter 0 # one chapter, for a faster look
```
