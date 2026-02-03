---
name: optimization-skill
description: Performance optimization patterns. Reduce jank, prevent leaks.
---

# Optimization Skill

## Quick Wins

```dart
// 1. const: const Text('Title') vs Text('Title')

// 2. Narrow BlocBuilder: Scaffold(body: BlocBuilder(...)) 
//    vs BlocBuilder(builder: (ctx, state) => Scaffold(...))

// 3. buildWhen: buildWhen: (prev, curr) => prev.data != curr.data

// 4. ListView.builder vs ListView(children: [])

// 5. Keys: key: ValueKey(items[i].id)

// 6. Batch API:
// ❌ for (item in items) await api.fetch(item.id);
// ✅ await Future.wait(items.map((i) => api.fetch(i.id)));
// ✅✅ await api.fetchBatch(ids);

// 7. Debounce:
Timer? _debounce;
void onSearchChanged(String q) {
  _debounce?.cancel();
  _debounce = Timer(Duration(milliseconds: 300), () => _search(q));
}

// 8. Heavy work: compute(_heavy, data) in Bloc, NOT in build()

// 9. Images: CachedNetworkImage with width/height

// 10. Memory leaks:
@override Future<void> close() { _controller.close(); return super.close(); }
@override void dispose() { _timer?.cancel(); super.dispose(); }
```

## Common Issues
- BlocBuilder wraps entire screen → Narrow scope
- Missing const → Add const
- No buildWhen → Filter rebuilds
- ListView without .builder → Use .builder
- Sequential API calls → Batch or parallel
- Heavy computation in build() → Move to Bloc/isolate
- Streams/timers not closed → Dispose properly

## Diagnostics
```bash
flutter run --profile --trace-skia
# DevTools → Memory, Performance
```
