#!/usr/bin/env node
const fs = require('fs');
const path = require('path');

// -- helpers --

function die(m) { console.error('Error: ' + m); process.exit(1); }

// Parse .env.local (simple KEY=VAL, no deps)
function loadEnv(root) {
  let raw;
  try { raw = fs.readFileSync(path.join(root, '.env.local'), 'utf8'); }
  catch { die('.env.local not found. Copy from .env.example and add your Toggl token.'); }
  const env = {};
  for (const line of raw.split('\n')) {
    const t = line.trim();
    if (!t || t.startsWith('#')) continue;
    const eq = t.indexOf('=');
    if (eq < 0) continue;
    const k = t.slice(0, eq).trim(), v = t.slice(eq + 1).trim();
    if (k) env[k] = v;
  }
  return env;
}

// Load Toggl project-to-category mapping (private config)
function loadConfig(root) {
  let raw;
  try { raw = fs.readFileSync(path.join(root, 'integrations', 'toggl.local.json'), 'utf8'); }
  catch { die('integrations/toggl.local.json not found'); }
  try { return JSON.parse(raw); }
  catch { die('integrations/toggl.local.json is not valid JSON'); }
}

// Today's date (year, month, day) in the configured IANA timezone
function todayLocal(tz) {
  const p = new Intl.DateTimeFormat('en-CA', { timeZone: tz, year: 'numeric', month: '2-digit', day: '2-digit' }).formatToParts(new Date());
  const g = (t) => parseInt(p.find(x => x.type === t).value, 10);
  return { y: g('year'), m: g('month'), d: g('day') };
}

// Convert a local date+time to UTC RFC3339 in an arbitrary IANA timezone.
//
// Node has no direct "local time → UTC" for a non-system timezone.
// We guess UTC via Date.UTC, then check what local time the guess produces
// via Intl.DateTimeFormat(timeZone). The difference tells us the true offset.
// Converges in 1-2 iterations (no DST edge case needs more).
function localToUtc(y, m, d, h, min, s, tz) {
  let ms = Date.UTC(y, m - 1, d, h, min, s);
  for (let i = 0; i < 3; i++) {
    const p = new Intl.DateTimeFormat('en-CA', { timeZone: tz, year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false }).formatToParts(new Date(ms));
    const g = (t) => parseInt(p.find(x => x.type === t).value, 10);
    if (g('year') === y && g('month') === m && g('day') === d && g('hour') === h && g('minute') === min) break;
    ms += Date.UTC(y, m - 1, d, h, min, s) - Date.UTC(g('year'), g('month') - 1, g('day'), g('hour'), g('minute'), g('second'));
  }
  return new Date(ms).toISOString();
}

async function fetchJson(url, token) {
  const b64 = Buffer.from(token + ':api_token').toString('base64');
  const res = await fetch(url, { headers: { Authorization: 'Basic ' + b64 } });
  if (!res.ok) die('Toggl API returned ' + res.status + ' ' + res.statusText);
  return res.json();
}

// Fetch completed Toggl entries for today's local date window
async function fetchTodaysEntries(token, tz) {
  const { y, m, d } = todayLocal(tz);
  const start = localToUtc(y, m, d, 0, 0, 0, tz);
  // Next calendar day's midnight — Date constructor handles month/year rollover
  const nx = new Date(y, m - 1, d + 1);
  const end = localToUtc(nx.getFullYear(), nx.getMonth() + 1, nx.getDate(), 0, 0, 0, tz);
  const url = 'https://api.track.toggl.com/api/v9/me/time_entries?start_date=' + encodeURIComponent(start) + '&end_date=' + encodeURIComponent(end) + '&meta=true';
  return { entries: await fetchJson(url, token), y, m, d };
}

// Sum completed entry durations into fixed categories: work, school, side quest, other
function sumCats(entries, map) {
  const cats = { work: 0, school: 0, 'side quest': 0, other: 0 };
  for (const e of entries) {
    if (typeof e.duration !== 'number' || e.duration <= 0) continue;
    // ponytail: exact match only (spec req), unknown → other
    const cat = map[e.project_name || ''] || 'other';
    cats[cat in cats ? cat : 'other'] += e.duration;
  }
  return cats;
}

function fmtDur(sec) {
  const m = Math.floor(sec / 60), h = Math.floor(m / 60), r = m % 60;
  if (h === 0) return r + 'm';
  if (r === 0) return h + 'h';
  return h + 'h ' + r + 'm';
}

// Build the replacement line, e.g. "- Work 🟡: 2h 45m (work 1h 30m, school 45m, side quest 30m)"
function buildLine(cats) {
  const total = Object.values(cats).reduce((a, b) => a + b, 0);
  if (total === 0) return '- Work 🟡: 0m';
  const parts = [];
  for (const k of ['work', 'school', 'side quest', 'other']) {
    if (cats[k] > 0) parts.push(k + ' ' + fmtDur(cats[k]));
  }
  return '- Work 🟡: ' + fmtDur(total) + ' (' + parts.join(', ') + ')';
}

// -- main --

async function main() {
  // Verify we are in the repo root
  const ROOT = path.resolve(__dirname, '..');
  if (!fs.existsSync(path.join(ROOT, 'AGENTS.md'))) die('repo root not found (AGENTS.md missing)');

  const env = loadEnv(ROOT);
  const token = env.TOGGL_API_TOKEN;
  const tz = env.TOGGL_TIMEZONE;
  if (!token) die('TOGGL_API_TOKEN not set in .env.local');
  if (!tz) die('TOGGL_TIMEZONE not set in .env.local');

  const cfg = loadConfig(ROOT);
  const map = cfg.projects || {};

  // Refuse if a timer is still running (data would be incomplete)
  const running = await fetchJson('https://api.track.toggl.com/api/v9/me/time_entries/current', token);
  if (running) die('a Toggl timer is running. Stop it first and re-run.');

  const { entries, y, m, d } = await fetchTodaysEntries(token, tz);
  const line = buildLine(sumCats(entries, map));

  const logPath = path.join(ROOT, 'logs', String(d).padStart(2, '0') + '-' + String(m).padStart(2, '0') + '-' + y + '.md');
  let note;
  try { note = fs.readFileSync(logPath, 'utf8'); }
  catch { die('daily note not found at ' + logPath); }

  // Replace the Work 🟡 line. The callback avoids $&/$`/$' interpretation in the replacement string.
  const updated = note.replace(/^- Work 🟡:.*$/m, () => line);
  if (updated === note) die('no "Work 🟡:" line found in ' + logPath);

  fs.writeFileSync(logPath, updated, 'utf8');
  console.log('Updated ' + logPath);
  console.log(line);
}

main().catch(e => { console.error('Unexpected:', e.message || e); process.exit(1); });
