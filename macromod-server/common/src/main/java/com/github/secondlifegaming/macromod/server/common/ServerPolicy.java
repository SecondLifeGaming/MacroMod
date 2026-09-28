package com.github.secondlifegaming.macromod.server.common;

/**
 * Server policy configuration data model exchanged over macromod:policy plugin channel.
 */
public class ServerPolicy {
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
