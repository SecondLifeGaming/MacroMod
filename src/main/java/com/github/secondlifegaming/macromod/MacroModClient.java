package com.github.secondlifegaming.macromod;

import com.github.secondlifegaming.macromod.config.MacroConfig;
import com.github.secondlifegaming.macromod.config.MacroConfigManager;
import com.github.secondlifegaming.macromod.gui.MacroConfigScreen;
import com.github.secondlifegaming.macromod.gui.MacroOverlayScreen;
import com.github.secondlifegaming.macromod.engine.MacroEngine;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client-side mod entrypoint initializing keybindings, tick events, and input processing.
 */
public class MacroModClient implements ClientModInitializer {
    public static final String MOD_ID = "macromod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final String MOD_DELIMITER = "_MOD_";
    private static final String KEY_KEYBOARD_PREFIX = "key.keyboard.";

    /**
     * Checks if the active runtime environment is Premium.
     *
     * @return {@code true} if Premium edition is active; {@code false} otherwise
     */
    public static boolean isPremium() {
        return com.github.secondlifegaming.macromod.service.MacroFeatureFactory.getService().isPremium();
    }

    public static final KeyMapping.Category MACROMOD_CATEGORY = KeyMapping.Category.register(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(MOD_ID, "keybinds")
    );

    /**
     * {@inheritDoc}
     */
    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing MacroMod Client on Minecraft 26.3!");

        MacroConfigManager.load();
        com.github.secondlifegaming.macromod.network.ServerPermissionHandler.init();
        com.github.secondlifegaming.macromod.service.MacroFeatureFactory.getService().checkForUpdatesAsync();

        final KeyMapping configKeyMapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.macromod.config",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_GRAVE,
                KeyMapping.Category.MISC
        ));

        final KeyMapping overlayKeyMapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.macromod.overlay",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_M,
                KeyMapping.Category.MISC
        ));

        final KeyMapping killKeyMapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.macromod.kill",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_K,
                KeyMapping.Category.MISC
        ));

        syncConfiguredKeyMappings();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) return;

            if (configKeyMapping.consumeClick()) {
                LOGGER.info("[MacroMod] Config keybind triggered! Opening MacroConfigScreen.");
                client.setScreenAndShow(new MacroConfigScreen(Component.literal("Macro Configuration")));
            }

            if (overlayKeyMapping.consumeClick()) {
                LOGGER.info("[MacroMod] Overlay keybind triggered! Opening MacroOverlayScreen.");
                client.setScreenAndShow(new MacroOverlayScreen(Component.literal("Macro Overlay")));
            }

            if (killKeyMapping.consumeClick()) {
                LOGGER.info("[MacroMod] Emergency stop keybind triggered!");
                MacroEngine.stopAll();
            }

            MacroEngine.tick();
            this.checkMacroThresholds(client);
        });
    }

    private static final java.util.Map<String, KeyMapping> DYNAMIC_KEY_MAPPINGS = new java.util.HashMap<>();

    /**
     * Synchronizes and registers native Minecraft {@link KeyMapping} objects for active macros.
     */
    public static void syncConfiguredKeyMappings() {
        MacroConfig config = MacroConfigManager.getConfig();
        java.util.Set<String> allKeys = new java.util.HashSet<>();
        if (config.scriptMacros != null) allKeys.addAll(config.scriptMacros.keySet());
        if (config.keyMacros != null) allKeys.addAll(config.keyMacros.keySet());

        for (String keyId : allKeys) {
            registerDynamicKeyMapping(keyId);
        }
    }

    private static void registerDynamicKeyMapping(String keyId) {
        if (keyId == null || !keyId.startsWith("KEY_")) return;
        String baseKey = keyId.contains(MOD_DELIMITER) ? keyId.substring(0, keyId.indexOf(MOD_DELIMITER)) : keyId;
        if (baseKey.endsWith("_UP")) baseKey = baseKey.substring(0, baseKey.length() - 3);

        if (!DYNAMIC_KEY_MAPPINGS.containsKey(baseKey)) {
            try {
                InputConstants.Key keyObj = parseKeyIdToKey(baseKey);
                if (keyObj != null && keyObj != InputConstants.UNKNOWN) {
                    KeyMapping km = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                            "key.macromod.dyn." + baseKey.toLowerCase(),
                            InputConstants.Type.KEYBOARD,
                            keyObj.getValue(),
                            KeyMapping.Category.MISC
                    ));
                    DYNAMIC_KEY_MAPPINGS.put(baseKey, km);
                    DYNAMIC_KEY_MAPPINGS.put("KEY_" + keyObj.getValue(), km);
                    LOGGER.info("[MacroMod] Registered native KeyMapping for keyId={}, keyVal={}", baseKey, keyObj.getValue());
                }
            } catch (Exception e) {
                LOGGER.debug("Could not register dynamic KeyMapping for " + baseKey, e);
            }
        }
    }

    /**
     * Parses a string key identifier into a Minecraft {@link InputConstants.Key} object.
     *
     * @param baseKeyId the string key identifier (e.g. "KEY_65" or "KEY_A")
     * @return resolved {@link InputConstants.Key} object or {@code null}
     */
    public static InputConstants.Key parseKeyIdToKey(String baseKeyId) {
        if (baseKeyId == null || !baseKeyId.startsWith("KEY_")) return null;
        String raw = baseKeyId.substring(4);
        
        InputConstants.Key namedKey = tryLookupNamedKey(raw);
        if (namedKey != null) return namedKey;

        return tryParseNumericOrCharKey(raw);
    }

    private static InputConstants.Key tryLookupNamedKey(String raw) {
        try {
            InputConstants.Key key = InputConstants.getKey(KEY_KEYBOARD_PREFIX + raw.toLowerCase());
            if (key != InputConstants.UNKNOWN) return key;
        } catch (Exception _) {
            // Key name lookup unmapped
        }
        return null;
    }

    private static InputConstants.Key tryParseNumericOrCharKey(String raw) {
        try {
            int code = Integer.parseInt(raw);
            return lookupKeyByCode(code);
        } catch (NumberFormatException _) {
            if (raw.length() == 1) {
                return tryLookupNamedKey(String.valueOf(Character.toUpperCase(raw.charAt(0))));
            }
        }
        return null;
    }

    private static InputConstants.Key lookupKeyByCode(int code) {
        if (code >= 65 && code <= 90) { // A-Z
            char ch = (char) ('A' + (code - 65));
            InputConstants.Key k = tryLookupNamedKey(String.valueOf(ch));
            if (k != null) return k;
        } else if (code >= 48 && code <= 57) { // 0-9
            char ch = (char) ('0' + (code - 48));
            InputConstants.Key k = tryLookupNamedKey(String.valueOf(ch));
            if (k != null) return k;
        }
        InputConstants.Key key = InputConstants.Type.KEYBOARD.getOrCreate(code);
        return key != InputConstants.UNKNOWN ? key : null;
    }

    /**
     * Evaluates and dispatches client threshold checks.
     *
     * @param client Minecraft client instance
     */
    private void checkMacroThresholds(net.minecraft.client.Minecraft client) {
        MacroConfig config = MacroConfigManager.getConfig();
        var policy = com.github.secondlifegaming.macromod.network.ServerPermissionHandler.getPolicy();
        com.github.secondlifegaming.macromod.service.MacroFeatureFactory.getService().dispatchThresholdChecks(client, config, policy);
    }

    private static final java.util.Map<String, Long> LAST_TRIGGER_TIME = new java.util.HashMap<>();

    /**
     * Handles keyboard events received from GLFW event mixin.
     *
     * @param inputKey input key object
     * @param action key action state (1 = press, 0 = release)
     * @param modifiers modifier keys state bitmask
     */
    public static void handleKeyObjectEvent(InputConstants.Key inputKey, int action, int modifiers) {
        net.minecraft.client.Minecraft client = net.minecraft.client.Minecraft.getInstance();

        if (client.player == null || (action != 1 && action != 0)) return;
        if (client.gui != null && client.gui.screen() instanceof net.minecraft.client.gui.screens.ChatScreen) return;

        MacroConfig config = MacroConfigManager.getConfig();
        int mods = modifiers & 7;

        if (action == 1) {
            handleKeyPress(config, inputKey, mods);
        } else {
            handleKeyRelease(config, inputKey, mods);
        }
    }

    private static void handleKeyPress(MacroConfig config, InputConstants.Key inputKey, int mods) {
        for (java.util.Map.Entry<String, String> entry : config.scriptMacros.entrySet()) {
            if (tryTriggerMacro(entry.getKey(), entry.getValue(), inputKey, mods, true)) return;
        }
        for (java.util.Map.Entry<String, String> entry : config.keyMacros.entrySet()) {
            if (entry.getKey() != null && !entry.getKey().endsWith("_UP") &&
                tryTriggerMacro(entry.getKey(), entry.getValue(), inputKey, mods, false)) {
                return;
            }
        }
    }

    private static void handleKeyRelease(MacroConfig config, InputConstants.Key inputKey, int mods) {
        for (java.util.Map.Entry<String, String> entry : config.keyMacros.entrySet()) {
            String keyId = entry.getKey();
            String command = entry.getValue();
            if (keyId != null && command != null && !command.isEmpty() && keyId.endsWith("_UP")) {
                String baseKey = keyId.substring(0, keyId.length() - 3);
                if (matchesKeyObject(baseKey, inputKey, mods)) {
                    LOGGER.debug("[MacroMod] Executing key release macro '{}' for key '{}'", command, keyId);
                    MacroEngine.run(command);
                    return;
                }
            }
        }
    }

    private static boolean tryTriggerMacro(String keyId, String target, InputConstants.Key inputKey, int mods, boolean isScript) {
        if (keyId == null || target == null || target.isEmpty()) return false;
        if (matchesKeyObject(keyId, inputKey, mods)) {
            long now = System.currentTimeMillis();
            Long last = LAST_TRIGGER_TIME.get(keyId);
            if (last != null && (now - last) < 200) {
                LOGGER.debug("[MacroMod] Debounced duplicate press for keyId={}", keyId);
                return true;
            }
            LAST_TRIGGER_TIME.put(keyId, now);
            if (isScript) {
                LOGGER.debug("[MacroMod] Executing script macro '{}' for key '{}'", target, keyId);
                MacroEngine.runScriptWithToggle(target, keyId);
            } else {
                LOGGER.debug("[MacroMod] Executing key macro '{}' for key '{}'", target, keyId);
                MacroEngine.runWithToggle(target, keyId);
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if a configured key identifier matches an incoming input key and modifier mask.
     *
     * @param configuredKeyId string key identifier configured in settings
     * @param inputKey input key object
     * @param inputMods modifier mask
     * @return {@code true} if matching; {@code false} otherwise
     */
    public static boolean matchesKeyObject(String configuredKeyId, InputConstants.Key inputKey, int inputMods) {
        if (configuredKeyId == null || inputKey == null || !configuredKeyId.startsWith("KEY_")) return false;

        String baseKey = configuredKeyId.contains(MOD_DELIMITER) ? configuredKeyId.substring(0, configuredKeyId.indexOf(MOD_DELIMITER)) : configuredKeyId;
        int reqMods = 0;
        if (configuredKeyId.contains(MOD_DELIMITER)) {
            try {
                reqMods = Integer.parseInt(configuredKeyId.substring(configuredKeyId.indexOf(MOD_DELIMITER) + 5));
            } catch (Exception _) {
                // Ignore parse error
            }
        }
        if (reqMods != inputMods) return false;

        InputConstants.Key configKey = parseKeyIdToKey(baseKey);
        if (configKey != null && configKey.equals(inputKey)) {
            return true;
        }

        String raw = baseKey.substring(4);
        String nameUpper = inputKey.getName().toUpperCase().replace("KEY.KEYBOARD.", "");
        if (raw.equalsIgnoreCase(nameUpper)) return true;

        try {
            int code = Integer.parseInt(raw);
            return code == inputKey.getValue();
        } catch (NumberFormatException _) {
            return false;
        }
    }

    /**
     * Legacy helper method handling raw GLFW key integer events.
     *
     * @param key GLFW key code
     * @param action key action state
     * @param modifiers modifier keys bitmask
     */
    public static void handleKeyEvent(int key, int action, int modifiers) {
        InputConstants.Key keyObj = InputConstants.Type.KEYBOARD.getOrCreate(key);
        handleKeyObjectEvent(keyObj, action, modifiers);
    }
}


