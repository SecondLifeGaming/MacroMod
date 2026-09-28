package com.github.secondlifegaming.macromod.gui;

import com.github.secondlifegaming.macromod.config.MacroConfig;
import com.github.secondlifegaming.macromod.config.MacroConfigManager;
import com.github.secondlifegaming.macromod.engine.MacroEngine;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * HUD Overlay screen for displaying interactive custom macro buttons.
 */
public class MacroOverlayScreen extends Screen {

    public MacroOverlayScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        MacroConfig config = MacroConfigManager.getConfig();
        for (MacroConfig.CustomButton customBtn : config.customButtons) {
            this.addRenderableWidget(Button.builder(Component.literal(customBtn.label), button -> {
                if (this.minecraft != null && this.minecraft.player != null) {
                    MacroEngine.run(customBtn.command);
                }
            })
            .bounds(customBtn.x, customBtn.y, customBtn.width, customBtn.height)
            .build());
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        this.extractTransparentBackground(context);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, Component.literal("Macro Overlay (Press ESC to close)"), this.width / 2, 10, 0xAAFFFF00);
    }
}
