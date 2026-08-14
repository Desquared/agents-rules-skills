#!/usr/bin/env node

import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const repoRoot = path.join(__dirname, '..');

const PLUGIN_NAME = 'accessibility-toolkit';
const pluginRoot = path.join(repoRoot, 'plugins', PLUGIN_NAME);

// Curated content for the plugin. Canonical sources stay in skills/, agents/,
// and rules/ at the repo root; everything under plugins/ is generated from here.
const CONTENT = {
  skills: [
    'accessibility-annotations',
    'ios-accessibility-validator',
    'android-accessibility-validator',
    'flutter-accessibility-validator',
  ],
  agents: [
    'ios-accessibility-specialist.md',
    'android-accessibility-specialist.md',
    'flutter-accessibility-specialist.md',
  ],
  // Cursor only: Claude Code and Codex have no rules concept. Plugin rules must
  // never be alwaysApply, since that loads them in every workspace, so each one
  // is scoped to the files it actually applies to.
  rules: [
    { file: 'flutter-accessibility.md', globs: '**/*.dart' },
  ],
};

// Manifests that carry a version. The Claude and Codex marketplace files have no
// version field by design, so they are left alone.
const MANIFESTS = [
  '.cursor-plugin/marketplace.json',
  `plugins/${PLUGIN_NAME}/.cursor-plugin/plugin.json`,
  `plugins/${PLUGIN_NAME}/.claude-plugin/plugin.json`,
  `plugins/${PLUGIN_NAME}/.codex-plugin/plugin.json`,
];

function readText(filePath) {
  return fs.readFileSync(filePath, 'utf8');
}

function writeText(filePath, content) {
  fs.mkdirSync(path.dirname(filePath), { recursive: true });
  fs.writeFileSync(filePath, content.trimEnd() + '\n', 'utf8');
}

function resetDir(dirPath) {
  fs.rmSync(dirPath, { recursive: true, force: true });
  fs.mkdirSync(dirPath, { recursive: true });
}

function copyDir(source, destination) {
  fs.mkdirSync(destination, { recursive: true });
  for (const entry of fs.readdirSync(source, { withFileTypes: true })) {
    const from = path.join(source, entry.name);
    const to = path.join(destination, entry.name);
    if (entry.isDirectory()) {
      copyDir(from, to);
    } else if (entry.isFile()) {
      fs.copyFileSync(from, to);
    }
  }
}

function requireSource(sourcePath, label) {
  if (!fs.existsSync(sourcePath)) {
    throw new Error(`Missing ${label}: ${path.relative(repoRoot, sourcePath)}`);
  }
  return sourcePath;
}

function firstHeading(content) {
  for (const line of content.split('\n')) {
    if (line.startsWith('# ')) return line.slice(2).trim();
  }
  return '';
}

function hasFrontmatter(content) {
  return content.startsWith('---\n');
}

function parseFrontmatter(content) {
  if (!hasFrontmatter(content)) return null;
  const end = content.indexOf('\n---\n', 4);
  if (end === -1) return null;
  const data = {};
  for (const line of content.slice(4, end).split('\n')) {
    const idx = line.indexOf(':');
    if (idx === -1) continue;
    data[line.slice(0, idx).trim()] = line.slice(idx + 1).trim();
  }
  return data;
}

// Agents and skills are matched by their frontmatter name, and plugin content is
// not namespaced by plugin. A name that drifts from its file name is either
// undiscoverable or collides with a sibling, so fail the build instead.
function assertName(sourcePath, expectedName, label) {
  const frontmatter = parseFrontmatter(readText(sourcePath));
  const relativePath = path.relative(repoRoot, sourcePath);
  if (!frontmatter) {
    throw new Error(`${label} is missing frontmatter: ${relativePath}`);
  }
  if (!frontmatter.description) {
    throw new Error(`${label} is missing a description: ${relativePath}`);
  }
  if (frontmatter.name !== expectedName) {
    throw new Error(
      `${label} name "${frontmatter.name}" does not match "${expectedName}": ${relativePath}`
    );
  }
}

// Rules ship as .mdc so Cursor can scope them. The canonical .md files stay
// plain so they keep working with the CLAUDE.md / .windsurfrules install paths.
function buildRule({ file, globs }) {
  const sourcePath = requireSource(path.join(repoRoot, 'rules', file), 'rule');
  const content = readText(sourcePath);
  if (hasFrontmatter(content)) {
    throw new Error(`Rule already has frontmatter, refusing to double-wrap: rules/${file}`);
  }
  const heading = firstHeading(content);
  const description = heading ? `${heading} guidance for ${globs} files.` : 'Rule guidance.';
  const frontmatter = ['---', `description: ${description}`, `globs: ${globs}`, 'alwaysApply: false', '---'].join('\n');
  const target = path.join(pluginRoot, 'rules', file.replace(/\.md$/, '.mdc'));
  writeText(target, `${frontmatter}\n\n${content.trim()}`);
  return path.basename(target);
}

function buildSkills() {
  const target = path.join(pluginRoot, 'skills');
  resetDir(target);
  for (const name of CONTENT.skills) {
    const source = requireSource(path.join(repoRoot, 'skills', name), 'skill');
    assertName(requireSource(path.join(source, 'SKILL.md'), 'skill manifest'), name, 'Skill');
    copyDir(source, path.join(target, name));
  }
  return CONTENT.skills;
}

function buildAgents() {
  const target = path.join(pluginRoot, 'agents');
  resetDir(target);
  for (const file of CONTENT.agents) {
    const source = requireSource(path.join(repoRoot, 'agents', file), 'agent');
    assertName(source, file.replace(/\.md$/, ''), 'Agent');
    fs.copyFileSync(source, path.join(target, file));
  }
  return CONTENT.agents;
}

function buildRules() {
  resetDir(path.join(pluginRoot, 'rules'));
  return CONTENT.rules.map(buildRule);
}

// Keep every manifest on the repository version so a release stamps one number.
function syncVersions() {
  const { version } = JSON.parse(readText(path.join(repoRoot, 'package.json')));
  for (const relativePath of MANIFESTS) {
    const manifestPath = path.join(repoRoot, relativePath);
    const manifest = JSON.parse(readText(requireSource(manifestPath, 'manifest')));
    if (manifest.metadata) {
      manifest.metadata.version = version;
    } else {
      manifest.version = version;
    }
    writeText(manifestPath, JSON.stringify(manifest, null, 2));
  }
  return version;
}

function writePluginReadme({ skills, agents, rules, version }) {
  const list = (items) => items.map((item) => `- \`${item}\``).join('\n');
  writeText(
    path.join(pluginRoot, 'README.md'),
    `# Accessibility Toolkit

Generated by \`npm run plugin:build\`. Do not edit these files directly — change the
canonical sources in \`skills/\`, \`agents/\`, and \`rules/\` at the repository root,
then rebuild.

Version: ${version}

## Skills

${list(skills)}

## Agents

${list(agents)}

## Rules

${list(rules)}

Install instructions live in [docs/plugin.md](../../docs/plugin.md).`
  );
}

function main() {
  const skills = buildSkills();
  const agents = buildAgents();
  const rules = buildRules();
  const version = syncVersions();
  writePluginReadme({ skills, agents, rules, version });

  console.log(
    `Built ${PLUGIN_NAME} v${version}: ${skills.length} skills, ${agents.length} agents, ${rules.length} rules`
  );
}

main();
