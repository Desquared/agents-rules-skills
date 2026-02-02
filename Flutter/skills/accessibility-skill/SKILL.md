---
name: accessibility-skill
description: Flutter accessibility quick reference. Semantics, WCAG compliance, screen readers.
---

# Accessibility Skill

## Semantic Labels
```dart
// Icons & buttons
Semantics(label: 'Settings', button: true, child: Icon(Icons.settings))

// Images (or ExcludeSemantics if decorative)
Semantics(label: 'Profile picture', image: true, child: Image.network(url))

// Interactive (prefer InkWell over GestureDetector)
Semantics(button: true, label: 'Open', child: InkWell(...))
```

## Dynamic Announcements
```dart
import 'package:flutter/semantics.dart';
SemanticsService.announce('Button unlocked!', TextDirection.ltr);
```

## Touch Targets & Forms
```dart
// Minimum 48x48 dp
IconButton(iconSize: 48, ...)

// Forms need labels
TextField(decoration: InputDecoration(
  labelText: 'Email',
  errorText: isValid ? null : 'Invalid',
))
```

## Color Contrast
```dart
// Use ColorPalette (WCAG-compliant)
Text('Text', style: TextStyle(
  color: ColorPalette.coloursBasicText.platformBrightnessColor(context),
))
```

## Focus Management
```dart
FocusTraversalGroup(
  policy: OrderedTraversalPolicy(),
  child: Column(children: [
    FocusTraversalOrder(order: NumericFocusOrder(1), child: TextField(...)),
    FocusTraversalOrder(order: NumericFocusOrder(2), child: Button(...)),
  ]),
)
```

## Testing
```dart
// Automated
await expectLater(tester, meetsGuideline(textContrastGuideline));
await expectLater(tester, meetsGuideline(androidTapTargetGuideline));
```

## Checklist
- [ ] Semantic labels on interactive elements
- [ ] Touch targets ≥ 48x48 dp
- [ ] Contrast ≥ 4.5:1 (text), 3:1 (UI)
- [ ] Forms have labels + errors
- [ ] Dynamic changes announced
- [ ] Logical focus order
- [ ] Tested with VoiceOver + TalkBack
