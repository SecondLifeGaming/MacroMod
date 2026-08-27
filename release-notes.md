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
