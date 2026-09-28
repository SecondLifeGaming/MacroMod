# MC Macro Mod - Release Notes

## Version 0.1.2-beta (Minecraft 26.3)

### Modular Free vs Premium Compilation Architecture

- **Compile-Time Source Set Separation**:
  - Restructured the codebase into decoupled Gradle source sets (`src/main`, `src/free`, and `src/premium`).
  - **Free Edition (`src/free`)**: Compiles a standalone binary containing no paywalled classes, `.mcm` file parser bytecode or event loop checks.
  - **Premium Edition (`src/premium`)**: Compiles the complete feature suite including multi-line `.mcm` script files, `ScriptInterpreter`, `PremiumEventTriggers` threshold checking, `WindMouse` `{LOOKAT}` camera automation, and the remote update checker.

### Modrinth Terms of Service Compliance

- **Physical Bytecode Removal**:
  - Eliminated artificial runtime boolean checks (`IS_PREMIUM` flags) in the free JAR.
  - All proprietary feature bytecode is physically excluded at compile time, ensuring compliance with Modrinth hosting policies for free downloads.

### Git Security & Privacy (`.gitignore`)

- Configured `.gitignore` to explicitly ignore `src/premium/` while keeping `src/main/` and `src/free/` tracked for GitHub open-source hosting.

### Modern PaperMC Adventure Component API Refactoring

- **Kyori Adventure API Migration**:
  - Refactored `MacroModServerPlugin.java` to replace deprecated `org.bukkit.ChatColor` with `net.kyori.adventure.text.Component` and `NamedTextColor`.
  - Replaced deprecated `getDescription().getVersion()` with modern `getPluginMeta().getVersion()`.
  - Cleaned up subproject command execution logic to return `false` on unrecognized subcommands for standard Bukkit usage help rendering.

### Performance & Memory Optimizations

- **Precompiled Regex Patterns**:
  - Optimized `InlineScriptInterpreter` by compiling whitespace regex patterns (`\s+`) into a static pre-compiled `Pattern` field, eliminating per-tick allocations.
  - Refactored tick processing to use `Optional<Boolean>` step execution control flow to reduce cognitive complexity.

---
