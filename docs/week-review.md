# Week Review Instructions

Use this file when the user asks for `week review` or a close equivalent.

Your job is to read the user's daily notes, create a direct weekly review,
score each day, identify patterns, and recommend a small number of practical
changes.

Tone:

- Direct
- Actionable
- No therapy vibe
- No motivational filler
- No long generic advice
- Be evidence-based
- Use English for generated notes and reviews
- Use emojis for visual scanning, but do not overdo it

Do not add these sections:

- Projects
- Separate Blockers

Project/work tracking is handled only through:

- What moved the needle today?
- Fake work / time leaks
- One change for tomorrow

---

## File conventions

Daily logs:

```txt
logs/DD-MM-YYYY.md
```

Weekly reviews:

```txt
reviews/YYYY/MM/WXX-review.md
```

Optional private habit rules:

```txt
rules/habit-rules.md
```

Optional private goals:

```txt
goals/goals.md
```

Templates:

```txt
templates/
```

Examples:

```txt
examples/
```

---

## Command: week review

When the user says exactly or roughly:

```txt
week review
```

Interpret it as:

> Analyze the current or most recent week of daily notes, show the direction of
> progress, create a concise weekly review, and set one experiment for the next
> week.

Steps:

1. Detect current date and ISO week when possible.
2. Prefer the current ISO week if it contains daily notes.
3. If the current week has no notes, use the most recent week folder with daily notes.
4. Read all daily notes from that week.
5. Read the previous weekly review if available.
6. If `rules/habit-rules.md` exists, read it and score habits only for dates on
   or after its `Active from` date.
7. If `goals/goals.md` exists, read it and validate the active goals using the
   Goal Alignment rules below.
8. Score each day from **0-10** using the scoring rules below.
9. Calculate the current week's average output score.
10. Compare output and habits with the previous review when comparable data
   exists, then choose the weekly direction using the rules below.
11. Create a visual daily trend with a score-label emoji and one short evidence
   reason on every day.
12. If valid active goals exist, create a compact `Goal Alignment` section.
13. If habit rules are active for any reviewed day, create a compact habit
    progress table and weighted overall percentage.
14. Write the review to:

```txt
reviews/YYYY/MM/WXX-review.md
```

15. Include compact `What Mattered` and `Next Week` sections.
16. Select exactly one concrete `Test` experiment for the next week.
17. Replace the weekly-experiment callout at the top of the private
    `templates/daily-template.md` with that `Test`, then refresh the Active
    goals callout using the rules below. Do not rewrite existing daily notes.
    If the weekly-experiment callout is missing, add it at the top.
18. Include at most three bullets in `Context For Future AI Reviews`.
19. Apply any explicit dated cleanup instruction in `rules/habit-rules.md` when
    its stated review date has been reached.
20. After writing the file, summarize the result briefly in the terminal/chat.

If week/date is ambiguous, do not ask unless necessary. Choose the most recent week folder with notes.

---

## Daily Score 0-10

Score based on evidence in the daily note, not mood.

### Positive signals

| Evidence                                                                                        | Points |
| ----------------------------------------------------------------------------------------------- | -----: |
| 🚀 Needle-moving output: shipped, commit, PR, deploy, outreach, post, revenue, real deliverable |     +4 |
| ✅ Small real progress: useful concrete progress but not a major output                         |     +2 |
| 🧨 Fake work / time leak named honestly                                                         |     +1 |
| 🔁 One concrete change for tomorrow                                                             |     +2 |
| ⚡ Useful energy pattern captured with fix/insight                                              |     +1 |
| 🧪 Followed or validated the current weekly experiment                                          |     +2 |

Rules:

- Do not double count `Needle-moving output` and `Small real progress` for the same item.
- If there are multiple outputs, still keep the score capped at 10.
- Optional Context is not required and should not be penalized if empty.

### Negative signals

| Evidence                                                       | Points |
| -------------------------------------------------------------- | -----: |
| 🔴 Draining activity without fix                               |     -1 |
| 🧨 Repeated fake work/drain from previous days without change  |     -2 |
| 🌫️ Vague day: no output, no fake work, no concrete next change |     -1 |

Final score:

```txt
max(0, min(10, raw_score))
```

Use whole numbers only.

---

## Habit Progress

Habit tracking is optional and configured only through
`rules/habit-rules.md`. If that file does not exist, or no reviewed date is on
or after its `Active from` date, omit habit scoring.

Score only logged evidence:

- A present checked checkbox, `[x]`, scores `100%`.
- A present unchecked checkbox, `[ ]`, scores `0%`.
- For a positive numeric target, score
  `min(actual / target, 1) × 100`.
- For a maximum target, score `100%` when `actual <= target`; otherwise score
  `(target / actual) × 100`.
- A missing numeric value or absent habit line is missing data. Exclude it from
  both numerator and denominator; do not score it as zero.
- Accept clear equivalent time formats such as `90m`, `1 h 30m`, or
  `1h 30m`.
- Cap every result at `100%` and round displayed percentages to whole numbers.

For each habit, calculate its weekly result as the average of its recorded
daily percentages. For the overall weekly result, every recorded daily habit
result contributes:

```txt
result × habit weight
```

Divide the sum by the sum of the corresponding recorded weights. Do not
reweight missing data.

Habit results do not change the existing daily output score. They may inform
`Keep / Reduce / Change / Test` when supported by repeated evidence.

Use a 10-character bar for each displayed percentage. Fill approximately one
block per 10 percentage points:

```txt
70% = ███████░░░
```

---

## Goal Alignment

Goal alignment is optional and configured only through the private
`goals/goals.md` file. If the file does not exist, omit the section and omit
the Active goals callout from `templates/daily-template.md`.

Each goal is defined by its heading and three fields:

```md
## 🔥 G1: Compiler portfolio

- Status: 🟢 active
- Deadline: 2026-07-31
- Done when: Public demo exists and was sent to 3 people.
```

Use this schema exactly:

- The goal heading is `## {{goal_id}}: {{goal_name}}`.
- Exactly one active goal must be primary, marked by `🔥` before the goal ID in
  the heading.
- Valid statuses are `🟢 active`, `⏸️ paused`, and `✅ done`.
- `Deadline` must use `YYYY-MM-DD` format.
- `Done when` is the completion condition.
- Do not use the old separate ID, primary, outcome, or milestone fields.

Evaluate only goals with `Status: 🟢 active`. There may be no more than three
active goals, and exactly one active goal must include `🔥` in the heading. If
the configuration is invalid, do not guess, evaluate, or refresh the Active
goals callout; report the setup issue in the terminal/chat.

Refresh the private daily template after each weekly review:

1. Place the Active goals callout after the `This week` callout and before
   `## 📊 Habits`.
2. Show only goals with `Status: 🟢 active`.
3. Omit the callout when there are no active goals.
4. Keep at most three active goals.
5. Link to the goal heading in `goals/goals.md`.
6. Strip one trailing period from `Done when` before appending ` by YYYY-MM-DD`.
7. Include this instruction as the final callout line:
   If output supports goal, write `[G1]` ... in "What moved the needle today?"

Callout format:

```md
> [!goal] 🎯 Active goals
> 🔥 [[goals/goals#🔥 G1: Compiler portfolio|G1: Compiler portfolio]] — done when: Public demo exists and was sent to 3 people by 2026-07-31
> [[goals/goals#G2: Run 5 km|G2: Run 5 km]] — done when: Run 5 km under X by 2026-08-15
> If output supports goal, write `[G1]` ... in "What moved the needle today?"
```

Match concrete output from the reviewed daily notes to each active goal:

1. An explicit `[G1]`, `[G2]`, or `[G3]` marker links that output to the
   matching active goal and overrides automatic matching.
2. Otherwise, match only when the output clearly supports the goal heading or
   `Done when` condition.
3. Do not use plans, intentions, fake work, or unlogged assumptions as progress.
4. If the evidence is ambiguous or absent, use `⚪ No evidence`.

Assign exactly one status per active goal:

- `🟢 On track`: concrete progress supports the completion condition and the
  deadline still appears realistic.
- `🟠 At risk`: progress is weak, indirect, or insufficient for the remaining
  time.
- `🔴 Off track`: the deadline has passed without completion, or logged
  evidence shows the current plan is no longer realistic.
- `⚪ No evidence`: the notes do not contain enough evidence to judge.

For every active goal, output exactly:

```md
### 🔥 G1: Compiler portfolio

- **Status:** 🟢 On track
- **Evidence:** [one evidence-based sentence]
- **Next step:** [one concrete action]
```

Use `### {{fire_if_primary}}{{goal_id}}: {{goal_name}}` as the heading style.
Do not add old role or result labels to the heading. Keep the evidence to one
sentence and the next step to one action. Say `No evidence` instead of
inventing progress, reasons, or confidence.

If logged evidence shows the goal appears complete, do not edit
`goals/goals.md` automatically. Use this exact next step instead:

```md
- **Next step:** Mark G1 as ✅ done.
```

Goal alignment must not change the daily output score, output average, habit
results, or weekly direction calculation. Repeated goal misalignment may inform
one existing `Keep / Reduce / Change / Test` item, but must not create extra
experiments or recommendation sections.

---

## Score labels

```txt
8-10 = 🟢 strong
6-7  = 🟢 good
4-5  = 🟠 mixed
0-3  = 🔴 weak
```

---

## Weekly Direction

The current output value is the arithmetic mean of all scored days in the
reviewed week, shown with one decimal place on a `0-10` scale.

When comparable previous values exist, calculate:

```txt
output change = current output average - previous output average
habit change = current overall habit percentage - previous habit percentage
```

A meaningful output change is at least `0.5`. A meaningful habit change is at
least `5` percentage points:

```txt
output up     = change >= 0.5
output down   = change <= -0.5
output stable = -0.5 < change < 0.5

habits up     = change >= 5
habits down   = change <= -5
habits stable = -5 < change < 5
```

Choose one overall status:

- 🟢 `↗ IMPROVING`: at least one comparable track is up and none is down.
- 🔴 `↘ DECLINING`: at least one comparable track is down and none is up.
- 🟠 `↕ MIXED RESULT`: one comparable track is up and another is down.
- 🟡 `→ STABLE WEEK`: comparable tracks have no meaningful change.
- ⚪ `FIRST WEEK — BASELINE`: no comparable previous value exists.

If only output or only habits has comparable previous data, decide from that
track alone. Show `Baseline` instead of a previous value for a newly activated
track.

If an older previous review has no output summary, calculate its average from
the daily scores shown in that review. If a previous value cannot be recovered
from logged evidence, treat that track as baseline.

The verdict is one direct sentence explaining which track changed. Do not use
motivational filler.

Use a 10-character bar for the output average, rounded to the nearest filled
block:

```txt
7.1 / 10 = ███████░░░
```

---

## Daily Score Evidence

Each daily trend row must include one short evidence reason after the score
label. This makes scores auditable instead of black-box.

Use only logged evidence from that day's note. Prefer concrete evidence:
shipped output, named fake work/time leak, energy pattern, or tomorrow change.
Keep it to one short clause or compact comma list. If logged evidence is too
sparse, write `Not enough logged evidence`.

Example:

```md
Mon 6/10 ██████░░░░ 🟢 — shipped X, named Y leak, set Z change
```

---

## Visual trend format

Use this style:

```md
## 📈 Daily Score Trend

Mon 6/10 ██████░░░░ 🟢 — shipped first deliverable, named tool leak, set output-first change
Tue 7/10 ███████░░░ 🟢 — finished useful progress, reduced docs drift, set narrow next task
Wed 4/10 ████░░░░░░ 🟠 — small progress, no clear output, next change logged
Thu 8/10 ████████░░ 🟢 — shipped visible output, followed experiment, captured energy fix
Fri 7/10 ███████░░░ 🟢 — completed real progress, named time leak, set concrete follow-up
Sat 5/10 █████░░░░░ 🟠 — useful progress but weak output, fake work logged, next change set
Sun 6/10 ██████░░░░ 🟢 — finished weekly review, reduced tool tweaking, set next test

Trend: 6 → 7 → 4 → 8 → 7 → 5 → 6
```

Every day must include its score-label emoji and one short evidence reason.

---

## Weekly review structure

Create this structure. Use `templates/weekly-review-template.md` as the shape
reference, and keep this section in sync with that template when the format
changes.

```md
# 🧭 WEEKLY DIRECTION

WXX · YYYY-MM-DD to YYYY-MM-DD

## [dynamic status]

🚀 **Output**
[bar] **current / 10**
Previous week: [previous or Baseline]

📊 **Habits**
[bar] **current percentage or Not active**
Previous week: [previous or Baseline]

**Verdict:** [one direct sentence]

## 📈 Daily Score Trend

## 🧭 Goal Alignment

## 📊 Habit Progress

## 🔍 What Mattered

- 🚀 **Moved forward:**
- 🧨 **Time leak:**
- ⚡ **Energy:**

## 🎯 Next Week

- ✅ **Keep:**
- 🔻 **Reduce:**
- 🔁 **Change:**
- 🧪 **Test:**

## 🧠 Context For Future AI Reviews
```

---

## Review rules

Be specific.

Bad:

```txt
Focus more.
Waste less time.
Be consistent.
```

Good:

```txt
First 60 min = output only, no docs/tutorials/tools.
If docs task is vague, reduce it to 20 min and ship a rough version.
No setup/tool tweaking before one visible deliverable.
```

Prioritize:

1. output over effort
2. shipping over planning
3. one specific change over a long list
4. repeated patterns over one-off events
5. high-control changes over abstract advice

For `What Mattered`:

- Use at most one concise line for each item.
- Prefer repeated patterns and high-impact evidence over isolated details.
- `Energy` should state what repeatedly helped and/or drained energy when that
  evidence is useful for next week.
- If evidence is insufficient, write `Not enough logged evidence`.

For `Next Week`:

- Keep each item to one concrete action.
- `Test` is the only weekly experiment.
- Copy the exact `Test` text into the private daily-template callout.
- The experiment should be visible and controllable, not an abstract goal.

---

## Handling sparse notes

The user logs quickly and may leave sections empty.

Do not over-interpret.

Use wording like:

```txt
Based on logged evidence...
Not enough data to conclude...
Pattern appears 2x this week...
```

Do not invent reasons.
