# Obsidian Tracker

A lightweight Obsidian vault for daily logging and weekly AI reviews.

Each day you write a short note: what moved, what leaked time, and one change
for tomorrow. At the end of the week, run `week review`; the agent reads the
daily notes, scores the week, writes a review, and sets next week's experiment.

This is not an app. It is a Markdown system built around Obsidian, `AGENTS.md`,
`docs/week-review.md`, templates, examples, and private local notes.

## Quick Start

Clone or download the repository, then create your private local files:

```bash
cp templates/daily-template.example.md templates/daily-template.md
cp rules/habit-rules.example.md rules/habit-rules.md
cp goals/goals.example.md goals/goals.md
```

Open the folder as an Obsidian vault.

Recommended Obsidian Daily Notes settings:

```txt
Date format:
DD-MM-YYYY

Template file location:
templates/daily-template

New file location:
logs
```

Use Obsidian's Daily Notes button or hotkey to create today's note.

## Daily Log

The most important fields are:

```txt
What moved the needle today?
Fake work / time leaks
One change for tomorrow
```

Log real output, not effort. A good note can take 1-5 minutes.

The daily template can also track habits, one `Hard thing`, goals, energy, and
optional context. These are useful, but secondary.

## Weekly Review

From this folder, open Codex/OpenCode or another compatible agent and type:

```txt
week review
```

The agent will:

- read the current or most recent week in `logs/`
- score each day from `0-10`
- calculate optional habit progress
- evaluate optional active goals
- write the review to `reviews/YYYY/MM/WXX-review.md`
- update `templates/daily-template.md` with next week's experiment

The review format and scoring rules live in `docs/week-review.md`.

## Optional Tracking

### Habits

Use `rules/habit-rules.md` to define private habit targets and weights. The
daily template can contain checkbox habits, measured habits, and limit habits.

Habit progress appears in the weekly review, but it does not change the output
score.

### Toggl Track Work Time

Optional: import today's tracked Toggl Track time into the daily `Work 🟡:`
line. This only fills that one line. It does not change weekly-review scoring
or output evaluation.

Set up private local files:

```bash
cp .env.example .env.local
cp integrations/toggl.example.json integrations/toggl.local.json
```

Get your API token from `https://track.toggl.com/profile`: scroll down to
**API Token**, copy it, and paste it into `.env.local`.

Edit `integrations/toggl.local.json` so the left side matches your exact Toggl
Track project names:

```json
{
  "projects": {
    "Client Work": "work",
    "Course Study": "school",
    "Side Project": "side quest"
  }
}
```

Then run:

```bash
node scripts/update-toggl-work.js
```

The private `.env.local` and `integrations/toggl.local.json` files are ignored
by Git.

### Hard Thing

Add one uncomfortable or avoided action per day:

```md
- [ ] Hard thing:
```

During `week review`, this is scored as a yes/no habit. It affects the output
score only when the action also created real logged output.

### Goals

Keep up to three active goals in `goals/goals.md`. Exactly one active goal must
be primary, marked with `🔥`.

```md
## 🔥 G1: Meal-planning demo

- Status: 🟢 active
- Deadline: 31.7.2026
- Done when: A visitor can create and save a seven-day meal plan.
```

Use `[G1]`, `[G2]`, or `[G3]` in daily output when the goal link would otherwise
be unclear. Goal alignment reports `On track`, `At risk`, `Off track`, or
`No evidence`; it does not automatically mark goals done.

## Privacy

Tracked files are intended to be public. Real personal data stays local.

Ignored by Git:

- `logs/`
- `reviews/`
- `templates/daily-template.md`
- `rules/habit-rules.md`
- `rules/active-rules.md`
- `goals/goals.md`
- `TODO_private.md`
- local Obsidian workspace/cache/plugin state
- attachments and local agent state

Public files use fictional examples only. The `logs/` and `reviews/` folders
are kept in the repository with `.gitkeep` placeholders.

## Files

- `AGENTS.md` - agent instructions and repository rules
- `docs/week-review.md` - exact weekly-review behavior
- `templates/daily-template.example.md` - public daily-note starter template
- `templates/weekly-review-template.md` - weekly review shape
- `rules/habit-rules.example.md` - public habit-rule example
- `goals/goals.example.md` - public goal example
- `examples/` - sample daily notes and weekly reviews
- `SYNC.md` - optional Android sync workflow

## Mobile

The vault is ordinary Markdown, so it can sync between desktop and Android with
Syncthing. Keep device-specific `.obsidian/` state local, wait for Syncthing to
show **Up to Date**, and avoid editing the same note on two devices at once.

See [SYNC.md](SYNC.md) for the full mobile setup.
