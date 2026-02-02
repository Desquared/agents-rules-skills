---
name: bug-solving-skill
description: Flutter-specific bug patterns. Extends bug-investigation skill.
---

# Bug Solving Skill (Flutter)

> **Foundation**: Extends [bug-investigation](../../../skills/bug-investigation/SKILL.md) with Flutter patterns.

## Flutter Context
```bash
flutter --version && grep "sdk:" pubspec.yaml
grep -r "BlocProvider\|ChangeNotifier\|GetX" lib/  # State management
```

## RCA: Check All Layers (data/domain/view)

```bash
# View Layer: Bloc/Cubit, widgets, state
find lib/feature/<feature>/view -name "*.dart"

# Domain Layer: Models, repository interfaces, business logic
find lib/feature/<feature>/domain -name "*.dart"

# Data Layer: DTOs, datasources, API calls
find lib/feature/<feature>/data -name "*.dart"
```

**Debug by layer:** View (Bloc emits?) → Domain (model mapping?) → Data (DTO parsing?)

## Common Bug Patterns

### Bloc State
```dart
// ❌ Missing emit → ✅ Add emit(LoadingState()), emit(LoadedState(data)), emit(ErrorState())
```

### Null Safety
```dart
// ❌ user!.name → ✅ user?.name ?? 'Guest'
```

### BuildContext After Async
```dart
// ❌ Navigator.pop(context) after await → ✅ if (!mounted) return; Navigator.pop(context);
```

### GetIt DI
```dart
// ❌ Not registered → ✅ Add @injectable, run build_runner
```

### Method Channels
```dart
// ❌ Wrong name → ✅ Match iOS/Android channel name
```

### Widget Rebuilds
```dart
// ❌ BlocBuilder wraps Scaffold → ✅ Wrap only body
```

### Memory Leaks
```dart
// ❌ Stream not closed → ✅ Override close(), call super.close()
```

## Error Quick Reference

| Error | Fix |
|-------|-----|
| `type 'Null' is not a subtype` | Add `??` defaults |
| `Looking up deactivated widget` | Check `mounted` |
| `Object not registered` | Add `@injectable`, run build_runner |
| `MissingPluginException` | Implement native iOS/Android |
| `setState() after dispose()` | Cancel timers/streams |
| `RenderBox overflow` | Fix constraints |

## Tools
```bash
flutter analyze              # Must pass (0 errors)
flutter test                 # All tests
flutter run --profile        # Performance
```

## Validation
- [ ] `flutter analyze` = 0 errors
- [ ] `flutter test` passes
- [ ] Manual test fixed
- [ ] Tested iOS + Android (if platform-specific)
