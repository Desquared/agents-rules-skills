# SwiftUI Architecture Patterns

## MVVM with @Observable (iOS 17+)

```swift
@Observable
class UserListViewModel {
    var users: [User] = []
    var isLoading = false
    private let service: UserService
    
    init(service: UserService) { self.service = service }
    
    func load() async {
        isLoading = true
        users = try? await service.fetch()
        isLoading = false
    }
}

struct UserListView: View {
    @State private var vm: UserListViewModel
    
    init(service: UserService) {
        _vm = State(initialValue: UserListViewModel(service: service))
    }
    
    var body: some View {
        List(vm.users) { Text($0.name) }
            .task { await vm.load() }
    }
}
```

## State Patterns

| Pattern | Use Case | Lifecycle |
|---------|----------|-----------|
| @State | View-local, temporary | Dies with view |
| @StateObject | View-owned ViewModel | Survives view updates |
| @ObservedObject | Passed-in ViewModel | Managed by parent |
| @Bindable | Two-way binding (iOS 17+) | Reference only |
| @Environment | Shared app state | Injected from ancestor |
| @EnvironmentObject | Injected dependencies | Must exist or crash |
| @Binding | Parent-child communication | Reference to parent state |

### Common State Issues
```swift
// ❌ @StateObject in List row (recreates on scroll)
List(items) { item in
    RowView(item: item)  // RowView has @StateObject inside
}

// ✅ Pass @ObservedObject or use @State
List(items) { item in
    RowView(viewModel: viewModel(for: item))  // Parent owns it
}
```

## Architecture Patterns

### 1. MVVM (Most Common)
```
View → ViewModel → UseCase → Repository → API/DB
```
**When**: Standard iOS apps, moderate complexity

### 2. TCA (The Composable Architecture)
```swift
struct FeatureReducer: Reducer {
    struct State { var count = 0 }
    enum Action { case increment, decrement }
    
    var body: some ReducerOf<Self> {
        Reduce { state, action in
            switch action {
            case .increment: state.count += 1; return .none
            case .decrement: state.count -= 1; return .none
            }
        }
    }
}
```
**When**: Complex state machines, time-travel debugging needed

### 3. Redux-like (Single Store)
```swift
class AppStore: ObservableObject {
    @Published var state: AppState
    func dispatch(_ action: Action) { /* reducer */ }
}

struct ContentView: View {
    @EnvironmentObject var store: AppStore
    var body: some View { Text("\(store.state.count)") }
}
```
**When**: Global state, predictable updates, undo/redo

### 4. MVI (Model-View-Intent)
```swift
@Observable
class FeatureViewModel {
    var state: ViewState = .idle
    
    func handle(_ intent: Intent) async {
        // Intent → State transformation
    }
}
```
**When**: Reactive streams, unidirectional flow

### 5. Clean Architecture (VIPER-inspired)
```
View → Presenter/ViewModel → Interactor/UseCase → Repository → DataSource
```
**When**: Large teams, strict boundaries, high testability
