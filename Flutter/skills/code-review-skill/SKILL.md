---
name: code-review-skill
description: Code review checklist. 7 criteria with severity levels.
---

# Code Review Skill

## 7 Criteria

### 1. Functionality (🔴 Blocker)
- Logic errors, missing error handling, null safety violations

### 2. Readability (🟠 Major)
- Clear naming, proper comments, no dead code

### 3. Optimization (🟠 Major)
- Unnecessary rebuilds, N+1 queries, missing const

### 4. Architecture (🟠 Major)
- data/domain/view pattern, repository pattern, @injectable DI

### 5. Design System (🟠 Major)
- ColorPalette, Spacing, Project Design System Widgets (no hardcoded)

### 6. Linting (🔴 Blocker)
- `flutter analyze` must pass (zero errors)

### 7. Testing (Optional)
- 90%+ unit, 50%+ widget coverage (suggest, don't block)

## Quick Checks

```dart
// ❌ Hardcoded color
Text('Hi', style: TextStyle(color: Colors.red))

// ✅ Use ColorPalette
Text('Hi', style: TextStyle(
  color: ColorPalette.coloursBasicText.platformBrightnessColor(context),
))

// ❌ Business logic in widget
final total = items.fold(0, (s, i) => s + i.price);

// ✅ Logic in Bloc/Cubit
@injectable class MyBloc { ... }

// ❌ No error handling
Future<Data> fetch() async {
  return await api.call();
}

// ✅ Try-catch
Future<Data> fetch() async {
  try {
    return await api.call();
  } catch (e) {
    throw NetworkException(e.toString());
  }
}
```

## Severity
- **🔴 Blocker**: Must fix (functionality, security, linting)
- **🟠 Major**: Should fix (performance, architecture, design system)
- **🟢 Minor**: Nice to fix (naming, comments)

## Process
1. `flutter analyze` (must pass)
2. Check file structure (data/domain/view)
3. Verify design system usage
4. Check state management (@injectable)
5. Verify error handling
6. Check tests (suggest improvements)
