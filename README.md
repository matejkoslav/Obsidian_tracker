# Obsidian_tracker

Markdown/Obsidian system for quick daily logging and weekly Codex CLI reviews.

## Core idea

Daily: spend **1–5 min** logging only the notable things you remember.

Weekly: open Codex CLI in this folder and type:

```txt
week review
```

Codex reads the current week, scores each day **0–10**, creates a visual trend, extracts patterns, evaluates rules, and writes a weekly review.

---

## Folder structure

```txt
Obsidian_tracker/
├── AGENTS.md
├── README.md
├── .gitignore
├── docs/
│   └── obsidian-setup.md
├── templates/
│   ├── daily-template.md
│   └── weekly-review-template.md
├── logs/
│   └── .gitkeep
├── reviews/
│   └── .gitkeep
├── rules/
│   └── active-rules.example.md
└── examples/
    ├── daily-example.md
    └── W25-review-example.md
```

---

## Setup

Clone or download this repository, then create your private rules file:

```bash
cp rules/active-rules.example.md rules/active-rules.md
```

Open the repository folder as an Obsidian vault.

Your personal daily logs, generated weekly reviews, active rules, and local
Obsidian workspace state are excluded from Git by `.gitignore`.

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

No terminal script needed for daily notes.

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
- create a visual trend
- write a review to `reviews/YYYY/MM/WXX-review.md`
- evaluate `rules/active-rules.md`
- propose exactly **3 rules** for next week

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
