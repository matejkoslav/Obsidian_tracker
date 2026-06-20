# Obsidian_tracker

Markdown/Obsidian system for quick daily logging and weekly Codex CLI reviews.

## Core idea

Daily: spend **1–5 min** logging only the notable things you remember.

Weekly: open Codex CLI in this folder and type:

```txt
week review
```

Codex reads the current week, scores each day **0–10**, calculates optional
habit progress, shows whether the week is improving or declining, and writes
one experiment into the daily template for the next week.

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
│   └── obsidian-setup.md
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

Customize `templates/daily-template.md` locally. It is excluded from Git, so
personal habit names do not need to be published. Customize
`rules/habit-rules.md` with matching targets and weights; higher weights have
more influence on the overall habit percentage.

Open the repository folder as an Obsidian vault.

Your personal daily logs, generated weekly reviews, habit configuration,
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

---

## Weekly workflow

From the `Obsidian_tracker/` folder, open Codex CLI and type:

```txt
week review
```

Expected result:

- read `AGENTS.md`
- analyze the current/most recent week in `logs/`
- score each day **0–10**
- compare the average output score with the previous review
- show a dynamic improving/stable/mixed/declining verdict
- create a daily visual trend with score-label emojis
- calculate optional habit progress
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
