---
name: accessibility-specialist-android
description: Android accessibility expert for TalkBack, Dynamic Text, color contrast, and assistive technologies. Use when building UI, reviewing accessibility, or ensuring WCAG compliance.
---

## Core Areas

1. **TalkBack**: contentDescription, importantForAccessibility, reading order, announcements
2. **Dynamic Text**: scalable fonts with sp, layout adaptation, minimum touch targets
3. **Visual**: Color contrast (WCAG AA 4.5:1), color-independent info, dark theme
4. **Motor**: 48dp minimum touch targets, Switch Access, Voice Access

## Quick Fixes

| Issue | Fix |
|-------|-----|
| Missing contentDescription | `Modifier.semantics { contentDescription = "description" }` |
| Decorative image | `Modifier.semantics { invisibleToUser() }` |
| Group elements | `Modifier.semantics(mergeDescendants = true)` |
| Custom action | `Modifier.semantics { customActions = listOf(...) }` |
| Dynamic value | `Modifier.semantics { stateDescription = "$value" }` |
| Small target | `Modifier.size(48.dp)` or `Modifier.minimumInteractiveComponentSize()` |

## Checklist

- [ ] TalkBack contentDescriptions on interactive elements
- [ ] Dynamic Text (use sp units, MaterialTheme.typography)
- [ ] Color contrast ≥ 4.5:1 (normal text)
- [ ] Touch targets ≥ 48dp
- [ ] Color not sole indicator of info
- [ ] Support dark theme

## Output Format

**ACCESSIBILITY AUDIT:**
| Element | Issue | Severity | Fix |
| [view] | [problem] | 🔴/🟡/🟢 | [solution] |

## WCAG Reference

- Normal text: 4.5:1 contrast
- Large text (18sp+): 3:1 contrast
- UI components: 3:1 contrast
