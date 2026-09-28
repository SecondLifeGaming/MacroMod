package com.github.secondlifegaming.macromod.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerPermissionHandler {

    private ServerPermissionHandler() { /* utility class */ }

    private static final Logger LOGGER = LoggerFactory.getLogger("MacroMod/ServerPermissionHandler");
    public static final Identifier POLICY_PACKET_ID = Identifier.fromNamespaceAndPath("macromod", "policy");

    /**
     * Custom packet payload data model for server policy sync.
     *
     * @param json raw policy JSON string
     */
    public record PolicyPayload(String json) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<PolicyPayload> PACKET_TYPE = new CustomPacketPayload.Type<>(POLICY_PACKET_ID);
        public static final StreamCodec<FriendlyByteBuf, PolicyPayload> STREAM_CODEC = CustomPacketPayload.codec(
                (payload, buf) -> buf.writeUtf(payload.json),
                buf -> new PolicyPayload(buf.readUtf())
        );

        /**
         * {@inheritDoc}
         */
        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return PACKET_TYPE;
        }
    }

    public static final Identifier HELLO_PACKET_ID = Identifier.fromNamespaceAndPath("macromod", "hello");

    /**
     * Custom packet payload data model for client discovery handshake.
     *
     * @param clientVersion client mod version string
     */
    public record HelloPayload(String clientVersion) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<HelloPayload> PACKET_TYPE = new CustomPacketPayload.Type<>(HELLO_PACKET_ID);
        public static final StreamCodec<FriendlyByteBuf, HelloPayload> STREAM_CODEC = CustomPacketPayload.codec(
                (payload, buf) -> buf.writeUtf(payload.clientVersion),
                buf -> new HelloPayload(buf.readUtf())
        );

        /**
         * {@inheritDoc}
         */
        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return PACKET_TYPE;
        }
    }

    private static ServerPolicy currentPolicy = new ServerPolicy();
    private static boolean policyReceived = false;

    /**
     * Data model holding server permission policy flags.
     */
    public static class ServerPolicy {
        public int version = 1;
        public boolean allowMovement = true;
        public boolean allowLook = true;
        public boolean allowInventoryAutomation = true;
        public boolean allowScriptFiles = true;
        public boolean allowLooping = true;
        public int maxLoopIterations = 0;
        public int chatRateLimitMs = 0;
        public int commandRateLimitMs = 0;
        public boolean allowEventTriggers = true;
        public boolean allowTriggerChat = true;
        public boolean allowTriggerJoinServer = true;
        public boolean allowTriggerDeath = true;
        public boolean allowTriggerRespawn = true;
        public boolean allowTriggerDimensionChange = true;
        public boolean allowTriggerContainerOpen = true;
        public boolean allowTriggerContainerClose = true;
        public boolean allowTriggerTakeDamage = true;
        public boolean allowTriggerLowHealth = true;
        public boolean allowTriggerLowHunger = true;
        public boolean allowTriggerInventoryFull = true;
        public boolean allowTriggerLowDurability = true;
        public boolean allowTriggerLowArmor = true;
        public boolean allowTriggerLowAir = true;
        public boolean allowTriggerLowXp = true;
    }

    /**
     * Initializes network packet listeners and handshake events.
     */
    public static void init() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> resetToDefaults());

        try {
            try {
                var playS2C = net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.class.getMethod("playS2C");
                Object reg = playS2C.invoke(null);
                reg.getClass().getMethod("register", CustomPacketPayload.Type.class, StreamCodec.class)
                        .invoke(reg, PolicyPayload.PACKET_TYPE, PolicyPayload.STREAM_CODEC);
            } catch (Throwable _) { /* Ignore if method signature differs */ }

            try {
                var playC2S = net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.class.getMethod("playC2S");
                Object reg = playC2S.invoke(null);
                reg.getClass().getMethod("register", CustomPacketPayload.Type.class, StreamCodec.class)
                        .invoke(reg, HelloPayload.PACKET_TYPE, HelloPayload.STREAM_CODEC);
            } catch (Throwable _) { /* Ignore if method signature differs */ }

            ClientPlayNetworking.registerGlobalReceiver(PolicyPayload.PACKET_TYPE, (payload, context) -> {
                if (payload != null && payload.json() != null) {
                    context.client().execute(() -> parseAndApplyPolicy(payload.json()));
                }
            });
        } catch (Throwable e) {
            LOGGER.warn("Could not register ClientPlayNetworking receiver for macromod:policy: {}", e.getMessage());
        }

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            var config = com.github.secondlifegaming.macromod.config.MacroConfigManager.getConfig();
            if (config.sendServerDiscoveryPing && ClientPlayNetworking.canSend(HelloPayload.PACKET_TYPE)) {
                String version = net.fabricmc.loader.api.FabricLoader.getInstance()
                        .getModContainer("macromod")
                        .map(c -> c.getMetadata().getVersion().getFriendlyString())
                        .orElse("0.0.6");
                ClientPlayNetworking.send(new HelloPayload(version));
                LOGGER.info("Sent macromod:hello packet to server.");
            }
        });
    }

    /**
     * Parses and applies a raw JSON server policy.
     *
     * @param jsonStr policy JSON payload
     */
    public static void parseAndApplyPolicy(String jsonStr) {
        if (jsonStr == null || jsonStr.isBlank()) return;
        try {
            com.google.gson.Gson gson = new com.google.gson.Gson();
            ServerPolicy policy = gson.fromJson(jsonStr, ServerPolicy.class);
            if (policy != null) {
                currentPolicy = policy;
                policyReceived = true;
                LOGGER.info("Applied macromod:policy settings from server.");
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to parse macromod:policy payload JSON: {}", jsonStr, e);
        }
    }

    /**
     * Resets server policy restriction state back to default open settings.
     */
    public static void resetToDefaults() {
        currentPolicy = new ServerPolicy();
        policyReceived = false;
    }

    /**
     * Gets the active server policy settings.
     *
     * @return current {@link ServerPolicy} instance
     */
    public static ServerPolicy getPolicy() { return currentPolicy; }

    /**
     * Checks if a server policy payload has been received.
     *
     * @return {@code true} if policy received; {@code false} otherwise
     */
    public static boolean isPolicyReceived() { return policyReceived; }

    /**
     * Checks if movement automation is allowed by the server.
     *
     * @return {@code true} if allowed; {@code false} otherwise
     */
    public static boolean isMovementAllowed() { return currentPolicy.allowMovement; }

    /**
     * Checks if camera look automation is allowed by the server.
     *
     * @return {@code true} if allowed; {@code false} otherwise
     */
    public static boolean isLookAllowed() { return currentPolicy.allowLook; }

    /**
     * Checks if inventory automation is allowed by the server.
     *
     * @return {@code true} if allowed; {@code false} otherwise
     */
    public static boolean isInventoryAllowed() { return currentPolicy.allowInventoryAutomation; }

    /**
     * Checks if event threshold triggers are allowed by the server.
     *
     * @return {@code true} if allowed; {@code false} otherwise
     */
    public static boolean areEventTriggersAllowed() { return currentPolicy.allowEventTriggers; }

    /**
     * Gets the chat rate limit delay in milliseconds.
     *
     * @return chat rate limit in milliseconds
     */
    public static int getChatRateLimitMs() { return currentPolicy.chatRateLimitMs; }

    /**
     * Gets the command rate limit delay in milliseconds.
     *
     * @return command rate limit in milliseconds
     */
    public static int getCommandRateLimitMs() { return currentPolicy.commandRateLimitMs; }
}

