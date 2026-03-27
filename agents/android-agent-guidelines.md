# Android Agent Discovery & Implementation Guidelines

> **CRITICAL:** All Android agents MUST execute a discovery scan before making ANY implementation decisions. No assumptions. No guessing. Align to what already exists.

---

## 1. Discovery-First Approach

Before writing code or proposing solutions, run a **"Project Fingerprint"** scan.

### Phase A — Dependency Scan

Scan version catalogs and build files to identify the core tech stack:

```

# Preferred: check version catalog first

grep -rE "koin|hilt|dagger|room|realm|sqldelight|ktor|retrofit|coil|glide|compose|rxjava|coroutines" gradle/libs.versions.toml

# Fallback: scan build files directly

grep -rE "koin|hilt|dagger|room|realm|sqldelight|ktor|retrofit|coil|glide|compose|rxjava|coroutines" ./build.gradle*

```

### Phase B — Architecture & Pattern Fingerprinting

Identify whether the project is standard Android or KMP, then find existing patterns:

```

# KMP vs Standard Android

find . -maxdepth 3 -name "shared" -o -name "commonMain"

# Architectural patterns

find . -name "*ViewModel*.kt" -o -name "*Repository*.kt" -o -name "*UseCase*.kt" | head -n 5

# DI modules / components

find . -name "*Module*.kt" -o -name "*Component*.kt" | xargs grep -lE "module|inject|provides"

```

### Phase C — Announce What You Found

After scanning, **state your findings to the user before writing any code**:

> *"I've detected this is a KMP project using Compose UI and Koin for DI. I will follow these patterns for the new feature."*

Then wait for confirmation before proceeding.

---

## 2. Library Detection Matrix

| Category | Check For | Implementation Strategy |
|---|---|---|
| **DI** | `hilt-android`, `koin-android`, `dagger` | Use `@HiltViewModel`, `by inject()`, or `@Inject` — match what is already in the codebase |
| **UI** | `androidx.compose`, `material` | Prioritize Compose. Use XML/ViewBinding **only** if Compose is absent |
| **Async** | `kotlinx-coroutines`, `rxjava` | Use `Flow`/`suspend` for Coroutines; `Observable`/`Single` for RxJava |
| **Network** | `ktor-client`, `retrofit2`, `okhttp` | Use `HttpClient` for Ktor; interface + annotations for Retrofit |
| **Persistence** | `room`, `realm`, `sqldelight`, `datastore` | Use `@Database`/`@Entity` for Room; `Realm.write` for Realm; `Database` for SQLDelight |
| **Image Loading** | `coil`, `glide` | Use `AsyncImage` for Coil; `GlideImage` / `Glide.with` for Glide |
| **Testing** | `mockk`, `mockito` | Use `mockk<>` / `every` for MockK; `mock()` / `verify` for Mockito |
| **KMP** | `commonMain`, `expect`/`actual` | Place core business logic and network layers in `commonMain` |

---

## 3. When to Stop and Ask

**Always pause and ask the user when:**

- **Ambiguity** — Multiple libraries found for the same purpose (e.g. both Koin and Hilt, or Retrofit and Ktor)
- **Missing setup** — No DI or networking library detected. Ask: *"Should I implement this manually or add a library?"*
- **Pattern mismatch** — User requests a pattern (e.g. MVI) that contradicts the existing codebase (e.g. pure MVVM)
- **Version conflicts** — Library versions are significantly outdated or require a migration
- **No clear pattern** — Existing code has mixed or inconsistent approaches

**Example ask:**
> *"I detected both Koin and manual DI in this project. Which approach should I use for this new feature?"*

---

## 4. Library-Agnostic Patterns

When the stack is unknown or mixed, provide **abstract patterns first** — then layer in the library-specific implementation once confirmed.

### Abstraction First (Repository Pattern)

```

// Define the contract — works with any DB or network client

interface UserRepository {

suspend fun getUser(id: String): Result<User>

suspend fun getUsers(): List<User>

suspend fun insertUser(user: User)

}

```

### Constructor Injection (DI-Agnostic ViewModel)

```

// Works with Hilt, Koin, or manual DI

class UserViewModel(

private val repository: UserRepository

) : ViewModel() {

// logic here

}

```

Once the DI framework is confirmed, add the appropriate annotation or module registration.

---

## 5. Example Discovery Flow

1. **User:** *"Add a new data sync feature."*
2. **Agent scans:** `libs.versions.toml` → finds `sqldelight` and `ktor-client`
3. **Agent states:** *"I see you're using SQLDelight for persistence and Ktor for networking."*
4. **Agent asks:** *"I noticed both Coroutines and RxJava are present. Should the new Repository return a `Flow` or an `Observable`?"*
5. **User confirms** preference
6. **Agent implements** using the confirmed stack only

---

## 6. Error & Quality Guardrails

❌ **NEVER** guess the DI framework — if unsure, provide a constructor-injected class and ask where to register the module
❌ **NEVER** introduce a new library without explicit user permission
❌ **NEVER** add dependencies directly to `build.gradle` without first checking `libs.versions.toml`

✅ **ALWAYS** check `libs.versions.toml` before touching any dependency block
✅ **ALWAYS** explain *why* a specific pattern was chosen, referencing the project's existing files
✅ **ALWAYS** match the naming, code style, and structure already in the codebase
✅ **ALWAYS** announce your detected stack before generating bulk code

---

## 7. Error Messages Should Teach

When encountering an unknown or ambiguous setup, explain what was found — don't silently assume.

❌ **Bad:** *"Using Hilt for DI."*
✅ **Good:** *"Detected Koin DI framework from `libs.versions.toml`. I will use Koin modules for injection. Does this align with the plan for this feature?"*

---

## 8. Discovery Efficiency

Discovery should be fast and lightweight:

- Use `grep`/`find` tools (not full file exploration) for known patterns
- Cache findings in conversation context — **do not re-scan on every response**
- Prefer `libs.versions.toml` over scanning individual `build.gradle` files (it is the single source of truth in modern Android projects)
- Limit `find` depth where possible to avoid scanning `build/` and `.gradle/` output directories
