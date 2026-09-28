package com.github.secondlifegaming.macromod.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardMixin {

    @Inject(method = "onKey(JIIII)V", at = @At("HEAD"), require = 0)
    private void onKey262(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        InputConstants.Key keyObj = InputConstants.Type.KEYBOARD.getOrCreate(key);
        if (keyObj != null) {
            com.github.secondlifegaming.macromod.MacroModClient.handleKeyObjectEvent(keyObj, action, modifiers);
        }
    }

    @Inject(method = "keyPress(JILnet/minecraft/client/input/KeyEvent;)V", at = @At("HEAD"), require = 0)
    private void onKeyPress263(long window, int action, KeyEvent event, CallbackInfo ci) {
        if (event != null) {
            InputConstants.Key keyObj = InputConstants.getKey(event);
            if (keyObj != null) {
                com.github.secondlifegaming.macromod.MacroModClient.handleKeyObjectEvent(keyObj, action, event.modifiers());
            }
        }
    }
}




