# Figma tooling & component kit

## Component kit

Prefer cloning **already-correct instances on the canvas** over creating a fresh instance from the library master, because canvas instances usually carry the project's label styling already.

Record the local node IDs for each of these in the project handoff — they are file-specific.

### Image

| Item | What to record | Notes |
|------|----------------|-------|
| Component set | Local component set ID | Directions: Up / Down / Left / Right |
| Pre-labeled `Alt=""` instances | One ID per direction you use | Clone these |

Property: `Label title` → `Alt=""`

### Button

| Item | What to record | Notes |
|------|----------------|-------|
| Component set | Local component set ID | |
| Back (Direction Right) | Instance ID | Keep the label fill from the template |
| Close (Direction Left) | Instance ID | |
| Filters (Direction Up) | Instance ID | |

Property: `Button Label` → `Button label= "Close"`. Match the exact quote characters used by the library.

### Role, Value, Label

| Item | What to record |
|------|----------------|
| Component set | Local component set ID |
| First card in a grid | Instance ID (Direction Right, Value on) |
| Teaser card | Instance ID (Direction Left, Value off) |
| Filter chip | Instance ID |

Editing Role body text often requires a font swap on the placeholder label text nodes first.

### Order

| Type | Notes |
|------|-------|
| Tab (red) | Focusable + reading |
| Reading (gray) | Text only |
| Focus number | Set via the component's focus-number property; a plain system font usually writes reliably |

### More info

| Use | Notes |
|-----|-------|
| Shimmer / loading (Pattern H) | Heading `Shimmer`; Description `Label : "Content is loading"`; Direction Left |
| Documentation-only notes | Do not add |

Avoid deprecated local helper components left over in the file — always use the current library components.

## Clone + font fallback recipe

Variable or brand fonts often fail to load inside the plugin sandbox, which breaks text writes and sometimes `appendChild`. Fall back to a safe font, then restore the fills.

```js
// 1) Clone a pre-labeled instance
const button = source.clone();

// 2) Try setProperties first
try {
  await button.setProperties({ [buttonLabelPropertyKey]: 'Button label= "Close"' });
} catch {
  // 3) Swap to a safe font, then edit
  await figma.loadFontAsync({ family: 'Inter', style: 'Regular' });
  for (const node of button.findAll((n) => n.type === 'TEXT')) {
    node.fontName = { family: 'Inter', style: 'Regular' };
    // edit characters if this is the label node
    node.fills = [{ type: 'SOLID', color: { r: 1, g: 1, b: 1 } }];
  }
}

// 4) Parent to the section, then position
section.appendChild(button);
button.x = targetX;
button.y = targetY;
```

For More info and Role reparenting failures, swap the font **before** `appendChild`.

## Callout Direction cheat sheet

| Direction value | Box sits | Arrow points |
|-----------------|----------|--------------|
| Right | Left of target | → at target |
| Left | Right of target | ← at target |
| Up | Below target | ↑ at target |
| Down | Above target | ↓ at target |

## Placement heuristics

- Section-relative coordinates: `abs(node) - abs(section)`.
- Leave gutters so annotations never sit on neighbouring screens.
- Offset sibling clusters by the screen `Δx`; correct Y when header heights differ.
- For control stacks on a screen edge, put Button callouts **outside** the screen with the Direction pointing inward.
- In chip grids, on-chip order markers are acceptable when side placement would be ambiguous.

## Inspection checklist (before writing)

1. Take a screenshot and read the metadata of the section (or inspect it with your Figma write skill).
2. List interactive controls versus reading-only text.
3. Check for existing AX instances so you do not duplicate them.
4. Pick the pattern, then list the intended order stream and callouts.
5. Write sequentially, screenshot the result, and update the handoff.
