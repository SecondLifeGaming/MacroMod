package com.github.secondlifegaming.macromod.mixin;

import com.github.secondlifegaming.macromod.config.MacroConfig;
import com.github.secondlifegaming.macromod.config.MacroConfigManager;
import com.github.secondlifegaming.macromod.engine.MacroEngine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseMixin {

    @Inject(method = "onButton", at = @At("HEAD"), require = 0)
    private void onMouseButton(long window, MouseButtonInfo info, int action, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        
        // Trigger mouse bind macros only when in-game (no active screen open) on mouse click press (action == 1) or release (action == 0)
        if (client.gui.screen() == null && client.player != null && (action == 1 || action == 0)) {
            MacroConfig config = MacroConfigManager.getConfig();
            
            // Mask modifiers to only include: Shift=1, Ctrl=2, Alt=4 (ignores CapsLock/NumLock)
            int mods = info.modifiers() & 7;
            String mouseId = "MOUSE_" + info.button();
            if (mods != 0) {
                mouseId = mouseId + "_MOD_" + mods;
            }
            if (action == 0) {
                mouseId = mouseId + "_UP";
            }
            
            // Script macros: only on press, with toggle support
            if (action == 1) {
                String scriptName = config.scriptMacros.get(mouseId);
                if (scriptName != null && !scriptName.isEmpty()) {
                    MacroEngine.runScriptWithToggle(scriptName, mouseId);
                    return;
                }
            }

            // Inline mouse macros: toggle on press, plain run on release
            String command = config.keyMacros.get(mouseId);
            if (command != null && !command.isEmpty()) {
                if (action == 1) {
                    MacroEngine.runWithToggle(command, mouseId);
                } else {
                    MacroEngine.run(command);
                }
            }
        }
    }
}
