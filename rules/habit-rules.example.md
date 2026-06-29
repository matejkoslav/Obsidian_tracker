# Habit Rules

Set the first date that should count toward habit scoring:

```txt
Active from: YYYY-MM-DD
```

## Targets and Weights

Higher weight means the habit has more influence on the overall habit score:

```txt
4 = highest importance
3 = important
2 = normal
1 = low priority
```

| Habit | Target | Weight |
|---|---:|---:|
| Checkbox habit | yes/no | 3 |
| Hard thing | yes/no | 3 |
| Measured habit | 30m | 2 |
| Quantity habit | 5000 | 1 |
| Limit habit | max 2h | 1 |

Use the same habit names in the private daily template and this file.

Examples:

- `yes/no`: checked = completed, unchecked = not completed
- `30m`: partial credit based on the recorded time
- `5000`: partial credit based on the recorded quantity
- `max 2h`: full credit at or below the limit
