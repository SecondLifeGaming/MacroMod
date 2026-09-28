package com.github.secondlifegaming.macromod.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration model storing user keybinds, script mappings, threshold triggers, and UI button setups.
 */
public class MacroConfig {
    public double durabilityThresholdPercent = 10.0;
    public String lowDurabilityCommand = "/fix all";
    public boolean lowDurabilityActionEnabled = false;

    public double healthThresholdPercent = 30.0;
    public String lowHealthCommand = "/heal";
    public boolean lowHealthActionEnabled = false;

    // Hunger Trigger
    public int hungerThresholdLevel = 6;
    public String lowHungerCommand = "/say Eating time!";
    public boolean lowHungerActionEnabled = false;

    // Additional event triggers
    public boolean inventoryFullActionEnabled = false;
    public String inventoryFullCommand = "/say My inventory is full!";

    public boolean itemPickupActionEnabled = false;
    public String itemPickupCommand = "{ECHO Picked up an item!}";

    public boolean inventoryChangeActionEnabled = false;
    public String inventoryChangeCommand = "{ECHO Inventory changed!}";

    public boolean joinServerActionEnabled = false;
    public String joinServerCommand = "/say Hello server!";

    public boolean worldLoadActionEnabled = false;
    public String worldLoadCommand = "{ECHO Loaded world environment!}";

    public boolean deathActionEnabled = false;
    public String deathCommand = "/say Oh no, I died!";

    public boolean respawnActionEnabled = false;
    public String respawnCommand = "/back";

    public boolean dimensionChangeActionEnabled = false;
    public String dimensionChangeCommand = "{ECHO Entered new dimension!}";

    public boolean chatTriggerActionEnabled = false;
    public String chatTriggerFilter = "welcome";
    public String chatTriggerCommand = "/say Thanks!";

    public boolean containerOpenActionEnabled = false;
    public String containerOpenCommand = "{ECHO Opened GUI container}";

    public boolean containerCloseActionEnabled = false;
    public String containerCloseCommand = "{ECHO Closed GUI container}";

    public boolean takeDamageActionEnabled = false;
    public String takeDamageCommand = "/say Ouch!";

    public int minChatDelayMs = 200;
    public int minCommandDelayMs = 100;
    public boolean serverSafeMode = false;
    public boolean debugChatAlerts = false;
    public boolean disclaimerShown = false;
    public boolean sendServerDiscoveryPing = true;

    public double lowArmorThresholdPercent = 15.0;
    public String lowArmorCommand = "/say Armor low!";
    public boolean lowArmorActionEnabled = false;

    public double lowAirThresholdPercent = 20.0;
    public String lowAirCommand = "/say Air low!";
    public boolean lowAirActionEnabled = false;

    public int lowXpThresholdLevel = 5;
    public String lowXpCommand = "/say XP level low!";
    public boolean lowXpActionEnabled = false;

    public List<CustomButton> customButtons = new ArrayList<>();
    public Map<String, String> keyMacros = new HashMap<>();

    /** Maps key IDs (e.g. "KEY_65") to script names (without .mcm extension). */
    public Map<String, String> scriptMacros = new HashMap<>();

    /**
     * Constructs a default {@link MacroConfig} with standard default key bindings.
     */
    public MacroConfig() {
        this.keyMacros.put("KEY_77", "{OPENOVERLAY}");
        this.keyMacros.put("KEY_75", "{STOPALL}");
    }

    /**
     * Data model representing a custom screen overlay button.
     */
    public static class CustomButton {
        public String label;
        public String command;
        public int x;
        public int y;
        public int width;
        public int height;

        /**
         * Constructs a {@link CustomButton} with target bounds and label.
         *
         * @param label button text label
         * @param command macro command action
         * @param x X coordinate position
         * @param y Y coordinate position
         * @param width button width in pixels
         * @param height button height in pixels
         */
        public CustomButton(String label, String command, int x, int y, int width, int height) {
            this.label = label;
            this.command = command;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
