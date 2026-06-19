# Obsidian Setup

## 1. Enable Daily Notes

```txt
Settings → Core plugins → Daily notes → ON
```

## 2. Configure Daily Notes

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

## 3. Optional: set hotkey

```txt
Settings → Hotkeys → Daily notes: Open today's daily note
```

Example hotkey:

```txt
Ctrl + Alt + D
```

## 4. Templates plugin error

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
