package com.github.secondlifegaming.macromod.engine;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight tick-by-tick interpreter for inline pipe macros (e.g. /say 1 | {WAIT 20} | /say 2).
 * Strictly used for free inline keybind macros. Zero paywalled code or .mcm file dependencies.
 */
public class InlineScriptInterpreter {

    private final List<String> steps;
    private int pc = 0;
    private int delay = 0;
    private boolean stopped = false;

    /**
     * Constructs an {@link InlineScriptInterpreter} with parsed macro step lines.
     *
     * @param steps the list of macro step strings to execute
     */
    public InlineScriptInterpreter(List<String> steps) {
        this.steps = steps != null ? steps : new ArrayList<>();
    }

    private static final java.util.regex.Pattern WHITESPACE_PATTERN = java.util.regex.Pattern.compile("\\s+");

    /**
     * Ticks the inline script interpreter by one game tick.
     *
     * @param client Minecraft client instance
     * @return {@code true} if execution has completed or stopped; {@code false} if paused/delayed
     */
    public boolean tick(Minecraft client) {
        if (stopped) return true;
        if (delay > 0) {
            delay--;
            return false;
        }

        while (pc < steps.size()) {
            String step = steps.get(pc).trim();
            pc++;
            if (step.isEmpty() || step.startsWith("#")) continue;

            java.util.Optional<Boolean> result = processStep(client, step);
            if (result.isPresent()) {
                return result.get();
            }
        }

        return pc >= steps.size();
    }

    /**
     * Processes a single step line, evaluating special controls like {@code {WAIT}} or {@code {STOP}}.
     *
     * @param client Minecraft client instance
     * @param step raw step string
     * @return {@link java.util.Optional} containing execution status, or empty if step was an action
     */
    private java.util.Optional<Boolean> processStep(Minecraft client, String step) {
        if (step.startsWith("{") && step.endsWith("}")) {
            String inner = step.substring(1, step.length() - 1).trim();
            String[] toks = WHITESPACE_PATTERN.split(inner);
            if (toks.length > 0 && "WAIT".equalsIgnoreCase(toks[0])) {
                if (toks.length > 1) {
                    try {
                        this.delay = Integer.parseInt(toks[1]);
                    } catch (NumberFormatException _) {
                        this.delay = 0;
                    }
                }
                return java.util.Optional.of(false);
            } else if ("STOP".equalsIgnoreCase(inner)) {
                this.stopped = true;
                return java.util.Optional.of(true);
            } else {
                // Any other {...} command (e.g. LOOKAT, RIGHTCLICK, CALL, KEY) MUST NEVER go to chat.
                return java.util.Optional.of(false);
            }
        }

        executeAction(client, step);
        return java.util.Optional.empty();
    }

    /**
     * Executes a chat message or server command action.
     *
     * @param client Minecraft client instance
     * @param step step string containing chat or command
     */
    private void executeAction(Minecraft client, String step) {
        if (client.player == null) return;
        if (step.startsWith("/")) {
            final String cmd = step.substring(1);
            client.execute(() -> {
                if (client.player != null && client.player.connection != null) {
                    client.player.connection.sendCommand(cmd);
                }
            });
        } else {
            client.execute(() -> {
                if (client.player != null && client.player.connection != null) {
                    client.player.connection.sendChat(step);
                }
            });
        }
    }

    /**
     * Stops macro execution immediately.
     */
    public void stop() {
        this.stopped = true;
    }

    /**
     * Releases active key bindings held by the macro.
     *
     * @param client Minecraft client instance
     */
    public void releaseKeys(Minecraft client) {
        // Stubs for key release
    }

    /**
     * Emergency helper releasing all standard movement and interaction keys.
     *
     * @param client Minecraft client instance
     */
    public static void releaseAllKeys(Minecraft client) {
        if (client == null || client.options == null) return;
        client.execute(() -> {
            client.options.keyAttack.setDown(false);
            client.options.keyUse.setDown(false);
            client.options.keyPickItem.setDown(false);
            client.options.keyUp.setDown(false);
            client.options.keyDown.setDown(false);
            client.options.keyLeft.setDown(false);
            client.options.keyRight.setDown(false);
            client.options.keyJump.setDown(false);
            client.options.keyShift.setDown(false);
            client.options.keySprint.setDown(false);
        });
    }
}
