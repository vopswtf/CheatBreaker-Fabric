package cc.vops.cheatbreaker.client.util;

import cc.vops.cheatbreaker.client.util.bridge.KeyBridge;
import com.mojang.blaze3d.platform.InputConstants;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;

public class Keyboard {
    @Setter
    private static KeyEvent keyEvent;
    @Setter
    private static CharacterEvent charEvent;

    public static CharacterEvent takeCharacterEvent() {
        CharacterEvent event = charEvent;
        charEvent = null;
        return event;
    }

    public static KeyEvent takeKeyEvent() {
        KeyEvent event = keyEvent;
        keyEvent = null;
        return event;
    }

    public static KeyEvent peekKeyEvent() {
        return keyEvent;
    }

    public static boolean isKeyDown(int key) {
        return KeyBridge.isKeyDown(key);
    }

    public static boolean isCtrlKeyDown() {
        return KeyBridge.isKeyDown(InputConstants.KEY_LCONTROL) || KeyBridge.isKeyDown(InputConstants.KEY_RCONTROL);
    }

    public static boolean isShiftKeyDown() {
        return KeyBridge.isKeyDown(InputConstants.KEY_LSHIFT) || KeyBridge.isKeyDown(InputConstants.KEY_RSHIFT);
    }

    public static String getKeyName(int key) {
        try {
            return KeyBridge.getKeyName(key);
        } catch (Exception e) {
            return "None";
        }
    }
}
