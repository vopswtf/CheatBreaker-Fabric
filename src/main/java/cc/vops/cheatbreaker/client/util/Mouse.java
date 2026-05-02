package cc.vops.cheatbreaker.client.util;

public class Mouse {
    public static boolean mouseLeftDown = false;
    public static boolean mouseMiddleDown = false;
    public static boolean mouseRightDown = false;
    public static boolean mouseSideButton1Down = false;
    public static boolean mouseSideButton2Down = false;

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
}
