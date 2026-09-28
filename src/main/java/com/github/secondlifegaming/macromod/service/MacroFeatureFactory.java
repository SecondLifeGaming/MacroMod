package com.github.secondlifegaming.macromod.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * Factory for instantiating the active {@link MacroFeatureService} instance.
 */
public class MacroFeatureFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger("MacroMod/FeatureFactory");
    private static MacroFeatureService INSTANCE;

    private MacroFeatureFactory() {}

    /**
     * Gets the active {@link MacroFeatureService} instance, lazy-loading the edition provider.
     *
     * @return the singleton service implementation instance
     */
    public static synchronized MacroFeatureService getService() {
        if (INSTANCE == null) {
            INSTANCE = loadProvider("com.github.secondlifegaming.macromod.service.PremiumMacroFeatureProviderImpl");
            if (INSTANCE == null) {
                INSTANCE = loadProvider("com.github.secondlifegaming.macromod.service.FreeMacroFeatureProviderImpl");
            }
            if (INSTANCE == null) {
                LOGGER.info("[MacroMod] Falling back to default free feature provider.");
                INSTANCE = new DefaultFreeFeatureService();
            }
        }
        return INSTANCE;
    }

    private static MacroFeatureService loadProvider(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            MacroFeatureService service = (MacroFeatureService) clazz.getDeclaredConstructor().newInstance();
            LOGGER.info("[MacroMod] Loaded feature provider: {}", className);
            return service;
        } catch (Exception e) {
            LOGGER.debug("[MacroMod] Could not load provider {}: {}", className, e.getMessage());
            return null;
        }
    }

    /**
     * Fallback implementation of {@link MacroFeatureService} used when no edition provider is present.
     */
    private static class DefaultFreeFeatureService implements MacroFeatureService {

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isPremium() { return false; }

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
        public void dispatchThresholdChecks(net.minecraft.client.Minecraft client, com.github.secondlifegaming.macromod.config.MacroConfig config, com.github.secondlifegaming.macromod.network.ServerPermissionHandler.ServerPolicy policy) {
            // Free edition: no event triggers
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void checkForUpdatesAsync() {
            // Free edition: no premium update checker
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
            // Free edition: no script saves
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void deleteScript(String name) {
            // Free edition: no script deletes
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public com.github.secondlifegaming.macromod.engine.InlineScriptInterpreter createInterpreter(List<String> lines, String originKeyId) {
            return new com.github.secondlifegaming.macromod.engine.InlineScriptInterpreter(lines);
        }

        /**
         * Sends a Premium feature notice to the player.
         */
        private void sendNotice() {
            net.minecraft.client.Minecraft client = net.minecraft.client.Minecraft.getInstance();
            if (client.player != null) {
                client.player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§c[Macro] Multi-line scripts (.mcm files) & Event Triggers are a Premium feature!"
                ));
            }
        }
    }
}
