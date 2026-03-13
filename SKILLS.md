# Skills Catalog

A focused, install-first page for all skills in this repository.

Naming convention:

- `android-*` for Android skills
- `ios-*` for iOS skills
- `flutter-*` for Flutter skills
- `shared-*` for cross-platform skills

Quick navigation:

- [Cross-platform](#cross-platform)
- [Flutter](#flutter)
- [iOS](#ios)
- [Android](#android)

## Quick Start

### How To Run These Commands

1. Open a terminal (`zsh`, `bash`, or VS Code terminal).
2. Paste one of the command blocks below exactly as shown.
3. Press Enter once to run it.

Notes:

- You can run these commands from any folder.
- The `skills` CLI may ask whether to install globally or for the current project.
- If `npx` is missing, install Node.js first.

Install one skill:

```bash
npx skills add https://github.com/Desquared/agents-rules-skills --skill <skill-name>
```

Install all skills (non-interactive, no repeated prompts):

```bash
npx skills add https://github.com/Desquared/agents-rules-skills --all
```

Install all skills globally (user-level):

```bash
npx skills add https://github.com/Desquared/agents-rules-skills --all --global
```

If you prefer explicit flags instead of `--all`:

```bash
npx skills add https://github.com/Desquared/agents-rules-skills --skill "*" --agent "*" --yes
```

Install by platform:

```bash
# Flutter
for skill in flutter-accessibility-validator flutter-bug-investigation flutter-code-review flutter-design-architecture flutter-feature-analysis flutter-feature-implementation flutter-performance-optimizer; do
  npx skills add https://github.com/Desquared/agents-rules-skills --skill "$skill"
done

# iOS
for skill in ios-accessibility-validator ios-performance-profiler ios-swift-api-design-reviewer ios-swiftui-architecture-review; do
  npx skills add https://github.com/Desquared/agents-rules-skills --skill "$skill"
done

# Android
for skill in android-accessibility-validator android-compose-architecture-review android-kotlin-api-design-reviewer android-performance-profiler; do
  npx skills add https://github.com/Desquared/agents-rules-skills --skill "$skill"
done
```

## Full Catalog

### Cross-platform

| Skill | Description | Install | Source |
|---|---|---|---|
| shared-bug-investigation | Scientific method expert for systematic bug investigation and root cause analysis. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill shared-bug-investigation` | [SKILL.md](skills/shared-bug-investigation/SKILL.md) |

### Flutter

| Skill | Description | Install | Source |
|---|---|---|---|
| flutter-accessibility-validator | Use when creating or modifying Flutter UI components, screens, or widgets, or when the user asks about accessibility, screen reader support, VoiceOver, TalkBack, touch targets, color contrast, or WCAG compliance in a Flutter app. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill flutter-accessibility-validator` | [SKILL.md](skills/flutter-accessibility-validator/SKILL.md) |
| flutter-bug-investigation | Use when investigating bugs, crashes, or unexpected behavior in a Flutter app. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill flutter-bug-investigation` | [SKILL.md](skills/flutter-bug-investigation/SKILL.md) |
| flutter-code-review | Use when reviewing Flutter code for correctness, architecture compliance, design system usage, linting, or testing coverage. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill flutter-code-review` | [SKILL.md](skills/flutter-code-review/SKILL.md) |
| flutter-design-architecture | Use when scaffolding a new Flutter feature, setting up the data/domain/view folder structure, creating datasources, DTOs, repositories, or state management classes, or when the user asks how to organize a Flutter project. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill flutter-design-architecture` | [SKILL.md](skills/flutter-design-architecture/SKILL.md) |
| flutter-feature-analysis | Use when assessing feasibility or complexity of a new Flutter feature before implementation. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill flutter-feature-analysis` | [SKILL.md](skills/flutter-feature-analysis/SKILL.md) |
| flutter-feature-implementation | Use when implementing a Flutter feature from scratch or completing one in progress. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill flutter-feature-implementation` | [SKILL.md](skills/flutter-feature-implementation/SKILL.md) |
| flutter-performance-optimizer | Use when Flutter UI has jank, slow scrolling, excessive rebuilds, or memory leaks. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill flutter-performance-optimizer` | [SKILL.md](skills/flutter-performance-optimizer/SKILL.md) |

### iOS

| Skill | Description | Install | Source |
|---|---|---|---|
| ios-accessibility-validator | Checks and suggests accessibility improvements for SwiftUI and UIKit code including VoiceOver labels, dynamic type support, and color contrast. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill ios-accessibility-validator` | [SKILL.md](skills/ios-accessibility-validator/SKILL.md) |
| ios-performance-profiler | Identifies potential performance bottlenecks in SwiftUI code including expensive view updates, unnecessary redraws, and memory issues. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill ios-performance-profiler` | [SKILL.md](skills/ios-performance-profiler/SKILL.md) |
| ios-swift-api-design-reviewer | Review function and class interfaces for Swift API Design Guidelines compliance. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill ios-swift-api-design-reviewer` | [SKILL.md](skills/ios-swift-api-design-reviewer/SKILL.md) |
| ios-swiftui-architecture-review | Analyze SwiftUI view hierarchies and suggest MVVM or other architectural improvements. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill ios-swiftui-architecture-review` | [SKILL.md](skills/ios-swiftui-architecture-review/SKILL.md) |
| swiftui-agent *(by twostraws)* | Use when building SwiftUI views, composing layouts, managing state, or applying modern SwiftUI patterns across iOS, macOS, watchOS, and tvOS apps. | `npx skills add https://github.com/twostraws/SwiftUI-Agent-Skill` | [SKILL.md](https://github.com/twostraws/SwiftUI-Agent-Skill/blob/main/SKILL.md) |

### Android

| Skill | Description | Install | Source |
|---|---|---|---|
| android-accessibility-validator | Checks and suggests accessibility improvements for Jetpack Compose and Android Views including TalkBack labels, dynamic text support, and color contrast. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill android-accessibility-validator` | [SKILL.md](skills/android-accessibility-validator/SKILL.md) |
| android-compose-architecture-review | Analyze Jetpack Compose UI hierarchies and suggest MVVM/MVI or other architectural improvements. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill android-compose-architecture-review` | [SKILL.md](skills/android-compose-architecture-review/SKILL.md) |
| android-kotlin-api-design-reviewer | Review function and class interfaces for Kotlin Coding Conventions compliance. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill android-kotlin-api-design-reviewer` | [SKILL.md](skills/android-kotlin-api-design-reviewer/SKILL.md) |
| android-performance-profiler | Identifies potential performance bottlenecks in Jetpack Compose code including expensive recompositions, unnecessary redraws, and memory issues. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill android-performance-profiler` | [SKILL.md](skills/android-performance-profiler/SKILL.md) |
