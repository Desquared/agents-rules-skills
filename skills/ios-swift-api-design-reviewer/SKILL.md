---

name: ios-swift-api-design-reviewer

description: Reviews Swift function, type, and protocol interfaces for alignment with the Swift API Design Guidelines, with emphasis on call-site clarity, naming, parameter labeling, mutability conventions, and modern async/throws patterns. Use for public APIs, reusable components, SDKs, and shared modules.

---

# Swift API Design Review

## What “good” looks like

- Prioritize **clarity at the call site**, even if the declaration becomes a bit longer.
- Follow established **standard library conventions** so your API “feels Swift”.
- Prefer **precise words** over generic ones (`data`, `info`, `manager`, `handler`, `util`).

## Naming Rules (Types, Properties, Methods)

- **Clear at point of use**, not at declaration.
- **Omit needless words**, but do not remove words that carry meaning.
- **lowerCamelCase**: functions, methods, properties, enum cases.
- **UpperCamelCase**: types, protocols.
- Avoid “noise” suffixes like `String`, `Array`, `Data` unless they add meaning.

### Boolean naming

- Prefer predicates: `is`, `has`, `can`, `should`, `did`.
- Match the natural reading of the call site.
    - ✅ `if user.isEligible { … }`
    - ✅ `if cache.hasValue(forKey: k) { … }`

### Verbs and mutability

- **Mutating** methods are verbs: `sort()`, `append(_)`, `removeAll()`.
- **Non-mutating** counterparts use “-ed”: `sorted()`, `appending(_)`, `removingAll()` when appropriate.

## Common Issues (with better fixes)

| Issue | Better Fix |
| --- | --- |
| `var visible` | `var isVisible` |
| `func get()` | Use a precise verb or a noun that reads well at the call site: `func userName()`, `func loadUserName()`, `func fetchUserName()` |
| `var nameString` | `var name` |
| `func add(item: x, to: y)` | `func add(_ item: Item, to collection: Collection)` |
| `func doStuff()` | Name the domain action: `func refresh()`, `func rebuildIndex()`, `func startSession()` |
| `func handle(_ x: …)` | Be specific: `func handleDeepLink(_:)` or `func route(_:)` |

## Parameter & Label Design (call-site first)

- The **first argument label** should be omitted when it forms a natural phrase:
    - ✅ `add(_ item:to:)`
    - ✅ `contains(_:)`
- Use labels to clarify roles, units, and semantics:
    - ✅ `move(from:to:)`
    - ✅ `setDeadline(_:, for:)`
    - ✅ `resize(to:)` (size), `resize(by:)` (scale factor)
- Keep **default parameters last** when it improves scanning and discoverability.
- Closure parameters are usually **last**, but:
    - If there are **multiple closures**, label them clearly.
    - If a closure is *configuration* rather than *action*, it may be clearer earlier.

### Preferred label vocabulary (common Swift patterns)

- `in:` container or scope
- `from:` source
- `to:` destination
- `at:` position or index
- `with:` accompanying value
- `using:` algorithm/tool dependency
- `for:` beneficiary or target entity
- `by:` delta, factor, or means

## Return Types & Error Handling

- Use **optional** only when `nil` is a meaningful “no value” state.
- Use **throws** for failures that should be handled via `do/catch`.
- Use **Result** when:
    - You need to store/transport outcomes,
    - You are bridging callback-based APIs,
    - You want explicit success/failure as a value.
- Prefer **structs** over tuples when:
    - More than 2–3 fields,
    - Fields need names that matter,
    - The value is passed around broadly.

## Async / Concurrency (modern Swift)

- Prefer `async`/`await` over completion handlers for new APIs.
- Avoid `Async` suffixes unless required for disambiguation.
    - ✅ `func refresh() async throws`
    - ✅ `func loadImage() async -> Image`
- Cancellation:
    - Prefer being cancellation-cooperative rather than inventing custom cancel APIs.
    - Consider how the API behaves when the task is cancelled (does it throw `CancellationError`?).
- Actor isolation:
    - Avoid marking pure data models `@MainActor`.
    - Keep UI-bound types `@MainActor` when they are truly view-facing.

## Type & Protocol Naming

- Protocols should name *capabilities*:
    - ✅ `Cache`, `ImageLoading`, `Persisting`
- Types should name *what they are*:
    - ✅ `ImageCache`, `URLSessionImageLoader`, `KeychainStore`
- Avoid vague names:
    - 🚫 `Manager`, `Helper`, `Util`, `Common`, `Base` (unless truly established in the domain)

## Quick Review Checklist

- [ ]  Does every API read clearly **at the call site**?
- [ ]  Are names consistent with **stdlib conventions** (mutating vs non-mutating)?
- [ ]  Are parameter labels meaningful (roles, units, direction: `from/to/at/with`)?
- [ ]  Are “get”, “do”, “handle”, “data/info” avoided unless truly accurate?
- [ ]  Are return types chosen intentionally (optional vs throws vs Result)?
- [ ]  Do async/throws APIs follow modern Swift patterns without “Async” noise?

## Severity

- 🔴 **Critical**: Violates guidelines or likely to cause misuse/confusion.
- 🟡 **Improvement**: Usable, but naming/labels could be clearer and more Swift-like.
- 🟢 **Enhancement**: Polish that improves consistency and ergonomics.

## Output format (recommended)

- **Summary**: 2–5 bullets of highest-impact issues.
- **Findings**: Grouped by Naming, Parameters, Return Types, Concurrency.
- Each issue labeled with 🔴🟡🟢 and includes a **before → after** suggestion.