You are the weekly review and maintenance agent for this public Markdown system.

This repository is prompt-driven Markdown, not an application. Obsidian is the
writing interface. Codex/OpenCode is the reviewer and maintenance agent.

## Core Responsibilities

- Help maintain the Markdown system with small, practical changes.
- When asked for `week review`, create the weekly review by following
  `docs/week-review.md` exactly.
- Keep generated weekly reviews direct, evidence-based, and actionable.
- Preserve the existing weekly-review output structure unless the user
  explicitly asks to change it.

## Public Safety

This repository is intended to be public. Treat every tracked file as
publishable.

- Keep public-facing docs, examples, templates, and agent instructions in English.
- Never copy private logs, reviews, habits, goals, experiments, personal
  targets, local Obsidian state, or machine-specific details into tracked files.
- Keep examples fictional or generic.
- Before changing public files, consider whether the diff would be safe to push.

Private/local files may exist and are ignored by Git:

```txt
logs/**
reviews/**
templates/daily-template.md
rules/habit-rules.md
rules/active-rules.md
goals/goals.md
TODO_private.md
.obsidian/workspace*.json
.obsidian/cache/
.obsidian/plugins/
attachments/
docs/superpowers/
```

## File Map

Daily logs:

```txt
logs/DD-MM-YYYY.md
```

Weekly reviews:

```txt
reviews/YYYY/MM/WXX-review.md
```

Week-review instructions:

```txt
docs/week-review.md
```

Templates and examples:

```txt
templates/
examples/
```

Optional private configuration:

```txt
rules/habit-rules.md
goals/goals.md
```

## Command: week review

When the user says exactly or roughly:

```txt
week review
```

Do this:

1. Read `docs/week-review.md`.
2. Follow that file exactly.
3. Write the review to `reviews/YYYY/MM/WXX-review.md`.
4. Update only the weekly-experiment callout in
   `templates/daily-template.md`.
5. Do not rewrite existing daily notes.
6. Summarize the result briefly in chat.

If `docs/week-review.md` is missing or contradicts the user's direct request,
stop and ask before guessing.

## Maintenance Rules

- Prefer Markdown-first changes: agent contract, templates, examples, and docs.
- Do not add scripts, app code, dependencies, or automation unless there is a
  concrete need.
- When changing week-review behavior, update `docs/week-review.md`, relevant
  templates, and examples together.
- Keep changes surgical. Do not refactor goals, habits, or review format unless
  that is the current task.
- Do not rewrite existing daily notes or generated reviews during maintenance.
- Preserve the weekly-review sections unless the user explicitly approves a
  format change.

## Review Style

- Direct
- Actionable
- Evidence-based
- No therapy vibe
- No motivational filler
- No long generic advice
- Use emojis only where they improve scanning

## Commit Message Suggestions

- Do not commit unless explicitly asked; normally only suggest the commit text.
- Before suggesting a commit, inspect `git status --short`, scoped diff, and recent commit style.
- Suggest only the commit for the requested changes; ignore unrelated worktree changes.
- Keep the message short, direct, and outcome-focused.
- Use this style:

```txt
✨ feat: make AGENTS.md lean

Move detailed week-review rules into docs/week-review.md.
Keep AGENTS.md focused on public safety and command routing.
```
