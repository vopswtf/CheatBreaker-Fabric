package cc.vops.cheatbreaker.client.util;

import net.minecraft.client.Minecraft;

public class Mouse {
    public static boolean mouseLeftDown = false;
    public static boolean mouseMiddleDown = false;
    public static boolean mouseRightDown = false;
    public static boolean mouseSideButton1Down = false;
    public static boolean mouseSideButton2Down = false;
    public static double mouseX = 0;
    public static double mouseY = 0;

    public static boolean isButtonDown(int button) {
        return switch (button) {
            case 0 -> mouseLeftDown;
            case 1 -> mouseRightDown;
            case 2 -> mouseMiddleDown;
            case 3 -> mouseSideButton1Down;
            case 4 -> mouseSideButton2Down;
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
