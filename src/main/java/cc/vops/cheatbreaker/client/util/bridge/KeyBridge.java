package cc.vops.cheatbreaker.client.util.bridge;

import java.util.Objects;

import com.mojang.blaze3d.platform.InputConstants;

//? if >=26.3 {
/*import org.lwjgl.sdl.SDLKeyboard;
*///? } else {
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
//? }

public class KeyBridge {
    public static String getKeyName(int keycode) {
        //? if >=26.3 {
        /*return InputConstants.Type.KEYBOARD.getOrCreate(keycode).getDisplayName().getString();
        *///? } else {
        return GLFW.glfwGetKeyName(keycode, 0).toUpperCase();
        //? }
    }

    public static boolean isKeyDown(int keycode) {
        //? if >=26.3 {
        /*return InputConstants.isKeyDown(keycode);
        *///? } else {
        return GLFW.glfwGetKey(Minecraft.getInstance().getWindow().handle(), keycode) == GLFW.GLFW_PRESS;
        //? }
    }
}
