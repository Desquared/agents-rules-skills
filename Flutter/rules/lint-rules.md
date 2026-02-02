# Linting & Analysis Rules

**CRITICAL: All changes MUST align with project's `analysis_options.yaml` and code formatting**

## Discovery & Validation

```bash
cat analysis_options.yaml           # Check project rules
flutter analyze                     # Must show 0 errors
dart format .                       # Check/apply formatting
```

## Core Principles

* **Follow Project Configuration:** Strictly adhere to ALL rules in `analysis_options.yaml`
* **Zero Errors:** `flutter analyze` must pass with 0 errors before committing
* **Code Formatting:** Follow project's formatting rules (line length, spacing, etc.)
* **Respect Severity:** Adhere to error/warning/info levels as configured
* **No Overrides:** No `// ignore:` comments without explicit approval
* **Custom Rules:** Adapt to project's linter package (`flutter_lints`, `very_good_analysis`, custom)

## Workflow

1. Check `analysis_options.yaml` before making changes
2. Write code following project's rules and formatting
3. Run `flutter analyze` - fix all errors
4. Run `dart format .` - ensure consistent formatting
5. After code generation: `dart run build_runner build` then re-analyze

## Handling Warnings

* Never suppress warnings without understanding root cause
* Document exceptions if `// ignore:` is absolutely required
* Prefer fixing code over suppressing warnings
* Ask for approval before disabling any lint rule
