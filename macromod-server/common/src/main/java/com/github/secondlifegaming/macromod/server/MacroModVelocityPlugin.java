package com.github.secondlifegaming.macromod.server;

import com.github.secondlifegaming.macromod.server.common.ServerPolicy;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;

/**
 * Velocity proxy plugin implementation for MacroMod permissions.
 */
@Plugin(
    id = "macromodserver",
    name = "MacroModServer",
    version = "0.1.2-beta",
    description = "Universal companion server plugin for MacroMod client permissions and policy control.",
    authors = {"SecondLifeGaming", "westkevin12"}
)
public class MacroModVelocityPlugin {

    public static final MinecraftChannelIdentifier POLICY_CHANNEL =
        MinecraftChannelIdentifier.from("macromod:policy");

    private final ProxyServer server;
    private final Logger logger;
    private final Gson gson = new Gson();
    private final ServerPolicy policy = new ServerPolicy();

    /**
     * Constructs a {@link MacroModVelocityPlugin} instance with dependency injection.
     *
     * @param server Velocity proxy server instance
     * @param logger Velocity SLF4J logger
     */
    @Inject
    public MacroModVelocityPlugin(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    /**
     * Handles proxy initialization events and registers plugin messaging channels.
     *
     * @param event proxy initialization event
     */
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getChannelRegistrar().register(POLICY_CHANNEL);
        logger.info("MacroModServer Velocity proxy plugin enabled (Channel: {}).", POLICY_CHANNEL);
    }

    /**
     * Handles player server connection events and sends restriction policy payloads.
     *
     * @param event server connected event
     */
    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        try {
            String json = gson.toJson(policy);
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            player.sendPluginMessage(POLICY_CHANNEL, bytes);
        } catch (Exception e) {
            logger.warn("Failed to send MacroMod policy payload to {}: {}", player.getUsername(), e.getMessage());
        }
    }
}
