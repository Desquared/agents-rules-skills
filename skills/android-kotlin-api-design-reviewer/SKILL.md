---
name: android-kotlin-api-design-reviewer
description: Review function and class interfaces for Kotlin Coding Conventions compliance. Use when creating public APIs, reusable components, library interfaces, or when the user asks for API design review or Kotlin naming conventions.
---

# Kotlin API Design Review

## Naming Rules

- Clear at point of use
- Omit needless words
- `lowerCamelCase`: properties, functions, variables
- `UpperCamelCase`: classes, interfaces, objects
- `UPPER_SNAKE_CASE`: constants
- Booleans: `is`, `has`, `can`, `should` prefix
- Collections: plural names (`users`, not `userList`)

## Compose-specific Naming
- Composable functions returning `Unit`: use PascalCase nouns (e.g., `UserProfileCard`, not `userProfile`)
- Avoid spaces and backticked identifiers in function names, especially in public APIs (discouraged by Kotlin conventions)

## Common Issues

| Issue                          | Fix |
|--------------------------------|-----|
| `var visible`                  | `var isVisible` |
| `fun getData()`                | `fun getUserName()` |
| `var nameString`               | `var name` |
| `userList`                     | `users` |
| Composable named like regular function | Rename to PascalCase noun (`ProfileScreen`) |
| Implicit return type in library | Add explicit `: String` |

## Function Design

- Named parameters for clarity beyond first param
- Default parameters at end
- Suspend functions for async operations
- Extension functions for utility methods
- Operator overloading when semantically appropriate
- Prefer properties over parameterless functions when the operation is cheap and non-throwing
- Use named arguments + trailing commas for multi-parameter calls in public APIs

## Return Types

- Nullable when null is meaningful
- Result<T> for failable operations
- Flow<T> for streams
- sealed class for finite states
- Explicitly declare return types in public library APIs (even if inferable)

## Data Classes

- Use for data transfer objects
- Immutable by default (val over var)
- Copy function for updates
- Destructuring support

## Public API / Library

- Provide KDoc for all public members
- Use `@JvmName` for interop-friendly naming
- Use backing properties (`_internalValue`) for observed state
- Consider type aliases for repeated complex function types

## Severity

- 🔴 Critical: Violates conventions
- 🟡 Improvement: Could be clearer
- 🟢 Enhancement: Optional polish