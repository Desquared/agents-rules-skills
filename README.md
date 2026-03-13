# Agents, Rules & Skills for Mobile Development

Ship better mobile code with AI out of the box.

This repository gives AI assistants specialized mobile expertise for Android, iOS, and Flutter through three building blocks:

- Skills: reusable capabilities (architecture, testing, performance, accessibility, security).
- Rules: coding guardrails and standards.
- Agents: role-based specialists for focused tasks.

## Start Here

If you only do one thing, install skills first:

```bash
npx skills add https://github.com/Desquared/agents-rules-skills --skill shared-bug-investigation
```

Then pick your path:

| I want to... | Go to |
|---|---|
| Install agents interactively | `npx github:Desquared/agents-rules-skills install-agents` |
| Install rules interactively | `npx github:Desquared/agents-rules-skills install-rules` |
| Browse the full skills catalog | [SKILLS.md](SKILLS.md) |
| Browse rules & manual install | [RULES.md](RULES.md) |
| Browse agents & manual install | [AGENTS.md](AGENTS.md) |

## Installation Model

### Rules & Agents — Interactive Installer

Dedicated CLI commands for each type:

```bash
# Agents only
npx github:Desquared/agents-rules-skills install-agents

# Rules only
npx github:Desquared/agents-rules-skills install-rules
```

The installer walks you through:
1. **Which IDE(s)** — Cursor, GitHub Copilot, Claude Code, Windsurf, or all
3. **Scope** — project (current directory) or global (all projects)
4. **Platform** — Flutter, iOS, Android, shared
5. **Which items** — pick from a list or keep all

Each tool gets files in the right location automatically (e.g., `.cursor/rules/` for Cursor, `.claude/agents/` for Claude Code, `.github/instructions/` for Copilot).

- Full rules catalog: [RULES.md](RULES.md)
- Full agents catalog: [AGENTS.md](AGENTS.md)

### Skills

Skills follow the Agent Skills standard and can be installed directly with `npx skills add`.

- Full catalog + install-all commands: [SKILLS.md](SKILLS.md)

### Manual examples (if you prefer curl)

```bash
# Rule -> Cursor project rules
mkdir -p .cursor/rules
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \
  -o .cursor/rules/ios-code-review.md

# Rule -> GitHub Copilot (append to project instructions)
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \
  >> .github/copilot-instructions.md

# Rule -> Claude Code (append to CLAUDE.md)
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \
  >> CLAUDE.md

# Rule -> Windsurf (append to .windsurfrules)
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \
  >> .windsurfrules

# Agent -> Claude Code (global, all projects)
mkdir -p ~/.claude/agents
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/agents/ios-security-reviewer.md \
  -o ~/.claude/agents/ios-security-reviewer.md
```

## How Rules & Agents Work After Install

### Rules — no invocation needed

Rules are passive. Once installed, your AI tool picks them up automatically:

| IDE | How |
|---|---|
| **GitHub Copilot** | `.instructions.md` files in `.github/instructions/` (project) or `~/Library/Application Support/Code/User/instructions/` (global) are injected into every chat automatically. Restart VS Code after installing. |
| **Cursor** | Files in `.cursor/rules/` are loaded as context for every chat/edit session. |
| **Claude Code** | Rules appended to `CLAUDE.md` are read at the start of every session. |
| **Windsurf** | Rules appended to `.windsurfrules` are injected into every Cascade session. |

Just ask the AI to review, write, or refactor code — the rule's standards are already in context.

### Agents — invoke by name or role

Agents are active. You call them explicitly:

- **Claude Code**: Claude automatically delegates to sub-agents in `~/.claude/agents/` or `.claude/agents/` when the task matches their specialty. You can also ask directly: *"Use the ios-security-reviewer agent to audit this file."*
- **GitHub Copilot / Cursor / Windsurf**: Agents are installed as custom instructions. Reference them by role in your prompt: *"Act as the android-code-reviewer and review this PR."*

### Skills — invoke by name or description

Skills are on-demand capabilities injected into the AI's context when you ask for them. After installing with `npx skills add`, trigger a skill in two ways:

**By slash command** (explicit):
```
/android-accessibility-validator
/shared-bug-investigation
/android-compose-architecture-review
```

**By describing the task** (automatic match):
- *"Review this file for accessibility issues"* → triggers `android-accessibility-validator`
- *"Analyze the architecture of this feature"* → triggers `android-compose-architecture-review`
- *"Investigate this bug systematically"* → triggers `shared-bug-investigation`

See [SKILLS.md](SKILLS.md) for the full catalog.

## Why Teams Use This Repo

- Faster onboarding for AI-assisted workflows.
- Consistent output quality across projects and developers.
- Practical, battle-tested mobile guidance instead of generic prompts.

## Repository Structure

Canonical install targets are top-level directories:

```text
skills/
rules/
agents/
```

Platform is encoded in filenames (`ios-`, `android-`, `flutter-`) where relevant.

## Keeping Docs Updated

This repository includes an exporter that regenerates all docs from current files.

```bash
npm run docs:export
```

## References

- https://agentskills.io/specification
- https://github.com/agentskills/agentskills
- https://cursor.com/blog/agent-best-practices
- https://www.builder.io/blog/agent-skills-rules-commands
- https://developers.openai.com/codex/skills/
