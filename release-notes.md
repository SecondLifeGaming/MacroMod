# MC Macro Mod - Release Notes

## Version 0.1.1-beta (Minecraft 26.3)

### Minecraft 26.3 Refactor & 26.2 Retirement

- **Minecraft 26.3 Native Support**:
  - Fully refactored for **Minecraft 26.3** and **Fabric Loader 0.19+**, compiled targeting **Java 25**.
  - Updated native keyboard input event pipeline to Mojang's `InputConstants.Key` API and updated `KeyboardMixin`.
  - Migrated GUI rendering engine extractors to MC 26.3 `GuiGraphicsExtractor` interfaces.
- **Retirement of Minecraft 26.2**:
  - Deprecated and retired backward compatibility for Minecraft 26.2 due to breaking Mojang API changes in input, rendering, and Fabric networking.

### Human WindMouse LOOKAT & Block Target Verification

- **Human-like `WindMouse` Camera Movement (`{LOOKAT}`)**:
  - Upgraded `{LOOKAT}` camera rotation from instant single-frame snapping to human-like **WindMouse** physics (curved vector paths, gravity pull, wind disturbance noise, and target approach deceleration).
- **Target Block Type Validation (`expectedBlock`)**:
  - Supports optional block validation argument: `{LOOKAT x y z [expectedBlock]}` (e.g. `{LOOKAT 100 64 -200 blast_furnace}`).
  - Validates block state at target coordinates before turning; halts macro and warns if block type doesn't match.
- **Target Inner Depth Margin (`innerMargin`)**:
  - Supports optional inner margin depth ratio argument (`0.0` to `0.45`, default `0.1`). Calculates target vector inside the block face bounding box with natural Gaussian center weighting.
- **Crosshair Raycast Verification**:
  - Verifies that player crosshair raycast (`hitResult`) has locked onto target `BlockPos` before advancing script execution.
- **Human Look Angle Commands (`{HLOOK}`, `{HLOOKRANGE}`, `{HLOOKRANDOM}`)**:
  - Introduced `{HLOOK yaw pitch}`, `{HLOOKRANGE}`, and `{HLOOKRANDOM}` for human-like WindMouse camera rotation targeting explicit yaw and pitch angles, reserving `{LOOK}` for instant 1-tick snapping.

### Interface & GUI Verification

- **Screen & Interface Verification (`{VERIFYSCREEN}`)**:
  - Added `{VERIFYSCREEN expectedInterface}` / `{VERIFYINTERFACE expectedInterface}` action command. Verifies active screen title or container type matches expected target (e.g. `furnace`, `chest`, `vault`) before interacting.
- **Dynamic Interface State Variables**:
  - Added `$screentitle`, `$screenclass`, `$containertype`, `$isfurnaceopen`, `$ischestopen`, `$iscraftingopen`, `$isanvilopen`.
  - Upgraded `{GETCONTAINERTITLE varName}` to dynamically read active GUI screen titles and menu types.

---
