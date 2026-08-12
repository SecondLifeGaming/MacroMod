# Minecraft Container & Inventory Slot Index Reference Map

Use these integer slot IDs with `{SLOTCLICK slot}`, `{SHIFTCLICK slot}`, and `{ALLCLICK slot}` actions.

---

## 1. Player Inventory (`InventoryMenu`)

Total slots: **46** (Indices `0` through `45`)

```
+-------------------------------------------------------------+
|                          CRAFTING                           |
|                    [ 1 ] [ 2 ]                              |
|                    [ 3 ] [ 4 ]   --->   [ 0 ] Result        |
+-------------------------------------------------------------+
|                           ARMOR                             |
|  [ 5 ] Helmet                                               |
|  [ 6 ] Chestplate                                           |
|  [ 7 ] Leggings                                             |
|  [ 8 ] Boots                                [ 45 ] Offhand  |
+-------------------------------------------------------------+
|                       MAIN INVENTORY                        |
|  [ 9 ] [10] [11] [12] [13] [14] [15] [16] [17]             |
|  [18] [19] [20] [21] [22] [23] [24] [25] [26]             |
|  [27] [28] [29] [30] [31] [32] [33] [34] [35]             |
+-------------------------------------------------------------+
|                          HOTBAR                             |
|  [36] [37] [38] [39] [40] [41] [42] [43] [44]             |
+-------------------------------------------------------------+
```

### Detailed Breakdown
* **Crafting Result**: `0`
* **Crafting Grid (2x2)**: `1` (top-left), `2` (top-right), `3` (bottom-left), `4` (bottom-right)
* **Armor Slots**: `5` (Helmet), `6` (Chestplate), `7` (Leggings), `8` (Boots)
* **Main Inventory (3 rows x 9 columns)**: `9` to `35`
* **Hotbar (Slots 1–9)**: `36` to `44`
* **Offhand Slot**: `45`

---

## 2. Chest & Shulker Containers (`ChestMenu`)

When a chest, double chest, or shulker box screen is open, **container slots come first** (0 to N-1), followed by the **player's main inventory**, and finally the **hotbar**.

### Single Chest / Shulker Box (27 Slots)
* **Chest Content (3x9 grid)**: `0` to `26`
* **Player Main Inventory (3x9)**: `27` to `53`
* **Player Hotbar (9 slots)**: `54` to `62`

### Double Chest (54 Slots)
* **Double Chest Content (6x9 grid)**: `0` to `53`
* **Player Main Inventory (3x9)**: `54` to `80`
* **Player Hotbar (9 slots)**: `81` to `89`

---

## 3. Crafting Table (`CraftingMenu` 3x3)
* **Result Slot**: `0`
* **Crafting Grid (3x3)**: `1` through `9` (row by row)
* **Player Main Inventory**: `10` to `36`
* **Player Hotbar**: `37` to `45`

---

## 4. Furnace / Blast Furnace / Smoker (`FurnaceMenu`)
* **Input Ingredient Slot**: `0`
* **Fuel Slot**: `1`
* **Output Result Slot**: `2`
* **Player Main Inventory**: `3` to `29`
* **Player Hotbar**: `30` to `38`

---

## 5. Anvil (`AnvilMenu`)
* **First Item Input**: `0`
* **Second Item / Sacrifice Input**: `1`
* **Output Result**: `2`
* **Player Main Inventory**: `3` to `29`
* **Player Hotbar**: `30` to `38`
