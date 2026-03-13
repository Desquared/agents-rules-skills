#!/usr/bin/env node
/**
 * bin/install-rules.js — Rules-only entry point
 *
 * Run directly:   node bin/install-rules.js
 * Run via npm:    npm run install-rules
 * Run via npx:    npx github:Desquared/agents-rules-skills install-rules
 */
process.argv.push('--type', 'rules');
await import('./setup.js');
