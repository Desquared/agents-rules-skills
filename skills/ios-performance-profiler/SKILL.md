---

name: ios-performance-profiler

description: Identifies potential performance bottlenecks in SwiftUI code — expensive view body computation, unnecessary redraws, identity instability, concurrency issues, and memory problems. Use for lists, animations, complex layouts, remote data, or when the user asks about performance.

---

# Performance Profiler

### View Identity & Diffing

- [ ]  Stable, persistent identity (`Identifiable` with stable `id`, avoid random UUIDs in rows)
- [ ]  Prefer value types + structural identity over class reference identity when possible
- [ ]  Avoid `.id()` unless absolutely necessary (use stable keys instead)

### Body Computation & Redraws

- [ ]  No heavy work (network, decoding, sorting, filtering) inside `body`
- [ ]  Expensive derived state computed once and stored (`.onAppear`, ViewModel, or memoized computed property)
- [ ]  `@State`, `@Bindable`, `@Observable` properties are narrowly scoped
- [ ]  Child views extracted when parent state changes frequently
- [ ]  Prefer `@Observable` (Observation framework) over `@ObservableObject` / `@Published` for new code — finer-grained invalidation, less boilerplate

### Lists & Large Collections

- [ ]  Use `List` / `LazyVStack` / `LazyHGrid` instead of `ScrollView + ForEach + VStack`
- [ ]  Rows are lightweight; avoid `@StateObject` / `@ObservedObject` inside row bodies
- [ ]  Use stable `Identifiable` conformance or explicit `id:` key path

### Concurrency & Main Actor (Swift 6 era)

- [ ]  `@MainActor` used appropriately on view-bound types (avoid overuse → thread hops)
- [ ]  Nonisolated properties / computed vars when safe
- [ ]  Actors used for model / service layers; avoid `@MainActor` on pure data models
- [ ]  No data races when using `@Observable` types across concurrency domains
- [ ]  Prefer `.task` over `.onAppear` for async work (automatic cancellation on view disappear, structured concurrency)

### Environment & State Propagation

- [ ]  Avoid injecting large `@Observable` objects via `@Environment` when only a small slice of state is needed — extract a child view that reads only what it needs
- [ ]  Minimize over-subscription from `EnvironmentObject` — split into focused, smaller observable types if necessary

### Memory & Retain Cycles

- [ ]  `[weak self]` in async / escaping closures that capture `self`
- [ ]  `@StateObject` for view-owned view models (not in `List` rows)
- [ ]  Remote images use proper caching (`AsyncImage` with cache, or `Kingfisher` / `Nuke`)
- [ ]  Downscale images to display size before rendering (`.resizable()` + `.frame()` alone doesn't reduce memory)
- [ ]  Use `preparingThumbnail(of:)` for large images
- [ ]  Avoid long-lived strong references in `@Observable` / `@ObservableObject`

### Layout & Geometry

- [ ]  Minimize `GeometryReader` usage inside scroll views (causes layout thrashing)
- [ ]  Avoid `PreferenceKey` updates that trigger parent re-layouts in tight loops
- [ ]  Use `drawingGroup()` for complex vector paths / heavy Canvas content

### Animations & Transitions

- [ ]  Scope animations narrowly (`.animation(…, value: …)`) instead of global `.animation()`
- [ ]  Use `.matchedGeometryEffect` only when necessary (expensive)
- [ ]  Test with reduced motion enabled

### Navigation & Sheets

- [ ]  Use lazy destination resolution in `NavigationStack` (avoid pre-building destination views)
- [ ]  Ensure `.sheet` / `.fullScreenCover` content isn't computed until presentation
- [ ]  Avoid deep view hierarchies in destinations — flatten or split into smaller views

---

## Quick Wins

| **Issue** | **Fix / Pattern** | **Severity** |
| --- | --- | --- |
| Parent redraws child unnecessarily | Extract stable child view / use `Equatable` conformance | 🟡 |
| Heavy logic in `body` | Move to ViewModel / `.task` / computed property | 🔴 |
| Unstable `ForEach` IDs | Use stable `id: \.self` or persistent model ID | 🔴 |
| Retain cycle | `\[weak self\]` in closures | 🔴 |
| `@StateObject` recreated in List row | Move ownership to parent / use `@Observable` value type | 🔴 |
| Broad / unnecessary animations | `.animation(nil)` or value-specific `.animation(…, value:)` | 🟡 |
| Frequent main-actor hops | Use nonisolated properties, actors for model layer | 🟡 |
| `GeometryReader` in scroll views | Move geometry reads outside scroll or cache values | 🟡 |
| Large images not downscaled | `preparingThumbnail(of:)` or resize before display | 🟡 |
| Over-subscribed `@Environment` | Extract child view reading only needed state | 🟡 |
| Eager sheet / destination init | Lazy destination resolution in `NavigationStack` | 🟡 |
| Still using `@ObservableObject` | Migrate to `@Observable` (Observation framework) | 🟢 |
| Complex vector / Canvas paths | Use `drawingGroup()` for off-screen rendering | 🟢 |

---

## Debug Helpers (Xcode 16+)

```swift
// In any View body — logs when/why view redraws
let _ = Self._printChanges()
```

```swift
// os_signpost for custom performance intervals
import os.signpost

let log = OSLog(subsystem: "com.app.performance", category: "ViewLoading")
let signpostID = OSSignpostID(log: log)
os_signpost(.begin, log: log, name: "LoadData", signpostID: signpostID)
// ... work ...
os_signpost(.end, log: log, name: "LoadData", signpostID: signpostID)
```

### Instruments & Tools

- **SwiftUI template** + **Animation instrument** in Instruments
- **SwiftUI View Body instrument** (Xcode 16+) — tracks body evaluations per view
- **SwiftUI Performance HUD** — `⌥⌘P` in Simulator
- **MetricKit** / `MXMetricManager` — production performance & diagnostic data
- **Memory Graph Debugger** — catch retain cycles and leaks at runtime

---

## Severity Legend

🔴 **Critical** — Visible lag, dropped frames, memory growth, crashes

🟡 **Moderate** — Noticeable jank on low-end devices or large datasets

🟢 **Minor** — Optimization opportunity / future-proofs for larger scale
