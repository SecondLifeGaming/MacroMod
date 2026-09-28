package com.github.secondlifegaming.macromod.service;

import com.github.secondlifegaming.macromod.config.MacroConfig;
import com.github.secondlifegaming.macromod.network.ServerPermissionHandler;
import net.minecraft.client.Minecraft;

import java.util.List;

/**
 * Service interface defining feature capabilities across Free and Premium editions.
 */
public interface MacroFeatureService {

    /**
     * Checks whether the currently active edition is Premium.
     *
     * @return {@code true} if running Premium edition; {@code false} otherwise.
     */
    boolean isPremium();

    /**
     * Executes a multi-line script by name.
     *
     * @param scriptName the name of the script file to execute
     * @param keyId the key binding identifier triggering execution
     */
    void runScript(String scriptName, String keyId);

    /**
     * Executes a multi-line script by name with toggle behavior.
     *
     * @param scriptName the name of the script file to execute
     * @param keyId the key binding identifier triggering execution
     */
    void runScriptWithToggle(String scriptName, String keyId);

    /**
     * Evaluates and dispatches event-based threshold checks (health, hunger, inventory, etc.).
     *
     * @param client the Minecraft client instance
     * @param config current macro configuration settings
     * @param policy current server restriction policy
     */
    void dispatchThresholdChecks(Minecraft client, MacroConfig config, ServerPermissionHandler.ServerPolicy policy);

    /**
     * Initiates an asynchronous check for mod updates.
     */
    void checkForUpdatesAsync();

    /**
     * Lists all available script names in the script library.
     *
     * @return a list of script file names
     */
    List<String> listScripts();

    /**
     * Loads the raw lines of a script file.
     *
     * @param name the script file name
     * @return a list of line strings
     */
    List<String> loadScriptLines(String name);

    /**
     * Saves raw lines to a script file.
     *
     * @param name the script file name
     * @param lines the line content to save
     */
    void saveScriptLines(String name, List<String> lines);

    /**
     * Deletes a script file from storage.
     *
     * @param name the script file name to delete
     */
    void deleteScript(String name);

    /**
     * Creates an appropriate {@link com.github.secondlifegaming.macromod.engine.InlineScriptInterpreter}
     * for inline macro execution. In Premium edition, this instantiates full {@code ScriptInterpreter}.
     *
     * @param lines raw script lines to execute
     * @param originKeyId key binding identifier
     * @return script interpreter instance
     */
    com.github.secondlifegaming.macromod.engine.InlineScriptInterpreter createInterpreter(List<String> lines, String originKeyId);
}
