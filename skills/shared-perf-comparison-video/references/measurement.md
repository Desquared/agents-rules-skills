# Choosing the moments

The film has exactly two honest jobs per chapter: start both clocks at the same real
event, and stop each one when that app is genuinely ready. Everything else is styling.

## t0 — the launch instant

Use the **first frame that moves** after the pre-launch stillness. Before an app opens,
a launcher or home screen is motionless: frame-to-frame change sits near zero. The window
transition is a cliff, not a slope, so the first frame above the noise floor is
unambiguous and — this is the point — it is found the same way on both sides.

Do not use:

- **File timestamps or clip starts.** Two recordings started by hand are never aligned.
- **A visible tap.** Most recorders do not draw touches, and when they do the indicator
  appears on one platform and not the other.
- **`am start` / launch-log timestamps** as the video's t0. They are excellent ground
  truth for a number in a report, but they do not correspond to a frame, so the clock on
  screen would drift from the picture behind it.

If a side was still animating before launch (a live wallpaper, a video), the automatic
detection will fire early. Dump frames around the candidate and pick the transition
manually.

## "Ready" — the judgement call

There is no universal definition, which is exactly why you must choose one, write it
down, and apply it to both sides. Reasonable rules, roughly from strictest to loosest:

1. **Screen goes static** — the last burst settles and nothing moves again. Cleanest to
   detect, but punishes an app that keeps a carousel animating and rewards one that
   silently never loads an image.
2. **Layout and text complete, remote images may still arrive.** Usually the fairest for
   content apps, because image latency is the network's fault on both sides equally.
3. **First frame of real content** — the skeleton is gone and something true is on
   screen. Generous; good when one app streams content in progressively and the other
   blocks on everything.

Whichever you pick, the test is: **at the stop frame, do both screens look equally
"arrived"?** Render the stills with `make_video.py film.json --check` and look. If one
side is fully painted and the other is still a grey skeleton, you have applied the rule
asymmetrically, whatever you believe the rule to be.

### When a close call goes either way

Pick the reading less flattering to the new version and say what the alternative would
have shown. A video that claims 1.9× and mentions a defensible 2.4× reading survives
scrutiny. One that claims 2.4× and gets questioned does not.

## Contamination by the person holding the phone

This is the most common way a comparison quietly stops measuring the app.

Symptom: the change curve goes **completely flat for a second or more** in the middle of
what you were about to call "loading", then a burst. Flatness means the app was not
working — it was waiting for a human. If your "ready" instant sits after such a gap, the
number includes someone's reaction time.

Real example: one app opened its home behind a bottom sheet. The screen was perfectly
still for 1.7s while the person decided to swipe it away, then the home finished
painting 0.4s later. Reading "ready" after the swipe would have charged that app ~2s of
human hesitation. The honest reading stopped the clock where the app itself stopped
working, and the gap was disclosed.

What to do:

- Stop the clock at the end of the app's own last burst, before the human-shaped gap.
- If the app genuinely never finished without the interaction, that is a real finding —
  report it as such rather than folding it into a speed number.
- Best fixed at capture time: tell the operator not to touch the screen until the screen
  stops changing. See `capture.md`.

## When the two recordings diverge

After the first milestone the recordings stop being comparable: the person scrolled at
different speeds and tapped at different moments. A single clock across the whole clip
then measures human timing, not software.

Use **one chapter per milestone**, each re-aligned at its own `t0`:

- Chapter 1 — cold start: both `t0` at the launch instant.
- Chapter 2 — open a detail screen: both `t0` at the first frame that responds to the tap.

Each chapter gets its own clock and its own verdict. Viewers read this as deliberate
structure rather than a dodge, and it is the only way the second comparison means
anything.

For chapter 2 style milestones, `t0` is the **first frame that responds to the tap**, not
the tap itself — you usually cannot see the tap. It is the same rule on both sides, so
the comparison holds.

## Caching, and other things that are real but need saying

A second-visit screen that loads instantly is a genuine product win and also not a
like-for-like measurement of the same work. If one side served a screen from cache and
the other fetched it cold, the number is true and the explanation belongs next to it.

Same for: a warm page cache after repeated launches, a backend that got faster between
the two recordings, a device that thermally throttled during the slower run. You cannot
always eliminate these. You can always mention them.

## Multiple runs

For a shareable clip a single clean run per side is normal, and the honest framing is
"here is a representative launch", not "here is the benchmark". If someone intends to
put the number in a document, record 5+ cold starts per side, alternate between the two
apps so backend drift hits both evenly, use the median, and cut the run closest to it.
