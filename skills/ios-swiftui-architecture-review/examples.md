# Architecture Examples

## Anti-Pattern → Fix

### Business Logic in View
```swift
// ❌ Bad
struct ProductView: View {
    @State var products: [Product] = []
    var body: some View {
        List(products) { Text($0.name) }
            .task {
                let (data, _) = try await URLSession.shared.data(from: url)
                products = try JSONDecoder().decode([Product].self, from: data)
            }
    }
}

// ✅ Good
@Observable class ProductViewModel {
    var products: [Product] = []
    func load() async { products = try? await service.fetch() }
}

struct ProductView: View {
    @State var vm = ProductViewModel()
    var body: some View {
        List(vm.products) { Text($0.name) }
            .task { await vm.load() }
    }
}
```

### Singleton Coupling
```swift
// ❌ Bad
List(ProductService.shared.getAll())

// ✅ Good
struct ProductView: View {
    let service: ProductService
    var body: some View { List(service.getAll()) }
}
```

## State Management Examples

### @StateObject vs @ObservedObject
```swift
// ❌ Bad - @StateObject in List row recreates on scroll
struct ProductRow: View {
    @StateObject var vm: ProductRowViewModel
    var body: some View { Text(vm.displayName) }
}

// ✅ Good - Parent owns ViewModels
struct ProductList: View {
    @State var viewModels: [ProductRowViewModel]
    
    var body: some View {
        List(viewModels) { vm in
            ProductRow(viewModel: vm)  // @ObservedObject
        }
    }
}
```

### @Bindable (iOS 17+)
```swift
// ✅ Two-way binding with @Observable
@Observable class Settings {
    var isEnabled: Bool = false
}

struct SettingsView: View {
    @State var settings = Settings()
    var body: some View {
        Toggle("Enable", isOn: Bindable(settings).isEnabled)
    }
}
```

### Environment Best Practices
```swift
// ❌ Bad - Mutating @Environment
struct ContentView: View {
    @Environment(\.theme) var theme
    var body: some View {
        Button("Change") { theme = .dark }  // Won't work
    }
}

// ✅ Good - Mutation in @Observable model
@Observable class ThemeManager {
    var theme: Theme = .light
    func toggle() { theme = theme == .light ? .dark : .light }
}

struct ContentView: View {
    @Environment(ThemeManager.self) var manager
    var body: some View {
        Button("Change") { manager.toggle() }
    }
}
```

### Memory Leak Prevention
```swift
// ❌ Bad - Retained cycle
@Observable class ViewModel {
    var onComplete: (() -> Void)?
    
    func setup() {
        onComplete = { self.cleanup() }  // Captures self
    }
}

// ✅ Good - Weak capture
@Observable class ViewModel {
    var onComplete: (() -> Void)?
    
    func setup() {
        onComplete = { [weak self] in self?.cleanup() }
    }
}
```
