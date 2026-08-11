---
name: shared-figma-accessibility-annotations
description: Annotate Figma UI screens with accessibility annotations — reading order, tab/focus order, Button, Image alt, Role/Value/Label and More info callouts — using the annotation library configured for the project. Use when asked for accessibility annotations, AX annotations, a11y markup in Figma, reading or focus order, screen reader labels for designs, or to continue an accessibility annotation handoff.
---

# Figma Accessibility Annotations

Annotate Figma screens with accessibility (AX) annotations so engineers know the reading order, focus order, roles, labels, and alt text for every element.

Work is **Figma-first**: use the annotation library configured for the project. Never invent components or draw homemade highlight shapes.

## Project configuration

This skill is design-system agnostic. Resolve these values before the first write, from the project handoff, the repo, or by asking once:

| Setting | How to resolve | Fallback |
|---------|----------------|----------|
| Annotation library | Handoff `Annotation library` field, or the AX library already published to the target Figma file | Ask the user for the library name/URL |
| Component names | Inspect the library's component sets in the file | `Order`, `Button`, `Image`, `Role Value Label`, `More info` |
| Brand font | The font used by the library's text layers | Detect at runtime; see [figma-tooling.md](figma-tooling.md) for the font-swap fallback |
| AX region | Handoff `AX region`, or the page/section holding the annotated screens | Ask which section to annotate |
| Signed-off templates | Handoff template table | The user's most recently corrected screen |

If the library names components differently (for example `Focus Order` instead of `Order`), map them once and record the mapping in the handoff. Everything else in this skill stays the same.

## Before you start

1. Load your environment's Figma write skill (for example `figma-use`) before every Figma write call.
2. Read the project handoff if one exists — it is the canonical status and lists signed-off templates. See [handoff-template.md](handoff-template.md).
3. Confirm the section/screen to annotate. Prefer **one screen at a time**; wait for user QA before applying to sibling screens.
4. Open the matching user-corrected pattern (see [patterns.md](patterns.md)) and clone from it. Do not invent denser layouts.

## Workflow

```
Task progress:
- [ ] Identify section + screens
- [ ] Pick pattern (A–I) from the signed-off template
- [ ] Inspect target nodes (texts, controls, existing AX)
- [ ] Clone library / canvas instances (never homemade shapes)
- [ ] Place orders + callouts (gutters, no stacking)
- [ ] Screenshot-verify
- [ ] Update handoff status
- [ ] Stop for user QA
```

### Process rules

1. **Sequential Figma writes only** — never run parallel write calls on the same file.
2. After the user corrects something, **inspect their nodes** before touching siblings — their edit is the new source of truth.
3. Clone **pre-labeled** instances instead of editing text properties on a fresh library instance.
4. If the brand font blocks `appendChild` or text edits: swap the text to a safe font (for example Inter) → reparent/edit → restore original fills.
5. Parent annotations to the **section** (or a stable overlay), not deep inside screen chrome, when overlays get messy.
6. Sibling state screens can be **leaner** — only annotate what is new; shared chrome lives on the primary screen.
7. Prefer **less clutter**. Never stack annotations on top of each other.

## Critical rules (non-negotiable)

### Image ≠ Button

| Use case | Component | Label |
|----------|-----------|-------|
| Real controls (Back, Close, CTAs, Filters…) | **Button** | `Button label= "…"` |
| Logos / hero / decorative / empty illustrations | **Image** | `Alt=""` (or a filled alt) |

- Never put `Alt=""` on a Button.
- Image/Button label text must keep its original fill colour after any font swap.

### Button ≠ Role (no doubles)

- **Button** = named actions (Close, Back, Clear filters, Try again, …).
- **Role** = richer traits (accordion, tappable teaser, first card in a grid, filter/checkbox/radio/dismiss chips) **and** no Button on that control.
- Never pair Button + Role on the same control.

### Order markers

- **Reading order (gray)** = text only. **Tab order (red)** = reading + focus.
- **One continuous number stream** across reading and tab markers.
- Default: markers in a **side gutter**. On-element only when side placement is ambiguous (for example chip grids).
- Single-element requests usually need **no** lone order `1`, unless the user asks for order or the block has multiple controls.

### Placement

- Keep callouts **next to** their targets; Callout Direction must point at the real control.
- Roles in side gutters; if the left is tight, use the right. Stagger Y.
- Keep Button/Image/Role callouts **off** screen content when they would hide UI.
- No homemade highlight rectangles. Use library rectangles only if the user asks.
- No documentation-only More info notes. Exception: **Pattern H Shimmer**.

### Loading / shimmer (Pattern H)

One More info table — not per-skeleton order markers:

| Prop | Value |
|------|-------|
| Type | More Info |
| State | Collapsed |
| Callout Direction | Left (box to the right of the skeleton) |
| Heading | `Shimmer` |
| Description | `Label : "Content is loading"` |

## Pattern picker (quick)

| Screen type | Pattern | Clone mindset |
|-------------|---------|---------------|
| Simple content + CTAs / dialogs | **A** | Reading on title/body + Tab on CTAs + Button |
| Onboarding / stories step | **B** | Stories chrome + content + Back/Next Buttons |
| Feed / long scroll | **C** | Filters/chips get Tab; logos are Image; no carousel chrome orders |
| List + logos | **D** | Back Button; row Tabs; logo Image; one Role on the first row |
| Card grid subpage | **E / E+** | Back; title Reading; one Tab per card; Role on first card; hero Image if present |
| Card / detail sheet | **F** | Side orders; Buttons for CTAs; Role only for accordion |
| Whole teaser card | **G** | One Role (Direction Left, Value off); logo Images; no separate CTA Button |
| Loading skeleton | **H** | More info Shimmer only |
| Filter sheets / search chips | **I** | Chip/checkbox/radio/dismiss Roles; CTA Buttons; no highlight rectangles |

Pattern detail: [patterns.md](patterns.md).
Component kit and tooling gotchas: [figma-tooling.md](figma-tooling.md).
Handoff format for multi-session work: [handoff-template.md](handoff-template.md).

## Role label recipes (filters)

| Control | Role | Label example | Value | Hint |
|---------|------|---------------|-------|------|
| Toggle filter chip | Button | `One out of seven. <Filter name>` | Selected / Unselected | Double tap to select / unselect |
| Category chip | Button | `One out of five. <Category>` | Selected / Unselected | Double tap to toggle |
| Sort accordion | Button | `Sort by` | Collapsed / Expanded | Double tap to expand / collapse |
| Checkbox row | Check box | `One out of four. <Option>` | Checked / Not checked | Double tap to toggle |
| Radio row | Radio Button | `One out of four. <Option>` | Selected / Unselected | Double tap to toggle |
| Dismissible chip | Button | `One out of three. <Value>. Delete icon.` | `-` | Double tap to dismiss |

## Teaser card (Pattern G) checklist

1. The whole card is one Role — not a separate CTA Button.
2. Direction **Left**; Value **off**.
3. Label is the full spoken string (title + CTA + summary).
4. Info covers the instant announcement and that the entire card is tappable.
5. Hint is `Double tap to activate`.
6. Logos inside the card → Image `Alt=""`; skip `+N` overflow chips.

## Figma plugin constraints

- Switch pages with the async page API only, and at most one page switch per call.
- Failed scripts are atomic — diagnose and fix, then retry. Do not blind-retry.
- Do not rely on toast notifications; always return the IDs of created or mutated nodes.
- Do not rely on variable/brand fonts loading successfully for text property writes.

## Done criteria (per screen)

- [ ] Correct pattern applied; cloned from the signed-off template when one exists
- [ ] Image vs Button correct; no Button+Role doubles
- [ ] Orders continuous; gutters clean; no stacking
- [ ] Callouts close to targets with the correct Direction
- [ ] Screenshot looks clean next to neighbouring screens
- [ ] Handoff updated; waiting on user QA before siblings
