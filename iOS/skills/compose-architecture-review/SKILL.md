---
name: compose-architecture-review
description: Analyze Jetpack Compose UI hierarchies and suggest MVVM/MVI or other architectural improvements. Use when **reviewing existing Compose code**, creating new Compose components, analyzing composable structure, or when the user asks about Compose architecture patterns. Best for code review and refactoring guidance.
---

# Compose Architecture Review

> **When to Use This Skill:**
> - Reviewing EXISTING Compose screens/ViewModels
> - Code review for architectural violations
> - Refactoring guidance for specific files
> - Validating state management patterns
>
> **When to Use `project-architecture-analyst-android` Agent Instead:**
> - Planning NEW features (uncertain file placement)
> - Understanding project-wide architecture
> - Major changes affecting multiple modules
> - Need to discover existing patterns before implementing

## Review Checklist

### Separation of Concerns
- [ ] Composables: presentation only (no business logic, network, DB)
- [ ] ViewModels: state + business logic (StateFlow, events)
- [ ] Repository/UseCase: data access separated from UI

### State Management
- `remember` - composable-local state only
- `rememberSaveable` - survive config changes
- `ViewModel` with `StateFlow/MutableStateFlow` - screen state
- `collectAsStateWithLifecycle()` - lifecycle-aware collection
- `derivedStateOf` - computed state from other state
- CompositionLocal - shared read-only state

#### State Lifecycle Issues
- [ ] No leaked ViewModels (use viewModel() or hiltViewModel())
- [ ] Proper cleanup in ViewModel.onCleared()
- [ ] StateFlow collected with lifecycle awareness
- [ ] No unnecessary state hoisting

### Dependency Injection
- [ ] Dependencies injected (check project: Koin, Hilt, Dagger, or manual)
- [ ] No hardcoded singletons in composables
- [ ] ViewModels receive dependencies via constructor

### Composable Design
- [ ] Composables are small (single responsibility)
- [ ] Complex screens broken into components
- [ ] Reusable components extracted
- [ ] Stateless composables preferred (state hoisting)

## Common Issues

| Issue | Fix |
|-------|-----|
| Business logic in composable | Extract to ViewModel |
| Network call in composable | Move to ViewModel with LaunchedEffect |
| Singleton in composable | Inject via DI or CompositionLocal |
| 200+ line composable | Break into smaller components |
| State not hoisted | Hoist to parent or ViewModel |

## State Hoisting Pattern

Composable receives: `value: T, onValueChange: (T) -> Unit`
ViewModel holds: `MutableStateFlow<T>`

## Severity

- 🔴 Critical: Architecture violation
- 🟡 Improvement: Better pattern available
- 🟢 Enhancement: Optional optimization
