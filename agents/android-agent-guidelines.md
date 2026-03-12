# Android Agent Guidelines

**CRITICAL: All Android agents MUST follow these discovery rules**

## 1. Discovery-First Approach

Before making ANY implementation decisions, agents must:

### Check Project Dependencies
```bash
# Scan build.gradle files
grep -r "implementation.*koin" build.gradle* app/build.gradle
grep -r "implementation.*hilt" build.gradle* app/build.gradle
grep -r "implementation.*room" build.gradle* app/build.gradle
grep -r "implementation.*realm" build.gradle* app/build.gradle
grep -r "implementation.*ktor" build.gradle* app/build.gradle
grep -r "implementation.*retrofit" build.gradle* app/build.gradle
```

### Scan Project Structure
```bash
# Find existing patterns
find . -name "*ViewModel*.kt" -o -name "*Repository*.kt" -o -name "*UseCase*.kt"
find . -name "*Module*.kt" | xargs grep -l "module\|inject"
```

## 2. Library Detection Matrix

| Category | Check For | Common Patterns |
|----------|-----------|-----------------|
| **DI** | koin-*, hilt-*, dagger-* | `module {`, `@HiltViewModel`, `@Inject` |
| **Persistence** | room-*, realm-*, sqldelight-*, datastore-* | `@Database`, `@Entity`, `Realm.write` |
| **HTTP** | ktor-*, retrofit2-*, okhttp-* | `HttpClient`, `Retrofit`, `suspend fun` |
| **Image Loading** | coil-*, glide-* | `AsyncImage`, `ImageLoader` |
| **Testing** | mockk-*, mockito-* | `mockk<>`, `verify`, `every` |

## 3. When to Ask User

**ALWAYS ask when:**
- Multiple libraries detected for same purpose
- No clear pattern found in existing code
- User requests feature using unfamiliar library
- Conflicting approaches in same codebase

**Example:**
```
"I detected both Koin and manual DI in this project. Which approach should I use for this new feature?"
```

## 4. Library-Agnostic Patterns

When possible, provide abstract patterns that work with any library:

### DI (works with Koin, Hilt, or manual)
```kotlin
// Interface-based dependency
interface UserRepository { suspend fun getUser(): User }

// ViewModel with constructor injection (works with any DI)
class UserViewModel(private val repository: UserRepository) : ViewModel()
```

### Persistence (works with Room, Realm, SQLDelight)
```kotlin
// Repository pattern abstracts DB details
interface UserRepository {
    suspend fun getUsers(): List<User>
    suspend fun insertUser(user: User)
}
```

## 5. Project-Specific Configuration

For this WhatsUp project specifically:
- **DI**: Koin (confirmed by user)
- **Persistence**: Room (confirmed by user)
- **KMP**: Yes (kmmsharedmodule exists)
- **Compose**: Yes (androidx.compose.ui:1.7.3)

**But still VERIFY before using** - project may have mixed patterns or migrations in progress.

## 6. Example Discovery Flow

```
1. User asks: "Add a new feature with networking"
2. Agent scans: build.gradle for HTTP client
3. Agent finds: ktor-client-android:2.x.x
4. Agent scans: existing network code patterns
5. Agent proposes: "I see you're using Ktor. I'll create ApiService using KtorClient with Koin injection."
6. Agent asks: "Does this align with your architecture?"
7. Only after confirmation → implement
```

## 7. Error Messages Should Teach

When encountering unknown setup:
❌ Bad: "Using Hilt for DI"
✅ Good: "Detected Koin DI framework from build.gradle. Using Koin modules for injection."

## 8. Token Efficiency

Discovery should be fast:
- Use grep/glob tools (not explore agent) for known patterns
- Cache findings in conversation context
- Don't re-scan on every response
