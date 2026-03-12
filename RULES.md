# Rules Catalog

Rules are manual-install artifacts (no universal cross-tool install standard yet).

## How To Install A Rule

1. Pick a rule file from the catalog below.
2. Copy it into your tool's rules folder.

Example (Cursor project rules):

```bash
mkdir -p .cursor/rules
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \
  -o .cursor/rules/ios-code-review.md
```

## Rule Catalog

### iOS

- [ios-agent-orchestration.md](rules/ios-agent-orchestration.md): route tasks between specialized agents.
- [ios-ask.md](rules/ios-ask.md): third-party dependency selection guidance.
- [ios-code-review.md](rules/ios-code-review.md): practical review checklist.
- [ios-modern-swift.md](rules/ios-modern-swift.md): modern Swift patterns and practices.
- [ios-project-arch.md](rules/ios-project-arch.md): architecture conventions.
- [ios-security-practices.md](rules/ios-security-practices.md): security requirements.
- [ios-swift-style.md](rules/ios-swift-style.md): style and naming consistency.
- [ios-ux-design-system.md](rules/ios-ux-design-system.md): UX and design-system consistency.
- [ios-validation-testing.md](rules/ios-validation-testing.md): test and validation expectations.

### Android

- [android-agent-orchestration.md](rules/android-agent-orchestration.md): route tasks between specialized agents.
- [android-ask.md](rules/android-ask.md): third-party dependency selection guidance.
- [android-code-review.md](rules/android-code-review.md): practical review checklist.
- [android-project-arch.md](rules/android-project-arch.md): architecture conventions.
- [android-security-practices.md](rules/android-security-practices.md): security requirements.
- [android-ux-design-system.md](rules/android-ux-design-system.md): UX and design-system consistency.
- [android-validation-testing.md](rules/android-validation-testing.md): test and validation expectations.

### Flutter

- [flutter-accessibility.md](rules/flutter-accessibility.md): accessibility standards.
- [flutter-architecture.md](rules/flutter-architecture.md): application architecture guidance.
- [flutter-core-principles.md](rules/flutter-core-principles.md): core engineering principles.
- [flutter-dart-best-practices.md](rules/flutter-dart-best-practices.md): Dart-specific coding guidance.
- [flutter-data-serialization.md](rules/flutter-data-serialization.md): data handling and serialization.
- [flutter-flutter-best-practices.md](rules/flutter-flutter-best-practices.md): Flutter framework best practices.
- [flutter-layout-ui.md](rules/flutter-layout-ui.md): layout and UI guidelines.
- [flutter-lint-rules.md](rules/flutter-lint-rules.md): linting and analyzer policy.
- [flutter-testing.md](rules/flutter-testing.md): testing strategy and expectations.
