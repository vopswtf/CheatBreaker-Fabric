package cc.vops.cheatbreaker.client.util;

import cc.vops.cheatbreaker.mixin.OptionsMixin;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;

public class Keyboard {
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
}
