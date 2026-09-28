package com.github.secondlifegaming.macromod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Manager class responsible for loading, persisting, and caching {@link MacroConfig} JSON data.
 */
public class MacroConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("macromod.json").toFile();
    private static MacroConfig config = new MacroConfig();

    private MacroConfigManager() {}

    /**
     * Retrieves the current cached configuration instance.
     *
     * @return active {@link MacroConfig} object
     */
    public static MacroConfig getConfig() {
        return config;
    }

    /**
     * Loads the configuration from disk, instantiating defaults if missing.
     */
    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            config = GSON.fromJson(reader, MacroConfig.class);
            if (config == null) {
                config = new MacroConfig();
            }
        } catch (IOException _) {
            config = new MacroConfig();
        }
        com.github.secondlifegaming.macromod.MacroModClient.syncConfiguredKeyMappings();
    }

    /**
     * Saves the current cached configuration to disk as formatted JSON.
     */
    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            org.slf4j.LoggerFactory.getLogger("MacroMod/ConfigManager").warn("Failed to save config file: {}", e.getMessage());
        }
        com.github.secondlifegaming.macromod.MacroModClient.syncConfiguredKeyMappings();
    }
}

