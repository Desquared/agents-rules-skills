---
name: performance-profiler-android
description: Identifies potential performance bottlenecks in Jetpack Compose code including expensive recompositions, unnecessary redraws, and memory issues. Use when code involves lists, animations, complex UI, or when the user asks about performance optimization.
---

# Performance Profiler (Android)

## Checklist

### Recompositions
- [ ] Composables only recompose when state changes
- [ ] State properly scoped with `remember`
- [ ] Stable item keys in LazyColumn

### Lists
- [ ] Use `LazyColumn` not `Column + verticalScroll` for large data
- [ ] Stable keys via `key()` parameter
- [ ] Lightweight item composables

### Memory
- [ ] No memory leaks (avoid Activity/View refs)
- [ ] Use Coil/Glide for image loading with caching
- [ ] Proper lifecycle management

### Body Computation
- [ ] No heavy work in composable body
- [ ] Use `remember` for expensive calculations
- [ ] Use `derivedStateOf` for derived state

## Quick Wins

| Issue | Fix |
|-------|-----|
| Parent recomposes | Extract stable child composables |
| Expensive body | Use `remember` or `derivedStateOf` |
| Unstable IDs | Use stable keys in LazyColumn |
| Memory leak | Avoid storing Activity/View references |
| Broad recomposition | Narrow state scope |

## Debug

Use Layout Inspector → Recomposition Counts to visualize recompositions

## Severity

- 🔴 Critical: Visible lag, leaks
- 🟡 Moderate: Noticeable impact
- 🟢 Minor: Optimization opportunity
