package com.github.secondlifegaming.macromod.engine;

import com.github.secondlifegaming.macromod.service.MacroFeatureFactory;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Central manager for all active inline and script macro executions.
 */
public class MacroEngine {

    private static final List<ActiveScript> ACTIVE = new ArrayList<>();
    private static final List<ActiveScript> PENDING_ADD = new ArrayList<>();
    private static boolean isTicking = false;

    /**
     * Container class holding an active script interpreter state.
     */
    public static final class ActiveScript {
        final InlineScriptInterpreter interp;
        final String keyId;
        final String scriptName;

        /**
         * Constructs an {@link ActiveScript} tracking instance.
         *
         * @param interp script interpreter
         * @param keyId key binding identifier
         * @param scriptName script file name
         */
        public ActiveScript(InlineScriptInterpreter interp, String keyId, String scriptName) {
            this.interp = interp;
            this.keyId = keyId;
            this.scriptName = scriptName;
        }
    }

    private MacroEngine() {}

    /**
     * Enqueues an active script for execution.
     *
     * @param as active script object
     */
    public static void addActive(ActiveScript as) {
        if (!Minecraft.getInstance().isSameThread()) {
            Minecraft.getInstance().execute(() -> addActive(as));
            return;
        }
        if (isTicking) {
            PENDING_ADD.add(as);
        } else {
            ACTIVE.add(as);
        }
    }

    /**
     * Runs an inline script string.
     *
     * @param script raw script string
     */
    public static void run(String script) {
        if (script == null || script.isBlank()) return;
        if (handlePromptPlaceholders(script)) return;
        addActive(new ActiveScript(toInterpreter(script), null, null));
    }

    /**
     * Runs an inline script with toggle behavior for key bindings.
     *
     * @param script raw script string
     * @param keyId key identifier
     */
    public static void runWithToggle(String script, String keyId) {
        if (script == null || script.isBlank()) return;
        if (stopByKeyId(keyId)) return;
        if (handlePromptPlaceholders(script)) return;
        addActive(new ActiveScript(toInterpreter(script), keyId, null));
    }

    /**
     * Executes a named script file.
     *
     * @param scriptName script file name
     */
    public static void runScript(String scriptName) {
        runScript(scriptName, null);
    }

    /**
     * Executes a named script file triggered by a key binding.
     *
     * @param scriptName script file name
     * @param keyId key identifier
     */
    public static void runScript(String scriptName, String keyId) {
        MacroFeatureFactory.getService().runScript(scriptName, keyId);
    }

    /**
     * Executes a named script file with toggle behavior.
     *
     * @param scriptName script file name
     * @param keyId key identifier
     */
    public static void runScriptWithToggle(String scriptName, String keyId) {
        MacroFeatureFactory.getService().runScriptWithToggle(scriptName, keyId);
    }

    /**
     * Stops all active and pending script executions.
     */
    public static void stopAll() {
        Minecraft client = Minecraft.getInstance();
        ACTIVE.forEach(as -> {
            as.interp.stop();
            as.interp.releaseKeys(client);
        });
        PENDING_ADD.forEach(as -> {
            as.interp.stop();
            as.interp.releaseKeys(client);
        });
        ACTIVE.clear();
        PENDING_ADD.clear();
        InlineScriptInterpreter.releaseAllKeys(client);
    }

    /**
     * Stops active macros associated with a specific key identifier.
     *
     * @param keyId key identifier
     * @return {@code true} if any script was stopped; {@code false} otherwise
     */
    public static boolean stopByKeyId(String keyId) {
        if (keyId == null || keyId.isBlank()) return false;
        Minecraft client = Minecraft.getInstance();
        boolean stopped = false;
        String baseKey = keyId.contains("_MOD_") ? keyId.substring(0, keyId.indexOf("_MOD_")) : keyId;

        for (Iterator<ActiveScript> it = ACTIVE.iterator(); it.hasNext(); ) {
            ActiveScript as = it.next();
            if (as.keyId != null && (as.keyId.equals(keyId) || as.keyId.startsWith(baseKey))) {
                as.interp.stop();
                as.interp.releaseKeys(client);
                it.remove();
                stopped = true;
            }
        }
        for (Iterator<ActiveScript> it = PENDING_ADD.iterator(); it.hasNext(); ) {
            ActiveScript as = it.next();
            if (as.keyId != null && (as.keyId.equals(keyId) || as.keyId.startsWith(baseKey))) {
                as.interp.stop();
                as.interp.releaseKeys(client);
                it.remove();
                stopped = true;
            }
        }
        if (stopped) {
            InlineScriptInterpreter.releaseAllKeys(client);
        }
        return stopped;
    }

    /**
     * Stops active macros matching a script file name.
     *
     * @param scriptName script file name
     * @return {@code true} if any script was stopped; {@code false} otherwise
     */
    public static boolean stopByScriptName(String scriptName) {
        if (scriptName == null || scriptName.isBlank()) return false;
        Minecraft client = Minecraft.getInstance();
        boolean stopped = false;

        for (Iterator<ActiveScript> it = ACTIVE.iterator(); it.hasNext(); ) {
            ActiveScript as = it.next();
            if (scriptName.equalsIgnoreCase(as.scriptName)) {
                as.interp.stop();
                as.interp.releaseKeys(client);
                it.remove();
                stopped = true;
            }
        }
        for (Iterator<ActiveScript> it = PENDING_ADD.iterator(); it.hasNext(); ) {
            ActiveScript as = it.next();
            if (scriptName.equalsIgnoreCase(as.scriptName)) {
                as.interp.stop();
                as.interp.releaseKeys(client);
                it.remove();
                stopped = true;
            }
        }
        if (stopped) {
            InlineScriptInterpreter.releaseAllKeys(client);
        }
        return stopped;
    }

    /**
     * Advances all running macro execution interpreters by one game tick.
     */
    public static void tick() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            ACTIVE.forEach(as -> as.interp.stop());
            PENDING_ADD.forEach(as -> as.interp.stop());
            ACTIVE.clear();
            PENDING_ADD.clear();
            InlineScriptInterpreter.releaseAllKeys(client);
            return;
        }
        isTicking = true;
        try {
            ACTIVE.removeIf(as -> {
                boolean done;
                try {
                    done = as.interp.tick(client);
                } catch (Exception e) {
                    org.slf4j.LoggerFactory.getLogger("MacroMod/MacroEngine")
                        .error("[MacroMod] Unhandled error during inline script execution for " + as.scriptName, e);
                    as.interp.stop();
                    done = true;
                }
                if (done) {
                    as.interp.releaseKeys(client);
                }
                return done;
            });
        } finally {
            isTicking = false;
        }
        if (!PENDING_ADD.isEmpty()) {
            ACTIVE.addAll(PENDING_ADD);
            PENDING_ADD.clear();
        }
    }

    /**
     * Converts an inline pipe script string into an {@link InlineScriptInterpreter}.
     *
     * @param inlineScript raw pipe delimited script string
     * @return initialized {@link InlineScriptInterpreter} instance
     */
    private static InlineScriptInterpreter toInterpreter(String inlineScript) {
        String[] parts = inlineScript.split("\\|");
        List<String> lines = new ArrayList<>(parts.length);
        for (String p : parts) lines.add(p.trim());
        return MacroFeatureFactory.getService().createInterpreter(lines, null);
    }

    /**
     * Handles interactive prompt placeholders (e.g. {@code $$[Prompt]}).
     *
     * @param script raw script string
     * @return {@code true} if prompt GUI screen was shown; {@code false} otherwise
     */
    private static boolean handlePromptPlaceholders(String script) {
        int index = script.indexOf("$$");
        if (index == -1) return false;

        String before = script.substring(0, index);
        String after = "";
        String promptLabel = "Enter value";

        if (index + 2 < script.length()) {
            char next = script.charAt(index + 2);
            if (next == '?') {
                after = script.substring(index + 3);
            } else if (next == '[') {
                int closeIndex = script.indexOf(']', index + 3);
                if (closeIndex != -1) {
                    promptLabel = script.substring(index + 3, closeIndex);
                    after = script.substring(closeIndex + 1);
                } else {
                    after = script.substring(index + 2);
                }
            } else {
                after = script.substring(index + 2);
            }
        }

        final String fp = promptLabel;
        final String fb = before;
        final String fa = after;
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> client.setScreenAndShow(
            new com.github.secondlifegaming.macromod.gui.MacroPromptScreen(fp, fb, fa)));
        return true;
    }
}
