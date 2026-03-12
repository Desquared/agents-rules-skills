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
| shared-bug-investigation | Scientific-method debugging and root-cause analysis across stacks. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill shared-bug-investigation` | [SKILL.md](skills/shared-bug-investigation/SKILL.md) |

### Flutter

| Skill | Description | Install | Source |
|---|---|---|---|
| flutter-accessibility-validator | Flutter accessibility quick reference for semantics and WCAG checks. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill flutter-accessibility-validator` | [SKILL.md](skills/flutter-accessibility-validator/SKILL.md) |
| flutter-bug-investigation | Flutter-specific bug patterns that complement shared bug investigation. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill flutter-bug-investigation` | [SKILL.md](skills/flutter-bug-investigation/SKILL.md) |
| flutter-code-review | Practical Flutter code review checklist with severity framing. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill flutter-code-review` | [SKILL.md](skills/flutter-code-review/SKILL.md) |
| flutter-design-architecture | Data/domain/view architecture and project structure guidance. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill flutter-design-architecture` | [SKILL.md](skills/flutter-design-architecture/SKILL.md) |
| flutter-feature-analysis | Feature feasibility and complexity analysis framework. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill flutter-feature-analysis` | [SKILL.md](skills/flutter-feature-analysis/SKILL.md) |
| flutter-feature-implementation | Feature implementation checklist with standards, DI, and testing. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill flutter-feature-implementation` | [SKILL.md](skills/flutter-feature-implementation/SKILL.md) |
| flutter-performance-optimizer | Performance optimization patterns to reduce jank and leaks. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill flutter-performance-optimizer` | [SKILL.md](skills/flutter-performance-optimizer/SKILL.md) |

### iOS

| Skill | Description | Install | Source |
|---|---|---|---|
| ios-accessibility-validator | SwiftUI/UIKit accessibility checks for VoiceOver, dynamic type, and contrast. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill ios-accessibility-validator` | [SKILL.md](skills/ios-accessibility-validator/SKILL.md) |
| ios-performance-profiler | SwiftUI performance bottleneck analysis and optimization guidance. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill ios-performance-profiler` | [SKILL.md](skills/ios-performance-profiler/SKILL.md) |
| ios-swift-api-design-reviewer | Swift API design guideline review for naming and interfaces. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill ios-swift-api-design-reviewer` | [SKILL.md](skills/ios-swift-api-design-reviewer/SKILL.md) |
| ios-swiftui-architecture-review | SwiftUI architecture review and refactoring guidance. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill ios-swiftui-architecture-review` | [SKILL.md](skills/ios-swiftui-architecture-review/SKILL.md) |

### Android

| Skill | Description | Install | Source |
|---|---|---|---|
| android-accessibility-validator | Compose/View accessibility checks for TalkBack, text scaling, and contrast. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill android-accessibility-validator` | [SKILL.md](skills/android-accessibility-validator/SKILL.md) |
| android-compose-architecture-review | Compose UI architecture analysis and refactoring suggestions. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill android-compose-architecture-review` | [SKILL.md](skills/android-compose-architecture-review/SKILL.md) |
| android-kotlin-api-design-reviewer | Kotlin API design review against conventions. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill android-kotlin-api-design-reviewer` | [SKILL.md](skills/android-kotlin-api-design-reviewer/SKILL.md) |
| android-performance-profiler | Compose performance bottleneck analysis and optimization guidance. | `npx skills add https://github.com/Desquared/agents-rules-skills --skill android-performance-profiler` | [SKILL.md](skills/android-performance-profiler/SKILL.md) |
