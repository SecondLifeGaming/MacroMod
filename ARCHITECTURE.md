# MacroMod — System Architecture

## 1. Deliverables Overview

MacroMod ships as three independently installable components. Users and server admins install only
what applies to their setup.

```
┌─────────────────────────────────────────────────────────────────┐
│                        MacroMod Ecosystem                       │
│                                                                 │
│  ┌───────────────────────┐     ┌──────────────────────────────┐ │
│  │  macromod (client)    │     │  macromod-server             │ │
│  │  Fabric client mod    │◄────│  Universal server companion  │ │
│  │  Free + Premium tiers │     │  (Paper / Folia / Velocity / │ │
│  │                       │     │   BungeeCord / Waterfall)    │ │
│  └───────────────────────┘     └──────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Component 1 — MacroMod Client (Fabric)

**Repo:** `SecondLifeGaming/MacroMod`
**Platform:** Fabric (client-side only)
**Publish:** Modrinth, CurseForge

### Tier Model

| Feature | Free | Premium |
|---|---|---|
| Inline pipe-separated macros | ✅ | ✅ |
| Keybind assignment | ✅ | ✅ |
| Config GUI (`MacroConfigScreen`) | ✅ | ✅ |
| HUD overlay | ✅ | ✅ |
| Update checker | ✅ | ✅ |
| All event triggers (health, hunger, etc.) | ❌ | ✅ |
| Multi-line `.mcm` script files | ❌ | ✅ |
| `call <script>` subroutine | ❌ | ✅ |
| Chat trigger (`onChat` filter → macro) | ❌ | ✅ |

### Source Structure

```
src/main/java/com/github/westkevin12/macromod/
├── MacroModClient.java          ← ClientModInitializer, tick loop, event threshold checks
├── engine/
│   ├── MacroEngine.java         ← Active script manager, tick dispatcher
│   ├── ScriptInterpreter.java   ← Line-by-line DSL executor
│   └── ScriptLibrary.java       ← .mcm file I/O from config/macromod/scripts/
├── gui/
│   ├── MacroConfigScreen.java   ← Main configuration UI
│   ├── MacroOverlayScreen.java  ← In-game HUD overlay
│   ├── MacroPromptScreen.java   ← $$ prompt input screen
│   └── ModMenuIntegration.java  ← Mod Menu API entry point
├── mixin/
│   ├── ChatMixin.java           ← Intercepts incoming chat for onChat trigger
│   ├── KeyboardMixin.java       ← Intercepts key press events for keybind macros
│   ├── MouseMixin.java          ← Intercepts mouse events
│   ├── KeyboardHandlerAccessor.java
│   └── MinecraftAccessor.java
├── config/
│   ├── MacroConfig.java         ← Config POJO (all settings fields)
│   └── MacroConfigManager.java  ← Load/save JSON config from disk
└── network/
    └── ServerPermissionHandler.java   ← [PLANNED] Receives macromod:policy payload
```

### Client-Side Compliance Layers

1. **Server-Safe Mode** (`MacroConfig.serverSafeMode`) — user-toggled, zero server cooperation
   required. When on a public multiplayer server, restricts movement, look, inventory automation,
   loop iterations, and chat rate client-side.

2. **Server Policy** (`ServerPermissionHandler`) — receives a `macromod:policy` JSON payload from
   a companion server plugin on join/server-switch. Granular per-feature flags override defaults
   for the session. Resets to unrestricted on disconnect.

---

## 3. Component 2 — macromod-server (Universal Companion Server Plugin)

**Repo:** `SecondLifeGaming/macromod-server` (separate repo, Gradle multi-module shaded JAR)
**Platforms:** Paper / Spigot / Purpur / Folia / Velocity 3.x / BungeeCord / Waterfall
**Publish:** Modrinth, Hangar
**Jar:** `macromod-server-<ver>.jar`

### Universal Multi-Platform Design

`macromod-server-<ver>.jar` is a single unified JAR that can be dropped into the `plugins/` directory of **any** supported server or proxy:
* **Paper / Spigot / Purpur / Folia (Bukkit API)** — via `plugin.yml` descriptor
* **Velocity 3.x** — via `velocity-plugin.json` descriptor
* **BungeeCord / Waterfall** — via `bungee.yml` / `plugin.yml` descriptor

When loaded by a server engine, the platform reads its specific descriptor, initializes its platform-specific entry point, and shares the core policy serialization and configuration handling logic (`common`).

### Source Structure

```
macromod-server/
├── common/
│   ├── ServerPolicy.java        ← POJO: flag fields with defaults (permissive)
│   ├── PolicyCodec.java         ← Gson JSON serialize/deserialize
│   └── PolicyFlags.java         ← String key constants
├── paper/                       ← Paper / Spigot / Purpur / Folia integration
│   ├── MacroModPaperPlugin.java ← Main Bukkit plugin class (extends JavaPlugin)
│   ├── PolicySender.java        ← Plugin message dispatcher
│   ├── FoliaScheduler.java      ← Folia-safe scheduler wrapper
│   └── LuckPermsHook.java       ← Optional LuckPerms meta key reader
├── proxy/
│   ├── velocity/                ← Velocity integration
│   │   ├── MacroModVelocityPlugin.java ← @Plugin entry point
│   │   └── PolicySender.java
│   └── bungeecord/              ← BungeeCord / Waterfall integration
│       ├── MacroModBungeePlugin.java   ← Main plugin class (extends Plugin)
│       └── PolicySender.java
└── resources/
    ├── plugin.yml               ← Spigot / Paper / BungeeCord plugin descriptor
    ├── velocity-plugin.json     ← Velocity plugin descriptor
    └── config.yml               ← Unified server configuration template
```

### Folia & Proxy Compatibility Notes
* **Folia**: When `Bukkit.isFolia()` is true, plugin messaging dispatches via `GlobalRegionScheduler` / `RegionScheduler`.
* **Proxies (Velocity & BungeeCord)**: Listens to `ServerConnectedEvent` to push policy payloads on initial connect and every mid-session server switch. Supports optional `servers:` overrides in `config.yml`.

### LuckPerms Integration

If LuckPerms is present, per-player or per-group meta keys override `config.yml` global defaults:

```yaml
# LuckPerms meta key format:
# /lp group <group> meta set macromod.allowMovement false
# /lp user <player> meta set macromod.chatRateLimitMs 2000
```

### config.yml Structure

```yaml
# MacroMod Server Companion — config.yml
# All fields default to permissive (true / 0). Set to restrict.

default-policy:
  allowMovement: true
  allowLook: true
  allowInventoryAutomation: true
  allowScriptFiles: true
  allowLooping: true
  maxLoopIterations: 0        # 0 = unlimited
  chatRateLimitMs: 0          # 0 = no limit
  commandRateLimitMs: 0

  # Per-event-trigger flags
  allowEventTriggers: true    # master toggle
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

# Optional per-backend server overrides (when running on Velocity or BungeeCord proxies)
# servers:
#   pvp-1:
#     allowMovement: false
#     chatRateLimitMs: 2000
#   creative:
#     allowInventoryAutomation: false
```

---

## 5. Communication Protocol

### Channel

```
macromod:policy
```

Direction: Server → Client (plugin message / custom payload).

### Payload

A UTF-8 JSON string. The client reads the payload using Fabric's `CustomPayload` API and a
`PacketCodec` wrapping a `PacketCodecs.STRING`.

### Versioning

The `"version"` integer field allows the client to handle future additions gracefully:
- **Missing fields** → client applies unrestricted default (`true` / `0`).
- **Unknown fields** → ignored silently.
- **Version mismatch** → client logs a warning but still applies all recognized fields.

### Full Flag Reference

| JSON Key | Type | Default | Description |
|---|---|---|---|
| `version` | int | — | Payload schema version |
| `allowMovement` | bool | `true` | `SNEAK`, `UNSNEAK`, `SPRINT`, `UNSPRINT`, `JUMP` |
| `allowLook` | bool | `true` | `LOOK`, `LOOKAT`, `LOOKRANGE`, `LOOKRANDOM` |
| `allowInventoryAutomation` | bool | `true` | `SLOTCLICK`, `SHIFTCLICK`, `ALLCLICK`, `DROP`, `DROPALL`, `SWAPSLOT` |
| `allowScriptFiles` | bool | `true` | Loading and executing `.mcm` script files |
| `allowLooping` | bool | `true` | `while` / `for` loop constructs |
| `maxLoopIterations` | int | `0` | Max loop iterations; `0` = unlimited |
| `chatRateLimitMs` | int | `0` | Min ms between chat messages; `0` = no limit |
| `commandRateLimitMs` | int | `0` | Min ms between `/commands`; `0` = no limit |
| `allowEventTriggers` | bool | `true` | Master toggle for all event triggers below |
| `allowTriggerChat` | bool | `true` | `onChat` filter trigger |
| `allowTriggerJoinServer` | bool | `true` | `onJoinServer` trigger |
| `allowTriggerDeath` | bool | `true` | `onDeath` trigger |
| `allowTriggerRespawn` | bool | `true` | `onRespawn` trigger |
| `allowTriggerDimensionChange` | bool | `true` | `onDimensionChange` trigger |
| `allowTriggerContainerOpen` | bool | `true` | `onContainerOpen` trigger |
| `allowTriggerContainerClose` | bool | `true` | `onContainerClose` trigger |
| `allowTriggerTakeDamage` | bool | `true` | `onTakeDamage` trigger |
| `allowTriggerLowHealth` | bool | `true` | `onLowHealth` trigger |
| `allowTriggerLowHunger` | bool | `true` | `onLowHunger` trigger |
| `allowTriggerInventoryFull` | bool | `true` | `onInventoryFull` trigger |
| `allowTriggerLowDurability` | bool | `true` | `onLowDurability` trigger |
| `allowTriggerLowArmor` | bool | `true` | `onLowArmor` trigger |
| `allowTriggerLowAir` | bool | `true` | `onLowAir` trigger |
| `allowTriggerLowXp` | bool | `true` | `onLowXp` trigger |

---

## 6. Repository Layout

```
SecondLifeGaming/MacroMod              ← Fabric client mod (this repo)
SecondLifeGaming/MacroMod-Website      ← Documentation website
SecondLifeGaming/macromod-server       ← Server companion plugins (new repo)
    ├── common/
    ├── paper/
    └── proxy/
        ├── velocity/
        └── bungeecord/
```


---

## 7. Release Matrix

| Artifact | Platform | Registry |
|---|---|---|
| `macromod-<ver>-free.jar` | Fabric (client) | Modrinth, CurseForge |
| `macromod-<ver>-premium.jar` | Fabric (client) | mc-macro-mod.web.app |
| `macromod-server-<ver>.jar` | Universal (Paper/Spigot/Folia/Velocity/Bungee) | Modrinth, Hangar |

---

## 8. Which Plugin Should Server Admins Install?

| My setup | Install |
|---|---|
| Any Paper / Spigot / Purpur / Folia backend server | `macromod-server-<ver>.jar` in server `/plugins` |
| Any Velocity / BungeeCord / Waterfall proxy network | `macromod-server-<ver>.jar` in proxy `/plugins` |
| Proxy network with per-backend policies | `macromod-server-<ver>.jar` on proxy (use `servers:` map in `config.yml`) |
| No server-side control needed | Nothing — client mod works standalone |
