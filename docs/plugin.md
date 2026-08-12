# Accessibility Toolkit Plugin

This repository is also a plugin marketplace. It ships one plugin, `accessibility-toolkit`, which bundles the accessibility skills, agents, and rules from this repo so a teammate gets the whole accessibility workflow in a single install instead of copying folders one at a time.

## What is in it

| Component | Contents |
|---|---|
| Skills | `accessibility-annotations`, `ios-accessibility-validator`, `android-accessibility-validator`, `flutter-accessibility-validator` |
| Agents | `ios-accessibility-specialist`, `android-accessibility-specialist`, `flutter-accessibility-specialist` |
| Rules | `flutter-accessibility` (scoped to `**/*.dart`) |
| MCP | Figma, which the annotation skill depends on |

The workflow it supports: annotate the design in Figma, implement against the platform validator, and review with the specialist agent.

Support differs per tool, because the tools support different component types:

| Tool | Skills | Agents | Rules |
|---|---|---|---|
| Cursor | yes | yes | yes |
| Claude Code | yes | yes | no such concept |
| Codex | yes | no such concept | no such concept |

## Install

### Cursor

Teams and Enterprise workspaces import the repository once as a team marketplace, in Dashboard, Plugins, Add Marketplace, Import from Repo. Members then install `accessibility-toolkit` from Customize in the sidebar. Enable Auto Refresh to pick up pushes to the tracked branch.

There is no install-from-URL command in Cursor. For local development or a personal install, symlink the plugin folder instead, then run Developer: Reload Window:

```bash
git clone https://github.com/Desquared/agents-rules-skills /tmp/agents-rules-skills
mkdir -p ~/.cursor/plugins/local
ln -sfn /tmp/agents-rules-skills/plugins/accessibility-toolkit ~/.cursor/plugins/local/accessibility-toolkit
```

### Claude Code

```bash
claude plugin marketplace add Desquared/agents-rules-skills
claude plugin install accessibility-toolkit@desquared-agent-plugins
```

The same commands work as `/plugin marketplace add` and `/plugin install` inside a session. Add `--scope project` to share the plugin with everyone who clones a given project. If the install summary asks you to activate, run `/reload-plugins`.

### Codex

```bash
codex plugin marketplace add Desquared/agents-rules-skills
codex plugin add accessibility-toolkit@desquared-agent-plugins
```

Codex reads the repository marketplace from `.agents/plugins/marketplace.json` and the plugin manifest from `.codex-plugin/plugin.json`. Pin a ref with `--ref` when adding the marketplace.

## Using it after install

Trigger a skill explicitly with a slash command, for example `/accessibility-annotations`, or just describe the task and let the agent match on the skill description. In Claude Code, plugin skills can also be namespaced as `/accessibility-toolkit:accessibility-annotations`.

## Maintaining the plugin

Everything under [plugins/accessibility-toolkit/](../plugins/accessibility-toolkit) is generated. Never edit it directly. Change the canonical source in `skills/`, `agents/`, or `rules/` at the repository root, then rebuild:

```bash
npm run plugin:build
```

The build script [bin/build-plugin.js](../bin/build-plugin.js) copies the curated content, converts rules to glob-scoped `.mdc` files, stamps every manifest with the version from `package.json`, and regenerates the plugin README. To add or remove content, edit the `CONTENT` allowlist at the top of that file.

The build fails on purpose when a skill or agent's frontmatter `name` does not match its file or folder name. Plugin components are matched by that name and are not namespaced per plugin, so a mismatch either makes the component undiscoverable or collides with a sibling.

Two rules to keep in mind when adding content:

- Never ship a rule with `alwaysApply: true`. Plugin rules load in every workspace of every member who installs the plugin, regardless of whether the project is relevant. Scope rules with `globs` instead.
- Keep the platform prefix in skill names. Plugin skills are not namespaced in Cursor, so a generic name like `accessibility` would collide with the skill of that name shipped by other installed plugins.

## Releasing

1. Bump `version` in [package.json](../package.json).
2. Run `npm run plugin:build` to stamp the manifests, and `npm run docs:export` to refresh the catalog pages.
3. Commit, then tag from the default branch after the change has merged, so the tag points at what users will actually resolve:

```bash
git checkout main && git pull
git tag -a accessibility-toolkit-v1.0.0 -m "accessibility-toolkit 1.0.0"
git push origin accessibility-toolkit-v1.0.0
```

Cursor team marketplaces with Auto Refresh re-index within about ten minutes. Claude Code and Codex users pick up the change with `claude plugin marketplace update desquared-agent-plugins` and `codex plugin marketplace upgrade desquared-agent-plugins` respectively. Both `marketplace add` commands accept a ref (`Desquared/agents-rules-skills@accessibility-toolkit-v1.0.0`) if you want members pinned to a release rather than tracking the branch.
