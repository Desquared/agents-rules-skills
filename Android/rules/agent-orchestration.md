# Agent Orchestration

## Auto-Invoke When

| Task | Agents |
|------|--------|
| New feature | architecture → (ux-ui, networking, layout, data) → (test, security, a11y) |
| UI screen/changes | ux-ui, layout, a11y, performance |
| API/networking | architecture, networking, testing |
| Data model | data-specialist, testing, security |
| Build failure | build-error-resolver |
| Performance issues | performance-optimizer |
| Pre-release | ALL agents |

## CRITICAL: Parallel Execution

Launch independent agents in ONE message with multiple Task calls.

## Agents That Wait for Approval
- project-architecture-analyst
- ux-ui-design-agent
