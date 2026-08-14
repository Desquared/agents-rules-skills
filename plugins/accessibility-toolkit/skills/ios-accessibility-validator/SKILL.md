---
name: ios-accessibility-validator
description: Checks and suggests accessibility improvements for SwiftUI and UIKit code including VoiceOver, Dynamic Type, color contrast, motion, and input methods. Use when creating or modifying UI components, views, or when the user asks about accessibility.
---

# Accessibility Validator

## Core Checklist (applies to most projects)

### VoiceOver & Screen Reader

- [ ]  Interactive elements have `.accessibilityLabel()` or meaningful built-in label
- [ ]  Decorative / purely visual elements use `.accessibilityHidden(true)`
- [ ]  Related elements grouped with `.accessibilityElement(children: .combine)` or `.accessibilityElement(children: .contain)`
- [ ]  Complex controls provide `.accessibilityHint()` when label alone is insufficient
- [ ]  Appropriate `.accessibilityTraits` (`.button`, `.header`, `.image`, `.adjustable`, `.toggle`, etc.)
- [ ]  Stateful controls provide `.accessibilityValue()` (e.g., "50%", "3 of 10", "On")
- [ ]  Custom gestures have equivalent `.accessibilityAction()` alternatives
- [ ]  Labels & hints are localized (use `LocalizedStringKey` or `NSLocalizedString`)
- [ ]  Use `.accessibilityRotor()` to provide quick navigation for repeated content types (feeds, search results, settings)

### Dynamic Type & Text Scaling

- [ ]  Use semantic font styles (`.font(.body)`, `.headline`, `.title`, etc.) — avoid `.system(size:)`
- [ ]  Support at least 200% text scaling without truncation, overlap, or horizontal scrolling
- [ ]  Use `@ScaledMetric` or `.dynamicTypeSize` modifiers for padding, spacing, icon sizes
- [ ]  Layout adapts gracefully to largest Dynamic Type sizes (use `.minimumScaleFactor` sparingly)
- [ ]  Use `ViewThatFits` to provide alternative layouts at extreme Dynamic Type sizes (e.g., vertical instead of horizontal)

### Color & Visual Indicators

- [ ]  Text contrast ≥ 4.5:1 (normal), ≥ 3:1 for large/bold text (≥18 pt bold or ≥24 pt regular)
- [ ]  Use semantic colors (`.primary`, `.secondary`, `.accentColor`, `.fill`) over hardcoded colors
- [ ]  Color is not the only way information is conveyed (use icons, patterns, text too)
- [ ]  Test with both light and dark appearance — ensure contrast holds in both
- [ ]  Support Increase Contrast setting (`.accessibilityContrast` environment value) with higher-contrast color variants

### Touch & Interaction Targets

- [ ]  Minimum touch target 44×44 pt (or 48×48 recommended in dense UIs)
- [ ]  `.contentShape(Rectangle())` used when hit area needs to be larger than visible bounds

### Focus Management

- [ ]  Use `@AccessibilityFocusState` to programmatically move VoiceOver focus after async updates or navigation
- [ ]  Focus moves logically after alerts, sheets, or content changes
- [ ]  `.accessibilityAddTraits(.isModal)` on modal overlays to trap focus

---

## Additional Checks (regulated, enterprise, or high-quality apps)

- [ ]  **Reduced Motion:** honor `.environment(\.accessibilityReduceMotion)` — replace motion with static alternatives
- [ ]  **Differentiate Without Color:** use shapes, patterns, underlines, icons when relying on color
- [ ]  **Voice Control:** test common spoken phrases work (custom `.accessibilityIdentifier` when needed)
- [ ]  **Voice Control Input Labels:** provide `.accessibilityInputLabels()` for elements where the visible label may be awkward to speak
- [ ]  **Switch Control / Full Keyboard Access:** testable via Accessibility Inspector
- [ ]  **Accessibility Sort Priority & Activation Point** when spatial order is non-obvious

---

## Quick Fixes

| **Issue** | **Fix** | **Severity** |
| --- | --- | --- |
| Missing label | `.accessibilityLabel("Add to cart")` | 🔴 |
| Decorative image / icon | `.accessibilityHidden(true)` | 🔴 |
| Group of related elements | `.accessibilityElement(children: .combine)` | 🔴 |
| Contrast too low | Use `.foregroundStyle(.primary)` or semantic color set | 🔴 |
| Custom gesture with no alternative | Provide `.accessibilityAction()` equivalent | 🔴 |
| Small tap target | `.frame(minWidth: 44, minHeight: 44)` or `.contentShape()` | 🟡 |
| Fixed font size | Use `.body`, `.title2`, etc. | 🟡 |
| Motion / animation heavy | `.transaction { $0.disablesAnimations = true }` when reduce motion | 🟡 |
| Focus doesn't move after content change | `@AccessibilityFocusState` to move focus programmatically | 🟡 |
| No dark mode contrast check | Test both appearances, use semantic colors | 🟡 |
| Using `AnyView` type erasure | Replace with `@ViewBuilder` or concrete types — hurts accessibility tree diffing | 🟢 |
| Missing `.accessibilityInputLabels()` | Add alternative spoken phrases for Voice Control | 🟢 |

---

## UIKit Bridging

When working with UIKit views or `UIViewRepresentable` wrappers, the same principles apply but through UIKit's accessibility API:

```swift
// UIView accessibility essentials
view.accessibilityLabel = "Add to cart"
view.accessibilityTraits = .button
view.accessibilityValue = "3 items"
view.isAccessibilityElement = true

// Focus management in UIKit
UIAccessibility.post(notification: .screenChanged, argument: targetView)
UIAccessibility.post(notification: .layoutChanged, argument: targetView)

// Grouping in UIKit
containerView.isAccessibilityElement = false
containerView.accessibilityElements = [label, button]
```

---

## Testing & Debug Tools

### Xcode & Simulator

- **Accessibility Inspector** (Xcode → Open Developer Tool) — audit labels, traits, contrast, hit areas
- **VoiceOver** on device — real-world screen reader testing (`Settings → Accessibility → VoiceOver`)
- **Voice Control** — test spoken commands match labels (`Settings → Accessibility → Voice Control`)

### In-Code Testing

```swift
// Dynamic Type preview in Xcode Previews
#Preview {
    MyView()
        .environment(\.sizeCategory, .accessibilityExtraExtraExtraLarge)
}
```

```swift
// Reduce motion preview
#Preview {
    MyView()
        .environment(\.accessibilityReduceMotion, true)
}
```

```swift
// Increase contrast preview
#Preview {
    MyView()
        .environment(\.accessibilityContrast, .increased)
}
```

### Snapshot & Automated Testing

- `accessibilitySnapshot` testing with `SnapshotTesting` library
- XCTest accessibility audits: `try app.performAccessibilityAudit()`  (Xcode 15+)

---

## Severity Legend

🔴 **Critical** — Blocks screen reader users or fails basic WCAG 2.1/2.2 Level AA

🟡 **Moderate** — Reduces usability / fails enhanced criteria

🟢 **Minor** — Polish / future-proofs

> **Notes:** Core checklist targets WCAG 2.1 AA + Apple Human Interface Guidelines. Additional checks help reach WCAG AAA or public-sector requirements.
>