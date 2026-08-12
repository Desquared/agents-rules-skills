# AX annotation patterns (A–I)

Clone from **user-corrected** screens in the active file. Record the node ID of each signed-off template in the project handoff and prefer it over anything written here.

Node IDs are intentionally absent from this file — they are file-specific and belong in the handoff.

## Pattern A — Simple content + CTAs / dialogs

**Use for:** opening screens, success/earned states, permission dialogs, simple modals.

- Reading order (gray) on title and body, placed in a side gutter.
- Tab order (red) on interactive controls.
- Library **Button** callouts for CTAs (Close, Cancel, Skip, …).
- Modal/dialog screens: annotate the **dialog/sheet only**, not the dimmed content behind it, unless the user asks for the full page.

## Pattern B — Onboarding / stories step

1. Stories chrome: Reading on the timer, Tab on Exit plus a Role.
2. Content: Reading on title and description.
3. Bottom bar: Tab on Back and Next/Got it, each with a Button callout.

## Pattern C — Feed / long scroll

1. Library components only.
2. No order markers on carousel chrome (arrows, dots).
3. Split subtitle and title into separate reading markers when both exist.
4. Filters and chips each get a tab number.
5. Brand logos → **Image** `Alt=""`, never a Button alt.
6. Role only where it adds meaning, and never duplicated with a Button.

## Pattern D — List + logos

1. Tab on Back plus a Button callout (placed left, Direction Right).
2. Reading on the page title.
3. Tab on each list row.
4. Each logo → Image `Alt=""` (Direction Up), label keeps its original fill.
5. One Role on the first row — no Button on that same row.

## Pattern E / E+ — Card grid (+ optional hero)

1. Back plus Button Back.
2. Title and section heading as reading order.
3. One Tab per card.
4. One Role on the **first card**, with the full spoken label (offer + product + brand).
5. Side gutter order markers.
6. Hero image, if any → Image `Alt=""`. No image alts on product photos inside the grid.

## Pattern F — Card / detail sheet

1. Side-column orders, mixing left and right to keep the screen readable.
2. Button callouts for Close / Copy / Save / map CTA — **no Role duplicates**.
3. A single Role for an accordion, only when needed.
4. No highlight rectangles. No documentation-only More info.

## Pattern G — Whole teaser / promo card

1. Annotate the **whole card** as one Button Role.
2. No separate CTA Button; no lone order marker for single-element work.
3. Direction **Left**; Value **off**.
4. Rich Label + Info (instant announcement, and that the entire card is tappable).
5. Logos inside the card → Image `Alt=""`; skip `+N` overflow chips.

## Pattern H — Loading / shimmer

1. One library More info (More Info / Collapsed / Direction Left).
2. Heading `Shimmer`, Description `Label : "Content is loading"`.
3. Do **not** number individual skeleton chips or cards.
4. Swap to a safe font before reparenting if the brand font blocks the write.

## Pattern I — Filter sheets / search chips

1. Follow the structure of the project's signed-off filter/search screen.
2. **No** homemade highlight rectangles.
3. **No** documentation-only More info.
4. Toggle chips, checkboxes, radios and dismiss chips → the Role recipes in `SKILL.md`.
5. Accordion → Role with Collapsed/Expanded — no Button on the same control.
6. CTAs → Button callouts only.
7. Roles in gutters; never stacked.
8. Sibling state screens can be leaner.
9. Sub-frame-only requests → annotate that block only.

Treat a first-pass filter/search screen as a draft until the user signs it off; do not clone from it before then.

## Hybrid notes

| Situation | Approach |
|-----------|----------|
| Map + chips + floating controls | D chrome + I chips + Buttons for map controls; skip pinning every map marker unless asked |
| Error state | Back Button + reading order + primary CTA Button (+ Image `Alt=""` on the illustration if present) |
| Search results full page | E + C hybrid, once the search block is signed off |
| Modal over a feed | Annotate the modal/sheet only (A or F) |
