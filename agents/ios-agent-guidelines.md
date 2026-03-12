# iOS Agent Guidelines

**CRITICAL: All iOS agents MUST follow these discovery rules**

## 1. Discovery-First Approach

Before making ANY implementation decisions, agents must:

### Check Project Dependencies
```bash
# Scan Package.swift (SPM)
grep -A 5 "dependencies:" Package.swift
grep -A 10 ".package" Package.swift

# Scan Podfile (CocoaPods)
grep "pod" Podfile

# Scan project files for patterns
find . -name "*.swift" | head -20
```

### Scan Project Structure
```bash
# Find existing patterns
find . -name "*ViewModel*.swift" -o -name "*Coordinator*.swift" -o -name "*Service*.swift"
find . -name "*.xcdatamodeld" -o -name "*PersistenceController*.swift"
grep -r "@Model" --include="*.swift" .
grep -r "NSPersistentContainer" --include="*.swift" .
```

## 2. Library Detection Matrix

| Category | Check For | Common Patterns |
|----------|-----------|-----------------|
| **DI** | Swinject, Resolver, Factory, manual | `Container`, `@Injected`, manual init injection |
| **Persistence** | SwiftData, Core Data, Realm, GRDB | `@Model`, `NSManagedObject`, `Realm`, `Database` |
| **HTTP** | Alamofire, Moya, URLSession | `AF.request`, `provider.request`, `URLSession.shared` |
| **Navigation** | NavigationStack, Coordinator | `NavigationStack`, `Coordinator protocol` |
| **State** | @Observable, ObservableObject, TCA | `@Observable`, `ObservableObject`, `Store<State, Action>` |
| **Testing** | XCTest, Quick/Nimble, Mockingbird | `XCTestCase`, `describe/it`, `mock()` |

## 3. When to Ask User

**ALWAYS ask when:**
- Multiple libraries detected for same purpose
- No clear pattern found in existing code
- User requests feature using unfamiliar framework
- Conflicting approaches in same codebase (e.g., SwiftData + Core Data migration)

**Example:**
```
"I detected both Core Data and SwiftData models. Are you migrating to SwiftData, or should I use Core Data for consistency?"
```

## 4. Library-Agnostic Patterns

When possible, provide abstract patterns that work with any library:

### DI (works with Swinject, Resolver, Factory, or manual)
```swift
// Protocol-based dependency
protocol UserRepository {
    func fetchUser() async throws -> User
}

// ViewModel with constructor injection (works with any DI)
@Observable
class UserViewModel {
    private let repository: UserRepository
    init(repository: UserRepository) {
        self.repository = repository
    }
}
```

### Persistence (works with SwiftData, Core Data, Realm)
```swift
// Repository pattern abstracts persistence details
protocol UserRepository {
    func getUsers() async -> [User]
    func insert(user: User) async throws
}
```

## 5. iOS Version & Feature Detection

Check deployment target to suggest appropriate APIs:
- iOS 17+: SwiftData, @Observable, Observable macro
- iOS 16+: NavigationStack, Layout protocol
- iOS 15+: async/await, .task modifier
- iOS 13+: Combine, ObservableObject, @Published

```bash
# Check deployment target
grep "IPHONEOS_DEPLOYMENT_TARGET" *.xcodeproj/project.pbxproj
```

## 6. Example Discovery Flow

```
1. User asks: "Add user profile fetching"
2. Agent scans: Package.swift for HTTP library
3. Agent finds: .package(url: "Alamofire/Alamofire", ...)
4. Agent scans: existing network code patterns
5. Agent finds: APIClient protocol with Alamofire implementation
6. Agent proposes: "I'll extend APIClient with async/await wrapper for profile endpoint."
7. Agent asks: "Use existing APIClient pattern with Alamofire?"
8. Only after confirmation → implement
```

## 7. Error Messages Should Teach

When encountering unknown setup:
❌ Bad: "Using Swinject for DI"
✅ Good: "Detected manual dependency injection via initializers. Following this pattern."

## 8. Token Efficiency

Discovery should be fast:
- Use grep/glob tools (not explore agent) for known patterns
- Cache findings in conversation context
- Don't re-scan on every response
- Check 1-2 files to infer pattern, don't read entire codebase

## 9. Common iOS Project Patterns

### Architecture Detection
- **MVVM**: Find `*ViewModel.swift`, views with `@StateObject`
- **MVI**: Find `*Intent.swift`, `*State.swift`, unidirectional flow
- **TCA**: Find `import ComposableArchitecture`, `Store<State, Action>`
- **VIPER**: Find `*Presenter.swift`, `*Interactor.swift`, `*Router.swift`
- **Coordinator**: Find `*Coordinator.swift` with navigation logic

### Persistence Detection
```swift
// SwiftData
@Model class User { }
let container = ModelContainer(for: User.self)

// Core Data
class User: NSManagedObject { }
NSPersistentContainer(name: "Model")

// Realm
class User: Object { }
let realm = try! Realm()
```

### DI Detection
```swift
// Swinject
Container().register(UserRepository.self) { _ in ... }

// Resolver
@Injected var repository: UserRepository

// Factory
Container.shared.userRepository.register { UserRepositoryImpl() }

// Manual
init(repository: UserRepository) { self.repository = repository }
```

## 10. Platform Considerations

For iOS projects:
- Check for watchOS, macOS, tvOS targets (multi-platform)
- SwiftUI vs UIKit detection: grep for `UIViewController` vs `View` conformance
- Check for Objective-C bridging header (mixed Swift/ObjC)

## 11. Project-Specific Notes

Document project-specific choices here after discovery:
- **Architecture**: [TBD - discover on first use]
- **DI**: [TBD - discover on first use]
- **Persistence**: [TBD - discover on first use]
- **Networking**: [TBD - discover on first use]

**Always verify before assuming** - even documented choices may have exceptions.
