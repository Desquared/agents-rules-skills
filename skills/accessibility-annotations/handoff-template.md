# Project handoff template

Use this markdown as the **canonical** multi-session status file (keep it in the project repo or a shared drive). Agents read it first and update it after each pass.

Keep it free of secrets and credentials. Figma file keys are fine only if the file itself is already shared with everyone reading the handoff.

```markdown
# Project handoff — [Product] accessibility annotations

**Date:** YYYY-MM-DD
**Primary work surface:** Figma
**Figma file:** https://www.figma.com/design/<FILE_KEY>/...
**File key:** `<FILE_KEY>`
**Page:** `<page name>` (`<pageId>`)
**AX region:** `Accessibility annotations` (`<sectionOrFrameId>`)
**Annotation library:** `<library name / URL>`
**Component name mapping:** default (Order, Button, Image, Role Value Label, More info)
**Brand font:** `<font family>` — needs safe-font swap: yes/no

## Project goal
Annotate the `| AX` screens with the annotation library components.
Follow skill: `shared-figma-accessibility-annotations`.

## How to work
1. Ask which section is next, or continue the named one.
2. Clone from user-corrected pattern templates only.
3. One screen/section at a time; wait for QA before applying to siblings.
4. Sequential Figma writes only.
5. After user corrections, inspect their nodes before touching siblings.

## User-corrected templates (clone from these)

| Pattern | Screen | Node | Status |
|---------|--------|------|--------|
| A | … | `…` | Signed off |
| B | … | `…` | Signed off |

## Draft / awaiting QA

| Section | Node | Notes |
|---------|------|-------|
| … | `…` | first pass |

## Not started

| Section | Node |
|---------|------|
| … | `…` |

## Key node IDs
(One table per section: orders, buttons, roles, images.)

## Open issues
- Font swap reminders
- Anything blocked on user QA

## Recommended next steps
1. …
```

## After each session

Update:

1. Date and the related chat/session name
2. Signed-off versus draft tables
3. Node ID tables for the annotations you created
4. Recommended next steps and QA priority
