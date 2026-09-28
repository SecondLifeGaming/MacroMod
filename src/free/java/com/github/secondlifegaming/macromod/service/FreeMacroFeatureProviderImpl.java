package com.github.secondlifegaming.macromod.service;

import com.github.secondlifegaming.macromod.config.MacroConfig;
import com.github.secondlifegaming.macromod.network.ServerPermissionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.List;

/**
 * Free edition implementation of {@link MacroFeatureService}.
 */
public class FreeMacroFeatureProviderImpl implements MacroFeatureService {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPremium() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runScript(String scriptName, String keyId) {
        sendNotice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runScriptWithToggle(String scriptName, String keyId) {
        sendNotice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispatchThresholdChecks(Minecraft client, MacroConfig config, ServerPermissionHandler.ServerPolicy policy) {
        // Free Edition: Event threshold triggers are completely excluded
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkForUpdatesAsync() {
        // Free Edition: Remote update checker excluded
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> listScripts() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> loadScriptLines(String name) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveScriptLines(String name, List<String> lines) {
        // Free Edition: no script saves
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteScript(String name) {
        // Free Edition: no script deletes
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public com.github.secondlifegaming.macromod.engine.InlineScriptInterpreter createInterpreter(List<String> lines, String originKeyId) {
        return new com.github.secondlifegaming.macromod.engine.InlineScriptInterpreter(lines);
    }

    /**
     * Sends a Premium feature notice message to the player.
     */
    private void sendNotice() {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            client.player.sendSystemMessage(Component.literal(
                    "§c[Macro] Multi-line scripts (.mcm files) & Event Triggers are a Premium feature!"
            ));
        }
    }
}
