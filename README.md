# MacroMod: Fabric Client-Side Macro Utility

MacroMod is a client-side automation utility for Minecraft Fabric, designed to assign scripting macros to keys, mouse clicks, event triggers, or draggable custom button overlays.

---

## ⚙️ Configuration & Opening the GUI

### How to Assign and Open the Configuration Screen
1. **Standard Controls Bind**:
   - Go to **Options** > **Controls** > **Key Binds** in Minecraft.
   - Scroll down to the **Misc** category.
   - **"MacroMod Configuration"**: Defaults to **`` ` ``** (Grave Accent / Backtick). Press **`` ` ``** in-game to open the configuration GUI.
   - **"MacroMod Overlay"**: Defaults to **`M`**. Press **`M`** in-game to open the transparent Custom Buttons Overlay screen.
   - **"Emergency Stop / Kill Switch"**: Defaults to **`K`**. Press **`K`** in-game to immediately halt all running scripts and release all held down keys (`keyAttack`, `keyLeft`, etc.).
2. **Default HUD Overlay Bind**:
   - By default, pressing **`M`** opens the transparent Custom Buttons Overlay screen (`{OPENOVERLAY}`).

### ⏳ Hold Down & Key Release (Up) Events
Inside the **Key Bindings** tab, selecting a key allows you to configure two separate macro actions in the right-hand editor panel:
* **On Press (Top field, labeled "Press:")**: Triggers exactly once when the key is pressed down. Pressing the key again while a script is running automatically toggles the script off and releases held keys.
* **On Release (Bottom field, labeled "Release:")**: Triggers exactly once when the key is released. This is perfect for toggle actions or stop triggers (e.g. running a walk command on press, and stopping on release).

---

## 📜 Macro Scripting Syntax

Script macros can execute chat messages, server commands, or simulated player actions. You can chain actions together in a single macro string by separating them with a vertical bar (`|`).

### 1. Chat Messages and Server Commands
* **Server Commands (Starts with `/`)**:
  - Sent directly to the server as a command without posting to chat.
  - *Example*: `/heal` or `/fix all`
* **Public Chat (Does NOT start with `/`)**:
  - Posted directly into standard public game chat.
  - *Example*: `Hello world!` or `/say Hello world!` (runs `/say` command on server).

### 2. Built-in Special Actions (Enclosed in `{}`)
* **`{WAIT ticks}`** / **`{WAIT min max}`** / **`{WAITRANDOM min max}`**: Inserts a tick delay before the next step. If min/max is passed, waits a random tick duration in that range.
  - *Example*: `/home | {WAIT 20 50} | /say I have arrived!`
* **`{LOOK yaw pitch}`**: Face a specific direction in-game.
  - *Example*: `{LOOK 90 0}` (looks east at the horizon).
* **`{LOOKRANGE minYaw maxYaw minPitch maxPitch}`**: Face a random direction bounded by min/max boundaries.
  - *Example*: `{LOOKRANGE 87 93 -3 3}` (random Yaw 87° to 93°, random Pitch -3° to +3°)
* **`{LOOKRANDOM centerYaw centerPitch yawSpread pitchSpread}`**: Face a random direction centered at target angle with ±spread variance. (Aliases: `{LOOKVARIANCE}`, `{LOOKSPREAD}`, `{LOOKOFFSET}`).
  - *Example*: `{LOOKRANDOM 90 0 3 3}` (center 90°, 0° with ±3° spread)
* **`{LEFTCLICK}`**: Simulates a left-click / attack once.
  - *Example*: `{LEFTCLICK} | {WAIT 10} | {LEFTCLICK}`
* **`{RIGHTCLICK}`**: Simulates a right-click / item-use once.
  - *Example*: `{RIGHTCLICK}`
* **`{SLOTCLICK slot [button] [type]}`**: Simulates clicking a specific container or inventory slot ID (see [SLOTMAP.md](file:///home/west/github.com/westkevin12/MacroMod/SLOTMAP.md) for full slot index diagrams).
  - *Example*: `{SLOTCLICK 0}` (left clicks slot 0), `{SLOTCLICK 0 1}` (right clicks slot 0), `{SLOTCLICK 0 0 QUICK_MOVE}` (shift clicks slot 0)
* **`{SHIFTCLICK slot}`**: Quick shortcut to shift-click an inventory or chest slot.
  - *Example*: `{SHIFTCLICK 0}`
* **`{ALLCLICK slot}`**: Quick shortcut to move or pickup all matching items of the same type across a container (Ctrl+Shift / Double click equivalent).
  - *Example*: `{ALLCLICK 0}`
* **`{OPENSCREEN}`**: Opens the configuration GUI screen.
* **`{OPENOVERLAY}`**: Opens the transparent HUD overlay containing your custom buttons.
* **`{STOP}`** / **`{STOP ALL}`** / **`{STOPALL}`**: Immediately stops script execution and releases all held down key states.
* **`{ECHO message}`**: Prints a local system message in the chat HUD that only you can see.
  - *Example*: `{ECHO Durability low!}`
* **`{SLOT slot}` / `{HOTBAR slot}`**: Instantly switches selected hotbar slot (0 to 8).
  - *Example*: `{SLOT 0}` (switches to slot 1).
* **`{LOOKAT x y z}`**: Automatically turns player camera to face specific 3D coordinates.
  - *Example*: `{LOOKAT 100 64 -200}`
* **`{DROP slot}`**: Drops the item in the specified inventory/container slot.
  - *Example*: `{DROP 0}`
* **`{DROPALL}`**: Drops all items across inventory slots.
* **`{CLOSESCREEN}`**: Closes the currently active GUI screen.
* **`{SNEAK}` / `{UNSNEAK}`**: Toggles sneaking state on or off.
* **`{SPRINT}` / `{UNSPRINT}`**: Toggles sprinting state on or off.
* **`{JUMP}`**: Simulates jumping once.
* **`{DISCONNECT}` / `{LOGOUT}`**: Disconnects from the current server or world.
* **`{CLEARCHAT}`**: Clears the client-side chat HUD.
* **`{KEYDOWN key}`**: Simulates holding down a keyboard key or mouse button. Supports keycode numbers (`65`), key names (`A`, `SPACE`, `SHIFT`), mouse codes (`-100`, `-101`, `-99`), or action aliases (`attack`, `use`, `jump`, `sneak`, `sprint`).
  - *Example (Hold Left Click 5s)*: `{KEYDOWN -100} | {WAIT 100} | {KEYUP -100}` or `{KEYDOWN attack} | {WAIT 100} | {KEYUP attack}`
  - *Example (Hold Sprint & Forward)*: `{KEYDOWN sprint} | {KEYDOWN w} | {WAIT 200} | {KEYUP w} | {KEYUP sprint}`
* **`{KEYUP key}`**: Simulates releasing a keyboard key or mouse button up.
* **`{KEY key}`**: Simulates a quick tap (press and release) of a keyboard key or mouse action.
  - *Example*: `{KEY space}` or `{KEY 32}` (taps jump).

* **`{STASHALL}`**: Automatically deposits all main player inventory items into an open container screen while protecting hotbar tools (pickaxes & shovels).
* **`{CHAT message}`**: Sends a chat message or server command (supports Minecraft color codes like `&a` -> `§a`).
* **`{INVENTORYGETSLOT item_id varName}`**: Returns the inventory slot index holding `item_id` into a variable.
* **`{GETHELDITEM varName}`**: Stores the held main-hand item name/identifier into a named variable.
* **`{GETCONTAINERTITLE varName}`**: Stores the title of the active container screen into a named variable.
* **`{SWAPSLOT fromSlot toSlot}`**: Swaps items between two inventory slot indexes.
* **`{READFILE "file.txt" arrayVar}`**: Reads lines from a file in `config/macromod/scripts/` into a named array variable.
* **`{ARRAYSIZE arrayVar targetVar}`**: Stores the number of elements in an array into a target variable.
* **`{GETARRAY arrayVar index targetVar}`**: Retrieves an element from an array by index into a target variable.
* **`{WAITUNTIL condition [timeout_ticks]}`**: Non-blocking yield that parks interpreter execution until condition evaluates to true or optional timeout (default 200 ticks) elapses.
  - *Example*: `{WAITUNTIL $containeropen == 1 100}`

### 3. Awaiting User Input (Placeholders)
You can pause macro execution to request input from the user:
* **`$$?`**: Opens a generic value entry box.
  - *Example*: `/pv $$?` (prompts the user, then runs `/pv 5` if they type `5`).
* **`$$[Prompt Label]`**: Opens an entry box showing a custom prompt message rendered as a text label directly above the input field.
  - *Example*: `/pv $$[Vault Number]` (displays "Vault Number:" above the text entry box).

### 4. Script Library & Subroutines (Long Complex Macros)
For longer, more complex automation, you can create multi-line scripts inside the **Scripts** tab:
* **Writing & Editing Scripts**:
  - Scripts are stored as `.mcm` files under `.minecraft/config/macromod/scripts/`.
  - Each statement is written on a new line (no vertical bars `|` needed).
  - Use the built-in **`▲` (Move Line Up)** and **`▼` (Move Line Down)** buttons in the editor panel to reorder lines effortlessly.
  - Comments can be written using `#`.
* **Flow Control**:
  - **`while condition` ... `endwhile`**: Loops as long as the condition evaluates to true.
  - **`for var start end` ... `endfor`**: Iterates a numeric variable from `start` to `end`.
  - **`break` / `continue`**: Exits loop early or skips directly to next iteration.
  - **`if condition` ... `else` ... `endif`**: Conditional execution block. Supports numeric comparisons and string equality comparisons (e.g. `if $dimension == "minecraft:the_nether"`).
  - **`set varName value` / `random varName min max` / `increment varName` / `decrement varName`**: Manage user-defined variables.
  - *Read-only player & world variables*: `$isburning`, `$onfire`, `$isswimming`, `$eyeheight`, `$health`, `$maxhealth`, `$xpos`, `$ypos`, `$zpos`, `$hunger`, `$invfull`, `$speed`, `$durability`, `$offhanddurability`, `$helmetdurability`, `$chestdurability`, `$leggingsdurability`, `$bootsdurability`, `$hasgui`, `$containeropen`, `$targettype` (0=Air, 1=Block, 2=Entity), `$targetdist`, `$targetx`, `$targety`, `$targetz`, `$targetname`, `$targethealth`, `$targetmaxhealth`, `$iteminhand`, `$freeinventory`, `$israining`, `$isnight`, `$blockbelow`, `$biome`, `$dimension`, `$server`, `$fps`, `$ping`, `$yaw`, `$pitch`, `$slot`, `$mainhand`, `$offhand`, `$time`, `$light`, `$level`, `$xp`, `$xpprogress`, `$xppercent`, `$air`, `$oxygen`, `$maxair`, `$saturation`, `$armor`, `$isthundering`, `$underwater`, `$onground`, `$isriding`, `$issneaking`, `$issprinting`, `$isflying`, `$facing`, `$direction`, `$gamemode`, `$difficulty`, `$weather`, `$playername`, `$username`, `$random` / `$rand`.
* **Calling Scripts (Subroutines)**:
  - **From an Inline Macro / Keybind**: Use `{CALL script_name}` (e.g. `{CALL test}`).
  - **From inside another script**: Use `call script_name` on its own line (e.g. `call test`).

---

## 🚨 Event Triggers
Configure auto-actions based on real-time game events under the **Event Triggers** tab:
1. **Durability**: Triggers when main-hand or off-hand item falls below `%` threshold (e.g., `/fix all`).
2. **Health**: Triggers when health falls below `%` threshold (e.g., `/heal`).
3. **Hunger**: Triggers when hunger level falls below threshold out of 20 (e.g., eating command).
4. **Inv Full**: Triggers when main inventory has no free slots.
5. **Item Pickup**: Triggers when a new item stack is picked up into inventory.
6. **Inventory Change**: Triggers on any inventory slot modification.
7. **World Load**: Triggers when loading into a world, server session, or changing dimension.
8. **Join Server**: Triggers once upon joining a world or server.
9. **On Death**: Triggers once upon dying.
10. **On Respawn**: Triggers once upon respawning after death.
11. **Dimension Change**: Triggers when changing dimensions (Overworld, Nether, End).
12. **Chat Filter**: Triggers when an incoming chat message matches a specified keyword/filter.
13. **Open GUI**: Triggers when opening any container/inventory screen.
14. **Close GUI**: Triggers when closing any container/inventory screen.
15. **On Damage**: Triggers whenever the player takes damage.
16. **Low Armor**: Triggers when any equipped piece of armor falls below durability threshold %.
17. **Low Air**: Triggers when player remaining air/oxygen % falls below threshold.
18. **Low XP**: Triggers when player experience level falls below threshold.

---

### 🛡️ Server Compliance & Permissions (`macromod:policy`)

MacroMod features built-in server compliance & policy controls:
* **Server-Safe Mode**: A user-configurable toggle in settings (`serverSafeMode`) that caps loop iterations, limits chat message rates, and disables movement/inventory automation when connected to public servers.
* **Universal Companion Plugin (`macromod-server`)**: Server admins can drop the companion plugin into Paper, Folia, Velocity, or BungeeCord servers to push granular policy flags (`macromod:policy` channel) to MacroMod clients upon join. See [macromod-server/README.md](file:///home/west/github.com/westkevin12/MacroMod/macromod-server/README.md) for full server plugin documentation.
* **LuckPerms Meta Keys**: Server admins running LuckPerms can configure per-player or per-group policy overrides using LuckPerms meta commands:
  - `/lp group default meta set macromod.allowMovement false`
  - `/lp user <player> meta set macromod.chatRateLimitMs 2000`
  - `/lp group vip meta set macromod.allowScriptFiles true`

### 🔒 Security & Anti-Cheat Advisory
* **Command Sandboxing**: MacroMod blocks script execution of dangerous Java prefixes (`java.`, `javax.`, `sun.`, `com.sun.`, `Runtime`, `ProcessBuilder`) to protect your local environment.
* **Fair Play Disclaimer**: Automated input macros may violate certain public server rules or trigger server-side anti-cheat mechanisms. Enable **Server-Safe Mode** in your client settings when playing on public servers. If you play on a server with macro restrictions, inform your server admin to install the **`macromod-server`** companion plugin so they can easily configure rate limits and granular feature permissions without banning macro utility mods.

---

## 🖱️ Custom Buttons & Draggable Overlay
* **Spawning Buttons**: In the **Custom Buttons** tab, click **`+ Add Button`** to spawn a new button.
* **Repositioning**: Left-click and drag buttons anywhere on the screen.
* **Editing & Deletion**: Left-click a button to open its configuration panel on the right. You can rename the button, edit its command, or click **`Delete Button`** to remove it.
* **HUD Overlay**: Open the clean overlay screen (`{OPENOVERLAY}`) to click your buttons during gameplay.

---

## 📌 Troubleshooting & Known Issues

For troubleshooting common issues, keybind conflicts, server policy flags, or reporting bugs for both the client mod and server companion plugin, please see [.github/ISSUES.md](file:///home/west/github.com/westkevin12/MacroMod/.github/ISSUES.md).

---

## 🛠️ Repository & Building

* **Server Companion Plugin Source**: The source code for `macromod-server` is open-source under [macromod-server/](file:///home/west/github.com/westkevin12/MacroMod/macromod-server/README.md).
* **Client Mod Releases**: Compiled Free JAR releases are stored in the [release/](file:///home/west/github.com/westkevin12/MacroMod/release) folder. Client mod source code (`src/`) and premium builds are private and gitignored.

### Build Server Plugin:
```bash
./gradlew :macromod-server:build
```
*Output*: `macromod-server/common/build/libs/` or `release/macromod-server-0.0.5.jar`

### Build Ecosystem Script:
```bash
./scripts/build.sh
```
*Output*: Builds all available ecosystem artifacts directly into the `release/` directory.



