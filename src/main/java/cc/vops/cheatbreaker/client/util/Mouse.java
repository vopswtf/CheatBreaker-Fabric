package cc.vops.cheatbreaker.client.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

public class Mouse {
    public static boolean mouseLeftDown = false;
    public static boolean mouseMiddleDown = false;
    public static boolean mouseRightDown = false;
    public static boolean mouseSideButton1Down = false;
    public static boolean mouseSideButton2Down = false;
    public static double mouseX = 0;
    public static double mouseY = 0;

    //? if >= 26.3 {
    public static final int MOUSE_BUTTON_LEFT = InputConstants.MOUSE_BUTTON_LEFT;
    public static final int MOUSE_BUTTON_RIGHT = InputConstants.MOUSE_BUTTON_RIGHT;
    public static final int MOUSE_BUTTON_MIDDLE = InputConstants.MOUSE_BUTTON_MIDDLE;
    public static final int MOUSE_BUTTON_SIDE1 = InputConstants.MOUSE_BUTTON_4;
    public static final int MOUSE_BUTTON_SIDE2 = InputConstants.MOUSE_BUTTON_5;
    //? } else {
    /*public static final int MOUSE_BUTTON_LEFT = 0;
    public static final int MOUSE_BUTTON_RIGHT = 1;
    public static final int MOUSE_BUTTON_MIDDLE = 2;
    public static final int MOUSE_BUTTON_SIDE1 = 3;
    public static final int MOUSE_BUTTON_SIDE2 = 4;
    *///? }

    public static boolean isButtonDown(int button) {
        return switch (button) {
            case MOUSE_BUTTON_LEFT -> mouseLeftDown;
            case MOUSE_BUTTON_RIGHT -> mouseRightDown;
            case MOUSE_BUTTON_MIDDLE -> mouseMiddleDown;
            case MOUSE_BUTTON_SIDE1 -> mouseSideButton1Down;
            case MOUSE_BUTTON_SIDE2 -> mouseSideButton2Down;
            default -> false;
        };

    }

    public static int getEventDWheel() {
        return 0;
    }

    public static double getX() {
        return Minecraft.getInstance().mouseHandler.getScaledXPos(Minecraft.getInstance().getWindow());
    }

    public static double getY() {
        return Minecraft.getInstance().mouseHandler.getScaledYPos(Minecraft.getInstance().getWindow());
    }
}
