---
name: android-accessibility-validator
description: Checks and suggests accessibility improvements for Jetpack Compose and Android Views including TalkBack labels, dynamic text support, and color contrast. Use when creating or modifying UI components, screens, or when the user asks about accessibility.
---

# Accessibility Validator (Android)

## Checklist

### TalkBack
- [ ] Interactive elements have `contentDescription` via semantics
- [ ] Decorative elements: `Modifier.semantics { invisibleToUser() }`
- [ ] Related elements: `Modifier.semantics(mergeDescendants = true)`
- [ ] contentDescription is localized
- [ ] Use `Role` (e.g., `Role.Button`, `Role.Switch`, `Role.Checkbox`) via `semantics { role = Role.Button }`
- [ ] Provide `stateDescription` for toggles/switches (e.g., "On", "Checked")
- [ ] Mark headings with `semantics { heading() }`
- [ ] Use `onClickLabel` for custom action descriptions
- [ ] Control traversal order with `isTraversalGroup` and `traversalIndex` when default order is insufficient
- [ ] Override defaults with `clearAndSetSemantics { ... }` for complex custom components

### Dynamic Text
- [ ] Use `sp` for text sizes, not `dp`
- [ ] Use `MaterialTheme.typography` scales
- [ ] Layout adapts to large text sizes
- [ ] Support pinch-to-zoom scalable content with `Modifier.transformable` + state updates where appropriate

### Color Contrast
- [ ] Text: 4.5:1 (normal), 3:1 (large 18sp+)
- [ ] Use Material Theme semantic colors
- [ ] Color not sole indicator

### Touch Targets
- [ ] Minimum 48dp
- [ ] Use `Modifier.minimumInteractiveComponentSize()`

### Testing
- [ ] Manual testing with TalkBack enabled
- [ ] Automated checks via `enableAccessibilityChecks()` in Compose tests

## Quick Fixes

| Issue                          | Fix |
|--------------------------------|-----|
| No contentDescription          | `Modifier.semantics { contentDescription = "desc" }` |
| Decorative                     | `Modifier.semantics { invisibleToUser() }` |
| Group                          | `Modifier.semantics(mergeDescendants = true)` |
| Small target                   | `Modifier.size(48.dp)` |
| Fixed font                     | Use MaterialTheme.typography.bodyLarge |
| Missing role or state          | `Modifier.semantics { role = Role.Switch; stateDescription = "On" }` |
| Wrong traversal order          | `Modifier.semantics { isTraversalGroup = true; traversalIndex = 1 }` |

## Severity

- 🔴 Critical: Blocks accessibility
- 🟡 Moderate: Reduces usability
- 🟢 Minor: Enhancement