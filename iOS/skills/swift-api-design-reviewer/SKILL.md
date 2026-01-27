---
name: swift-api-design-reviewer
description: Review function and class interfaces for Swift API Design Guidelines compliance. Use when creating public APIs, reusable components, library interfaces, or when the user asks for API design review or Swift naming conventions.
---

# Swift API Design Review

## Naming Rules

- Clear at point of use, not declaration
- Omit needless words
- `lowerCamelCase`: properties, methods
- `UpperCamelCase`: types, protocols
- Booleans: `is`, `has`, `can`, `should`
- Mutating: verb (`sort()`), Non-mutating: noun (`sorted()`)

## Common Issues

| Issue | Fix |
|-------|-----|
| `var visible` | `var isVisible` |
| `func get()` | `func getUserName()` |
| `var nameString` | `var name` |
| `add(item: x, to: y)` | `add(_ item: x, to: y)` |

## Parameter Design

- Closure parameters: last
- Default parameters: at end
- First label: omit when forming phrase with function
- Labels clarify purpose

## Return Types

- Optional when nil is meaningful
- Result for failable operations
- Tuples max 3 elements (else use struct)

## Severity

- 🔴 Critical: Violates guidelines
- 🟡 Improvement: Could be clearer
- 🟢 Enhancement: Optional polish
