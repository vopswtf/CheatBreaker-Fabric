package cc.vops.cheatbreaker.client.ui.module;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.AbstractGui;
import net.minecraft.client.Minecraft;

/**
 * Deals with anchoring.
 * uses {@link CBPositionEnum}
 */
public class CBAnchorHelper {

    public static GuiAnchor getAnchor(float x, float y, AbstractGui gui) {
        double scaledHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight() / CheatBreaker.getScaleFactor();
        double scaledWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth() / CheatBreaker.getScaleFactor();
        if (x < (float) (scaledWidth / 3) && y < (float) (scaledHeight / 3)) {
            return GuiAnchor.LEFT_TOP;
        }
        if (x > (float) (scaledWidth / 3 * 2) && y < (float) (scaledHeight / 3)) {
            return GuiAnchor.RIGHT_TOP;
        }
        if (y < (float) (scaledHeight / 3)) {
            return GuiAnchor.MIDDLE_TOP;
        }
        if (x < (float) (scaledWidth / 3) && y < (float) (scaledHeight / 3 * 2)) {
            return GuiAnchor.LEFT_MIDDLE;
        }
        if (x > (float) (scaledWidth / 3 * 2) && y < (float) (scaledHeight / 3 * 2)) {
            return GuiAnchor.RIGHT_MIDDLE;
        }
        if (y < (float) (scaledHeight / 3 * 2)) {
            return GuiAnchor.MIDDLE_MIDDLE;
        }
        if (x < (float) (scaledWidth / 3)) {
            return GuiAnchor.LEFT_BOTTOM;
        }
        if (x < (float) (scaledWidth / 3 * 2)) {
            if (x > (float) (scaledWidth / 3 + scaledWidth / 6)) {
                return GuiAnchor.MIDDLE_BOTTOM_RIGHT;
            }
            return GuiAnchor.MIDDLE_BOTTOM_LEFT;
        }
        return GuiAnchor.RIGHT_BOTTOM;
    }

    public static float[] getPositions(float x, float y, AbstractGui gui) {
        float scaledWidth = gui.getScaledWidth();
        float scaledHeight = gui.getScaledHeight();
        GuiAnchor cBGuiAnchor = CBAnchorHelper.getAnchor(x, y, gui);
        float returnX = 0.0f;
        float returnY = 0.0f;
        switch (cBGuiAnchor) {
            case LEFT_TOP: {
                returnX = 0.0f;
                returnY = 0.0f;
                break;
            }
            case RIGHT_TOP: {
                returnX = scaledWidth / (float) 3 * 2.0f;
                returnY = 0.0f;
                break;
            }
            case MIDDLE_TOP: {
                returnX = scaledWidth / (float) 3;
                returnY = 0.0f;
                break;
            }
            case LEFT_MIDDLE: {
                returnX = 0.0f;
                returnY = scaledHeight / (float) 3;
                break;
            }
            case RIGHT_MIDDLE: {
                returnX = scaledWidth / (float) 3 * 2.0f;
                returnY = scaledHeight / (float) 3;
                break;
            }
            case MIDDLE_MIDDLE: {
                returnX = scaledWidth / (float) 3;
                returnY = scaledHeight / (float) 3;
                break;
            }
            case LEFT_BOTTOM: {
                returnX = 0.0f;
                returnY = scaledHeight / (float) 3 * 2.0f;
                break;
            }
            case MIDDLE_BOTTOM_RIGHT: {
                returnX = scaledWidth / (float) 3 + scaledWidth / (float) 6;
                returnY = scaledHeight / (float) 3 * 2.0f;
                break;
            }
            case MIDDLE_BOTTOM_LEFT: {
                returnX = scaledWidth / (float) 3;
                returnY = scaledHeight / (float) 3 * 2.0f;
                break;
            }
            case RIGHT_BOTTOM: {
                returnX = scaledWidth / (float) 3 * 2.0f;
                returnY = scaledHeight / (float) 3 * 2.0f;
            }
        }
        return new float[]{returnX, returnY};
    }

    public static float[] getPositions(GuiAnchor anchor, AbstractGui gui, float x, float y, float scale) {
        float returnX = 0.0f;
        float returnY = 0.0f;
        x *= scale;
        y *= scale;
        switch (anchor) {
            case LEFT_TOP: {
                returnX = 2.0f;
                returnY = 2.0f;
                break;
            }
            case LEFT_MIDDLE: {
                returnX = 2.0f;
                returnY = (float) (gui.getScaledHeight() / 2) - y / 2.0f;
                break;
            }
            case LEFT_BOTTOM: {
                returnY = (float) gui.getScaledHeight() - y - 2.0f;
                returnX = 2.0f;
                break;
            }
            case MIDDLE_TOP: {
                returnX = (float) (gui.getScaledWidth() / 2) - x / 2.0f;
                returnY = 2.0f;
                break;
            }
            case MIDDLE_MIDDLE: {
                returnX = (float) (gui.getScaledWidth() / 2) - x / 2.0f;
                returnY = (float) (gui.getScaledHeight() / 2) - y / 2.0f;
                break;
            }
            case MIDDLE_BOTTOM_LEFT: {
                returnX = (float) (gui.getScaledWidth() / 2) - x;
                returnY = (float) gui.getScaledHeight() - y - 2.0f;
                break;
            }
            case MIDDLE_BOTTOM_RIGHT: {
                returnX = (float) gui.getScaledWidth() / 2;
                returnY = (float) gui.getScaledHeight() - y - 2.0f;
                break;
            }
            case RIGHT_TOP: {
                returnX = (float) gui.getScaledWidth() - x - 2.0f;
                returnY = 2.0f;
                break;
            }
            case RIGHT_MIDDLE: {
                returnX = (float) gui.getScaledWidth() - x;
                returnY = (float) (gui.getScaledHeight() / 2) - y / 2.0f;
                break;
            }
            case RIGHT_BOTTOM: {
                returnX = (float) gui.getScaledWidth() - x;
                returnY = (float) gui.getScaledHeight() - y;
            }
        }
        return new float[]{returnX, returnY};
    }

    public static float[] getPositions(AbstractModule module, float x, float y, AbstractGui gui) {
        float scaledWidth = gui.getScaledWidth();
        float scaledHeight = gui.getScaledHeight();
        GuiAnchor cBGuiAnchor = CBAnchorHelper.getAnchor(x, y, gui);
        float moduleWidth = module.getWidth() * (Float) module.scale.getValue();
        float moduleHeight = module.getHeight() * (Float) module.scale.getValue();
        float returnX = 0.0f;
        float returnY = 0.0f;
        switch (cBGuiAnchor) {
            case LEFT_TOP: {
                returnX = moduleWidth / 2.0f;
                returnY = moduleHeight / 2.0f;
                break;
            }
            case RIGHT_TOP: {
                returnX = scaledWidth / (float) 3 - moduleWidth / 2.0f;
                returnY = moduleHeight / 2.0f;
                break;
            }
            case MIDDLE_TOP: {
                returnX = scaledWidth / (float) 6;
                returnY = moduleHeight / 2.0f;
                break;
            }
            case LEFT_MIDDLE: {
                returnX = moduleWidth / 2.0f;
                returnY = scaledHeight / (float) 6;
                break;
            }
            case RIGHT_MIDDLE: {
                returnX = scaledWidth / (float) 3 - moduleWidth / 2.0f;
                returnY = scaledHeight / (float) 6;
                break;
            }
            case MIDDLE_MIDDLE: {
                returnX = scaledWidth / (float) 6;
                returnY = scaledHeight / (float) 6;
                break;
            }
            case LEFT_BOTTOM:
            case MIDDLE_BOTTOM_RIGHT: {
                returnX = moduleWidth / 2.0f;
                returnY = scaledHeight / (float) 3 - moduleHeight / 2.0f;
                break;
            }
            case MIDDLE_BOTTOM_LEFT: {
                returnX = scaledWidth / (float) 6 - moduleWidth / 2.0f;
                returnY = scaledHeight / (float) 3 - moduleHeight / 2.0f;
                break;
            }
            case RIGHT_BOTTOM: {
                returnX = scaledWidth / (float) 3 - moduleWidth / 2.0f;
                returnY = scaledHeight / (float) 3 - moduleHeight / 2.0f;
            }
        }
        return new float[]{returnX, returnY};
    }

    public static float[] getPositions(GuiAnchor cBGuiAnchor) {
        float f = 0.0f;
        float f2 = 0.0f;
        switch (cBGuiAnchor) {
            case RIGHT_MIDDLE: {
                f = -2;
                break;
            }
            case LEFT_BOTTOM: {
                f = 2.0f;
                f2 = -34;
                break;
            }
            case MIDDLE_MIDDLE: {
                f2 = -50;
                f = 0.0f;
            }
        }
        return new float[]{f, f2};
    }

    public static CBPositionEnum getVerticalPositionEnum(GuiAnchor cBGuiAnchor) {
        switch (cBGuiAnchor) {
            case LEFT_TOP:
            case RIGHT_TOP:
            case MIDDLE_TOP: {
                return CBPositionEnum.TOP;
            }
            case LEFT_MIDDLE:
            case RIGHT_MIDDLE:
            case MIDDLE_MIDDLE: {
                return CBPositionEnum.CENTER;
            }
            case LEFT_BOTTOM:
            case MIDDLE_BOTTOM_RIGHT:
            case MIDDLE_BOTTOM_LEFT:
            case RIGHT_BOTTOM: {
                return CBPositionEnum.BOTTOM;
            }
        }
        return null;
    }

    public static CBPositionEnum getHorizontalPositionEnum(GuiAnchor cBGuiAnchor) {
        switch (cBGuiAnchor) {
            case LEFT_TOP:
            case LEFT_MIDDLE:
            case LEFT_BOTTOM:
            case MIDDLE_BOTTOM_RIGHT: {
                return CBPositionEnum.LEFT;
            }
            case MIDDLE_TOP:
            case MIDDLE_MIDDLE: {
                return CBPositionEnum.CENTER;
            }
            case RIGHT_TOP:
            case RIGHT_MIDDLE:
            case MIDDLE_BOTTOM_LEFT:
            case RIGHT_BOTTOM: {
                return CBPositionEnum.RIGHT;
            }
        }
        return null;
    }
}
