package com.github.secondlifegaming.macromod.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Invoker("startAttack")
    boolean callStartAttack();

    @Invoker("startUseItem")
    void callStartUseItem();
}
