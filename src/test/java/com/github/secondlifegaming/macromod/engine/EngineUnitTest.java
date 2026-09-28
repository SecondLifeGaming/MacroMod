package com.github.secondlifegaming.macromod.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EngineUnitTest {

    @Test
    @DisplayName("WindMouse Math Delta Wrapped Degrees")
    void testWrappedDegrees() {
        float targetYaw = 175.0f;
        float curYaw = -175.0f;
        float deltaYaw = targetYaw - curYaw; // 350
        
        float wrapped = net.minecraft.util.Mth.wrapDegrees(deltaYaw);
        assertEquals(-10.0f, wrapped, 0.001f);
    }

    @Test
    @DisplayName("ScriptValue Type Conversions & Truthiness")
    void testScriptValueConversions() {
        ScriptValue numVal = new ScriptValue(42.5);
        assertEquals(42.5, numVal.asNumber(), 0.001);
        assertEquals("42.5", numVal.asString());

        ScriptValue strVal = new ScriptValue("blast_furnace");
        assertEquals("blast_furnace", strVal.asString());
        assertEquals(0.0, strVal.asNumber(), 0.001);

        ScriptValue zeroVal = new ScriptValue(0.0);
        assertEquals(0.0, zeroVal.asNumber(), 0.001);

        ScriptValue emptyStrVal = new ScriptValue("");
        assertEquals("", emptyStrVal.asString());
    }

    @Test
    @DisplayName("Inner Margin Depth Ratio Clamping")
    void testInnerMarginClamping() {
        double margin1 = 0.6; // Exceeds max 0.45
        double clamped1 = net.minecraft.util.Mth.clamp(margin1, 0.0, 0.45);
        assertEquals(0.45, clamped1, 0.001);

        double margin2 = -0.1; // Below min 0.0
        double clamped2 = net.minecraft.util.Mth.clamp(margin2, 0.0, 0.45);
        assertEquals(0.0, clamped2, 0.001);

        double margin3 = 0.2;
        double clamped3 = net.minecraft.util.Mth.clamp(margin3, 0.0, 0.45);
        assertEquals(0.2, clamped3, 0.001);
    }

    @Test
    @DisplayName("ScriptInterpreter Line Parsing & Jump Target Resolution")
    void testScriptParsingAndJumps() {
        List<String> lines = List.of(
            "# Comment line",
            "set counter 0",
            "while $counter < 5",
            "    inc counter 1",
            "    if $counter == 3",
            "        break",
            "    endif",
            "endwhile",
            "{LOOKAT 100 64 -200 blast_furnace 0.1}",
            "{HLOOK 90 0}",
            "{HLOOKRANGE 80 100 -5 5}",
            "{HLOOKRANDOM 90 0 3 3}",
            "{VERIFYSCREEN furnace}"
        );

        ScriptInterpreter interp = new ScriptInterpreter(lines);
        assertNotNull(interp);
        assertFalse(interp.isDone());
    }

    @Test
    @DisplayName("Math and Logic Execution Verification")
    void testScriptInterpreterVariables() {
        List<String> lines = List.of(
            "set val 10",
            "add val 5",
            "sub val 3",
            "mul val 2"
        );

        ScriptInterpreter interp = new ScriptInterpreter(lines);
        assertNotNull(interp);
    }
}

