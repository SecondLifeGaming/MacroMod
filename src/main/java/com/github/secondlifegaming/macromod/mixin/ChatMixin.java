package com.github.secondlifegaming.macromod.mixin;

import com.github.secondlifegaming.macromod.config.MacroConfig;
import com.github.secondlifegaming.macromod.config.MacroConfigManager;
import com.github.secondlifegaming.macromod.engine.MacroEngine;
import com.github.secondlifegaming.macromod.MacroModClient;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatMixin {

    @Inject(method = "addClientSystemMessage", at = @At("HEAD"))
    private void onAddClientSystemMessage(Component message, CallbackInfo ci) {
        handleChatMessage(message);
    }

    @Inject(method = "addServerSystemMessage", at = @At("HEAD"))
    private void onAddServerSystemMessage(Component message, CallbackInfo ci) {
        handleChatMessage(message);
    }

    @Inject(method = "addPlayerMessage", at = @At("HEAD"))
    private void onAddPlayerMessage(Component message, MessageSignature signature, GuiMessageTag tag, CallbackInfo ci) {
        handleChatMessage(message);
    }

    @Unique
    private void handleChatMessage(Component message) {
        if (!MacroModClient.isPremium() || message == null) return;
        var policy = com.github.secondlifegaming.macromod.network.ServerPermissionHandler.getPolicy();
        if (!policy.allowEventTriggers || !policy.allowTriggerChat) return;

        MacroConfig config = MacroConfigManager.getConfig();

        if (config.chatTriggerActionEnabled && config.chatTriggerFilter != null && !config.chatTriggerFilter.isBlank()) {
            String text = message.getString();
            if (text.toLowerCase().contains(config.chatTriggerFilter.toLowerCase())) {
                net.minecraft.client.Minecraft.getInstance().execute(() -> MacroEngine.run(config.chatTriggerCommand));
            }
        }
    }
}

