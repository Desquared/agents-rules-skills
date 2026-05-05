# RADD Composable Preference

## Rule

When writing Compose UI code, **always prefer RADD library composables over native Jetpack Compose equivalents**.

RADD composables use the `RADD` prefix. Known mappings:

| Native Compose | RADD Equivalent |
|----------------|-----------------|
| `Box` | `RADDBox` |
| `Row` | `RADDRow` |
| `Column` | `RADDColumn` |
| `Text` | `RADDText` |
| `Button` | `RADDButton` |
| `Image` | `RADDImage` |

## Before Using Any Composable

1. Check if a RADD-prefixed version exists (e.g., `RADDImage`, `RADDButton`, etc.)
2. If it exists, use it instead of the native one
3. Import from `com.desquared.radd.containers` (for layout) or the appropriate RADD package

## Why

RADD composables integrate with the project's design system, supporting tokens for colors, spacing, corner radius, and theming out of the box.

