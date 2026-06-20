# Obsidian Setup

## 1. Create the private daily template

From the vault root:

```bash
cp templates/daily-template.example.md templates/daily-template.md
```

Customize `templates/daily-template.md` with the habits or prompts you want to
track. This personal file is excluded from Git.

## 2. Enable Daily Notes

```txt
Settings → Core plugins → Daily notes → ON
```

## 3. Configure Daily Notes

```txt
Settings → Daily notes
```

Use:

```txt
Date format:
logs/DD-MM-YYYY

Template file location:
templates/daily-template
```

## 4. Optional: set hotkey

```txt
Settings → Hotkeys → Daily notes: Open today's daily note
```

Example hotkey:

```txt
Ctrl + Alt + D
```

## 5. Templates plugin error

If Obsidian shows:

```txt
Failed to list templates. No template folder configured.
```

It means the Templates core plugin has no template folder set.

Fix:

```txt
Settings → Core plugins → Templates → Template folder location
```

Set it to:

```txt
templates
```

Daily Notes can still work without manually inserting templates, as long as the Daily Notes template location is set correctly.
