package com.github.secondlifegaming.macromod.mixin;

import com.github.secondlifegaming.macromod.MacroModClient;
import com.github.secondlifegaming.macromod.config.MacroConfig;
import com.github.secondlifegaming.macromod.config.MacroConfigManager;
import com.github.secondlifegaming.macromod.engine.MacroEngine;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class WorldLoadMixin {

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void onHandleLogin(ClientboundLoginPacket packet, CallbackInfo ci) {
        triggerWorldLoad();
    }

    @Inject(method = "handleRespawn", at = @At("RETURN"))
    private void onHandleRespawn(ClientboundRespawnPacket packet, CallbackInfo ci) {
        triggerWorldLoad();
    }

    private void triggerWorldLoad() {
        if (!MacroModClient.isPremium()) return;
        MacroConfig config = MacroConfigManager.getConfig();
        if (config.worldLoadActionEnabled) {
            net.minecraft.client.Minecraft.getInstance().execute(() -> MacroEngine.run(config.worldLoadCommand));
        }
    }
}
