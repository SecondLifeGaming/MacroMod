# MC Macro Mod - Release Notes

## Version 0.0.6

### 🚀 Package Migration, Type-Safe Scripting Engine & Library Overhaul

- **Package Namespace Migration**:
  - Migrated all Java classes, plugin descriptors (`fabric.mod.json`, `plugin.yml`, `paper-plugin.yml`, `bungee.yml`, `velocity-plugin.json`), and build configurations from `com.github.westkevin12.macromod` to `com.github.secondlifegaming.macromod`.
- **Type-Safe Variable Engine (`ScriptValue`)**:
  - Replaced separate disconnected variable maps (`userVars`, `userStringVars`, `userArrays`) with a unified, type-safe `ScriptValue` model (`NUMBER`, `STRING`, `BOOLEAN`, `ARRAY`), eliminating variable shadowing bugs.
- **Parameterized `INC` & `DEC`**:
  - Updated `inc` and `dec` parsing and execution to support optional custom step values: `inc counter 5`, `inc counter $step`, `dec health 10`.
- **Native Arithmetic Instructions**:
  - Added native math operations: `add <var> <val>`, `sub` / `subtract`, `mul` / `multiply`, `div` / `divide`, `mod`.
- **String & Array Utility Functions**:
  - Added string statements: `concat(target, s1, s2)`, `substr(var, start, len)`, `length(target, var)`, `lower(var)`, `upper(var)`.
  - Added array management: `arraypush(arr, val)`, `arraypop(arr, [target])`, `arrayclear(arr)`, `arraycontains(arr, val, target)`.

---

## Version 0.0.5

> [!NOTE]
> **Status:** Version 0.0.5 marks the final alpha development milestone. The plugin and companion server ecosystem are fully feature-complete and feature-locked, preparing for upcoming public Beta releases.

### 🚀 Enhancements, Inventory Automation & Engine Lifecycle Stability

- **Automated Container Stashing**:
  - Introduced `{STASHALL}` keyword: automatically deposits main player inventory items into open container/vault screens while protecting hotbar tools (pickaxes & shovels).
- **Screen Lifecycle & Container Sync Fixes**:
  - Refactored `{CLOSESCREEN}` to call `Screen.onClose()` on the active GUI screen, properly triggering screen cleanup handlers and dispatching container close packets.
  - Resolved container desynchronization and screen overlay lockups when running automated container sequences (such as `/pv` vault macros).
  - Pinned outbound commands (`sendCommand`) and chat messages (`sendChat`) to Minecraft's main client execution thread (`client.execute`).
  - Added single-slot click actions `{SLOTCLICK}`, `{SHIFTCLICK}`, `{CTRLSHIFTCLICK}`, `{QUICKMOVE}`, `{QUICKMOVEALL slot}`, and `{CONTAINERCLICK}` for granular container manipulation.
- **Hotbar Slot Packet Synchronization**:
  - Updated `{SLOT 1..9}` to convert 1-based hotbar numbers (1..9) to 0-based indices (0..8) and send explicit `ServerboundSetCarriedItemPacket` network packets to synchronize selected hotbar slots with the server.
- **File Array Operations & Chat Formatting**:
  - Introduced `{READFILE "file.txt" arrayVar}`, `{ARRAYSIZE arrayVar targetVar}`, and `{GETARRAY arrayVar index targetVar}` for reading and indexing text file arrays from `.mcm` scripts.
  - Added `{CHAT message}` command with color section sign translation (`&a` -> `§a`) for rich chat logging and command dispatching.

---

## Version 0.0.4

### 🚀 Enhancements, Security & Server Policy Integration

- **Security & Sandbox Hardening**:
  - Audit and enforcement blocking user-supplied strings starting with dangerous reflection/process lookup prefixes (`java.`, `javax.`, `sun.`, `com.sun.`, `Runtime`, `ProcessBuilder`).
  - Added `Throwable` catch wrapping in `ScriptInterpreter.tick()` to gracefully isolate failing script instances without crashing the client tick loop.
  - Added friendly syntax error handling during script parsing.

- **New Event Triggers & Movement Options**:
  - Implemented `onItemPickup` and `onInventoryChange` triggers via `InventoryMixin`.
  - Implemented `onWorldLoad` trigger via `WorldLoadMixin`.
  - Extended durability threshold checking to cover off-hand items.
  - Implemented script execution pause/resume suspension for priority event triggers.

- **Script Engine API Extensions & DSL Enhancements**:
  - Added read-only getters for `$isburning`, `$isswimming`, `$eyeheight`.
  - Added inventory query actions: `{INVENTORYGETSLOT}`, `{GETHELDITEM}`, `{GETCONTAINERTITLE}`, `{SWAPSLOT}`.
  - Added `break` and `continue` keyword support for `while` and `for` loops.
  - Enhanced condition evaluation to support string equality comparisons (e.g. `if $dimension == "minecraft:the_nether"`).

- **Companion Server Plugin & Network Enforcement**:
  - Created `ServerPermissionHandler` custom payload network listener for `macromod:policy` packets.
  - Initialized `macromod-server` companion server multi-module plugin for Paper, Folia, Velocity, and BungeeCord.
  - Added LuckPerms meta-key integration (`macromod.<flag>`) allowing per-player or per-group policy overrides.
  - Implemented unmanaged server discovery ping (`/macromod-info`).

- **Build Pipeline & Version Bumping**:
  - Multi-artifact automated build pipeline (`scripts/build.sh`) producing `macromod-0.0.4-free.jar`, `macromod-0.0.4-premium.jar`, and `macromod-server-0.0.4.jar`.
  - Automated version bump script (`scripts/bump-version.sh`) and un-gated update checker for all users.

---
