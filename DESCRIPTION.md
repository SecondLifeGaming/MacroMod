# MC Macro Mod

**MC Macro Mod** is a highly versatile, premium client-side automation utility for Minecraft Fabric. Designed to put complete control in the hands of power users and administrators, it allows you to bind custom scripting macros to keys, mouse clicks, automated event triggers, and custom draggable buttons on a HUD overlay.

Whether you need simple shortcut commands, rapid keypress combinations, or complex multi-line logic with conditions and loops, MC Macro Mod provides a native, seamless, and high-performance execution engine directly in your Minecraft client.

---

## ⚙️ Core Features

* **Visual Key Bindings Visualizer**: A complete visual keyboard interface to easily click and bind commands or actions to any key, mouse button, or modifier combination.
* **Emergency Kill Switch (`K` Key)**: Instantly halts all active macros and releases all held key states (attacking, sneaking, movement, etc.).
* **On Press & On Release Bindings**: Bind distinct actions to a single key—one that triggers immediately when pressed down, and another that triggers when released (perfect for movement or toggle switches).
* **Automated Event Triggers**: Automatically execute macros in response to **18 live player events** — low durability (main-hand & off-hand), low health, low hunger, inventory full, item pickup, inventory slot change, world/dimension load, join server, death, respawn, dimension change, chat keyword match, GUI open/close, on damage taken, low armor, low air, and low XP.
* **Server Compliance & Permissions (`macromod:policy`)**: Built-in Server-Safe Mode toggle and custom payload integration with `macromod-server` companion plugin (with LuckPerms meta-key support `macromod.<flag>`) for server admin policy enforcement.
* **Draggable HUD Overlay**: Create custom, clickable button panels that can be dragged anywhere on the screen and triggered with a click (`M` key) during gameplay.
* **Multi-Line Scripting & Subroutines**: Write complex scripts with logic, loops (`break`/`continue`), custom variables, string equality conditions (`if $dimension == "minecraft:the_nether"`), line reordering (`▲`/`▼` buttons), and subroutine calls.
* **Interactive Prompt Placeholders**: Build macros that pause execution and prompt the user for input (`$$?` or `$$[Prompt Label]`) before running.

---

## 📜 Macro Scripting Syntax

Script macros can run server commands, chat messages, or simulate hardware actions. Chained commands are separated by `|` in inline macros, while script files run sequentially line-by-line.

### Special Actions
* **`{WAIT ticks}`** / **`{WAITRANDOM min max}`**: Delay macro execution (fixed or random tick duration).
* **`{LOOK yaw pitch}`**: Instantly face a specific direction.
* **`{LOOKRANGE minYaw maxYaw minPitch maxPitch}`**: Face a random direction bounded by min/max boundaries.
* **`{LOOKRANDOM centerYaw centerPitch yawSpread pitchSpread}`**: Face a random direction centered at target angle with ±spread variance. (Aliases: `{LOOKVARIANCE}`, `{LOOKSPREAD}`, `{LOOKOFFSET}`).
* **`{LEFTCLICK}` / `{RIGHTCLICK}`**: Simulates mouse clicks.
* **`{STASHALL}`**: Automatically deposits main player inventory items into open container/vault screens while protecting hotbar tools (pickaxes & shovels).
* **`{CHAT message}`**: Sends a chat message or server command (supports Minecraft color codes like `&a` -> `§a`).
* **`{INVENTORYGETSLOT item_id varName}`**: Returns slot index holding item.
* **`{GETHELDITEM varName}`**: Stores main-hand item name into variable.
* **`{GETCONTAINERTITLE varName}`**: Stores open screen title into variable.
* **`{SWAPSLOT from to}`**: Quick-swaps two inventory slot positions.
* **`{READFILE "file.txt" arrayVar}`**: Reads lines from a file in `config/macromod/scripts/` into a named array variable.
* **`{ARRAYSIZE arrayVar targetVar}`**: Stores array length into a target variable.
* **`{GETARRAY arrayVar index targetVar}`**: Retrieves an array element by index into a target variable.
* **`{KEY key}` / `{KEYDOWN key}` / `{KEYUP key}`**: Simulates tapping or holding keyboard keys **and mouse buttons**. Accepts GLFW key codes, key names (`A`, `SPACE`, `ENTER`), mouse codes (`-100`, `-101`), or action aliases (`attack`, `use`, `jump`, `sneak`, `sprint`, `forward`, `back`).
* **`{OPENSCREEN}` / `{OPENOVERLAY}`**: Opens the configuration GUI (default `` ` ``) or custom buttons overlay (default `M`).
* **`{STOP}` / `{STOP ALL}` / `{STOPALL}`**: Immediately stops script execution and releases all held keys.
* **`{ECHO message}`**: Prints a local client-only system message.
* **`{CALL script_name}`**: Invokes a multi-line script from the script library.

### User Input
* **`$$?`**: Prompts the user with a generic input field.
* **`$$[Prompt Label]`**: Prompts the user with a custom text label directly above the entry field (e.g. `$$[Vault Number]`).

---

## 🚨 Event Triggers

Automate your actions with real-time state tracking across **18 trigger types**:
1. **Durability**: Triggers when main-hand or off-hand item durability drops below a `%` threshold.
2. **Health**: Triggers when player health falls below a `%` threshold.
3. **Hunger**: Triggers when hunger level drops below a configurable level (out of 20).
4. **Inventory Full**: Triggers when the inventory has no empty slots remaining.
5. **Item Pickup**: Triggers when a new item stack is picked up.
6. **Inventory Change**: Triggers on any inventory slot modification.
7. **World Load**: Triggers when loading into a world, server session, or dimension re-entry.
8. **Join Server**: Runs a command or script automatically upon joining a world or server.
9. **On Death**: Runs a command or script immediately upon dying.
10. **On Respawn**: Triggers once upon respawning after death.
11. **Dimension Change**: Triggers when entering a new dimension (Overworld, Nether, End).
12. **Chat Filter**: Triggers when an incoming chat message matches a configurable keyword.
13. **Open GUI**: Triggers when a container or inventory screen is opened.
14. **Close GUI**: Triggers when a container or inventory screen is closed.
15. **On Damage**: Triggers whenever the player takes any damage.
16. **Low Armor**: Triggers when any equipped armor piece drops below a durability `%` threshold.
17. **Low Air**: Triggers when remaining air/oxygen drops below `%` threshold.
18. **Low XP**: Triggers when experience level drops below threshold.

---

## 📁 Script Library

Write `.mcm` script files in the **Scripts** tab to unlock advanced programmatic control:
* **While Loops & Break/Continue**: `while $health > 10` ... `break` ... `endwhile`
* **If/Else Conditionals**: `if $dimension == "minecraft:the_nether"` ... `else` ... `endif`
* **Custom & Built-in Variables**: Track user variables (`set`, `inc var [step]`, `dec var [step]`, `add`, `sub`, `mul`, `div`, `mod`, `random`) or read live player stats (`$isburning`, `$isswimming`, `$eyeheight`, `$xpos`, `$ypos`, `$zpos`, `$health`, `$maxhealth`, `$hunger`, `$level`, `$xp`, `$air`, `$facing`, `$gamemode`, `$durability`, `$offhanddurability`, etc.).
* **String & Array Functions**: `concat`, `substr`, `length`, `lower`, `upper`, `arraypush`, `arraypop`, `arrayclear`, `arraycontains`.

---

## 🔒 License & Usage

Copyright (c) 2026 westkevin12. All rights reserved. Licensed under a Commercial & Proprietary License. Unauthorized redistribution, modification for redistribution, or copying is strictly prohibited.
