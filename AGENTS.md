# Agents Catalog

Agents are manual-install artifacts (no universal cross-tool install standard yet).

## How To Install An Agent

1. Pick an agent file from the catalog below.
2. Copy it into your tool's agents folder.

Example (Claude global agents):

```bash
mkdir -p ~/.claude/agents
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/agents/ios-security-reviewer.md \
  -o ~/.claude/agents/ios-security-reviewer.md
```

## Agent Catalog

### iOS

- [ios-accessibility-specialist.md](agents/ios-accessibility-specialist.md): accessibility audits and fixes.
- [ios-build-error-resolver.md](agents/ios-build-error-resolver.md): Xcode/Swift build error triage.
- [ios-concurrency-networking-agent.md](agents/ios-concurrency-networking-agent.md): Swift concurrency and networking.
- [ios-coredata-swiftdata-specialist.md](agents/ios-coredata-swiftdata-specialist.md): persistence, migration, sync.
- [ios-performance-optimizer.md](agents/ios-performance-optimizer.md): performance profiling and optimization.
- [ios-project-architecture-analyst.md](agents/ios-project-architecture-analyst.md): architecture planning.
- [ios-security-reviewer.md](agents/ios-security-reviewer.md): security review and hardening.
- [ios-swiftui-layout-specialist.md](agents/ios-swiftui-layout-specialist.md): complex SwiftUI layout work.
- [ios-testing-qa-agent.md](agents/ios-testing-qa-agent.md): testing strategy and implementation.
- [ios-ux-ui-design-agent.md](agents/ios-ux-ui-design-agent.md): UX/design-system alignment.
- [ios-agent-guidelines.md](agents/ios-agent-guidelines.md): iOS orchestration and usage guidelines.

### Android

- [android-accessibility-specialist-android.md](agents/android-accessibility-specialist-android.md): accessibility audits and fixes.
- [android-build-error-resolver-android.md](agents/android-build-error-resolver-android.md): Gradle/Kotlin build error triage.
- [android-compose-layout-specialist-android.md](agents/android-compose-layout-specialist-android.md): complex Compose layout work.
- [android-concurrency-networking-agent-android.md](agents/android-concurrency-networking-agent-android.md): coroutines/Flow/networking.
- [android-performance-optimizer-android.md](agents/android-performance-optimizer-android.md): performance profiling and optimization.
- [android-persistence-specialist-android.md](agents/android-persistence-specialist-android.md): persistence and data modeling.
- [android-project-architecture-analyst-android.md](agents/android-project-architecture-analyst-android.md): architecture planning.
- [android-security-reviewer-android.md](agents/android-security-reviewer-android.md): security review and hardening.
- [android-testing-qa-agent-android.md](agents/android-testing-qa-agent-android.md): testing strategy and implementation.
- [android-ux-ui-design-agent-android.md](agents/android-ux-ui-design-agent-android.md): UX/design-system alignment.
- [android-agent-guidelines.md](agents/android-agent-guidelines.md): Android orchestration and usage guidelines.

### Flutter

- [flutter-accessibility-specialist-flutter.md](agents/flutter-accessibility-specialist-flutter.md): accessibility audits and fixes.
- [flutter-bug-solver-flutter.md](agents/flutter-bug-solver-flutter.md): structured bug investigation.
- [flutter-code-reviewer-flutter.md](agents/flutter-code-reviewer-flutter.md): code review and merge readiness.
- [flutter-feature-analyst-flutter.md](agents/flutter-feature-analyst-flutter.md): feature feasibility and planning.
- [flutter-performance-optimizer-flutter.md](agents/flutter-performance-optimizer-flutter.md): performance profiling and optimization.
- [flutter-project-architecture-analyst-flutter.md](agents/flutter-project-architecture-analyst-flutter.md): architecture planning.
- [flutter-refactoring-specialist-flutter.md](agents/flutter-refactoring-specialist-flutter.md): safe refactoring and migration.
- [flutter-agent-guidelines.md](agents/flutter-agent-guidelines.md): Flutter orchestration and usage guidelines.
