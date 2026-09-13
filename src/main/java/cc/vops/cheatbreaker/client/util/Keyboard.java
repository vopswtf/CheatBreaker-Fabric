package cc.vops.cheatbreaker.client.util;

import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;

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
        if (key < 6) {
            System.err.println("Warning: Key code " + key + " is reserved for mouse buttons. Use Mouse.isButtonDown() instead.");
            Thread.dumpStack();
            return false;
        }
        long window = Minecraft.getInstance().getWindow().handle();
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
    }

    public static boolean isCtrlKeyDown() {
        return isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    public static boolean isShiftKeyDown() {
        return isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static String getKeyName(int key) {
        try {
            String name = GLFW.glfwGetKeyName(key, 0);
            if (name == null) throw new IllegalArgumentException();
            return name.toUpperCase();
        } catch (Exception e) {
            return "None";
        }
    }
}
