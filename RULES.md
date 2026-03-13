# Rules Catalog

Rules are plain Markdown files. Every major AI coding tool supports them — the only difference is where each tool looks for them.

## Quick Install

The interactive installer handles all IDE paths for you:

```bash
npx github:Desquared/agents-rules-skills install-rules
```

Pick your IDE(s), choose a scope (project or global), filter by platform, and the installer places them in the right location automatically.

## Manual Install

Pick a rule file, then copy it to your tool's expected location.

### Cursor

Cursor loads rules from `.cursor/rules/` (project-level) or `~/.cursor/rules/` (global).

```bash
# Project-level
mkdir -p .cursor/rules
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \
  -o .cursor/rules/ios-code-review.md

# Global (applies to all projects)
mkdir -p ~/.cursor/rules
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \
  -o ~/.cursor/rules/ios-code-review.md
```

### GitHub Copilot (VS Code)

Copilot reads a single `.github/copilot-instructions.md` file (project-level) or individual `.instructions.md` files from `.github/instructions/`.

```bash
# Append a rule to the project instructions file
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \
  >> .github/copilot-instructions.md

# Or as a standalone instruction file (VS Code 1.96+)
mkdir -p .github/instructions
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \
  -o .github/instructions/ios-code-review.instructions.md
```

### Claude Code

Claude Code reads `CLAUDE.md` at the project root (project-level) or `~/.claude/CLAUDE.md` (global).

```bash
# Append a rule to the project CLAUDE.md
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \
  >> CLAUDE.md

# Global (applies to all projects)
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \
  >> ~/.claude/CLAUDE.md
```

### Windsurf

Windsurf reads `.windsurfrules` (project-level) or `~/.codeium/windsurf/memories/global_rules.md` (global).

```bash
# Append a rule to the project rules file
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \
  >> .windsurfrules

# Global
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \
  >> ~/.codeium/windsurf/memories/global_rules.md
```

## Rule Catalog

### Flutter

- [flutter-accessibility.md](rules/flutter-accessibility.md): Accessibility (A11Y).
- [flutter-architecture.md](rules/flutter-architecture.md): Application Architecture.
- [flutter-core-principles.md](rules/flutter-core-principles.md): Core Flutter Principles.
- [flutter-dart-best-practices.md](rules/flutter-dart-best-practices.md): Dart Best Practices.
- [flutter-data-serialization.md](rules/flutter-data-serialization.md): Data Handling & Serialization.
- [flutter-flutter-best-practices.md](rules/flutter-flutter-best-practices.md): Flutter Best Practices.
- [flutter-layout-ui.md](rules/flutter-layout-ui.md): Layout & UI Components.
- [flutter-lint-rules.md](rules/flutter-lint-rules.md): Linting & Analysis Rules.
- [flutter-testing.md](rules/flutter-testing.md): Testing.

### iOS

- [ios-agent-orchestration.md](rules/ios-agent-orchestration.md): Agent Orchestration.
- [ios-ask.md](rules/ios-ask.md): Third-Party Libraries.
- [ios-code-review.md](rules/ios-code-review.md): Code Review Checklist.
- [ios-modern-swift.md](rules/ios-modern-swift.md): Modern Swift & iOS.
- [ios-project-arch.md](rules/ios-project-arch.md): Project Architecture.
- [ios-security-practices.md](rules/ios-security-practices.md): Security Best Practices.
- [ios-swift-style.md](rules/ios-swift-style.md): Swift Style Guide Compliance.
- [ios-ux-design-system.md](rules/ios-ux-design-system.md): UX & Design System.
- [ios-validation-testing.md](rules/ios-validation-testing.md): Validation & Testing.

### Android

- [android-agent-orchestration.md](rules/android-agent-orchestration.md): Agent Orchestration.
- [android-ask.md](rules/android-ask.md): Third-Party Libraries.
- [android-code-review.md](rules/android-code-review.md): Code Review Checklist.
- [android-project-arch.md](rules/android-project-arch.md): Project Architecture.
- [android-security-practices.md](rules/android-security-practices.md): Security Best Practices.
- [android-style.md](rules/android-style.md): Android Style Guide.
- [android-ux-design-system.md](rules/android-ux-design-system.md): UX & Design System.
- [android-validation-testing.md](rules/android-validation-testing.md): Validation & Testing.
