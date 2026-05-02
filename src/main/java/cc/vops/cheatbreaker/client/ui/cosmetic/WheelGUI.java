package cc.vops.cheatbreaker.client.ui.cosmetic;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.AbstractGui;
import cc.vops.cheatbreaker.client.util.Keyboard;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import com.google.common.base.Preconditions;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.List;
import java.util.function.Consumer;

public class WheelGUI extends AbstractGui {
    public static final int maxOptions = 8;
    private final IconButton[] buttons = new IconButton[8];
    private final WheelElement[] elements = new WheelElement[8];
    private final int openMenuButton;
    protected Consumer<IconButton> consumer;
    public int tick = 0;

    public WheelGUI(int openButton, List<IconButton> list) {
        Preconditions.checkNotNull(list, "options");
        Preconditions.checkArgument(list.size() <= 8, "cannot have more than 8 options");
        for (int i = 0; i < list.size(); ++i) {
            this.buttons[i] = list.get(i);
        }
        for (int i = 0; i < this.elements.length; ++i) {
            IconButton iconButton = null;
            if (i < list.size()) {
                iconButton = list.get(i);
            }
            this.elements[i] = new WheelElement(this, i, iconButton);
        }
        this.openMenuButton = openButton;
    }

    @Override
    protected void initMenu() {

    }

    @Override
    protected void drawMenu(GuiGraphicsExtractor gfx, float mouseX, float mouseY, float delta) {
        float n3 = CheatBreaker.getScaledWidth() / CheatBreaker.getScaleFactor();
        float n4 = CheatBreaker.getScaledHeight() / CheatBreaker.getScaleFactor();
        for (WheelElement element : this.elements) {
            if (element == null) continue;
            element.handleElementDraw(gfx, mouseX, mouseY, true);
        }
        float f2 = 10.0f;
        float opacity = (float) this.tick >= f2 ? 1.0f : (float) this.tick / f2;
        gfx.pose().pushMatrix();
        int color = CheatBreaker.getColor(0.0f, 0.0f, 0.0f, 0.5f * opacity);
        RenderUtil.drawCircleWithOutLine(gfx, (float)n3 / 2.0f, (float)n4 / 2.0f, 90.0, 88.0, color);
        RenderUtil.drawCircleWithOutLine(gfx, (float)n3 / 2.0f, (float)n4 / 2.0f, 20.0, 18.0, color);
        gfx.pose().popMatrix();
    }

    @Override
    public void tick() {
        ++this.tick;
        if (!Keyboard.isKeyDown(this.openMenuButton)) {
            if (this.consumer != null) {
                for (int i = 0; i < this.elements.length; ++i) {
                    WheelElement element = this.elements[i];
                    IconButton iconButton = this.buttons[i];
                    Window window = Minecraft.getInstance().getWindow();
                    double x = Minecraft.getInstance().mouseHandler.getScaledXPos(window) / CheatBreaker.getScaleFactor();
                    double y = Minecraft.getInstance().mouseHandler.getScaledYPos(window) / CheatBreaker.getScaleFactor();

                    if (iconButton != null && element.isMouseInsideElement((float) x, (float) y)) {
                        this.consumer.accept(iconButton);
                        break;
                    }
                }
            }
            this.mc.setScreen(null);
        }
    }

    @Override
    protected boolean onMouseClicked(double mx, double my, int button) {
        return false;
    }

    @Override
    protected void onMouseReleased(double mx, double my, int button) {

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }

    @Override
    public boolean allowMovement() {
        return true;
    }
}
