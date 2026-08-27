# MacroModServer: Universal Companion Server Plugin

**MacroModServer** is the official server-side companion plugin for [MacroMod](file:///home/west/github.com/secondlifegaming/MacroMod/README.md). It provides server administrators with granular policy controls, rate limiting, and permission management for players running the MacroMod Fabric client mod.

---

## 🌟 Supported Server Platforms

MacroModServer is compiled as a multi-platform companion plugin supporting:
* **Paper / Folia / Purpur / Spigot** (1.20+)
* **Velocity Proxy** (3.x & 4.x)
* **BungeeCord / Waterfall Proxy**

---

## ⚙️ How It Works

Upon player login (or when `/macromod reload` is executed), `MacroModServer` sends a JSON policy payload over the custom plugin messaging channel **`macromod:policy`**.

When a MacroMod client connects:
1. The client receives the `macromod:policy` payload.
2. The client automatically enforces the server's policy rules (e.g. disabling movement macros, capping loop iterations, or imposing chat rate limits).
3. If no server plugin is present, the client follows its local settings or Server-Safe Mode configuration.

---

## 📜 Configuration (`config.yml`)

The plugin generates a `config.yml` file on first startup:

```yaml
version: 1

# General Macro Features
allowMovement: true              # Allow WASD / movement automation
allowLook: true                  # Allow camera turning automation ({LOOK}, {LOOKAT})
allowInventoryAutomation: true   # Allow container/inventory slot clicks and stashing
allowScriptFiles: true           # Allow running multi-line .mcm script files
allowLooping: true               # Allow while/for loops in scripts
maxLoopIterations: 0             # Max loop iterations allowed (0 = unlimited)

# Rate Limits (in milliseconds)
chatRateLimitMs: 0               # Minimum delay between chat messages (0 = disabled)
commandRateLimitMs: 0            # Minimum delay between command executions (0 = disabled)

# Event Trigger Master Toggle
allowEventTriggers: true         # Master switch for all client event triggers

# Granular Event Trigger Toggles
allowTriggerChat: true
allowTriggerJoinServer: true
allowTriggerDeath: true
allowTriggerRespawn: true
allowTriggerDimensionChange: true
allowTriggerContainerOpen: true
allowTriggerContainerClose: true
allowTriggerTakeDamage: true
allowTriggerLowHealth: true
allowTriggerLowHunger: true
allowTriggerInventoryFull: true
allowTriggerLowDurability: true
allowTriggerLowArmor: true
allowTriggerLowAir: true
allowTriggerLowXp: true
```

---

## 🔑 LuckPerms Integration (Per-Group / Per-Player Overrides)

If [LuckPerms](https://luckperms.net/) is installed on your server, `MacroModServer` automatically checks for LuckPerms metadata nodes. Metadata values override global `config.yml` settings on a per-player or per-group basis!

### Supported LuckPerms Meta Keys:
* `macromod.allowMovement` (`true`/`false`)
* `macromod.allowLook` (`true`/`false`)
* `macromod.allowInventoryAutomation` (`true`/`false`)
* `macromod.allowScriptFiles` (`true`/`false`)
* `macromod.allowLooping` (`true`/`false`)
* `macromod.maxLoopIterations` (integer, e.g. `100`)
* `macromod.chatRateLimitMs` (integer ms, e.g. `1000`)
* `macromod.commandRateLimitMs` (integer ms, e.g. `500`)
* `macromod.allowEventTriggers` (`true`/`false`)

### Example Commands:

```bash
# Disable movement automation for default players
/lp group default meta set macromod.allowMovement false

# Enforce a 2-second chat rate limit for default players
/lp group default meta set macromod.chatRateLimitMs 2000

# Allow full script execution for VIP group
/lp group vip meta set macromod.allowScriptFiles true
/lp group vip meta set macromod.allowMovement true
```

---

## 💻 Commands & Permissions

| Command | Permission | Description |
| :--- | :--- | :--- |
| `/macromod reload` | `macromod.admin` (or OP) | Reloads `config.yml` and pushes updated policy payloads to all online clients. |

---

## 🛠️ Compiling from Source

To compile the `macromod-server` plugin JAR:

```bash
# From the root repository directory
./gradlew :macromod-server:build
```

The compiled plugin JAR will be created in `macromod-server/common/build/libs/` and pre-built binaries are available under `release/macromod-server-*.jar`.
