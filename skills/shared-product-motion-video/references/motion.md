# House style: motion, pacing and choreography

The films are **pure functions of time**. Every property is computed from `t` (seconds) through a
window and an easing curve; there is no animation state, no physics simulation, no keyframe editor.
That makes any frame reproducible on its own, which is what makes the stills-first QA loop possible.

```kotlin
fun window(t: Float, start: Float, duration: Float) = ((t - start) / duration).coerceIn(0f, 1f)
// e.g. alpha of a card appearing at 3.2 s over half a second:
val p = easeOutCubic(window(t, 3.2f, 0.5f))
```

## The curves (exact formulas)

| Curve | Formula (x in 0..1) | Used for |
| --- | --- | --- |
| easeOutCubic | `1 - (1 - x)^3` | Every entrance and every pen stroke (underline, arrow, tick, star, strike) |
| easeInOutCubic | `x < .5 ? 4x^3 : 1 - (-2x + 2)^3 / 2` | Camera moves, cursor travel, loops, the marker sweep, zooms |
| easeInCubic | `x^3` | Exits: scene fade-out, the hook lines lifting away |
| easeOutBack | `1 + c3 (x - 1)^3 + s (x - 1)^2`, `c3 = s + 1` | Things that land: words `s = 1.4`, cards `1.3 to 1.5`, phones and chips `1.8 to 2.0` |
| spring | `1 - e^(-d x) cos(2 pi f x)` | Icon arrival (`d 6, f 1.6` over 1.1 s), state change bump (`d 7, f 2.2 to 2.4` over 0.6 to 0.8 s), device swap (`d 8, f 2`) |
| bob | `sin(2 pi speed t) * amount` | Idle float of chips: `0.35 Hz, 4 dp`, phase offset 0.3 s per chip |
| press | `1 - sin(pi p) * depth` over 0.3 s | Buttons being clicked: depth 0.06 to 0.08 |

Never use linear motion for anything that moves. Linear is only for the paper drift and the audio
fade.

## Durations

| Motion | Duration | Detail |
| --- | --- | --- |
| Entrance (`enter`) | 0.4 to 0.6 s | alpha 0 to 1, rise 18 dp (30 to 60 dp for windows and big cards) |
| Word pop (`Words`) | 0.5 s each, 0.09 s stagger (0.12 s for a wordmark) | alpha over 0.35 s; rise 26 dp x (1 - easeOutBack 1.4) |
| Caption build | tag 0.4 s at `s`; title lines at `s + 0.12`, 0.22 s apart; detail at `s + 0.55` over 0.5 s | |
| Card or phone pop | 0.5 to 0.55 s; alpha over the first 0.2 s | scale 0.6 (or 0.9 for cards) to 1, rise 30 to 40 dp |
| Stagger between siblings | 0.12 s (row of phones), 0.14 s (chips), 0.28 s (variants), 0.3 to 0.32 s (checklist ticks) | |
| Scene dissolve | 0.3 s overlap; outgoing fades over its last 0.35 s (easeInCubic) and drifts up 16 dp (14 in tutorials) | first scene never fades in; last scene never fades out, it holds |
| Footage shot change | 0.35 s crossfade | |
| Before/after morph | 0.45 s crossfade + spring bump 0.9 to 1 over 0.8 s + sparkle at +0.1 s; label flips at +0.2 s | |
| Device swap | old fades and shrinks to 0.94 over 0.45 s; new springs in from 0.92 | |
| Camera move | 1.0 to 1.6 s between keys, easeInOutCubic | zoom 1 to 2.6x max |
| Cursor travel | 0.38 to 0.6 s per hop (easeInOutCubic), fades in over 0.2 to 0.25 s | |
| Click | 0.28 to 0.3 s | cursor scale -12%, blue ripple radius 6 to 24 dp at 25% alpha fading out |
| Rest after a click | 0.6 s | the cursor stays still, so the viewer sees what the click did |
| Underline / loop / strike | 0.45 to 0.5 / 0.5 to 0.55 / 0.35 s | |
| Arrow | 0.35 to 0.45 s | body in the first 85%, head in the last 15% |
| Sparkle / tick / star | 0.6 / 0.35 / 0.5 s | |
| Marker sweep | 0.45 s, easeInOutCubic, starting ~0.25 s after its words | |
| Count-up of a number | 1.3 s, easeOutCubic, rounded to an integer | |
| Typing | form field 0.045 s/char, hero prompt 0.038 s/char, assistant request 0.032 s/char | caret `▍` solid while typing, 1 Hz blink when idle |
| Assistant "working" | 1.5 s of `Editing <file>` + animated dots (4 per second), then tick | |

## Pacing

- **Scene length**: promo scenes 3.6 to 5.6 s (hook and lockup 4.4 s, product scenes 4.3 to 5.6 s,
  end card 2.6 to 3.4 s). Tutorial steps 6.7 to 13 s; title card 4.3 s; end card 4.5 to 5.8 s.
- **Total length**: promo **40 s or less**. Tutorials **50 to 60 s** for five or six steps; stretch to
  120 s only by adding steps, never by slowing steps down.
- **The rhythm of a product scene**: caption lands in the first 0.7 s; the product appears by 0.5 s;
  the one action happens between 1 s and 3.5 s; the result holds for at least 1 s before the dissolve.
- **One idea per scene.** If a scene needs two captions, it is two scenes.
- **Nothing starts in the first 0.1 s of a scene**, so the dissolve reads as a cut to something new.
- **Holds**: the final state of every scene is on screen, still, for 0.8 to 1.5 s. The end card holds
  about 1.5 s after its last element lands, while the music fades.
- Reading time: allow about **0.3 s per word** of detail copy on screen before the scene exits.

## Cursor and click choreography

The cursor is a macOS arrow (white 3 dp outline, black fill) drawn in the doodle layer, so it is
always above the UI. Choreography per click:

1. Cursor fades in 0.25 s before its first key, usually from the lower right of the page
   (about `930, 520` dp) or low in the footage (`0.6 to 0.8, 0.8`).
2. It travels to the target on easeInOutCubic (0.4 to 0.6 s). While the camera is zooming, the cursor
   moves with the footage because its keys are in footage fractions.
3. Where the target is important, a loop starts drawing 0.2 to 0.6 s before the click.
4. The click: 0.3 s press (shrink and ripple), the UI element's own press (`pressScale`) at the same
   instant, and a click sound on the same frame.
5. The result lands 0.2 to 0.7 s later (a pop, a tick, a shot change), then the cursor rests 0.6 s or
   leaves (fades out over 0.3 s about 0.5 to 1 s after the click).

Aim the cursor at **measured positions**: in the kit, `Modifier.mark("key")` records where a word or
button was laid out, and doodles read `Marks["key"]`. Never hard-code a position you could measure.
On footage, aim with fractions of the capture (`Point(t, 0.651f, 0.027f, click = true)`).

## Camera over footage

A tutorial step is typically five camera keys:

| Key | Time in step | Camera |
| --- | --- | --- |
| 1 | 0.0 s | whole window (`cx .5, cy .48, s 1`) while the caption lands |
| 2 | 1.6 s | still whole, the cursor starts moving |
| 3 | 2.8 s | pushed in 2.1 to 2.6x on the target |
| 4 | 3.8 s | held (the loop and click happen here) |
| 5 | 4.6 s | pulled back to 1.3 to 1.35x on the new state |

The view is clamped inside the capture, so pushing towards an edge never shows past the image.
Capture resolution must survive the push: source width at least `displayed px x max zoom`
(a 560 dp window is 1120 px; at 2.6x that needs 2912 px, which is why desktop captures were 3360 px).

## Transitions

Only one transition exists: the **dissolve with a lift** between scenes (above), with a whoosh under
it. No wipes, no slides, no zoom-throughs, no 3D. Inside a scene, change happens by dissolving
footage shots (0.35 s) or morphing before/after (0.45 s).
