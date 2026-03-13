#!/usr/bin/env node

import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const repoRoot = path.join(__dirname, '..');
const REPO_URL = 'https://github.com/Desquared/agents-rules-skills';

const PLATFORM_ORDER = ['shared', 'flutter', 'ios', 'android', 'other'];
const PLATFORM_LABELS = {
  shared: 'Cross-platform',
  flutter: 'Flutter',
  ios: 'iOS',
  android: 'Android',
  other: 'Other',
};

function readText(filePath) {
  return fs.readFileSync(filePath, 'utf8');
}

function writeText(filePath, content) {
  fs.writeFileSync(filePath, content.trimEnd() + '\n', 'utf8');
}

function listDirNames(dirPath) {
  if (!fs.existsSync(dirPath)) return [];
  return fs
    .readdirSync(dirPath)
    .filter((name) => fs.statSync(path.join(dirPath, name)).isDirectory())
    .sort((a, b) => a.localeCompare(b));
}

function listMarkdownFiles(dirPath) {
  if (!fs.existsSync(dirPath)) return [];
  return fs
    .readdirSync(dirPath)
    .filter((name) => name.endsWith('.md') && fs.statSync(path.join(dirPath, name)).isFile())
    .sort((a, b) => a.localeCompare(b));
}

function parseFrontmatter(content) {
  if (!content.startsWith('---\n')) return {};
  const end = content.indexOf('\n---\n', 4);
  if (end === -1) return {};
  const block = content.slice(4, end).split('\n');
  const data = {};
  for (const line of block) {
    const idx = line.indexOf(':');
    if (idx === -1) continue;
    const key = line.slice(0, idx).trim();
    const value = line.slice(idx + 1).trim();
    data[key] = value;
  }
  return data;
}

function firstSentence(text) {
  if (!text) return '';
  const trimmed = text.replace(/\s+/g, ' ').trim();
  const idx = trimmed.search(/[.!?](\s|$)/);
  if (idx === -1) return trimmed;
  return trimmed.slice(0, idx + 1).trim();
}

function firstHeading(content) {
  const lines = content.split('\n');
  for (const line of lines) {
    if (line.startsWith('# ')) return line.slice(2).trim();
  }
  return '';
}

function platformFromName(name) {
  const prefix = name.split('-')[0].toLowerCase();
  if (prefix === 'android') return 'android';
  if (prefix === 'ios') return 'ios';
  if (prefix === 'flutter') return 'flutter';
  if (prefix === 'shared') return 'shared';
  return 'other';
}

function groupedByPlatform(items) {
  const groups = { shared: [], flutter: [], ios: [], android: [], other: [] };
  for (const item of items) {
    const platform = item.platform && groups[item.platform] !== undefined ? item.platform : platformFromName(item.name);
    groups[platform].push(item);
  }
  return groups;
}

function titleCasePlatform(key) {
  return PLATFORM_LABELS[key] || 'Other';
}

function loadExternalSkills() {
  const externalPath = path.join(repoRoot, 'external-skills.json');
  if (!fs.existsSync(externalPath)) return [];
  try {
    const data = JSON.parse(fs.readFileSync(externalPath, 'utf8'));
    return data.map((s) => ({
      name: s.name,
      description: s.description || 'No description provided.',
      sourcePath: null,
      platform: s.platform,
      external: true,
      repo: s.repo,
      skillPath: s.skillPath || 'SKILL.md',
      author: s.author || null,
    }));
  } catch {
    return [];
  }
}

function buildSkillsData() {
  const skillsDir = path.join(repoRoot, 'skills');
  return listDirNames(skillsDir)
    .filter((dirName) => fs.existsSync(path.join(skillsDir, dirName, 'SKILL.md')))
    .map((dirName) => {
      const skillPath = path.join(skillsDir, dirName, 'SKILL.md');
      const content = readText(skillPath);
      const frontmatter = parseFrontmatter(content);
      return {
        name: dirName,
        description: frontmatter.description ? firstSentence(frontmatter.description) : 'No description provided.',
        sourcePath: `skills/${dirName}/SKILL.md`,
      };
    });
}

function buildRulesData() {
  const rulesDir = path.join(repoRoot, 'rules');
  return listMarkdownFiles(rulesDir).map((fileName) => {
    const fullPath = path.join(rulesDir, fileName);
    const content = readText(fullPath);
    const frontmatter = parseFrontmatter(content);
    const heading = firstHeading(content);
    const description = frontmatter.description
      ? firstSentence(frontmatter.description)
      : heading
        ? `${heading}.`
        : 'Rule guidance.';
    return {
      name: fileName,
      description,
      sourcePath: `rules/${fileName}`,
    };
  });
}

function buildAgentsData() {
  const agentsDir = path.join(repoRoot, 'agents');
  return listMarkdownFiles(agentsDir).map((fileName) => {
    const fullPath = path.join(agentsDir, fileName);
    const content = readText(fullPath);
    const frontmatter = parseFrontmatter(content);
    const heading = firstHeading(content);
    const description = frontmatter.description
      ? firstSentence(frontmatter.description)
      : heading
        ? `${heading}.`
        : 'Agent guidance.';
    return {
      name: fileName,
      description,
      sourcePath: `agents/${fileName}`,
    };
  });
}

function buildManifest(rules, agents) {
  const toEntry = (item, folder) => ({
    name: item.name.replace(/\.md$/, ''),
    file: item.sourcePath,
    platform: platformFromName(item.name),
    description: item.description,
  });
  return {
    generatedAt: new Date().toISOString(),
    rules: rules.map((r) => toEntry(r, 'rules')),
    agents: agents.map((a) => toEntry(a, 'agents')),
  };
}

function renderSkillsMd(skills) {
  const groups = groupedByPlatform(skills);
  const flutter = groups.flutter.filter((s) => !s.external).map((s) => s.name);
  const ios = groups.ios.filter((s) => !s.external).map((s) => s.name);
  const android = groups.android.filter((s) => !s.external).map((s) => s.name);

  const nav = PLATFORM_ORDER.filter((k) => groups[k].length > 0)
    .map((k) => `- [${titleCasePlatform(k)}](#${titleCasePlatform(k).toLowerCase().replace(/[^a-z0-9]+/g, '-')})`)
    .join('\n');

  const renderTable = (items) => {
    const rows = items
      .map((s) => {
        if (s.external) {
          const sourceUrl = `${s.repo}/blob/main/${s.skillPath}`;
          const nameCell = s.author ? `${s.name} *(by ${s.author})*` : s.name;
          return `| ${nameCell} | ${s.description} | \`npx skills add ${s.repo}\` | [SKILL.md](${sourceUrl}) |`;
        }
        return `| ${s.name} | ${s.description} | \`npx skills add ${REPO_URL} --skill ${s.name}\` | [SKILL.md](${s.sourcePath}) |`;
      })
      .join('\n');

    return `| Skill | Description | Install | Source |\n|---|---|---|---|\n${rows}`;
  };

  const sections = PLATFORM_ORDER.filter((k) => groups[k].length > 0)
    .map((k) => `### ${titleCasePlatform(k)}\n\n${renderTable(groups[k])}`)
    .join('\n\n');

  const flutterLoop = flutter.length > 0 ? flutter.join(' ') : 'flutter-example-skill';
  const iosLoop = ios.length > 0 ? ios.join(' ') : 'ios-example-skill';
  const androidLoop = android.length > 0 ? android.join(' ') : 'android-example-skill';

  return `# Skills Catalog

A focused, install-first page for all skills in this repository.

Naming convention:

- \`android-*\` for Android skills
- \`ios-*\` for iOS skills
- \`flutter-*\` for Flutter skills
- \`shared-*\` for cross-platform skills

Quick navigation:

${nav}

## Quick Start

### How To Run These Commands

1. Open a terminal (\`zsh\`, \`bash\`, or VS Code terminal).
2. Paste one of the command blocks below exactly as shown.
3. Press Enter once to run it.

Notes:

- You can run these commands from any folder.
- The \`skills\` CLI may ask whether to install globally or for the current project.
- If \`npx\` is missing, install Node.js first.

Install one skill:

\`\`\`bash
npx skills add ${REPO_URL} --skill <skill-name>
\`\`\`

Install all skills (non-interactive, no repeated prompts):

\`\`\`bash
npx skills add ${REPO_URL} --all
\`\`\`

Install all skills globally (user-level):

\`\`\`bash
npx skills add ${REPO_URL} --all --global
\`\`\`

If you prefer explicit flags instead of \`--all\`:

\`\`\`bash
npx skills add ${REPO_URL} --skill "*" --agent "*" --yes
\`\`\`

Install by platform:

\`\`\`bash
# Flutter
for skill in ${flutterLoop}; do
  npx skills add ${REPO_URL} --skill "\$skill"
done

# iOS
for skill in ${iosLoop}; do
  npx skills add ${REPO_URL} --skill "\$skill"
done

# Android
for skill in ${androidLoop}; do
  npx skills add ${REPO_URL} --skill "\$skill"
done
\`\`\`

## Full Catalog

${sections}`;
}

function renderRulesMd(rules) {
  const groups = groupedByPlatform(rules);
  const sections = PLATFORM_ORDER.filter((k) => groups[k].length > 0)
    .map((k) => {
      const lines = groups[k]
        .map((r) => `- [${r.name}](${r.sourcePath}): ${r.description}`)
        .join('\n');
      return `### ${titleCasePlatform(k)}\n\n${lines}`;
    })
    .join('\n\n');

  return `# Rules Catalog

Rules are plain Markdown files. Every major AI coding tool supports them — the only difference is where each tool looks for them.

## Quick Install

The interactive installer handles all IDE paths for you:

\`\`\`bash
npx github:Desquared/agents-rules-skills install-rules
\`\`\`

Pick your IDE(s), choose a scope (project or global), filter by platform, and the installer places them in the right location automatically.

## Manual Install

Pick a rule file, then copy it to your tool's expected location.

### Cursor

Cursor loads rules from \`.cursor/rules/\` (project-level) or \`~/.cursor/rules/\` (global).

\`\`\`bash
# Project-level
mkdir -p .cursor/rules
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \\
  -o .cursor/rules/ios-code-review.md

# Global (applies to all projects)
mkdir -p ~/.cursor/rules
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \\
  -o ~/.cursor/rules/ios-code-review.md
\`\`\`

### GitHub Copilot (VS Code)

Copilot reads a single \`.github/copilot-instructions.md\` file (project-level) or individual \`.instructions.md\` files from \`.github/instructions/\`.

\`\`\`bash
# Append a rule to the project instructions file
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \\
  >> .github/copilot-instructions.md

# Or as a standalone instruction file (VS Code 1.96+)
mkdir -p .github/instructions
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \\
  -o .github/instructions/ios-code-review.instructions.md
\`\`\`

### Claude Code

Claude Code reads \`CLAUDE.md\` at the project root (project-level) or \`~/.claude/CLAUDE.md\` (global).

\`\`\`bash
# Append a rule to the project CLAUDE.md
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \\
  >> CLAUDE.md

# Global (applies to all projects)
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \\
  >> ~/.claude/CLAUDE.md
\`\`\`

### Windsurf

Windsurf reads \`.windsurfrules\` (project-level) or \`~/.codeium/windsurf/memories/global_rules.md\` (global).

\`\`\`bash
# Append a rule to the project rules file
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \\
  >> .windsurfrules

# Global
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \\
  >> ~/.codeium/windsurf/memories/global_rules.md
\`\`\`

## Rule Catalog

${sections}`;
}

function renderAgentsMd(agents) {
  const groups = groupedByPlatform(agents);
  const sections = PLATFORM_ORDER.filter((k) => groups[k].length > 0)
    .map((k) => {
      const lines = groups[k]
        .map((a) => `- [${a.name}](${a.sourcePath}): ${a.description}`)
        .join('\n');
      return `### ${titleCasePlatform(k)}\n\n${lines}`;
    })
    .join('\n\n');

  return `# Agents Catalog

Agents are role-based Markdown files. **Claude Code has first-class support** for sub-agents today. Cursor, Windsurf, and GitHub Copilot handle agents differently — see the per-tool notes below.

## Quick Install

The interactive installer handles all IDE paths for you:

\`\`\`bash
npx github:Desquared/agents-rules-skills install-agents
\`\`\`

Pick your IDE(s), choose a scope (project or global), filter by platform, and the installer places files in the correct location — native agents folder for Claude Code, custom instructions for others.

## Manual Install

### How To Install An Agent

### Claude Code

Claude Code loads agents from \`~/.claude/agents/\` (global) or \`.claude/agents/\` (project-local). Each Markdown file becomes a sub-agent that Claude can automatically delegate to.

\`\`\`bash
# Global (available in every project)
mkdir -p ~/.claude/agents
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/agents/ios-security-reviewer.md \\
  -o ~/.claude/agents/ios-security-reviewer.md

# Project-local (only available in this project)
mkdir -p .claude/agents
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/agents/ios-security-reviewer.md \\
  -o .claude/agents/ios-security-reviewer.md
\`\`\`

### GitHub Copilot

GitHub Copilot supports project and global agent folders.

\`\`\`bash
# Global (available in every project)
mkdir -p ~/.copilot/agents
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/agents/ios-security-reviewer.md \\
  -o ~/.copilot/agents/ios-security-reviewer.md

# Project-local (only available in this project)
mkdir -p .github/agents
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/agents/ios-security-reviewer.md \\
  -o .github/agents/ios-security-reviewer.md
\`\`\`

### Cursor, Windsurf

These tools do not currently support a "drop a file = new sub-agent" pattern equivalent to Claude Code agents. Instead:

- **Cursor**: Use the agent files as custom instructions or rules (see [RULES.md](RULES.md)). The content works just as well as detailed system-level guidance.
- **Windsurf**: Paste the agent content into a Flow, or add it to a rule file (see [RULES.md](RULES.md)) for persistent context.

## Agent Catalog

${sections}`;
}

function renderReadmeMd(skills) {
  const starter = skills.find((s) => s.name === 'shared-bug-investigation')?.name || skills[0]?.name || '<skill-name>';

  return `# Agents, Rules & Skills for Mobile Development

Ship better mobile code with AI out of the box.

This repository gives AI assistants specialized mobile expertise for Android, iOS, and Flutter through three building blocks:

- Skills: reusable capabilities (architecture, testing, performance, accessibility, security).
- Rules: coding guardrails and standards.
- Agents: role-based specialists for focused tasks.

## Start Here

If you only do one thing, install skills first:

\`\`\`bash
npx skills add ${REPO_URL} --skill ${starter}
\`\`\`

Then pick your path:

| I want to... | Go to |
|---|---|
| Install agents interactively | \`npx github:Desquared/agents-rules-skills install-agents\` |
| Install rules interactively | \`npx github:Desquared/agents-rules-skills install-rules\` |
| Browse the full skills catalog | [SKILLS.md](SKILLS.md) |
| Browse rules & manual install | [RULES.md](RULES.md) |
| Browse agents & manual install | [AGENTS.md](AGENTS.md) |

## Installation Model

### Rules & Agents — Interactive Installer

Dedicated CLI commands for each type:

\`\`\`bash
# Agents only
npx github:Desquared/agents-rules-skills install-agents

# Rules only
npx github:Desquared/agents-rules-skills install-rules
\`\`\`

The installer walks you through:
1. **Which IDE(s)** — Cursor, GitHub Copilot, Claude Code, Windsurf, or all
3. **Scope** — project (current directory) or global (all projects)
4. **Platform** — Flutter, iOS, Android, shared
5. **Which items** — pick from a list or keep all

Each tool gets files in the right location automatically (e.g., \`.cursor/rules/\` for Cursor, \`.claude/agents/\` for Claude Code, \`.github/instructions/\` for Copilot).

- Full rules catalog: [RULES.md](RULES.md)
- Full agents catalog: [AGENTS.md](AGENTS.md)

### Skills

Skills follow the Agent Skills standard and can be installed directly with \`npx skills add\`.

- Full catalog + install-all commands: [SKILLS.md](SKILLS.md)

### Manual examples (if you prefer curl)

\`\`\`bash
# Rule -> Cursor project rules
mkdir -p .cursor/rules
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \\
  -o .cursor/rules/ios-code-review.md

# Rule -> GitHub Copilot (append to project instructions)
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \\
  >> .github/copilot-instructions.md

# Rule -> Claude Code (append to CLAUDE.md)
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \\
  >> CLAUDE.md

# Rule -> Windsurf (append to .windsurfrules)
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/rules/ios-code-review.md \\
  >> .windsurfrules

# Agent -> Claude Code (global, all projects)
mkdir -p ~/.claude/agents
curl -fsSL https://raw.githubusercontent.com/Desquared/agents-rules-skills/main/agents/ios-security-reviewer.md \\
  -o ~/.claude/agents/ios-security-reviewer.md
\`\`\`

## How Rules & Agents Work After Install

### Rules — no invocation needed

Rules are passive. Once installed, your AI tool picks them up automatically:

| IDE | How |
|---|---|
| **GitHub Copilot** | \`.instructions.md\` files in \`.github/instructions/\` (project) or \`~/Library/Application Support/Code/User/instructions/\` (global) are injected into every chat automatically. Restart VS Code after installing. |
| **Cursor** | Files in \`.cursor/rules/\` are loaded as context for every chat/edit session. |
| **Claude Code** | Rules appended to \`CLAUDE.md\` are read at the start of every session. |
| **Windsurf** | Rules appended to \`.windsurfrules\` are injected into every Cascade session. |

Just ask the AI to review, write, or refactor code — the rule's standards are already in context.

### Agents — invoke by name or role

Agents are active. You call them explicitly:

- **Claude Code**: Claude automatically delegates to sub-agents in \`~/.claude/agents/\` or \`.claude/agents/\` when the task matches their specialty. You can also ask directly: *"Use the ios-security-reviewer agent to audit this file."*
- **GitHub Copilot / Cursor / Windsurf**: Agents are installed as custom instructions. Reference them by role in your prompt: *"Act as the android-code-reviewer and review this PR."*

### Skills — invoke by name or description

Skills are on-demand capabilities injected into the AI's context when you ask for them. After installing with \`npx skills add\`, trigger a skill in two ways:

**By slash command** (explicit):
\`\`\`
/android-accessibility-validator
/shared-bug-investigation
/android-compose-architecture-review
\`\`\`

**By describing the task** (automatic match):
- *"Review this file for accessibility issues"* → triggers \`android-accessibility-validator\`
- *"Analyze the architecture of this feature"* → triggers \`android-compose-architecture-review\`
- *"Investigate this bug systematically"* → triggers \`shared-bug-investigation\`

See [SKILLS.md](SKILLS.md) for the full catalog.

## Why Teams Use This Repo

- Faster onboarding for AI-assisted workflows.
- Consistent output quality across projects and developers.
- Practical, battle-tested mobile guidance instead of generic prompts.

## Repository Structure

Canonical install targets are top-level directories:

\`\`\`text
skills/
rules/
agents/
\`\`\`

Platform is encoded in filenames (\`ios-\`, \`android-\`, \`flutter-\`) where relevant.

## Keeping Docs Updated

This repository includes an exporter that regenerates all docs from current files.

\`\`\`bash
npm run docs:export
\`\`\`

## References

- https://agentskills.io/specification
- https://github.com/agentskills/agentskills
- https://cursor.com/blog/agent-best-practices
- https://www.builder.io/blog/agent-skills-rules-commands
- https://developers.openai.com/codex/skills/`;
}

function exportAll() {
  const localSkills = buildSkillsData();
  const externalSkills = loadExternalSkills();
  const skills = [...localSkills, ...externalSkills];
  const rules = buildRulesData();
  const agents = buildAgentsData();

  if (localSkills.length === 0) {
    throw new Error('No skills found under skills/*/SKILL.md');
  }

  writeText(path.join(repoRoot, 'README.md'), renderReadmeMd(skills));
  writeText(path.join(repoRoot, 'SKILLS.md'), renderSkillsMd(skills));
  writeText(path.join(repoRoot, 'RULES.md'), renderRulesMd(rules));
  writeText(path.join(repoRoot, 'AGENTS.md'), renderAgentsMd(agents));
  writeText(path.join(repoRoot, 'manifest.json'), JSON.stringify(buildManifest(rules, agents), null, 2));

  console.log('Export complete: README.md, SKILLS.md, RULES.md, AGENTS.md, manifest.json');
}

function watchMode() {
  const watchTargets = ['skills', 'rules', 'agents'].map((dir) => path.join(repoRoot, dir));
  let timer = null;

  const triggerExport = () => {
    clearTimeout(timer);
    timer = setTimeout(() => {
      try {
        exportAll();
      } catch (error) {
        console.error('Export failed:', error.message);
      }
    }, 150);
  };

  for (const target of watchTargets) {
    if (!fs.existsSync(target)) continue;
    fs.watch(target, { recursive: true }, triggerExport);
  }

  console.log('Watching skills/, rules/, and agents/ for changes...');
}

function main() {
  exportAll();
  if (process.argv.includes('--watch')) {
    watchMode();
  }
}

main();
