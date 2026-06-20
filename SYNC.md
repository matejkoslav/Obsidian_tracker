# Android Sync and Mobile Week Review

> **Android only.** This setup syncs an Obsidian vault between a computer and
> Android, then runs Codex CLI on the phone so the agent can perform a
> `week review`.

## 1. Back up the vault

Before the first sync, create a full copy of the vault on the computer.

```bash
cp -a "/path/to/vault" "/path/to/vault-backup"
```

Close Obsidian on all devices during the initial sync.

## 2. Set up Syncthing

Install Syncthing on the computer. On Debian/Ubuntu-based Linux:

```bash
sudo apt update
sudo apt install syncthing
systemctl --user enable --now syncthing
xdg-open http://127.0.0.1:8384
```

On Android, install:

- Obsidian
- [Syncthing-Fork](https://github.com/researchxxl/syncthing-android)

The original official Syncthing Android app is deprecated, so this guide uses
the maintained community fork.

### Connect the devices

1. Add the computer and phone to each other using their Syncthing Device IDs.
2. On the computer, add the Obsidian vault as a folder.
3. Initially set the computer folder to **Send Only**.
4. Share the folder with the Android device.
5. On Android, accept it into an empty folder, for example:

   ```text
   /storage/emulated/0/Documents/Obsidian/MyVault
   ```

6. Initially set the Android folder to **Receive Only**.
7. Wait until both devices show **Up to Date**.
8. Change both folders to **Send & Receive**.
9. In Obsidian for Android, select **Open folder as vault** and choose the
   synced folder.

## 3. Ignore device-specific Obsidian settings

Create `.stignore` in the root of the synced vault on **each device**:

```gitignore
.git/
.obsidian/
.trash/
.DS_Store
Thumbs.db
desktop.ini
```

This syncs Markdown files and attachments while keeping Git metadata, plugins,
workspace layout, and device-specific Obsidian settings local. Syncthing does
not sync the `.stignore` file itself.

Enable **File Versioning → Trash Can File Versioning** for the folder and keep
deleted or replaced files for about 30 days.

## 4. Install Codex CLI on Android

Install [Termux](https://github.com/termux/termux-app) from F-Droid or its
official GitHub releases, not from the outdated Google Play build.

In Termux:

```bash
pkg update
pkg install proot-distro
termux-setup-storage
proot-distro install alpine
```

Enter Alpine with Android storage mounted:

```bash
proot-distro login alpine --bind /storage/emulated/0:/sdcard
```

Inside Alpine:

```bash
apk update
apk add nodejs npm git
npm install -g @openai/codex
codex
```

On the first run, sign in with a supported ChatGPT account or an API key.

## 5. Run the weekly review on Android

Enter Alpine:

```bash
proot-distro login alpine --bind /storage/emulated/0:/sdcard
```

Open the synced vault and start Codex:

```bash
cd "/sdcard/Documents/Obsidian/MyVault"
codex
```

Then tell the agent:

```text
week review
```

Codex reads the synced daily notes and project instructions, then writes the
weekly review back into the vault. Syncthing sends the result to the other
device.

Before editing on another device, always wait until Syncthing shows
**Up to Date**. Avoid editing the same note on two devices at the same time.

## Sources

- [Codex CLI](https://developers.openai.com/codex/cli)
- [PRoot-Distro](https://github.com/termux/proot-distro)
- [Syncthing: Ignoring Files](https://docs.syncthing.net/users/ignoring.html)
- [Syncthing: File Versioning](https://docs.syncthing.net/users/versioning.html)
