# Agents, Rules & Skills for Mobile Development

A standardized, tool-agnostic framework for autonomous mobile development. By separating logic into **Agents**, **Rules**, and **Skills**, AI models can execute complex tasks with minimal token consumption and maximum consistency.

## What’s inside

### Agents
Autonomous assistants that can execute multi-step workflows (for example, generating code, refactoring, running checks, and preparing PR-ready output) with limited supervision.

### Rules
Guardrails and standards that keep outputs consistent and safe (coding conventions, architecture constraints, security checks, team practices).

### Skills
Reusable, modular capabilities that agents can call to perform specific tasks (testing, deployment steps, database queries, scaffolding, and other repeatable workflows).

> **Tip:** Keep instructions clear and concise. Models already know how to write code. These files should encode your project-specific guardrails.

---

## Supported tools

This structure is known to work well with:
- **Claude**
- **Cursor**
- **Copilot CLI**

It can also be adapted for **VS Code**, following the same structure and access patterns.

---

## Directory structure

We aim to keep everything as library-agnostic as possible. Tweak as needed.

- **Android**
	- Kotlin
	- Compose Multiplatform
	- KMP-specific logic
- **iOS**
	- Swift
	- SwiftUI
	- Xcode optimization

---

## Installation

To integrate these into your environment (Mac/Linux):

1. Locate your AI tool configuration folder:
	- Cursor: `~/.cursor`
	- Claude: `~/.claude`
	- Copilot CLI: (your local Copilot config directory)

2. Copy the relevant `.md` files into the appropriate folders.

3. Keep instructions short and focused on your project constraints.

**Mac Finder tip:** Press `command + shift + .` to show or hide hidden folders.

---

## Links

### Agents
- Android: https://github.com/Desquared/agents-rules-skills/tree/main/Android/agents
- iOS: https://github.com/Desquared/agents-rules-skills/tree/main/iOS/agents

### Skills
- Android: https://github.com/Desquared/agents-rules-skills/tree/main/Android/skills
- iOS: https://github.com/Desquared/agents-rules-skills/tree/main/iOS/skills

### Rules
Rules are currently shared patterns for mobile platforms and live inside each platform folder.

---

## References

- https://agentskills.io/specification
- https://cursor.com/blog/agent-best-practices
- https://www.builder.io/blog/agent-skills-rules-commands
- https://opencode.ai/docs/rules/
- https://dev.to/onlyoneaman/building-agent-skills-from-scratch-lbl
- https://developers.openai.com/codex/skills/
- https://github.com/agentskills/agentskills
