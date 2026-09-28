package com.github.secondlifegaming.macromod.server;

import com.github.secondlifegaming.macromod.server.common.ServerPolicy;
import com.google.gson.Gson;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.StandardCharsets;

/**
 * Bukkit/Spigot/Paper companion server plugin providing permission control and policy synchronization.
 */
public class MacroModServerPlugin extends JavaPlugin implements Listener {

    public static final String POLICY_CHANNEL = "macromod:policy";
    private final Gson gson = new Gson();
    private ServerPolicy policy = new ServerPolicy();

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadPolicyFromConfig();

        getServer().getMessenger().registerOutgoingPluginChannel(this, POLICY_CHANNEL);
        getServer().getPluginManager().registerEvents(this, this);

        org.bukkit.command.PluginCommand cmd = getCommand("macromod");
        if (cmd != null) {
            cmd.setExecutor(this);
        }

        getLogger().info("MacroModServer companion plugin enabled (Channel: " + POLICY_CHANNEL + ").");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, POLICY_CHANNEL);
        getLogger().info("MacroModServer companion plugin disabled.");
    }

    /**
     * Handles player join events to send initial policy payload.
     *
     * @param event player join event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        sendPolicy(player);
    }

    /**
     * Sends active permission policy payload to a target player.
     *
     * @param player target player
     */
    public void sendPolicy(Player player) {
        try {
            ServerPolicy effectivePolicy = getEffectivePolicy(player);
            String json = gson.toJson(effectivePolicy);
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            player.sendPluginMessage(this, POLICY_CHANNEL, bytes);
        } catch (Exception e) {
            getLogger().warning("Failed to send MacroMod policy payload to " + player.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Resolves effective server policy, querying LuckPerms metadata if present.
     *
     * @param player target player
     * @return resolved {@link ServerPolicy} instance
     */
    private ServerPolicy getEffectivePolicy(Player player) {
        if (getServer().getPluginManager().getPlugin("LuckPerms") == null) {
            return this.policy;
        }

        try {
            Class<?> providerClass = Class.forName("net.luckperms.api.LuckPermsProvider");
            Object luckPerms = providerClass.getMethod("get").invoke(null);
            Object userManager = luckPerms.getClass().getMethod("getUserManager").invoke(luckPerms);
            Object lpUser = userManager.getClass().getMethod("getUser", java.util.UUID.class).invoke(userManager, player.getUniqueId());
            if (lpUser == null) return this.policy;

            Object cachedData = lpUser.getClass().getMethod("getCachedData").invoke(lpUser);
            Object metaData = cachedData.getClass().getMethod("getMetaData").invoke(cachedData);
            java.lang.reflect.Method getMetaVal = metaData.getClass().getMethod("getMetaValue", String.class);

            ServerPolicy playerPolicy = new ServerPolicy();
            playerPolicy.version = this.policy.version;
            playerPolicy.allowMovement = parseMetaBool(getMetaVal, metaData, "macromod.allowMovement", this.policy.allowMovement);
            playerPolicy.allowLook = parseMetaBool(getMetaVal, metaData, "macromod.allowLook", this.policy.allowLook);
            playerPolicy.allowInventoryAutomation = parseMetaBool(getMetaVal, metaData, "macromod.allowInventoryAutomation", this.policy.allowInventoryAutomation);
            playerPolicy.allowScriptFiles = parseMetaBool(getMetaVal, metaData, "macromod.allowScriptFiles", this.policy.allowScriptFiles);
            playerPolicy.allowLooping = parseMetaBool(getMetaVal, metaData, "macromod.allowLooping", this.policy.allowLooping);
            playerPolicy.maxLoopIterations = parseMetaInt(getMetaVal, metaData, "macromod.maxLoopIterations", this.policy.maxLoopIterations);
            playerPolicy.chatRateLimitMs = parseMetaInt(getMetaVal, metaData, "macromod.chatRateLimitMs", this.policy.chatRateLimitMs);
            playerPolicy.commandRateLimitMs = parseMetaInt(getMetaVal, metaData, "macromod.commandRateLimitMs", this.policy.commandRateLimitMs);
            playerPolicy.allowEventTriggers = parseMetaBool(getMetaVal, metaData, "macromod.allowEventTriggers", this.policy.allowEventTriggers);
            return playerPolicy;
        } catch (Exception e) {
            getLogger().warning("Error querying LuckPerms meta for " + player.getName() + ": " + e.getMessage());
            return this.policy;
        }
    }

    /**
     * Parses a boolean LuckPerms metadata entry.
     */
    private static boolean parseMetaBool(java.lang.reflect.Method getMetaVal, Object metaData, String key, boolean def) {
        try {
            String val = (String) getMetaVal.invoke(metaData, key);
            return val != null ? Boolean.parseBoolean(val) : def;
        } catch (Exception e) {
            org.bukkit.Bukkit.getLogger().fine(e.getMessage());
            return def;
        }
    }

    /**
     * Parses an integer LuckPerms metadata entry.
     */
    private static int parseMetaInt(java.lang.reflect.Method getMetaVal, Object metaData, String key, int def) {
        try {
            String val = (String) getMetaVal.invoke(metaData, key);
            if (val == null) return def;
            return Integer.parseInt(val);
        } catch (Exception e) {
            org.bukkit.Bukkit.getLogger().fine(e.getMessage());
            return def;
        }
    }

    /**
     * Loads base policy parameters from config.yml.
     */
    private void loadPolicyFromConfig() {
        policy = new ServerPolicy();
        policy.version = getConfig().getInt("version", 1);
        policy.allowMovement = getConfig().getBoolean("allowMovement", true);
        policy.allowLook = getConfig().getBoolean("allowLook", true);
        policy.allowInventoryAutomation = getConfig().getBoolean("allowInventoryAutomation", true);
        policy.allowScriptFiles = getConfig().getBoolean("allowScriptFiles", true);
        policy.allowLooping = getConfig().getBoolean("allowLooping", true);
        policy.maxLoopIterations = getConfig().getInt("maxLoopIterations", 0);
        policy.chatRateLimitMs = getConfig().getInt("chatRateLimitMs", 0);
        policy.commandRateLimitMs = getConfig().getInt("commandRateLimitMs", 0);
        policy.allowEventTriggers = getConfig().getBoolean("allowEventTriggers", true);
        policy.allowTriggerChat = getConfig().getBoolean("allowTriggerChat", true);
        policy.allowTriggerJoinServer = getConfig().getBoolean("allowTriggerJoinServer", true);
        policy.allowTriggerDeath = getConfig().getBoolean("allowTriggerDeath", true);
        policy.allowTriggerRespawn = getConfig().getBoolean("allowTriggerRespawn", true);
        policy.allowTriggerDimensionChange = getConfig().getBoolean("allowTriggerDimensionChange", true);
        policy.allowTriggerContainerOpen = getConfig().getBoolean("allowTriggerContainerOpen", true);
        policy.allowTriggerContainerClose = getConfig().getBoolean("allowTriggerContainerClose", true);
        policy.allowTriggerTakeDamage = getConfig().getBoolean("allowTriggerTakeDamage", true);
        policy.allowTriggerLowHealth = getConfig().getBoolean("allowTriggerLowHealth", true);
        policy.allowTriggerLowHunger = getConfig().getBoolean("allowTriggerLowHunger", true);
        policy.allowTriggerInventoryFull = getConfig().getBoolean("allowTriggerInventoryFull", true);
        policy.allowTriggerLowDurability = getConfig().getBoolean("allowTriggerLowDurability", true);
        policy.allowTriggerLowArmor = getConfig().getBoolean("allowTriggerLowArmor", true);
        policy.allowTriggerLowAir = getConfig().getBoolean("allowTriggerLowAir", true);
        policy.allowTriggerLowXp = getConfig().getBoolean("allowTriggerLowXp", true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if ("reload".equalsIgnoreCase(args[0])) {
                reloadConfig();
                loadPolicyFromConfig();
                sender.sendMessage(Component.text("[MacroModServer] Configuration reloaded successfully.", NamedTextColor.GREEN));

                for (Player player : getServer().getOnlinePlayers()) {
                    sendPolicy(player);
                }
                return true;
            }
            return false;
        }

        sender.sendMessage(Component.text("MacroModServer v" + getPluginMeta().getVersion() + " companion plugin.", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("Usage: /macromod reload - Reload policy config and re-sync online clients.", NamedTextColor.YELLOW));
        return true;
    }
}
