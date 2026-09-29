# House style: the look

Every value here comes from the source of the three reference films (a 40 s promo and two tutorials,
51 s and 57 s). Sizes are in **dp on a 960 x 540 page rendered at density 2**, so 1 dp = 2 px of the
1920 x 1080 frame. If you work in pixels, double every number.

The feel in one line: a person annotating the real product on a warm notebook page. Calm paper,
heavy black headlines, pastel highlighter tags, pen marks that draw themselves, and the product UI as
the only thing with real colour in it.

## Palette

| Token | Hex | Use |
| --- | --- | --- |
| `paper` | `#FBFAF7` | The background of every frame. Never pure white, never dark mode |
| `card` | `#FFFFFF` | Cards, windows, fields |
| `ink` | `#191919` | Headlines, pen strokes, the dark CTA pill, selected chips |
| `muted` | `#6F6E69` | Body copy, labels |
| `faint` | `#A5A39E` | Tertiary text ("Copy", "Close"), separators dots |
| `line` | `#E6E4DF` | 1 dp hairlines, card borders, unselected chip borders, grey "before" pills |
| `dot` | `#E3E1DB` | The dot grid on the paper |
| `codeBg` | `#F6F5F2` | Code blocks inside cards |
| `windowBar` | `#F3F2EF` | The macOS window title bar |
| `yellow` | `#FBE7A1` | Highlighter: the marker sweep, "Inspect" and "Tutorial" tags |
| `blue` | `#D6E8F5` | Highlighter tag |
| `green` | `#DDEEDC` | Highlighter tag, "Open" / "Designed" result pills |
| `pink` | `#F7DDE6` | Highlighter tag, safe-area bands |
| `purple` | `#E9E1F5` | Highlighter tag, assistant / AI label |
| `orange` | `#FBE3CF` | Highlighter tag |
| `blueInk` | `#2F6FDE` | Selection boxes (12% fill + 1.6 dp stroke), focus rings, click ripples, primary buttons in forms |
| `greenInk` | `#2E8B57` | Ticks and success copy ("Copied.", "Updated ...") |
| `brand` | yours | The app icon and at most one accent. The reference films used the product's brand orange only in the icon |

Rules:

- **Tag colour rotates by scene** (blue, purple, yellow, green, orange, pink): each section gets its
  own pastel so the viewer feels the change of topic. Text on a pastel is always `ink`.
- **Saturated colour belongs to the product.** Nothing the film draws competes with the UI: pastels,
  ink and two small inks only.
- Shadows are soft and cool: cards `12 dp` elevation, ambient `#22000000`, spot `#33000000`; windows
  `22 dp`, ambient `#33000000`, spot `#44000000`.

## Typography

Four roles. The reference films used Apple's SF Pro Display, SF Pro Text, New York Italic and SF Mono
from the Mac. Apple licenses those for mock-ups of Apple-platform UI, so for anything published use
the open-licence equivalents the starter kit loads (or get the SF licence cleared):

| Role | Reference font | Open-licence default | Where |
| --- | --- | --- | --- |
| Display | SF Pro Display | Inter Display | Headlines, wordmark, card titles |
| Text | SF Pro Text | Inter | Body, labels, UI in cards |
| Serif | New York Italic | Newsreader Italic | One emphasised word, margin notes |
| Mono | SF Mono | JetBrains Mono | Tags, paths, code, prompts, device labels |

Display style: `letterSpacing -0.02em`, `lineHeight 1.06 x size`. Body style: `lineHeight 1.4 x size`.

| Use | Size (sp) | Weight | Colour |
| --- | --- | --- | --- |
| Hook headline | 66, and 74 for the answer line | Black | ink |
| Wordmark in the lockup | 70 | Black | ink |
| Tutorial title card | 62 | Black | ink |
| End-card line | 52 | Black | ink |
| Full-width section title | 40 to 44 | Bold | ink |
| Caption title (left column) | 40; 36 to 38 for three lines; 33 when a line is long | Bold | ink |
| Lockup tagline | 23 body, emphasised word 25 serif italic | Regular | muted / ink |
| Title-card or end-card italic line | 20 to 24 serif italic | Regular | ink |
| Caption detail | 15 | Regular | muted |
| Margin note by a doodle | 16 serif italic | Regular | ink |
| CTA pill label | 14 to 16 | Medium | white on ink |
| Chips floating around a lockup | 15 | Medium | ink on pastel |
| Card title | 17 to 24 | SemiBold / Bold | ink |
| Section tag (mono pill) | 11 (10 in dense cards, 12 on title cards) | Medium | ink on pastel |
| Code and prompt lines | 10.5 to 13 mono | Regular | ink / muted |
| Smallest text anywhere | 9 to 10 | Regular | muted |

Legibility floor: nothing a viewer must read is under **11 sp (22 px)**. The 9 to 10 sp sizes are only
for window titles and chrome that just has to look right.

Copy voice: short declaratives with a full stop. Two-beat titles built on repetition or contrast
("Every screen. / Every state.", "Real devices. / Real safe areas.", "Point at it. / Change it.").
The detail line is one plain sentence that states a real, checkable fact, usually with a number
computed from the product ("<N> live states on one wall. Nothing exported, nothing out of date.").
One word per scene may go in italic serif, usually the claim ("the real app.").

## Layout grid

```
0                60        360 372                                   932  960 dp
|  margin 60 dp  | caption  |12| product area (560 dp)                |28 |
|                | column   |  | windows, devices, cards               |   |
top: tags/titles from y 44 to 150; product area from y 52 to 70; step dots at y 24, right edge 936
```

- **Product scene** (most scenes): caption column at `x 60, y 150, width 300`; product at
  `x 372, width 560` (a macOS window of footage, or phones, or cards), starting at `y 58 to 70`.
- **Full-width scene** (a journey, a PR): tag + title top-left at `x 60, y 44`, content below from
  `y 196`, 40 dp side margins, or centred on the page with a top padding of 50 dp.
- **Centre scene** (hook, lockup, title card, end card): one centred column. The end card lifts 30 dp
  above centre and puts a small row (mono pill, a 5 dp dot, one line) 58 dp above the bottom edge.
- **Safe margins**: 40 dp minimum from any edge for anything readable (7% horizontally); chips in a
  lockup may sit at 96 dp from the left. Step dots sit 24 dp from the top and right.
- Phones: 238 to 460 dp tall. A single hero phone 420 to 460 dp; a row of five 262 dp; a variant trio
  238 dp beside a 330 dp original. Label each variant with a 10 sp pill 22 dp above it.

## Components the films are built from

| Piece | Spec |
| --- | --- |
| Paper | `paper` fill; dots radius 0.9 dp every 24 dp; the grid drifts 3 dp/s down and 1.5 dp/s right |
| Pill / tag | pastel fill, radius 6, padding 8 x 3 dp, Medium; mono for section tags |
| Card | white, 1 dp `line` border, radius 12 (14 to 16 for big cards), soft shadow |
| macOS window | white, radius 12, 24 dp title bar `windowBar`, traffic lights 8 dp (`#FF5F57 #FEBC2E #28C840`, 5 dp apart), 9 sp centred title |
| Device | the real screenshot inside its device shell (captured with the shell, or a plain black bezel drawn around a raw capture); keep the capture's aspect ratio |
| CTA pill | `ink` fill, radius 20 to 22, padding 16 x 8 (20 x 10 large), white Medium 14 to 16 sp, may carry a glyph (`▶`, `↗`) |
| Property chips | 11 sp, radius 6, padding 7 x 3; selected = ink fill, white text; else paper fill, `line` border |
| Assistant card | card with a purple mono label pill and a faint mono context path, pasted lines in muted mono 11 sp, the request as `› typed text▍` mono 13 sp, `Editing <file>...` in blueInk, then a tick and green done line |
| Form card | 470 dp card, 24 dp padding, 24 sp title, fields with 11 sp label and 15 sp value, blue 2 dp focus ring while typing, blue primary button + outlined secondary |
| Result card | 430 to 500 dp card: green status pill ("Open"), 17 to 19 sp title, mono branch or path line, hairline, then ticked checks 0.3 s apart |
| Step dots | "Step" 11 sp + 22 dp circles 8 dp apart: current ink with white number, done `line`, upcoming white with `line` border |
| Morph label | a mono pill under the before/after box: grey `line` "Draft", turning green "Designed by ..." |

## Doodles (the hand-drawn layer)

All are paths revealed like a pen: round caps, round joins, ink colour, 2.2 to 2.8 dp wide.

| Mark | Use | Shape |
| --- | --- | --- |
| Strike | Crossing out the wrong idea in the hook | one slightly rising cubic, 6 dp past each end, **5 dp wide** |
| Underline | Under the italic claim word | a wavy cubic plus the flick back, 2 dp under the word |
| Loop | Circling the thing that matters in the UI | an ellipse 10 dp / 8 dp outside the target, 1.075 turns so it overshoots, 3-lobe wobble of 3 dp |
| Arrow | Pointing from a note or between steps | quadratic curve bent 15% to 30%, head at 2.6 rad, 7 to 12 dp |
| Sparkle | "New", "done", a reveal | six strokes (four long, two diagonal at 55%), each 0.08 later |
| Tick | Every success | three-point check in `greenInk`, 2.6 dp |
| Star | "This one" on a chosen variant | five-point outline, 2 dp |
| Marker | The answer in the hook | `yellow` rounded rect (radius 6) behind the lower 42% of the words, 8 dp past each side |
| Selection box | Pointing at a UI element | `blueInk` 12% fill + 1.6 dp stroke, radius 3 to 4, then a thin blue arrow to its explanation card |

Doodle discipline: at most two or three marks per scene, each tied to a word or a UI element found by
its measured position (never floating decoration), and always paired with a scribble sound.
A margin note (16 sp serif italic) may sit next to a loop or star: "13 login states, all of them
live", "B, but bolder".

## How the product appears

- **Real UI only.** Every screen, window and component in the reference films was rendered from the
  product's real code with fixture data, or is a real capture of the tool. Nothing was redrawn,
  mocked in a design tool, or faked. The numbers on screen (377 states, 191 components, 1,502
  properties) were computed from the product at capture time and counted up on screen.
- Phones appear in their device shell at 3x; desktop tools appear inside a light macOS window at 2x.
- A component on its own sits on a white card ("stage") so it reads as the product's canvas.
- Crop away chrome that should never be seen (the films hid the bottom 70 px status strip of every
  desktop capture).
- The film's own cards (assistant, form, result) are drawn in the house style, not the product style:
  they represent the workflow around the product, not the product.
