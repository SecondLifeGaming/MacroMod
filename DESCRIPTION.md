# MC Macro Mod

**MC Macro Mod** is a client-side command shortcut and keybinding utility for Minecraft Fabric. Designed for convenience and productivity, it allows you to bind custom server commands, chat shortcuts, and inline macro sequences to keys, mouse clicks, or custom draggable HUD overlay buttons.

Whether you need quick shortcut commands (`/home`, `/warp`, `/pay`), interactive user prompts (`$$?`), or custom HUD action panels, MC Macro Mod provides a native execution engine directly in your Minecraft client.

---

## Free Edition Features

- **Visual Key Bindings Visualizer**: A complete visual keyboard interface to easily click and bind commands or actions to any key, mouse button, or modifier combination.
- **Inline Macro Execution**: Bind chained actions or commands separated by `|` to any hotkey or mouse button.
- **Emergency Kill Switch (`K` Key)**: Instantly halts all active macros and releases all held key states (attacking, sneaking, movement, etc.).
- **On Press & On Release Bindings**: Bind distinct actions to a single key—one that triggers immediately when pressed down, and another that triggers when released.
- **Draggable HUD Overlay**: Create custom, clickable button panels that can be dragged anywhere on the screen and triggered with a click (`M` key) during gameplay.
- **Interactive Prompt Placeholders**: Build macros that pause execution and prompt the user for input (`$$?` or `$$[Prompt Label]`) before running.
- **Server Compliance & Permissions (`macromod:policy`)**: Built-in Server-Safe Mode toggle and custom payload integration with `macromod-server` companion plugin (with LuckPerms meta-key support `macromod.<flag>`) for server admin policy enforcement.

---

## Macro Scripting Syntax

Script macros can run server commands, chat messages, or simulate hardware actions. Chained commands are separated by `|` in inline macros.

### Special Actions

- **`{WAIT ticks}`** / **`{WAITRANDOM min max}`**: Delay macro execution (fixed or random tick duration).
- **`{CHAT message}`**: Sends a chat message or server command (supports Minecraft color codes like `&a` -> `§a`).
- **`{STASHALL}`**: Automatically deposits main player inventory items into open container/vault screens while protecting hotbar tools (pickaxes & shovels).
- **`{INVENTORYGETSLOT item_id varName}`**: Returns slot index holding item.
- **`{GETHELDITEM varName}`**: Stores main-hand item name into variable.
- **`{GETCONTAINERTITLE varName}`**: Stores open screen title into variable.
- **`{SWAPSLOT from to}`**: Quick-swaps two inventory slot positions.
- **`{KEY key}` / `{KEYDOWN key}` / `{KEYUP key}`**: Binds shortcuts to keyboard keys or key combinations. Accepts key names (`A`, `SPACE`, `ENTER`) or GLFW key codes.
- **`{OPENSCREEN}` / `{OPENOVERLAY}`**: Opens the configuration GUI (default `` ` ``) or custom buttons overlay (default `M`).
- **`{STOP}` / `{STOP ALL}` / `{STOPALL}`**: Immediately stops script execution and releases all held key states.
- **`{ECHO message}`**: Prints a local client-only system message.

### User Input

- **`$$?`**: Prompts the user with a generic input field.
- **`$$[Prompt Label]`**: Prompts the user with a custom text label directly above the entry field (e.g. `$$[Vault Number]`).

---

