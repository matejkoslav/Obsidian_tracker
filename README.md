# Obsidian Tracker

Prompt-driven Markdown/Obsidian system for quick daily logging and weekly AI
reviews. This is not an application: most behavior lives in `AGENTS.md`,
`docs/week-review.md`, the Markdown templates, examples, and local private
files.

Obsidian is the writing interface. Codex/OpenCode (or any other agent) is the
weekly reviewer and maintenance agent.

## Core loop

1. Spend **1–5 min** logging the day in `logs/DD-MM-YYYY.md`.
2. At the end of the week, open Codex/OpenCode (or any other agent) in this
   folder and type:

```txt
week review
```

3. The agent reads the current week, scores each day **0–10**, calculates
   optional habit progress, checks optional active goals, and compares the week
   with the previous review.
4. The agent writes the review to `reviews/YYYY/MM/WXX-review.md`.
5. The agent writes one concrete experiment and current active-goal visibility
   into `templates/daily-template.md` for the next week.

## AI maintainer context

- Keep public-facing docs, examples, and templates in English.
- Treat `logs/`, `reviews/`, `templates/daily-template.md`,
  `rules/habit-rules.md`, and `goals/goals.md` as private/local data.
- Prefer Markdown-first changes: agent contract, templates, examples, and docs
  before adding scripts or app code.
- Keep week-review behavior in `docs/week-review.md`; keep `AGENTS.md` as the
  lean public-safe router.
- Do not rewrite existing daily notes when changing the review process.

---

## Folder structure

```txt
Obsidian_tracker/
├── AGENTS.md
├── README.md
├── SYNC.md
├── .gitignore
├── .stignore
├── docs/
│   ├── obsidian-setup.md
│   └── week-review.md
├── goals/
│   └── goals.example.md
├── templates/
│   ├── daily-template.example.md
│   └── weekly-review-template.md
├── logs/
│   └── .gitkeep
├── reviews/
│   └── .gitkeep
├── rules/
│   └── habit-rules.example.md
└── examples/
    ├── daily-example.md
    └── W25-review-example.md
```

---

## Setup

Clone or download this repository, then create your private daily template:

```bash
cp templates/daily-template.example.md templates/daily-template.md
```

For optional habit scoring, also create private habit rules:

```bash
cp rules/habit-rules.example.md rules/habit-rules.md
```

For optional goal alignment, create a private goals file:

```bash
cp goals/goals.example.md goals/goals.md
```

Customize `templates/daily-template.md` locally. It is excluded from Git, so
personal habit names do not need to be published. Customize
`rules/habit-rules.md` with matching targets and weights; higher weights have
more influence on the overall habit percentage.

Open the repository folder as an Obsidian vault.

Your personal daily logs, generated weekly reviews, goals, habit configuration,
private daily template, idea notes, and local Obsidian workspace state are
excluded from Git by `.gitignore`.

The `logs/` and `reviews/` folders already exist in the repository because they
contain `.gitkeep` placeholder files.

---

## Daily workflow

Use Obsidian Daily Notes.

Recommended Obsidian settings:

```txt
Date format:
DD-MM-YYYY

Template file location:
templates/daily-template

New file location:
logs
```

Then use the Daily Notes button or hotkey to open today's note.

The first callout in the template contains one weekly experiment. Every
`week review` replaces it with the next experiment, so newly created daily
notes keep the current focus visible.

If `goals/goals.md` contains valid active goals, the private daily template also
shows an Active goals callout after the weekly experiment and before habits.
When there are no active goals, the callout is omitted.

No terminal script needed for daily notes.

---

## Optional habit tracking

The private daily template can contain:

- checkbox habits for yes/no results
- measured habits such as time or quantity
- limit habits where lower values meet the target

Optional targets and weights can be stored in the private
`rules/habit-rules.md` file. During `week review`, Codex can calculate each
habit's weekly result and one weighted overall percentage. Missing measured
values are excluded rather than counted as failures.

Habit progress is shown in the same weekly review as the output score, but it
does not alter that score.

For a daily uncomfortable action, add a yes/no habit such as `Hard thing` to the
private template and rules. During `week review`, it is scored as a habit. It
only affects the output score when the action also created real logged output.

---

## Optional goal alignment

Keep no more than three active goals in `goals/goals.md`. Exactly one active
goal must be primary, marked by `🔥` in the heading.

Use this simplified schema:

```md
## 🔥 G1: Meal-planning demo

- Status: 🟢 active
- Deadline: 31.7.2026
- Done when: A visitor can create and save a seven-day meal plan.
```

Valid statuses are `🟢 active`, `⏸️ paused`, and `✅ done`. Each weekly review
compares concrete logged output with active goal headings, completion
conditions, and deadlines. It does not automatically mark goals done.

Use an optional `[G1]`, `[G2]`, or `[G3]` marker in a daily output when its goal
would otherwise be ambiguous. Goal alignment reports `On track`, `At risk`,
`Off track`, or `No evidence`, plus one next step. It does not change output or
habit scores.

During `week review`, valid active goals are refreshed into the private daily
template so new daily notes show what matters. Only `🟢 active` goals appear,
and the callout is omitted when there are no active goals.

---

## Weekly workflow

From the `Obsidian_tracker/` folder, open Codex CLI and type:

```txt
week review
```

Expected result:

- read `AGENTS.md` and `docs/week-review.md`
- analyze the current/most recent week in `logs/`
- score each day **0–10**
- compare the average output score with the previous review
- show a dynamic improving/stable/mixed/declining verdict
- create a daily visual trend with score-label emojis
- include one short evidence reason for every daily score
- calculate optional habit progress
- evaluate optional active goals and deadline feasibility
- write a review to `reviews/YYYY/MM/WXX-review.md`
- summarize what moved, leaked time, and affected energy
- select one weekly experiment and update the private daily template

---

## Optional Android sync and mobile review

Because the system uses ordinary Markdown files, logs, templates, private
rules, and generated reviews can sync between a computer and Android through
Syncthing. Codex CLI can then run the same `week review` command from the
synced vault on the phone.

Keep device-specific `.obsidian/` state local, wait for Syncthing to show
**Up to Date**, and avoid editing the same note on two devices simultaneously.

See [SYNC.md](SYNC.md) for the full setup.

---

## Daily note priority

The most important sections are:

```txt
What moved the needle today?
Fake work / time leaks
One change for tomorrow
```

The Energy Log is useful but secondary.

Optional Context is only for relevant state changes like bad sleep, caffeine, hunger, messy room, stress, or flow.
