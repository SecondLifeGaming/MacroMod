package com.github.secondlifegaming.macromod.gui;

import com.github.secondlifegaming.macromod.config.MacroConfig;
import com.github.secondlifegaming.macromod.config.MacroConfigManager;
import com.github.secondlifegaming.macromod.service.MacroFeatureFactory;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class MacroConfigScreen extends Screen {

    private enum Tab {
        KEYS,
        EVENTS,
        BUTTONS,
        SCRIPTS
    }

    private static final String[][] KEY_LABELS = {
        {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "="},
        {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
        {"A", "S", "D", "F", "G", "H", "J", "K", "L"},
        {"Z", "X", "C", "V", "B", "N", "M"},
        {"Shift", "Ctrl", "Alt", "Space", "Enter", "Back"},
        {"L-Click", "R-Click", "M-Click", "^", "v", "<", ">"}
    };

    protected static final int[][] KEY_CODES = {
        {GLFW.GLFW_KEY_1, GLFW.GLFW_KEY_2, GLFW.GLFW_KEY_3, GLFW.GLFW_KEY_4, GLFW.GLFW_KEY_5, GLFW.GLFW_KEY_6, GLFW.GLFW_KEY_7, GLFW.GLFW_KEY_8, GLFW.GLFW_KEY_9, GLFW.GLFW_KEY_0, GLFW.GLFW_KEY_MINUS, GLFW.GLFW_KEY_EQUAL},
        {GLFW.GLFW_KEY_Q, GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_E, GLFW.GLFW_KEY_R, GLFW.GLFW_KEY_T, GLFW.GLFW_KEY_Y, GLFW.GLFW_KEY_U, GLFW.GLFW_KEY_I, GLFW.GLFW_KEY_O, GLFW.GLFW_KEY_P},
        {GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_D, GLFW.GLFW_KEY_F, GLFW.GLFW_KEY_G, GLFW.GLFW_KEY_H, GLFW.GLFW_KEY_J, GLFW.GLFW_KEY_K, GLFW.GLFW_KEY_L},
        {GLFW.GLFW_KEY_Z, GLFW.GLFW_KEY_X, GLFW.GLFW_KEY_C, GLFW.GLFW_KEY_V, GLFW.GLFW_KEY_B, GLFW.GLFW_KEY_N, GLFW.GLFW_KEY_M},
        {GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_SPACE, GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_BACKSPACE},
        {-100, -101, -102, GLFW.GLFW_KEY_UP, GLFW.GLFW_KEY_DOWN, GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_RIGHT}
    };

    private static final String THRESHOLD_LITERAL = "Threshold";
    private static final String ARROW_UP = "\u25b2";
    private static final String ARROW_DOWN = "\u25bc";
    private static final int EDITOR_VISIBLE_LINES = 8;
    private static final int EDITOR_ROW_HEIGHT    = 14;

    private Tab activeTab = Tab.KEYS;
    private int eventsScrollOffset = 0;
    private MacroConfig.CustomButton draggedButton = null;
    private double dragOffsetX;
    private double dragOffsetY;

    // Selected key variables
    private int selectedKeyCode = -1;
    private String selectedKeyId = null;
    private String selectedKeyLabel = null;

    // Selected button variable
    private MacroConfig.CustomButton selectedButton = null;

    // Modifier toggles
    private boolean modifierCtrl = false;
    private boolean modifierShift = false;
    private boolean modifierAlt = false;

    // Scripts tab state
    private String editingScriptName = null;
    private final List<String> editorLines = new ArrayList<>();
    private int editorScrollOffset = 0;
    private String newScriptNameValue = "";

    public MacroConfigScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();

        // 1. Render Tab Selectors at the top of the screen
        boolean isPremium = com.github.secondlifegaming.macromod.MacroModClient.isPremium();
        int tabCount = isPremium ? 4 : 2;
        int tabWidth = 90;
        int tabSpacing = 6;
        int totalTabsWidth = (tabWidth * tabCount) + (tabSpacing * (tabCount - 1));
        int startTabX = this.width / 2 - totalTabsWidth / 2;

        int currentTabIdx = 0;
        this.addRenderableWidget(Button.builder(Component.literal("Key Bindings"), button -> {
            this.activeTab = Tab.KEYS;
            this.rebuildWidgets();
        })
        .bounds(startTabX + (tabWidth + tabSpacing) * (currentTabIdx++), 40, tabWidth, 20)
        .build());

        if (isPremium) {
            this.addRenderableWidget(Button.builder(Component.literal("Event Triggers"), button -> {
                this.activeTab = Tab.EVENTS;
                this.rebuildWidgets();
            })
            .bounds(startTabX + (tabWidth + tabSpacing) * (currentTabIdx++), 40, tabWidth, 20)
            .build());
        }

        this.addRenderableWidget(Button.builder(Component.literal("Custom Buttons"), button -> {
            this.activeTab = Tab.BUTTONS;
            this.rebuildWidgets();
        })
        .bounds(startTabX + (tabWidth + tabSpacing) * (currentTabIdx++), 40, tabWidth, 20)
        .build());

        if (isPremium) {
            this.addRenderableWidget(Button.builder(Component.literal("Scripts"), button -> {
                this.activeTab = Tab.SCRIPTS;
                this.rebuildWidgets();
            })
            .bounds(startTabX + (tabWidth + tabSpacing) * currentTabIdx, 40, tabWidth, 20)
            .build());
        }

        // 2. Render Tab-Specific Content
        MacroConfig config = MacroConfigManager.getConfig();

        if (this.activeTab == Tab.KEYS) {
            this.initKeysTab(config);
        } else if (this.activeTab == Tab.EVENTS && isPremium) {
            this.initEventsTab(config);
        } else if (this.activeTab == Tab.BUTTONS) {
            this.initButtonsTab(config);
        } else if (this.activeTab == Tab.SCRIPTS && isPremium) {
            this.initScriptsTab(config);
        }

        // 3. Render Close Button at the bottom
        this.addRenderableWidget(Button.builder(Component.literal("Close"), button -> this.onClose())
                .bounds(this.width / 2 - 100, this.height - 30, 200, 20)
                .build());
    }

    private void initKeysTab(MacroConfig config) {
        int startY = this.height / 2 - 55;
        int keySize = 16;
        int spacing = 2;

        this.renderVisualKeyboard(config, startY, keySize, spacing);

        if (this.selectedKeyId != null) {
            int panelX = this.width / 2 + 52;
            this.renderKeyEditPanel(config, panelX, startY);
        }
    }

    private void renderVisualKeyboard(MacroConfig config, int startY, int keySize, int spacing) {
        for (int row = 0; row < KEY_LABELS.length; row++) {
            int startX = this.width / 2 - 200 + (row * 4); // offset row start slightly for typewriter aesthetic

            int currentX = startX;
            for (int col = 0; col < KEY_LABELS[row].length; col++) {
                String label = KEY_LABELS[row][col];
                int code = KEY_CODES[row][col];

                // Check configuration indicators on keys
                boolean isConfigured = this.isKeyConfigured(config, code);
                String displayLabel = label + (isConfigured ? "*" : "");

                int btnWidth = Math.max(keySize + 4, this.font.width(displayLabel) + 8);

                this.addRenderableWidget(Button.builder(Component.literal(displayLabel), button -> {
                    this.selectedKeyCode = code;
                    this.selectedKeyLabel = label;
                    this.selectedKeyId = this.getModifiedKeyId();
                    this.rebuildWidgets();
                })
                .bounds(currentX, startY + row * (keySize + spacing), btnWidth, keySize)
                .build());

                currentX += btnWidth + spacing;
            }
        }
    }

    private void renderKeyEditPanel(MacroConfig config, int panelX, int panelY) {
        // Render Modifier Toggles
        String ctrlText = "Ctrl: " + (this.modifierCtrl ? "ON" : "OFF");
        this.addRenderableWidget(Button.builder(Component.literal(ctrlText), button -> {
            this.modifierCtrl = !this.modifierCtrl;
            this.updateSelectedKeyId();
        })
        .bounds(panelX, panelY + 20, 48, 16)
        .build());

        String shiftText = "Shift: " + (this.modifierShift ? "ON" : "OFF");
        this.addRenderableWidget(Button.builder(Component.literal(shiftText), button -> {
            this.modifierShift = !this.modifierShift;
            this.updateSelectedKeyId();
        })
        .bounds(panelX + 51, panelY + 20, 50, 16)
        .build());

        String altText = "Alt: " + (this.modifierAlt ? "ON" : "OFF");
        this.addRenderableWidget(Button.builder(Component.literal(altText), button -> {
            this.modifierAlt = !this.modifierAlt;
            this.updateSelectedKeyId();
        })
        .bounds(panelX + 104, panelY + 20, 46, 16)
        .build());

        // Add text box to type the macro command on key press
        EditBox commandEditBox = new EditBox(this.font, panelX, panelY + 50, 150, 18, Component.literal("On Press Command"));
        commandEditBox.setMaxLength(128);
        commandEditBox.setValue(config.keyMacros.getOrDefault(this.selectedKeyId, ""));
        
        // Save automatically on text change
        commandEditBox.setResponder(val -> {
            if (val.trim().isEmpty()) {
                config.keyMacros.remove(this.selectedKeyId);
            } else {
                config.keyMacros.put(this.selectedKeyId, val);
                clearKeyFromMap(config.scriptMacros, this.selectedKeyId);
            }
            MacroConfigManager.save();
        });
        this.addRenderableWidget(commandEditBox);

        // Add text box to type the macro command on key release
        String upKeyId = this.selectedKeyId + "_UP";
        EditBox releaseEditBox = new EditBox(this.font, panelX, panelY + 90, 150, 18, Component.literal("On Release Command"));
        releaseEditBox.setMaxLength(128);
        releaseEditBox.setValue(config.keyMacros.getOrDefault(upKeyId, ""));
        
        // Save automatically on text change
        releaseEditBox.setResponder(val -> {
            if (val.trim().isEmpty()) {
                config.keyMacros.remove(upKeyId);
            } else {
                config.keyMacros.put(upKeyId, val);
                clearKeyFromMap(config.scriptMacros, upKeyId);
            }
            MacroConfigManager.save();
        });
        this.addRenderableWidget(releaseEditBox);
    }

    private void initEventsTab(MacroConfig config) {
        int startX = this.width / 2 - 200;
        int startY = 82;
        int spacing = 22;
        int maxVisibleEvents = Math.max(4, (this.height - 120) / spacing);
        int maxScroll = Math.max(0, 15 - maxVisibleEvents);
        this.eventsScrollOffset = Math.clamp(this.eventsScrollOffset, 0, maxScroll);

        // Scroll Up / Down Buttons
        this.addRenderableWidget(Button.builder(Component.literal(ARROW_UP), button -> {
            if (this.eventsScrollOffset > 0) {
                this.eventsScrollOffset--;
                this.rebuildWidgets();
            }
        }).bounds(startX + 385, startY, 20, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal(ARROW_DOWN), button -> {
            int maxSc = Math.max(0, 15 - maxVisibleEvents);
            if (this.eventsScrollOffset < maxSc) {
                this.eventsScrollOffset++;
                this.rebuildWidgets();
            }
        }).bounds(startX + 385, startY + 24, 20, 20).build());

        for (int i = 0; i < 15; i++) {
            int visibleRow = i - this.eventsScrollOffset;
            if (visibleRow < 0 || visibleRow >= maxVisibleEvents) continue;
            int rowY = startY + visibleRow * spacing;
            addEventRow(config, i, startX, rowY);
        }
    }

    private record ThresholdRowSpec(String label, boolean enabled, java.util.function.Consumer<Boolean> toggleConsumer, String threshVal, java.util.function.Consumer<String> threshConsumer, String cmdVal, java.util.function.Consumer<String> cmdConsumer, int maxThreshLen) {}

    private void addEventRow(MacroConfig config, int idx, int startX, int rowY) {
        switch (idx) {
            case 0 -> addThresholdEventRow(new ThresholdRowSpec("Durability", config.lowDurabilityActionEnabled, val -> config.lowDurabilityActionEnabled = val, String.valueOf(config.durabilityThresholdPercent), val -> config.durabilityThresholdPercent = Double.parseDouble(val), config.lowDurabilityCommand, val -> config.lowDurabilityCommand = val, 5), startX, rowY);
            case 1 -> addThresholdEventRow(new ThresholdRowSpec("Health", config.lowHealthActionEnabled, val -> config.lowHealthActionEnabled = val, String.valueOf(config.healthThresholdPercent), val -> config.healthThresholdPercent = Double.parseDouble(val), config.lowHealthCommand, val -> config.lowHealthCommand = val, 5), startX, rowY);
            case 2 -> addThresholdEventRow(new ThresholdRowSpec("Hunger", config.lowHungerActionEnabled, val -> config.lowHungerActionEnabled = val, String.valueOf(config.hungerThresholdLevel), val -> config.hungerThresholdLevel = Integer.parseInt(val), config.lowHungerCommand, val -> config.lowHungerCommand = val, 2), startX, rowY);
            case 3 -> addSimpleEventRow("Inv Full", config.inventoryFullActionEnabled, val -> config.inventoryFullActionEnabled = val, config.inventoryFullCommand, val -> config.inventoryFullCommand = val, startX, rowY);
            case 4 -> addSimpleEventRow("Join Server", config.joinServerActionEnabled, val -> config.joinServerActionEnabled = val, config.joinServerCommand, val -> config.joinServerCommand = val, startX, rowY);
            case 5 -> addSimpleEventRow("On Death", config.deathActionEnabled, val -> config.deathActionEnabled = val, config.deathCommand, val -> config.deathCommand = val, startX, rowY);
            case 6 -> addSimpleEventRow("On Respawn", config.respawnActionEnabled, val -> config.respawnActionEnabled = val, config.respawnCommand, val -> config.respawnCommand = val, startX, rowY);
            case 7 -> addSimpleEventRow("Dimension", config.dimensionChangeActionEnabled, val -> config.dimensionChangeActionEnabled = val, config.dimensionChangeCommand, val -> config.dimensionChangeCommand = val, startX, rowY);
            case 8 -> addChatFilterRow(config, startX, rowY);
            case 9 -> addSimpleEventRow("Open GUI", config.containerOpenActionEnabled, val -> config.containerOpenActionEnabled = val, config.containerOpenCommand, val -> config.containerOpenCommand = val, startX, rowY);
            case 10 -> addSimpleEventRow("Close GUI", config.containerCloseActionEnabled, val -> config.containerCloseActionEnabled = val, config.containerCloseCommand, val -> config.containerCloseCommand = val, startX, rowY);
            case 11 -> addSimpleEventRow("On Damage", config.takeDamageActionEnabled, val -> config.takeDamageActionEnabled = val, config.takeDamageCommand, val -> config.takeDamageCommand = val, startX, rowY);
            case 12 -> addThresholdEventRow(new ThresholdRowSpec("Low Armor", config.lowArmorActionEnabled, val -> config.lowArmorActionEnabled = val, String.valueOf(config.lowArmorThresholdPercent), val -> config.lowArmorThresholdPercent = Double.parseDouble(val), config.lowArmorCommand, val -> config.lowArmorCommand = val, 5), startX, rowY);
            case 13 -> addThresholdEventRow(new ThresholdRowSpec("Low Air", config.lowAirActionEnabled, val -> config.lowAirActionEnabled = val, String.valueOf(config.lowAirThresholdPercent), val -> config.lowAirThresholdPercent = Double.parseDouble(val), config.lowAirCommand, val -> config.lowAirCommand = val, 5), startX, rowY);
            case 14 -> addThresholdEventRow(new ThresholdRowSpec("Low XP", config.lowXpActionEnabled, val -> config.lowXpActionEnabled = val, String.valueOf(config.lowXpThresholdLevel), val -> config.lowXpThresholdLevel = Integer.parseInt(val), config.lowXpCommand, val -> config.lowXpCommand = val, 3), startX, rowY);
            default -> { /* No-op for unexpected index */ }
        }
    }

    private void addSimpleEventRow(String label, boolean enabled, java.util.function.Consumer<Boolean> toggleConsumer, String cmdVal, java.util.function.Consumer<String> cmdConsumer, int startX, int rowY) {
        String text = label + ": " + (enabled ? "ON" : "OFF");
        this.addRenderableWidget(Button.builder(Component.literal(text), button -> {
            toggleConsumer.accept(!enabled);
            MacroConfigManager.save();
            this.rebuildWidgets();
        }).bounds(startX, rowY, 90, 16).build());

        EditBox cmd = new EditBox(this.font, startX + 95, rowY, 285, 16, Component.literal(label + " Command"));
        cmd.setMaxLength(128);
        cmd.setValue(cmdVal);
        cmd.setResponder(val -> { cmdConsumer.accept(val); MacroConfigManager.save(); });
        this.addRenderableWidget(cmd);
    }

    private void addThresholdEventRow(ThresholdRowSpec spec, int startX, int rowY) {
        String text = spec.label() + ": " + (spec.enabled() ? "ON" : "OFF");
        this.addRenderableWidget(Button.builder(Component.literal(text), button -> {
            spec.toggleConsumer().accept(!spec.enabled());
            MacroConfigManager.save();
            this.rebuildWidgets();
        }).bounds(startX, rowY, 90, 16).build());

        EditBox thresh = new EditBox(this.font, startX + 95, rowY, 30, 16, Component.literal(THRESHOLD_LITERAL));
        thresh.setMaxLength(spec.maxThreshLen());
        thresh.setValue(spec.threshVal());
        thresh.setResponder(val -> {
            try { spec.threshConsumer().accept(val); MacroConfigManager.save(); } catch (NumberFormatException _) { /* Ignore invalid number format */ }
        });
        this.addRenderableWidget(thresh);

        EditBox cmd = new EditBox(this.font, startX + 130, rowY, 250, 16, Component.literal(spec.label() + " Command"));
        cmd.setMaxLength(128);
        cmd.setValue(spec.cmdVal());
        cmd.setResponder(val -> { spec.cmdConsumer().accept(val); MacroConfigManager.save(); });
        this.addRenderableWidget(cmd);
    }

    private void addChatFilterRow(MacroConfig config, int startX, int rowY) {
        String text = "Chat Filter: " + (config.chatTriggerActionEnabled ? "ON" : "OFF");
        this.addRenderableWidget(Button.builder(Component.literal(text), button -> {
            config.chatTriggerActionEnabled = !config.chatTriggerActionEnabled;
            MacroConfigManager.save();
            this.rebuildWidgets();
        }).bounds(startX, rowY, 90, 16).build());

        EditBox filter = new EditBox(this.font, startX + 95, rowY, 80, 16, Component.literal("Chat Keyword"));
        filter.setMaxLength(32);
        filter.setValue(config.chatTriggerFilter);
        filter.setResponder(val -> { config.chatTriggerFilter = val; MacroConfigManager.save(); });
        this.addRenderableWidget(filter);

        EditBox cmd = new EditBox(this.font, startX + 180, rowY, 200, 16, Component.literal("Chat Command"));
        cmd.setMaxLength(128);
        cmd.setValue(config.chatTriggerCommand);
        cmd.setResponder(val -> { config.chatTriggerCommand = val; MacroConfigManager.save(); });
        this.addRenderableWidget(cmd);
    }

    private void initButtonsTab(MacroConfig config) {
        // "+" Button to add new clickable macros
        this.addRenderableWidget(Button.builder(Component.literal("+ Add Button"), button -> {
            MacroConfig.CustomButton newBtn = new MacroConfig.CustomButton("Macro " + (config.customButtons.size() + 1), "say hello", this.width / 2 - 80, this.height / 2 - 10, 100, 20);
            config.customButtons.add(newBtn);
            this.selectedButton = newBtn;
            MacroConfigManager.save();
            this.rebuildWidgets();
        })
        .bounds(10, 10, 90, 20)
        .build());

        // Render active custom buttons using standard Button widgets
        for (MacroConfig.CustomButton customBtn : config.customButtons) {
            String labelText = customBtn.label + (customBtn == this.selectedButton ? " *" : "");
            this.addRenderableWidget(Button.builder(Component.literal(labelText), button -> {
                this.selectedButton = customBtn;
                this.rebuildWidgets();
            })
            .bounds(customBtn.x, customBtn.y, customBtn.width, customBtn.height)
            .build());
        }

        // Render Button Edit Panel on the right side
        if (this.selectedButton != null) {
            int panelX = Math.max(10, this.width - 170);
            int panelY = 85;
            int panelWidth = 160;

            // Label Input
            EditBox labelBox = new EditBox(this.font, panelX, panelY + 16, panelWidth, 16, Component.literal("Button Label"));
            labelBox.setMaxLength(32);
            labelBox.setValue(this.selectedButton.label);
            labelBox.setResponder(val -> {
                this.selectedButton.label = val;
                MacroConfigManager.save();
            });
            this.addRenderableWidget(labelBox);

            // Command Input
            EditBox commandBox = new EditBox(this.font, panelX, panelY + 48, panelWidth, 16, Component.literal("Command"));
            commandBox.setMaxLength(128);
            commandBox.setValue(this.selectedButton.command);
            commandBox.setResponder(val -> {
                this.selectedButton.command = val;
                MacroConfigManager.save();
            });
            this.addRenderableWidget(commandBox);

            // Move Handle Button (4-directional move icon ✢)
            // Clicking and holding/dragging this button moves the custom button live, releasing locks position
            this.addRenderableWidget(Button.builder(Component.literal("✢ Move Button"), button -> {
                // Button press callback (backup trigger)
                this.draggedButton = this.selectedButton;
                this.dragOffsetX = this.selectedButton.width / 2.0;
                this.dragOffsetY = this.selectedButton.height / 2.0;
            })
            .bounds(panelX, panelY + 80, panelWidth, 20)
            .build());

            // Delete Button
            this.addRenderableWidget(Button.builder(Component.literal("Delete Button"), button -> {
                config.customButtons.remove(this.selectedButton);
                if (this.draggedButton == this.selectedButton) {
                    this.draggedButton = null;
                }
                this.selectedButton = null;
                MacroConfigManager.save();
                this.rebuildWidgets();
            })
            .bounds(panelX, panelY + 108, panelWidth, 20)
            .build());
        }
    }

    // -------------------------------------------------------------------------
    // Scripts Tab
    // -------------------------------------------------------------------------

    private void initScriptsTab(MacroConfig config) {
        initScriptsLibraryPanel(config);
        if (this.editingScriptName != null) {
            initScriptsEditorPanel();
        }
    }

    /** Left panel: script library list, new-script creation, delete buttons. */
    private void initScriptsLibraryPanel(MacroConfig config) {
        int leftX = 10;

        // New script name box
        EditBox nameBox = new EditBox(this.font, leftX, 82, 96, 16, Component.literal("script_name"));
        nameBox.setMaxLength(32);
        nameBox.setValue(this.newScriptNameValue);
        nameBox.setResponder(val -> this.newScriptNameValue = val);
        this.addRenderableWidget(nameBox);

        // [New] button — creates and immediately opens the named script
        this.addRenderableWidget(Button.builder(Component.literal("New"), button -> {
            String name = this.newScriptNameValue.trim().replaceAll("[^a-zA-Z0-9_\\-]", "_");
            if (!name.isEmpty()) {
                List<String> initial = new ArrayList<>();
                initial.add("# " + name);
                initial.add("");
                MacroFeatureFactory.getService().saveScriptLines(name, initial);
                this.editingScriptName = name;
                this.editorLines.clear();
                this.editorLines.addAll(initial);
                this.editorScrollOffset = 0;
                this.newScriptNameValue = "";
                this.rebuildWidgets();
            }
        }).bounds(110, 82, 30, 16).build());

        // Script list
        List<String> scripts = MacroFeatureFactory.getService().listScripts();
        int maxListItems = (this.height - 120) / 18;
        for (int i = 0; i < Math.min(scripts.size(), maxListItems); i++) {
            final String sn = scripts.get(i);
            int listY = 104 + i * 18;
            addScriptListEntry(config, sn, leftX, listY);
        }
    }

    /** Adds one script entry row (select button + delete button) to the library list. */
    private void addScriptListEntry(MacroConfig config, String sn, int leftX, int listY) {
        String label = sn.equals(this.editingScriptName) ? "\u25ba " + sn : sn;
        this.addRenderableWidget(Button.builder(Component.literal(label), button -> {
            // Auto-save current script before switching
            if (this.editingScriptName != null && !this.editorLines.isEmpty()) {
                saveCurrentEditorLines();
            }
            this.editingScriptName = sn;
            List<String> loaded = MacroFeatureFactory.getService().loadScriptLines(sn);
            this.editorLines.clear();
            this.editorLines.addAll(loaded.isEmpty() ? List.of("") : loaded);
            this.editorScrollOffset = 0;
            this.rebuildWidgets();
        }).bounds(leftX, listY, 96, 16).build());

        this.addRenderableWidget(Button.builder(Component.literal("Del"), button -> {
            MacroFeatureFactory.getService().deleteScript(sn);
            config.scriptMacros.values().removeIf(v -> v.equals(sn));
            MacroConfigManager.save();
            if (sn.equals(this.editingScriptName)) {
                this.editingScriptName = null;
                this.editorLines.clear();
            }
            this.rebuildWidgets();
        }).bounds(110, listY, 30, 16).build());
    }

    /** Strips trailing blank lines from {@link #editorLines} and saves to disk. */
    private void saveCurrentEditorLines() {
        List<String> toSave = new ArrayList<>(this.editorLines);
        while (!toSave.isEmpty() && toSave.get(toSave.size() - 1).isBlank()) {
            toSave.removeLast();
        }
        if (!toSave.isEmpty()) MacroFeatureFactory.getService().saveScriptLines(this.editingScriptName, toSave);
    }

    /** Right panel: stacked EditBox editor, scroll/line controls, save button, key assignment. */
    private void initScriptsEditorPanel() {
        int rightX      = 150;
        int editorWidth = Math.max(100, this.width - rightX - 10);
        int editorY     = 82;

        // Clamp scroll offset
        this.editorScrollOffset = Math.clamp(
            this.editorScrollOffset, 0, Math.max(0, this.editorLines.size() - EDITOR_VISIBLE_LINES));
        if (this.editorLines.isEmpty()) this.editorLines.add("");

        int boxWidth = editorWidth - 36;

        // Stacked single-line EditBoxes — one per visible script line with Move Up / Move Down buttons
        for (int row = 0; row < EDITOR_VISIBLE_LINES; row++) {
            int lineIdx = this.editorScrollOffset + row;
            while (this.editorLines.size() <= lineIdx) this.editorLines.add("");

            int rowY = editorY + row * EDITOR_ROW_HEIGHT;

            EditBox lineBox = new EditBox(
                this.font, rightX, rowY,
                boxWidth, EDITOR_ROW_HEIGHT - 1, Component.empty());
            lineBox.setMaxLength(256);
            lineBox.setValue(this.editorLines.get(lineIdx));
            final int fi = lineIdx;
            lineBox.setResponder(val -> {
                while (this.editorLines.size() <= fi) this.editorLines.add("");
                this.editorLines.set(fi, val);
            });
            this.addRenderableWidget(lineBox);

            // Move line UP button
            final int upTarget = lineIdx - 1;
            Button upBtn = Button.builder(Component.literal(ARROW_UP), button -> moveScriptLine(fi, upTarget))
                .bounds(rightX + boxWidth + 2, rowY, 16, EDITOR_ROW_HEIGHT - 1)
                .build();
            if (lineIdx == 0) upBtn.active = false;
            this.addRenderableWidget(upBtn);

            // Move line DOWN button
            final int downTarget = lineIdx + 1;
            Button dnBtn = Button.builder(Component.literal(ARROW_DOWN), button -> moveScriptLine(fi, downTarget))
                .bounds(rightX + boxWidth + 19, rowY, 16, EDITOR_ROW_HEIGHT - 1)
                .build();
            if (lineIdx >= this.editorLines.size() - 1) dnBtn.active = false;
            this.addRenderableWidget(dnBtn);
        }

        int controlY = editorY + EDITOR_VISIBLE_LINES * EDITOR_ROW_HEIGHT + 4;
        addEditorControls(rightX, controlY);
    }

    private void moveScriptLine(int fromIndex, int toIndex) {
        if (fromIndex >= 0 && fromIndex < this.editorLines.size() &&
            toIndex >= 0 && toIndex < this.editorLines.size()) {
            String temp = this.editorLines.get(fromIndex);
            this.editorLines.set(fromIndex, this.editorLines.get(toIndex));
            this.editorLines.set(toIndex, temp);
            this.rebuildWidgets();
        }
    }

    /** Adds scroll buttons, line-management buttons, save button, and key assignment row. */
    private void addEditorControls(int rightX, int controlY) {
        this.addRenderableWidget(Button.builder(Component.literal(ARROW_UP), button -> {
            this.editorScrollOffset = Math.max(0, this.editorScrollOffset - 1);
            this.rebuildWidgets();
        }).bounds(rightX, controlY, 20, 16).build());

        this.addRenderableWidget(Button.builder(Component.literal(ARROW_DOWN), button -> {
            int maxScroll = Math.max(0, this.editorLines.size() - EDITOR_VISIBLE_LINES);
            if (this.editorScrollOffset < maxScroll) this.editorScrollOffset++;
            this.rebuildWidgets();
        }).bounds(rightX + 22, controlY, 20, 16).build());

        this.addRenderableWidget(Button.builder(Component.literal("+Line"), button -> {
            this.editorLines.add("");
            this.editorScrollOffset = Math.max(0, this.editorLines.size() - EDITOR_VISIBLE_LINES);
            this.rebuildWidgets();
        }).bounds(rightX + 46, controlY, 44, 16).build());

        this.addRenderableWidget(Button.builder(Component.literal("-Line"), button -> {
            if (this.editorLines.size() > 1) {
                this.editorLines.removeLast();
                int maxScroll = Math.max(0, this.editorLines.size() - EDITOR_VISIBLE_LINES);
                this.editorScrollOffset = Math.clamp(this.editorScrollOffset, 0, maxScroll);
            }
            this.rebuildWidgets();
        }).bounds(rightX + 92, controlY, 44, 16).build());

        this.addRenderableWidget(Button.builder(Component.literal("Save"),
            button -> saveCurrentEditorLines())
            .bounds(rightX + 140, controlY, 44, 16).build());
    }

    private String getKeyId(int code) {
        if (code == -100) return "MOUSE_0";
        if (code == -101) return "MOUSE_1";
        if (code == -102) return "MOUSE_2";
        if (code >= GLFW.GLFW_KEY_A && code <= GLFW.GLFW_KEY_Z) {
            return "KEY_" + (char) ('A' + (code - GLFW.GLFW_KEY_A));
        }
        if (code >= GLFW.GLFW_KEY_0 && code <= GLFW.GLFW_KEY_9) {
            return "KEY_" + (char) ('0' + (code - GLFW.GLFW_KEY_0));
        }
        return lookupNamedKeyOrCode(code);
    }

    private String lookupNamedKeyOrCode(int code) {
        return switch (code) {
            case GLFW.GLFW_KEY_GRAVE_ACCENT -> "KEY_GRAVE";
            case GLFW.GLFW_KEY_SPACE -> "KEY_SPACE";
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> "KEY_ENTER";
            case GLFW.GLFW_KEY_BACKSPACE -> "KEY_BACKSPACE";
            case GLFW.GLFW_KEY_TAB -> "KEY_TAB";
            case GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT -> "KEY_SHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL -> "KEY_CTRL";
            case GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT -> "KEY_ALT";
            case GLFW.GLFW_KEY_UP -> "KEY_UP";
            case GLFW.GLFW_KEY_DOWN -> "KEY_DOWN";
            case GLFW.GLFW_KEY_LEFT -> "KEY_LEFT";
            case GLFW.GLFW_KEY_RIGHT -> "KEY_RIGHT";
            default -> "KEY_" + code;
        };
    }


    private String getModifiedKeyId() {
        if (this.selectedKeyCode == -1) return null;
        
        String baseId = getKeyId(this.selectedKeyCode);
        
        // Calculate GLFW modifier mask: Shift = 1, Ctrl = 2, Alt = 4
        int mask = 0;
        if (this.modifierShift) mask |= 1;
        if (this.modifierCtrl) mask |= 2;
        if (this.modifierAlt) mask |= 4;
        
        if (mask == 0) {
            return baseId;
        } else {
            return baseId + "_MOD_" + mask;
        }
    }

    private static void clearKeyFromMap(java.util.Map<String, String> map, String keyId) {
        if (map == null || keyId == null) return;
        map.remove(keyId);
        // Clean up legacy numeric key code entries if applicable (e.g. KEY_78 for KEY_N)
        if (keyId.startsWith("KEY_")) {
            com.mojang.blaze3d.platform.InputConstants.Key kObj = com.github.secondlifegaming.macromod.MacroModClient.parseKeyIdToKey(keyId);
            if (kObj != null) {
                map.remove("KEY_" + kObj.getValue());
            }
        }
    }

    private void updateSelectedKeyId() {
        this.selectedKeyId = this.getModifiedKeyId();
        this.rebuildWidgets();
    }

    private boolean isKeyConfigured(MacroConfig config, int code) {
        String baseId = getKeyId(code);
        if (config.keyMacros.containsKey(baseId) && !config.keyMacros.get(baseId).isEmpty()) {
            return true;
        }
        for (int mask = 1; mask <= 7; mask++) {
            String comboId = baseId + "_MOD_" + mask;
            if (config.keyMacros.containsKey(comboId) && !config.keyMacros.get(comboId).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean handled) {
        if (activeTab == Tab.BUTTONS) {
            double mouseX = event.x();
            double mouseY = event.y();
            int button = event.button();

            MacroConfig config = MacroConfigManager.getConfig();
            for (MacroConfig.CustomButton customBtn : config.customButtons) {
                if (button == 0 && mouseX >= customBtn.x && mouseX <= customBtn.x + customBtn.width &&
                    mouseY >= customBtn.y && mouseY <= customBtn.y + customBtn.height) {
                    this.selectedButton = customBtn;
                    this.draggedButton = customBtn;
                    this.dragOffsetX = mouseX - customBtn.x;
                    this.dragOffsetY = mouseY - customBtn.y;
                    this.rebuildWidgets();
                    return true;
                }
            }
        }
        return super.mouseClicked(event, handled);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (activeTab == Tab.BUTTONS && this.draggedButton != null) {
            double mouseX = event.x();
            double mouseY = event.y();
            this.draggedButton.x = (int) (mouseX - this.dragOffsetX);
            this.draggedButton.y = (int) (mouseY - this.dragOffsetY);
            this.rebuildWidgets();
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.draggedButton != null) {
            this.draggedButton = null;
            MacroConfigManager.save();
            this.rebuildWidgets();
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.activeTab == Tab.EVENTS) {
            int maxVisibleEvents = Math.max(4, (this.height - 120) / 22);
            int maxScroll = Math.max(0, 15 - maxVisibleEvents);
            if (verticalAmount > 0 && this.eventsScrollOffset > 0) {
                this.eventsScrollOffset--;
                this.rebuildWidgets();
                return true;
            } else if (verticalAmount < 0 && this.eventsScrollOffset < maxScroll) {
                this.eventsScrollOffset++;
                this.rebuildWidgets();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        Component tabLabel = Component.literal("Active Tab: " + activeTab.name());
        context.centeredText(this.font, tabLabel, this.width / 2, 70, 0xAAAAAA);

        switch (activeTab) {
            case KEYS -> renderKeysTabOverlay(context);
            case EVENTS -> renderEventsTabOverlay(context);
            case BUTTONS -> renderButtonsTabOverlay(context);
            case SCRIPTS -> renderScriptsTabOverlay(context);
        }
    }

    private void renderKeysTabOverlay(GuiGraphicsExtractor context) {
        Component keysHint = Component.literal("Click a key in the visualizer to assign a command macro.");
        context.centeredText(this.font, keysHint, this.width / 2, 90, 0x777777);
        if (this.selectedKeyLabel != null) {
            int panelX = this.width / 2 + 52;
            int panelY = this.height / 2 - 55;
            context.text(this.font, Component.literal("Key: " + this.selectedKeyLabel), panelX, panelY, 0xFFFFFF);
            context.text(this.font, Component.literal("Press:"), panelX, panelY + 39, 0xAAAAAA);
            context.text(this.font, Component.literal("Release:"), panelX, panelY + 79, 0xAAAAAA);
        }
    }

    private void renderEventsTabOverlay(GuiGraphicsExtractor context) {
        int maxVisibleEvents = Math.max(4, (this.height - 120) / 22);
        int endIdx = Math.min(15, this.eventsScrollOffset + maxVisibleEvents);
        Component eventsHint = Component.literal("Configure event actions (Showing " + (this.eventsScrollOffset + 1) + "–" + endIdx + " of 15 - Use mouse wheel or \u25b2/\u25bc to scroll)");
        context.centeredText(this.font, eventsHint, this.width / 2, 68, 0x777777);
    }

    private void renderButtonsTabOverlay(GuiGraphicsExtractor context) {
        Component dragHint = Component.literal("Left-click to select button. Click '✢ Move Button' to drag with mouse. Bind key to {OPENOVERLAY} for HUD.");
        context.centeredText(this.font, dragHint, this.width / 2, 68, 0x777777);

        if (this.selectedButton != null) {
            int panelX = Math.max(10, this.width - 170);
            int panelY = 85;
            context.text(this.font, Component.literal("Edit Selected Button"), panelX, panelY, 0xFFFFFF);
            context.text(this.font, Component.literal("Button Display Label:"), panelX, panelY + 6, 0xAAAAAA);
            context.text(this.font, Component.literal("Command / Action:"), panelX, panelY + 38, 0xAAAAAA);
        }
    }

    private void renderScriptsTabOverlay(GuiGraphicsExtractor context) {
        context.text(this.font, Component.literal("Script Library"), 10, 72, 0xFFFFFF);
        if (this.editingScriptName != null) {
            int totalLines = this.editorLines.size();
            int visibleEnd = Math.min(this.editorScrollOffset + EDITOR_VISIBLE_LINES, totalLines);
            String scrollInfo = " §8(" + (this.editorScrollOffset + 1) + "–" + visibleEnd + " / " + totalLines + " lines)";
            context.text(this.font, Component.literal("Editing: §e" + this.editingScriptName + scrollInfo), 150, 72, 0xFFFFFF);

            MacroConfig renderCfg = MacroConfigManager.getConfig();
            List<String> assignedKeys = new ArrayList<>();
            renderCfg.scriptMacros.forEach((k, v) -> {
                if (this.editingScriptName.equals(v)) assignedKeys.add(k);
            });
            int assignY = 82 + EDITOR_VISIBLE_LINES * EDITOR_ROW_HEIGHT + 4 + 22;
            String assignText = assignedKeys.isEmpty()
                ? "§8No key assigned (use Key Bindings tab to assign a key)"
                : "§aAssigned to: §f" + String.join(", ", assignedKeys);
            context.text(this.font, Component.literal(assignText), 150, assignY, 0xFFFFFF);
        } else {
            context.centeredText(this.font, Component.literal("§7\u2190 Select or create a script to edit"), this.width / 2, 140, 0x555555);
        }
    }
}
