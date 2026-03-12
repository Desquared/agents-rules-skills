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
| Install and browse all skills | [SKILLS.md](SKILLS.md) |
| Browse and manually install rules | [RULES.md](RULES.md) |
| Browse and manually install agents | [AGENTS.md](AGENTS.md) |

## Installation Model

### Skills

Skills follow the Agent Skills standard and can be installed directly with `npx skills add`.

- Full catalog + install-all commands: [SKILLS.md](SKILLS.md)

### Rules and Agents

Rules and agents do not have one universal cross-tool CLI standard yet, so installation is manual.

- Rules manual install guide: [RULES.md](RULES.md)
- Agents manual install guide: [AGENTS.md](AGENTS.md)

Quick manual examples:

```bash
# Rule -> Cursor project rules
mkdir -p .cursor/rules
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \
  -o .cursor/rules/ios-code-review.md

# Agent -> Claude local agents
mkdir -p ~/.claude/agents
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/agents/ios-security-reviewer.md \
  -o ~/.claude/agents/ios-security-reviewer.md
```

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

## References

- https://agentskills.io/specification
- https://github.com/agentskills/agentskills
- https://cursor.com/blog/agent-best-practices
- https://www.builder.io/blog/agent-skills-rules-commands
- https://developers.openai.com/codex/skills/
