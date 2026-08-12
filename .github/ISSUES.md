# 📌 MacroMod & MacroModServer Issue Guide

This document outlines common troubleshooting steps, known limitations, and guidelines for opening issues for both the **MacroMod Client Mod** and the **MacroModServer Companion Plugin**.

---

## 🛠️ Client Mod Issues (`MacroMod`)

### 1. Keybind Conflicts or GUI Not Opening
* **Symptom**: Pressing `` ` `` (Backtick) or `M` does not open the configuration menu or overlay.
* **Resolution**:
  1. Check **Options** > **Controls** > **Key Binds** > **Misc**.
  2. Ensure **MacroMod Configuration** and **MacroMod Overlay** are assigned to valid, non-conflicting keys.
  3. If another mod overrides backtick, rebind MacroMod to a different key (e.g. `HOME` or `F6`).

### 2. Emergency Stop / Script Halts Unexpectedly
* **Symptom**: Macro script stops mid-execution or held keys are released.
* **Resolution**:
  - The default Emergency Stop / Kill Switch key is **`K`**. Pressing `K` instantly terminates active interpreter threads.
  - Pressing a keybind again while its macro is running toggles script execution off.
  - Verify if a `{STOP}` directive or server rate limit was encountered.

### 3. Server Restrictions & Policy Enforcements
* **Symptom**: Movement actions (`{KEYDOWN w}`, `{LOOKAT}`), script loops, or event triggers do not run when connected to a multiplayer server.
* **Resolution**:
  - Check if **Server-Safe Mode** is enabled in client settings (`serverSafeMode = true`).
  - If the server has installed the **`macromod-server`** companion plugin, the server enforces active policies via the `macromod:policy` channel.
  - Contact the server administrator to adjust policy settings or LuckPerms metadata nodes for your player group.

---

## 🖥️ Server Companion Plugin Issues (`MacroModServer`)

### 1. Policy Payloads Not Pushing to Clients
* **Symptom**: Player joins the server, but policy rules from `config.yml` are not enforced on their client.
* **Resolution**:
  - Verify `MacroModServer` is loaded in `/plugins` (run `/plugins` on Spigot/Paper or proxy command).
  - Ensure plugin channel messaging (`macromod:policy`) is not blocked by custom proxy network firewalls.
  - Run `/macromod reload` in server console or as OP to re-broadcast policy payloads to online players.

### 2. LuckPerms Metadata Overrides Not Working
* **Symptom**: Setting `/lp group default meta set macromod.allowMovement false` does not restrict players.
* **Resolution**:
  - Confirm [LuckPerms](https://luckperms.net/) is installed and active on the server.
  - Ensure metadata key names are exact (case-sensitive):
    - `macromod.allowMovement`
    - `macromod.allowLook`
    - `macromod.allowInventoryAutomation`
    - `macromod.allowScriptFiles`
    - `macromod.allowLooping`
    - `macromod.maxLoopIterations`
    - `macromod.chatRateLimitMs`
    - `macromod.commandRateLimitMs`
    - `macromod.allowEventTriggers`
  - Re-log player or run `/macromod reload` after changing LuckPerms meta nodes.

---

## 📋 Opening a GitHub Issue

Before opening an issue on GitHub, please check the following:
1. **Search existing issues** to ensure the bug or feature hasn't already been reported.
2. **Identify component**: Specify whether your issue pertains to the **Client Mod** or **Server Plugin (`macromod-server`)**.
3. **Include environment details**:
   - Minecraft version (e.g., 1.20.4, 1.21.x)
   - Mod Loader version (Fabric Loader)
   - Server platform (Paper, Velocity, BungeeCord, Singleplayer)
   - MacroMod version (e.g. `0.0.5`)
4. **Provide reproduction steps**: Clear instructions or `.mcm` script snippets that reproduce the problem.
