---
name: feature-implementation-skill
description: Implementation checklist. Coding standards, DI, testing.
---

# Feature Implementation Skill

## Checklist

### Code Structure
- [ ] Files in data/domain/view (DTOs → data, Models → domain)
- [ ] @JsonSerializable on DTOs, Equatable on Models
- [ ] Repository interface in domain, implementation in data
- [ ] Bloc (complex) or Cubit (simple) in view

### Design System
- [ ] ColorPalette (no hardcoded colors)
- [ ] Spacing (no magic numbers)
- [ ] Project's Design System Text Widgets (typography)
- [ ] Project's Design System Assets (images/icons)
- [ ] Core widgets (`lib/core/widgets/`)

### State Management
- [ ] Bloc for complex (multiple events), Cubit for simple
- [ ] @injectable on classes (auto-registration)
- [ ] BlocProvider at page level
- [ ] BlocBuilder scoped to minimal subtree

### Error Handling
- [ ] Try-catch in repositories
- [ ] Map DioException → custom exceptions (NetworkException, NotFoundException)
- [ ] Error states in Bloc/Cubit
- [ ] User-friendly messages

### Testing
- [ ] Unit tests for Bloc/Cubit (90%+ target)
- [ ] Mock with mocktail
- [ ] Test structure mirrors source
- [ ] Use fixtures for mock data

### Quality
- [ ] `flutter analyze` passes (zero errors)
- [ ] Run `./build_runner.sh` after changes
- [ ] Commit *.g.dart files

## Patterns

```dart
// ✅ Clear naming
class FeatureRepository {}

// ✅ Null safety
final name = user?.name ?? 'Guest';

// ✅ const
const Text('Static');

// ✅ Async/await
try {
  final data = await repository.fetch();
  emit(LoadedState(data));
} catch (e) {
  emit(ErrorState(e.toString()));
}

// ✅ Bloc test
blocTest<MyBloc, MyState>(
  'emits [Loading, Loaded]',
  build: () {
    when(() => repository.fetch()).thenAnswer((_) async => data);
    return bloc;
  },
  act: (bloc) => bloc.add(LoadRequested()),
  expect: () => [LoadingState(), LoadedState(data)],
);
```

## Resources
- Extensions → `lib/core/utils/extensions/`
- Helpers → `lib/core/utils/helpers/`
- Validators → `lib/core/utils/validators/`
