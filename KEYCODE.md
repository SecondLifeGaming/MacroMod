# GLFW Keycodes Reference Table

Use these keycode integers inside your `{KEY keycode}`, `{KEYDOWN keycode}`, and `{KEYUP keycode}` macros.

## Alphanumeric Keys
| Key | Keycode | Key | Keycode |
| --- | --- | --- | --- |
| **A** | 65 | **N** | 78 |
| **B** | 66 | **O** | 79 |
| **C** | 67 | **P** | 80 |
| **D** | 68 | **Q** | 81 |
| **E** | 69 | **R** | 82 |
| **F** | 70 | **S** | 83 |
| **G** | 71 | **T** | 84 |
| **H** | 72 | **U** | 85 |
| **I** | 73 | **V** | 86 |
| **J** | 74 | **W** | 87 |
| **K** | 75 | **X** | 88 |
| **L** | 76 | **Y** | 89 |
| **M** | 77 | **Z** | 90 |

| Number | Keycode |
| --- | --- |
| **0** | 48 |
| **1** | 49 |
| **2** | 50 |
| **3** | 51 |
| **4** | 52 |
| **5** | 53 |
| **6** | 54 |
| **7** | 55 |
| **8** | 56 |
| **9** | 57 |

## Special & Navigation Keys
| Key | Keycode |
| --- | --- |
| **Space** | 32 |
| **Enter** | 257 |
| **Escape** | 256 |
| **Tab** | 258 |
| **Backspace** | 259 |
| **Insert** | 260 |
| **Delete** | 261 |
| **Right Arrow** | 262 |
| **Left Arrow** | 263 |
| **Down Arrow** | 264 |
| **Up Arrow** | 265 |
| **Page Up** | 266 |
| **Page Down** | 267 |
| **Home** | 268 |
| **End** | 269 |
| **Grave Accent / Backtick (\`)** | 96 |

## Modifiers & Function Keys
| Key | Keycode |
| --- | --- |
| **Left Shift** | 340 |
| **Left Control** | 341 |
| **Left Alt** | 342 |
| **Right Shift** | 344 |
| **Right Control** | 345 |
| **Right Alt** | 346 |
| **F1** | 290 |
| **F2** | 291 |
| **F3** | 292 |
| **F4** | 293 |
| **F5** | 294 |
| **F6** | 295 |
| **F7** | 296 |
| **F8** | 297 |
| **F9** | 298 |
| **F10** | 299 |
| **F11** | 300 |
| **F12** | 301 |

## Mouse Button & Action Aliases
The `{KEY}`, `{KEYDOWN}`, and `{KEYUP}` action blocks support mouse buttons, key names, and game action aliases in addition to GLFW keycode integers:

| Target / Action | Keycode / Name Aliases | Description |
| --- | --- | --- |
| **Left Click / Attack** | `-100`, `LMB`, `ATTACK`, `LEFTCLICK` | Holds or releases Left Click (Attack / Mine block) |
| **Right Click / Use** | `-101`, `-99`, `RMB`, `USE`, `RIGHTCLICK` | Holds or releases Right Click (Use / Place block / Eat) |
| **Middle Click / Pick** | `-102`, `-98`, `MMB`, `PICK`, `MIDDLECLICK` | Holds or releases Middle Click (Pick block) |
| **Forward** | `W`, `FORWARD` | Moves forward |
| **Back** | `S`, `BACK` | Moves backward |
| **Left** | `A`, `LEFT` | Strafe left |
| **Right** | `D`, `RIGHT` | Strafe right |
| **Jump** | `SPACE`, `JUMP` | Jump |
| **Sneak** | `SHIFT`, `SNEAK` | Crouch / Sneak |
| **Sprint** | `SPRINT` | Sprint |

### Example Macro Usage:
* Hold Left Click down for 5 seconds (100 ticks), then release:
  `{KEYDOWN -100} | {WAIT 100} | {KEYUP -100}`
* Hold Attack down using name alias:
  `{KEYDOWN attack} | {WAIT 100} | {KEYUP attack}`
* Sprint-fly / auto-walk forward:
  `{KEYDOWN sprint} | {KEYDOWN w} | {WAIT 200} | {KEYUP w} | {KEYUP sprint}`
