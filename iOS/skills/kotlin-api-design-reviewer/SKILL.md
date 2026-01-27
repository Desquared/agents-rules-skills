---
name: kotlin-api-design-reviewer
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

## Common Issues

| Issue | Fix |
|-------|-----|
| `var visible` | `var isVisible` |
| `fun getData()` | `fun getUserName()` |
| `var nameString` | `var name` |
| `userList` | `users` |

## Function Design

- Named parameters for clarity beyond first param
- Default parameters at end
- Suspend functions for async operations
- Extension functions for utility methods
- Operator overloading when semantically appropriate

## Return Types

- Nullable when null is meaningful
- Result<T> for failable operations
- Flow<T> for streams
- sealed class for finite states

## Data Classes

- Use for data transfer objects
- Immutable by default (val over var)
- Copy function for updates
- Destructuring support

## Severity

- 🔴 Critical: Violates conventions
- 🟡 Improvement: Could be clearer
- 🟢 Enhancement: Optional polish
