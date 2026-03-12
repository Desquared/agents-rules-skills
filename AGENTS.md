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

### Flutter

- [flutter-accessibility-specialist.md](agents/flutter-accessibility-specialist.md): Accessibility specialist for Flutter apps.
- [flutter-agent-guidelines.md](agents/flutter-agent-guidelines.md): Flutter Agent Guidelines.
- [flutter-bug-solver.md](agents/flutter-bug-solver.md): Scientific method expert for systematic bug investigation.
- [flutter-code-reviewer.md](agents/flutter-code-reviewer.md): Code review specialist for Flutter features.
- [flutter-feature-analyst.md](agents/flutter-feature-analyst.md): Feature analysis specialist.
- [flutter-performance-optimizer.md](agents/flutter-performance-optimizer.md): Flutter performance specialist for profiling, optimization, and efficiency.
- [flutter-project-architecture-analyst.md](agents/flutter-project-architecture-analyst.md): Architecture planner for new features.
- [flutter-refactoring-specialist.md](agents/flutter-refactoring-specialist.md): Specialized agent for migrating legacy code to data/domain/view structure.

### iOS

- [ios-accessibility-specialist.md](agents/ios-accessibility-specialist.md): iOS accessibility expert for VoiceOver, Dynamic Type, color contrast, and assistive technologies.
- [ios-agent-guidelines.md](agents/ios-agent-guidelines.md): iOS Agent Guidelines.
- [ios-build-error-resolver.md](agents/ios-build-error-resolver.md): Xcode build error specialist.
- [ios-concurrency-networking-agent.md](agents/ios-concurrency-networking-agent.md): Expert in Swift concurrency and networking.
- [ios-coredata-swiftdata-specialist.md](agents/ios-coredata-swiftdata-specialist.md): Core Data / SwiftData specialist.
- [ios-performance-optimizer.md](agents/ios-performance-optimizer.md): iOS performance specialist for profiling, optimization, and efficiency.
- [ios-project-architecture-analyst.md](agents/ios-project-architecture-analyst.md): Architecture analyst for planning new features and core changes.
- [ios-security-reviewer.md](agents/ios-security-reviewer.md): iOS security specialist for code auditing.
- [ios-swiftui-layout-specialist.md](agents/ios-swiftui-layout-specialist.md): Expert SwiftUI layout specialist for complex layouts, custom view modifiers, geometry calculations, and responsive design.
- [ios-testing-qa-agent.md](agents/ios-testing-qa-agent.md): Expert testing and quality assurance specialist for XCTest, UI testing, mocking strategies, test-driven development, and code coverage.
- [ios-ux-ui-design-agent.md](agents/ios-ux-ui-design-agent.md): UX/UI design specialist focused on user experience, design system compliance, user flow optimization, and modern iOS design patterns.

### Android

- [android-accessibility-specialist.md](agents/android-accessibility-specialist.md): Android accessibility expert for TalkBack, Dynamic Text, color contrast, and assistive technologies.
- [android-agent-guidelines.md](agents/android-agent-guidelines.md): Android Agent Guidelines.
- [android-build-error-resolver.md](agents/android-build-error-resolver.md): Android/Gradle build error specialist.
- [android-compose-layout-specialist.md](agents/android-compose-layout-specialist.md): Expert Jetpack Compose layout specialist for complex layouts, custom modifiers, measurement/layout, and responsive design.
- [android-concurrency-networking-agent.md](agents/android-concurrency-networking-agent.md): Expert in Kotlin coroutines and networking.
- [android-performance-optimizer.md](agents/android-performance-optimizer.md): Android performance specialist for profiling, optimization, and efficiency.
- [android-persistence-specialist.md](agents/android-persistence-specialist.md): Room / Realm / DataStore specialist.
- [android-project-architecture-analyst.md](agents/android-project-architecture-analyst.md): Architecture analyst for planning new features and core changes.
- [android-security-reviewer.md](agents/android-security-reviewer.md): Android security specialist for code auditing.
- [android-testing-qa-agent.md](agents/android-testing-qa-agent.md): Expert testing and quality assurance specialist for JUnit, Espresso, Compose UI testing, MockK, test-driven development, and code coverage.
- [android-ux-ui-design-agent.md](agents/android-ux-ui-design-agent.md): UX/UI design specialist focused on user experience, Material Design compliance, user flow optimization, and modern Android design patterns.
