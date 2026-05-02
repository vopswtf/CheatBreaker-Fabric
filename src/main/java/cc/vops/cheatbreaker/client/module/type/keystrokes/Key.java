package cc.vops.cheatbreaker.client.module.type.keystrokes;

import cc.vops.cheatbreaker.client.ui.module.CBModulesGui;
import cc.vops.cheatbreaker.client.util.Keyboard;
import cc.vops.cheatbreaker.client.util.Mouse;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.mixin.KeyMappingAccessor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;

import java.awt.*;

@Getter
public class Key {
    private final String displayString;
    private int keyCode;
    private final float width;
    private final float height;
    private float fadeTime;
    private boolean pressed;
    private long lastPressed;
    private Color pressedColor;
    private Color unpressedColor;

    public Key(String string, int keyCode, float width, float height, float fadeTime) {
        this.displayString = string;
        this.keyCode = keyCode;
        this.width = width;
        this.height = height;
        this.fadeTime = fadeTime;
    }

    public void render(GuiGraphicsExtractor gui, final float n, final float n2, final int n3, final int n4, final int n5, final int n6) {
        final Minecraft minecraft = Minecraft.getInstance();
        final boolean pressed = (minecraft.screen == null || minecraft.screen instanceof ContainerScreen || minecraft.screen instanceof CBModulesGui) && (keyCode < 6 ? Mouse.isButtonDown(this.keyCode) : Keyboard.isKeyDown(this.keyCode));
        if (pressed && !this.pressed) {
            this.pressed = true;
            this.lastPressed = System.currentTimeMillis();
            this.pressedColor = new Color(n5, true);
            this.unpressedColor = new Color(n6, true);
        }
        else if (this.pressed && !pressed) {
            this.pressed = false;
            this.lastPressed = System.currentTimeMillis();
            this.pressedColor = new Color(n6, true);
            this.unpressedColor = new Color(n5, true);
        }
//        if (pressed) {
//            CheatBreaker.LOGGER.info(displayString);
//        }
        int rgb;
        if (System.currentTimeMillis() - this.lastPressed < fadeTime) {
            final float n9 = (System.currentTimeMillis() - this.lastPressed) / fadeTime;
            rgb = new Color(
                    (int)Math.abs(n9 * this.unpressedColor.getRed() + (1.0f - n9) * this.pressedColor.getRed()),
                    (int)Math.abs(n9 * this.unpressedColor.getGreen() + (1.0f - n9) * this.pressedColor.getGreen()),
                    (int)Math.abs(n9 * this.unpressedColor.getBlue() + (1.0f - n9) * this.pressedColor.getBlue()),
                    (int)Math.abs(n9 * this.unpressedColor.getAlpha() + (1.0f - n9) * this.pressedColor.getAlpha())
            ).getRGB();
        }
        else {
            rgb = (pressed ? n6 : n5);
        }
        RenderUtil.drawRect(gui, n, n2, n + this.width, n2 + this.height, rgb);
        if (this.keyCode == ((KeyMappingAccessor) Minecraft.getInstance().options.keyJump).getKey().getValue()) {
            RenderUtil.drawRect(gui, n + this.width / 2 - this.width / 8, n2 + this.height / 2, n + this.width / 2.0f + this.width / 8, n2 + this.height / 2 + 1, 0xFF000000 | (pressed ? n4 : n3));
        }
        else {
            int width = minecraft.font.width(displayString);
            RenderUtil.drawCenteredString(
                    gui,
                    minecraft.font,
                    this.displayString,
                    (n + this.width / 2.0f),
                    (n2 + this.height / 2.0f - (float) minecraft.font.lineHeight / 2 + 1.0f),
                    pressed ? n4 : n3
            );
        }
    }
}
