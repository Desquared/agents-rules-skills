#!/usr/bin/env node
/**
 * bin/install-agents.js — Agents-only entry point
 *
 * Run directly:   node bin/install-agents.js
 * Run via npm:    npm run install-agents
 * Run via npx:    npx github:Desquared/agents-rules-skills install-agents
 */
process.argv.push('--type', 'agents');
await import('./setup.js');
