# MC Macro Mod - Release Notes

## Version 0.1.0-beta

### 🛡️ Modrinth Policy Compliance & Protocol-Level Server Discovery

- **Modrinth Section 3.3 & Content Rules Audit**:
  - Dynamically removed non-compliant UI tabs (**Event Triggers** & **Scripts**) in the Free Edition configuration screen.
- **Protocol-Level Server Discovery (`macromod:hello`)**:
  - Replaced legacy `/macromod-info` chat command execution with Fabric's native `ClientPlayNetworking` custom payload packet (`macromod:hello`).
  - Fixed `Unknown command` errors and unprompted chat messages when joining vanilla or non-plugin Minecraft servers.

---
