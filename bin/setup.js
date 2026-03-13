#!/usr/bin/env node
/**
 * bin/setup.js — Interactive Rules & Agents installer
 *
 * Run from this repo:   node bin/setup.js
 * Run via npx:          npx github:Desquared/agents-rules-skills
 */

import fs from 'fs';
import path from 'path';
import os from 'os';
import { fileURLToPath } from 'url';
import inquirer from 'inquirer';
import chalk from 'chalk';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const repoRoot = path.join(__dirname, '..');

// Returns the VS Code User data directory (cross-platform)
function vsCodeUserDataDir() {
  const platform = os.platform();
  if (platform === 'darwin') {
    return path.join(os.homedir(), 'Library', 'Application Support', 'Code', 'User');
  } else if (platform === 'win32') {
    return path.join(process.env.APPDATA || os.homedir(), 'Code', 'User');
  } else {
    return path.join(os.homedir(), '.config', 'Code', 'User');
  }
}

// ─── IDE definitions ───────────────────────────────────────────────────────
//
// rulesConfig.install:
//   'file'              → copy as-is into a directory
//   'instructions-file' → copy renamed to *.instructions.md (Copilot)
//   'append'            → append into a single file with idempotent markers
//
// agentsConfig: null means "fall back to treating agents as rules"

const IDES = [
  {
    key: 'cursor',
    label: 'Cursor',
    rulesConfig: {
      install: 'file',
      projectPath: (cwd) => path.join(cwd, '.cursor', 'rules'),
      globalPath: () => path.join(os.homedir(), '.cursor', 'rules'),
    },
    agentsConfig: null,
  },
  {
    key: 'copilot',
    label: 'GitHub Copilot',
    rulesConfig: {
      install: 'instructions-file',
      projectPath: (cwd) => path.join(cwd, '.github', 'instructions'),
      globalPath: () => path.join(vsCodeUserDataDir(), 'instructions'),
    },
    agentsConfig: {
      install: 'file',
      projectPath: (cwd) => path.join(cwd, '.github', 'agents'),
      globalPath: () => path.join(os.homedir(), '.copilot', 'agents'),
    },
  },
  {
    key: 'claude',
    label: 'Claude Code',
    rulesConfig: {
      install: 'append',
      projectPath: (cwd) => path.join(cwd, 'CLAUDE.md'),
      globalPath: () => path.join(os.homedir(), '.claude', 'CLAUDE.md'),
    },
    agentsConfig: {
      install: 'file',
      projectPath: (cwd) => path.join(cwd, '.claude', 'agents'),
      globalPath: () => path.join(os.homedir(), '.claude', 'agents'),
    },
  },
  {
    key: 'windsurf',
    label: 'Windsurf',
    rulesConfig: {
      install: 'append',
      projectPath: (cwd) => path.join(cwd, '.windsurfrules'),
      globalPath: () =>
        path.join(os.homedir(), '.codeium', 'windsurf', 'memories', 'global_rules.md'),
    },
    agentsConfig: null,
  },
];

// ─── Manifest helpers ──────────────────────────────────────────────────────

function parseFrontmatter(content) {
  if (!content.startsWith('---\n')) return {};
  const end = content.indexOf('\n---\n', 4);
  if (end === -1) return {};
  const data = {};
  for (const line of content.slice(4, end).split('\n')) {
    const idx = line.indexOf(':');
    if (idx === -1) continue;
    data[line.slice(0, idx).trim()] = line.slice(idx + 1).trim();
  }
  return data;
}

function firstHeading(content) {
  for (const line of content.split('\n')) {
    if (line.startsWith('# ')) return line.slice(2).trim();
  }
  return '';
}

function firstSentence(text) {
  if (!text) return '';
  const trimmed = text.replace(/\s+/g, ' ').trim();
  const idx = trimmed.search(/[.!?](\s|$)/);
  return idx === -1 ? trimmed : trimmed.slice(0, idx + 1).trim();
}

function platformFromFilename(fileName) {
  const base = fileName.replace(/\.md$/, '');
  const prefix = base.split('-')[0].toLowerCase();
  if (['android', 'ios', 'flutter', 'shared'].includes(prefix)) return prefix;
  return 'other';
}

function buildManifestEntry(folder, fileName) {
  const fullPath = path.join(repoRoot, folder, fileName);
  const content = fs.readFileSync(fullPath, 'utf8');
  const fm = parseFrontmatter(content);
  const heading = firstHeading(content);
  const rawDesc = fm.description || (heading ? `${heading}.` : '');
  return {
    name: fileName.replace(/\.md$/, ''),
    file: `${folder}/${fileName}`,
    platform: platformFromFilename(fileName),
    description: firstSentence(rawDesc) || 'No description.',
  };
}

function buildManifestFromDisk() {
  const listMd = (folder) => {
    const dir = path.join(repoRoot, folder);
    if (!fs.existsSync(dir)) return [];
    return fs
      .readdirSync(dir)
      .filter((f) => f.endsWith('.md') && fs.statSync(path.join(dir, f)).isFile())
      .sort();
  };

  return {
    generatedAt: new Date().toISOString(),
    rules: listMd('rules').map((f) => buildManifestEntry('rules', f)),
    agents: listMd('agents').map((f) => buildManifestEntry('agents', f)),
  };
}

function loadManifest() {
  const manifestPath = path.join(repoRoot, 'manifest.json');
  if (fs.existsSync(manifestPath)) {
    try {
      return JSON.parse(fs.readFileSync(manifestPath, 'utf8'));
    } catch {
      // fall through to disk scan
    }
  }
  return buildManifestFromDisk();
}

// ─── Install helpers ───────────────────────────────────────────────────────

function getContent(item) {
  return fs.readFileSync(path.join(repoRoot, item.file), 'utf8');
}

function installFile(targetDir, fileName, content) {
  fs.mkdirSync(targetDir, { recursive: true });
  const dest = path.join(targetDir, fileName);
  fs.writeFileSync(dest, content, 'utf8');
  return dest;
}

function installAppend(targetFile, content, itemName) {
  const startMarker = `<!-- mobile-ai:start ${itemName} -->`;
  const endMarker = `<!-- mobile-ai:end ${itemName} -->`;
  const section = `\n${startMarker}\n${content.trimEnd()}\n${endMarker}\n`;

  let existing = '';
  if (fs.existsSync(targetFile)) {
    existing = fs.readFileSync(targetFile, 'utf8');
  }

  fs.mkdirSync(path.dirname(targetFile), { recursive: true });

  if (existing.includes(startMarker)) {
    // Replace existing section (idempotent re-install)
    const startIdx = existing.indexOf(startMarker);
    const endIdx = existing.indexOf(endMarker) + endMarker.length;
    const updated = existing.slice(0, startIdx).trimEnd() + section + existing.slice(endIdx).replace(/^\n+/, '\n');
    fs.writeFileSync(targetFile, updated, 'utf8');
  } else {
    fs.appendFileSync(targetFile, section, 'utf8');
  }

  return targetFile;
}

function doInstall(item, ide, type, scope, cwd) {
  const baseName = path.basename(item.file); // e.g. "ios-code-review.md"
  const content = getContent(item);

  // Agents on IDEs that support them natively (Claude Code)
  if (type === 'agents' && ide.agentsConfig) {
    const config = ide.agentsConfig;
    const targetDir =
      scope === 'global' ? config.globalPath() : config.projectPath(cwd);
    return installFile(targetDir, baseName, content);
  }

  // Rules (or agents falling back to rules-style install)
  const config = ide.rulesConfig;

  if (config.install === 'file') {
    const targetDir =
      scope === 'global' ? config.globalPath() : config.projectPath(cwd);
    return installFile(targetDir, baseName, content);
  }

  if (config.install === 'instructions-file') {
    // Copilot: rename to .instructions.md
    const targetDir = scope === 'global' ? config.globalPath() : config.projectPath(cwd);
    const instrName = baseName.replace(/\.md$/, '.instructions.md');
    return installFile(targetDir, instrName, content);
  }

  if (config.install === 'append') {
    const targetFile =
      scope === 'global' ? config.globalPath() : config.projectPath(cwd);
    return installAppend(targetFile, content, item.name);
  }

  throw new Error(`Unknown install type: ${config.install}`);
}

// ─── UI helpers ─────────────────────────────────────────────────────────────

const PLATFORM_ORDER = ['flutter', 'ios', 'android', 'shared', 'other'];
const PLATFORM_LABELS = {
  flutter: 'Flutter',
  ios: 'iOS',
  android: 'Android',
  shared: 'Shared / Cross-platform',
  other: 'Other',
};

function buildGroupedChoices(items) {
  const choices = [];
  for (const platform of PLATFORM_ORDER) {
    const group = items.filter((i) => i.platform === platform);
    if (group.length === 0) continue;
    choices.push(new inquirer.Separator(`── ${PLATFORM_LABELS[platform]}`));
    for (const item of group) {
      choices.push({
        name: `${item.name}  ${chalk.dim(item.description)}`,
        value: item,
        checked: false,
      });
    }
  }
  return choices;
}

function shortenPath(p) {
  const home = os.homedir();
  const cwd = process.cwd();
  if (p.startsWith(cwd)) return p.replace(cwd, '.');
  if (p.startsWith(home)) return p.replace(home, '~');
  return p;
}

// ─── Main ──────────────────────────────────────────────────────────────────

async function main() {
  console.log(chalk.bold.cyan('\n🛠  Mobile AI — Rules & Agents Installer\n'));
  console.log(chalk.dim('  Arrow keys to navigate · Space to select · Enter to confirm\n'));

  const manifest = loadManifest();

  // ── Step 1: What to install? ───────────────────────────────────────────
  // Accept --type <rules|agents> flag or positional subcommand:
  //   npx github:Desquared/agents-rules-skills install-agents
  //   npx github:Desquared/agents-rules-skills install-rules
  //   node bin/setup.js --type agents
  const subcmd = process.argv[2];
  if (subcmd === 'install-agents') process.argv.splice(2, 1, '--type', 'agents');
  if (subcmd === 'install-rules')  process.argv.splice(2, 1, '--type', 'rules');

  const typeFlag = (() => {
    const i = process.argv.indexOf('--type');
    const val = i !== -1
      ? process.argv[i + 1]
      : process.argv.find((a) => a.startsWith('--type='))?.split('=').slice(1).join('=');
    return val && ['rules', 'agents'].includes(val) ? val : null;
  })();

  let type;
  if (typeFlag) {
    type = typeFlag;
    const label = { rules: 'Rules', agents: 'Agents' }[type];
    console.log(chalk.dim(`  Installing: ${label}\n`));
  } else {
    ({ type } = await inquirer.prompt([
      {
        type: 'list',
        name: 'type',
        message: 'What do you want to install?',
        choices: [
          { name: 'Rules   — coding standards & guardrails', value: 'rules' },
          { name: 'Agents  — role-based AI specialists',     value: 'agents' },
        ],
      },
    ]));
  }

  const doRules = type !== 'agents';
  const doAgents = type !== 'rules';

  // ── Step 2: IDE(s) ────────────────────────────────────────────────────
  const { selectedIdeKeys } = await inquirer.prompt([
    {
      type: 'checkbox',
      name: 'selectedIdeKeys',
      message: 'Install for which IDE(s)?',
      choices: [
        ...IDES.map((ide) => ({
          name: ide.label,
          value: ide.key,
          checked: false,
        })),
        new inquirer.Separator(),
        { name: 'All of the above', value: '__all__' },
      ],
      validate: (v) => v.length > 0 || 'Select at least one IDE.',
    },
  ]);

  const ideKeys = selectedIdeKeys.includes('__all__')
    ? IDES.map((i) => i.key)
    : selectedIdeKeys;
  const selectedIdes = IDES.filter((i) => ideKeys.includes(i.key));

  // Warn about agent fallback for non-Claude IDEs
  if (doAgents) {
    const noNativeAgents = selectedIdes.filter((i) => !i.agentsConfig);
    if (noNativeAgents.length > 0) {
      console.log(
        chalk.yellow(
          `\n  ⚠  ${noNativeAgents.map((i) => i.label).join(', ')} don't support sub-agents natively.`
        )
      );
      console.log(chalk.yellow('     Agents will be installed as custom instructions for those tools.\n'));
    }
  }

  // ── Step 3: Scope ─────────────────────────────────────────────────────
  const { scope } = await inquirer.prompt([
    {
      type: 'list',
      name: 'scope',
      message: 'Where to install?',
      choices: [
        { name: 'Project  — current directory only', value: 'project' },
        { name: 'Global   — applies to all projects', value: 'global' },
      ],
    },
  ]);

  // ── Step 3b: Target directory (project scope only) ────────────────────
  let targetCwd = process.cwd();
  if (scope === 'project') {
    const { projectDir } = await inquirer.prompt([
      {
        type: 'input',
        name: 'projectDir',
        message: 'Project directory:',
        default: process.cwd(),
      },
    ]);
    targetCwd = path.resolve(projectDir);
    if (!fs.existsSync(targetCwd)) {
      console.log(chalk.red(`\n  Directory not found: ${targetCwd}\n`));
      process.exit(1);
    }
  }

  // ── Step 4: Platform filter ───────────────────────────────────────────
  const { platforms } = await inquirer.prompt([
    {
      type: 'checkbox',
      name: 'platforms',
      message: 'Filter by platform:',
      choices: [
        { name: 'Flutter',                   value: 'flutter', checked: false },
        { name: 'iOS',                       value: 'ios',     checked: false },
        { name: 'Android',                   value: 'android', checked: false },
        { name: 'Shared / Cross-platform',   value: 'shared',  checked: false },
      ],
      validate: (v) => v.length > 0 || 'Select at least one platform.',
    },
  ]);

  const filterByPlatform = (items) =>
    items.filter((item) => platforms.includes(item.platform));

  const rulesPool  = doRules  ? filterByPlatform(manifest.rules)  : [];
  const agentsPool = doAgents ? filterByPlatform(manifest.agents) : [];

  if (rulesPool.length === 0 && agentsPool.length === 0) {
    console.log(chalk.yellow('\n  No items found for the selected filters. Exiting.\n'));
    process.exit(0);
  }

  // ── Step 5a: Select rules ─────────────────────────────────────────────
  let chosenRules = [];
  if (rulesPool.length > 0) {
    const { rules } = await inquirer.prompt([
      {
        type: 'checkbox',
        name: 'rules',
        message: `Select rules to install: (${rulesPool.length} available)`,
        choices: buildGroupedChoices(rulesPool),
        validate: (v) => v.length > 0 || 'Select at least one rule.',
        pageSize: 20,
      },
    ]);
    chosenRules = rules;
  }

  // ── Step 5b: Select agents ────────────────────────────────────────────
  let chosenAgents = [];
  if (agentsPool.length > 0) {
    const { agents } = await inquirer.prompt([
      {
        type: 'checkbox',
        name: 'agents',
        message: `Select agents to install: (${agentsPool.length} available)`,
        choices: buildGroupedChoices(agentsPool),
        validate: (v) => v.length > 0 || 'Select at least one agent.',
        pageSize: 20,
      },
    ]);
    chosenAgents = agents;
  }

  // ── Step 6: Confirm ───────────────────────────────────────────────────
  const total = chosenRules.length + chosenAgents.length;
  const ideLabel = selectedIdes.map((i) => i.label).join(', ');

  console.log(
    chalk.bold(`\n  About to install ${chalk.cyan(total)} item(s) for ${chalk.cyan(ideLabel)} (${scope}).`)
  );
  if (chosenRules.length > 0) {
    console.log(chalk.dim(`  Rules:  ${chosenRules.map((r) => r.name).join(', ')}`));
  }
  if (chosenAgents.length > 0) {
    console.log(chalk.dim(`  Agents: ${chosenAgents.map((a) => a.name).join(', ')}`));
  }
  console.log();

  const { confirm } = await inquirer.prompt([
    {
      type: 'confirm',
      name: 'confirm',
      message: 'Proceed?',
      default: true,
    },
  ]);

  if (!confirm) {
    console.log(chalk.yellow('\n  Cancelled.\n'));
    process.exit(0);
  }

  // ── Step 7: Install ───────────────────────────────────────────────────
  const cwd = targetCwd;
  let success = 0;
  let errors = 0;

  for (const ide of selectedIdes) {
    console.log(chalk.bold(`\n  ▸ ${ide.label}`));

    for (const item of chosenRules) {
      try {
        const dest = doInstall(item, ide, 'rules', scope, cwd);
        console.log(chalk.green(`    ✓ ${item.name}`) + chalk.dim(`  →  ${shortenPath(dest)}`));
        success++;
      } catch (err) {
        console.log(chalk.red(`    ✗ ${item.name}: ${err.message}`));
        errors++;
      }
    }

    for (const item of chosenAgents) {
      try {
        const dest = doInstall(item, ide, 'agents', scope, cwd);
        const note = !ide.agentsConfig ? chalk.yellow(' (as instruction)') : '';
        console.log(chalk.green(`    ✓ ${item.name}`) + note + chalk.dim(`  →  ${shortenPath(dest)}`));
        success++;
      } catch (err) {
        console.log(chalk.red(`    ✗ ${item.name}: ${err.message}`));
        errors++;
      }
    }
  }

  console.log();
  if (success > 0) console.log(chalk.bold.green(`  ✅ ${success} installed`));
  if (errors > 0) console.log(chalk.bold.red(`  ✗  ${errors} failed`));
  console.log(chalk.dim('\n  Tip: Restart your IDE to pick up the new files.\n'));
}

main().catch((err) => {
  console.error(chalk.red('\n  Fatal error:'), err.message);
  process.exit(1);
});
