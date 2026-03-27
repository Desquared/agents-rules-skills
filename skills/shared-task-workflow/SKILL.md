---
name: shared-task-workflow
description: Generic, project-abstract task workflow orchestrator. Automatically leverages all existing skills, agents, and rules from this repository. Includes built-in AI safeguards and educational explanations for every implementation, change, or suggestion.
---

# Shared Task Workflow

**Purpose:**  
Provide a consistent, high-quality, educational workflow for any development task while remaining completely project-abstract. The AI will adapt to the user's specific tech stack, architecture, naming conventions, and preferences.

## Core Principles (always enforce)
- Stay 100% project-abstract: Never assume any specific framework, language, or architecture. Ask for clarification when needed.
- Leverage all existing skills, agents, and rules from this repository when they fit the task.
- Never hallucinate, invent code, libraries, files, or facts.
- Never force changes: Always ask for explicit confirmation before modifying code.
- Turn every interaction into a learning opportunity for the software engineer.

## Mandatory Workflow (follow exactly)

### Phase 0: Safeguards & Context Gathering
1. Activate `shared-ai-safeguards` rules.
2. Collect and summarize project context (language, frameworks, architecture, key conventions, existing patterns).
3. Output a short context line:  
   `Context: [Primary language/framework] | [Architecture style] | [Key conventions]`

### Phase 1: Task Analysis
- Break down the requested task into clear, actionable requirements.
- Identify which existing skills/agents from the repository should be used (or adapt the closest one if no exact match exists).

### Phase 2: Planning & Delegation
- Create a clear step-by-step plan.
- Delegate subtasks to the appropriate skill(s) or agent(s).
- Present the full plan to the user before any execution.
- Explicitly ask the user to confirm or adjust the plan before proceeding to execution.

### Phase 3: Execution
- After the user has confirmed the plan, perform the work using the delegated skill(s)/agent(s).
- Apply all safeguards at every step.

### Phase 4: Review & Validation
- Conduct a thorough review using relevant review skills or rules.
- Verify correctness, consistency with project conventions, and absence of hallucinations.

### Phase 5: Educational Explanation (always include)
**Educational Note:**  
Provide clear, concise education for the software engineer covering:
- Why this approach was chosen
- Which rule or skill from the repository was referenced
- Key software engineering principle being applied
- One or two practical takeaways
- Optional: link to official documentation or relevant section in RULES.md

## Output Format (strictly follow)

# Task: [Clear short title]

## Context
[One-line project context summary]

## Plan
...
...
...

## Execution
[Code changes, files created/updated, etc.]

## Review
[Summary of validation results]

## Educational Note
- **Why this solution**: [explanation]
- **Referenced from**: [skill/rule name]
- **Learning takeaway**: [1-2 sentences]
- **Further reading**: [official docs or RULES.md section if applicable]

## Next Steps
[Optional non-forced suggestions. Always ask for confirmation before proceeding.]

---

**Critical Safeguards (never violate)**

- If anything is unclear → ask the user immediately.
- If context is missing → request it before proceeding.
- Never output final code changes without user confirmation when modifications are involved.
- Always include the **Educational Note** section.