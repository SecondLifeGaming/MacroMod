package com.github.secondlifegaming.macromod.mixin;

import com.github.secondlifegaming.macromod.MacroModClient;
import com.github.secondlifegaming.macromod.config.MacroConfig;
import com.github.secondlifegaming.macromod.config.MacroConfigManager;
import com.github.secondlifegaming.macromod.engine.MacroEngine;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public class InventoryMixin {

    @Inject(method = "setItem", at = @At("HEAD"))
    private void onSetItem(int slot, ItemStack stack, CallbackInfo ci) {
        if (!MacroModClient.isPremium() || stack == null) return;
        net.minecraft.client.Minecraft client = net.minecraft.client.Minecraft.getInstance();
        if (client.player == null) return;

        MacroConfig config = MacroConfigManager.getConfig();

        if (config.inventoryChangeActionEnabled) {
            client.execute(() -> MacroEngine.run(config.inventoryChangeCommand));
        }

        if (config.itemPickupActionEnabled && !stack.isEmpty()) {
            Inventory inv = (Inventory) (Object) this;
            ItemStack oldStack = inv.getItem(slot);
            if (oldStack.isEmpty() || (ItemStack.isSameItemSameComponents(oldStack, stack) && stack.getCount() > oldStack.getCount())) {
                client.execute(() -> MacroEngine.run(config.itemPickupCommand));
            }
        }
    }
}
