package com.github.secondlifegaming.macromod.gui;

import com.github.secondlifegaming.macromod.engine.MacroEngine;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class MacroPromptScreen extends Screen {

    private final String promptLabel;
    private final String before;
    private final String after;
    private EditBox inputBox;

    public MacroPromptScreen(String promptLabel, String before, String after) {
        super(Component.literal("Macro Prompt"));
        this.promptLabel = promptLabel;
        this.before = before;
        this.after = after;
        com.github.secondlifegaming.macromod.MacroModClient.LOGGER.info("Opening MacroPromptScreen with label: '{}'", promptLabel);
    }

    @Override
    protected void init() {
        super.init();

        this.inputBox = new EditBox(this.font, this.width / 2 - 100, this.height / 2, 200, 20, Component.literal(this.promptLabel));
        this.inputBox.setMaxLength(64);
        this.inputBox.setFocused(true);
        this.addRenderableWidget(this.inputBox);

        this.addRenderableWidget(Button.builder(Component.literal("Submit"), button -> this.submit())
                .bounds(this.width / 2 - 100, this.height / 2 + 25, 95, 20)
                .build());

        this.addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> this.onClose())
                .bounds(this.width / 2 + 5, this.height / 2 + 25, 95, 20)
                .build());
    }

    private void submit() {
        String val = this.inputBox.getValue();
        this.onClose();
        // Continue execution by executing the resolved script string
        MacroEngine.run(this.before + val + this.after);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        if (event.key() == 257 || event.key() == 335) { // Enter / Numpad Enter keys
            this.submit();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        // Title: "Macro Prompt" centered near top of dialog area
        context.centeredText(this.font, Component.literal("Macro Prompt"), this.width / 2, this.height / 2 - 40, 0xFFFFFFFF);
        // Field label rendered just above the input box
        context.centeredText(this.font, Component.literal(this.promptLabel + ":"), this.width / 2, this.height / 2 - 12, 0xFFAAAAAA);
        // Hint text rendered below the buttons
        context.centeredText(this.font, Component.literal("Press Enter to submit"), this.width / 2, this.height / 2 + 50, 0xFF555555);
    }
}
