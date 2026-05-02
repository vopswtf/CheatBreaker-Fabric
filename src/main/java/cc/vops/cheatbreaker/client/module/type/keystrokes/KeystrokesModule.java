package cc.vops.cheatbreaker.client.module.type.keystrokes;


import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.event.type.GuiDrawEvent;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.module.GuiAnchor;
import cc.vops.cheatbreaker.mixin.KeyMappingAccessor;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class KeystrokesModule extends AbstractModule {
    private final Setting showClicks;
    private final Setting showMovementKeys;
    private final Setting showSpacebar;
    public final Setting replaceNamesWithArrows;
    private final Setting textColor;
    private final Setting textColorPressed;
    private final Setting backgroundColor;
    private final Setting backgroundColorPressed;
    private final Setting boxSize;
    private final Setting gap;
    public final Setting fadeTime;
    private Key upKey;
    private Key leftKey;
    private Key rightKey;
    private Key downkey;
    private Key leftMouseKey;
    private Key rightMouseKey;
    private Key spaceBarKey;

    public KeystrokesModule() {
        super("Key Strokes");
        this.setDefaultAnchor(GuiAnchor.RIGHT_TOP);
        this.setDefaultTranslations(-70, 5);
        this.setDefaultState(false);
        this.showClicks = new Setting(this, "Show clicks").setValue(true);
        this.showMovementKeys = new Setting(this, "Show movement keys").setValue(true);
        this.showSpacebar = new Setting(this, "Show spacebar").setValue(false);
        this.replaceNamesWithArrows = new Setting(this, "Replace names with arrows").setValue(false);
        this.boxSize = new Setting(this, "Box size").setValue(18).setMinMax(10, 32);
        this.gap = new Setting(this, "Gap").setValue(1).setMinMax(1, 4);
        this.fadeTime = new Setting(this, "Fade Time").setValue(75).setMinMax(0, 100);
        this.textColor = new Setting(this, "Text Color").setValue(-1).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.textColorPressed = new Setting(this, "Text Color (Pressed)").setValue(-16777216).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.backgroundColor = new Setting(this, "Background Color").setValue(0x6F000000).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.backgroundColorPressed = new Setting(this, "Background Color (Pressed)").setValue(0x6FFFFFFF).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.initialize();
        this.setPreviewIcon(CheatBreaker.asset("icons/mods/wasd.png"), 55, 37);
        this.addEvent(GuiDrawEvent.class, this::onDraw);
    }

    private void onDraw(GuiDrawEvent drawEvent) {
        if (!this.isRenderHud()) return;

//        GL11.glPushMatrix();
        GuiGraphicsExtractor gfx = drawEvent.getGraphics();
        gfx.pose().pushMatrix();
        gfx.pose().scale(CheatBreaker.getScaleFactor(), CheatBreaker.getScaleFactor());
//        this.scaleAndTranslate(drawEvent.getResolution());
        this.scaleAndTranslate(gfx);

        float width = 0.0f;
        float height = 0.0f;
        int textColor = this.textColor.getColorValue();
        int textColorPressed = this.textColorPressed.getColorValue();
        int backgroundColor = this.backgroundColor.getColorValue();
        int backgroundColorPressed = this.backgroundColorPressed.getColorValue();
        int gap = (Integer)this.gap.getValue();
        if ((Boolean) this.showMovementKeys.getValue()) {
            this.upKey.render(gfx, this.leftKey.getWidth() + gap, 0.0f, textColor, textColorPressed, backgroundColor, backgroundColorPressed);
            this.leftKey.render(gfx, 0.0f, this.upKey.getHeight() + gap, textColor, textColorPressed, backgroundColor, backgroundColorPressed);
            this.downkey.render(gfx, this.leftKey.getWidth() + gap, this.upKey.getHeight() + gap, textColor, textColorPressed, backgroundColor, backgroundColorPressed);
            this.rightKey.render(
                    gfx, this.leftKey.getWidth() + this.downkey.getWidth() + gap*2, this.upKey.getHeight() + gap, textColor, textColorPressed, backgroundColor, backgroundColorPressed);
            width = this.leftKey.getWidth() + this.downkey.getWidth() + this.rightKey.getWidth() + gap;
            height += this.upKey.getHeight() + gap * 2 + this.downkey.getHeight();
        }
        if ((Boolean) this.showClicks.getValue()) {
            this.leftMouseKey.render(gfx, 0.0f, height, textColor, textColorPressed, backgroundColor, backgroundColorPressed);
            this.rightMouseKey.render(gfx, this.leftMouseKey.getWidth() + gap, height, textColor, textColorPressed, backgroundColor, backgroundColorPressed);
            width = this.leftMouseKey.getWidth() + this.rightMouseKey.getWidth() + gap;
            height += this.rightMouseKey.getHeight() + gap;
        }
        if ((Boolean) this.showSpacebar.getValue()) {
            this.spaceBarKey.render(gfx, 0.0f, height, textColor, textColorPressed, backgroundColor, backgroundColorPressed);
            height += this.spaceBarKey.getHeight() + gap;
        }
        this.setDimensions(width + 2, height < (float)18 ? (float)18 : height + 2.0f);
        gfx.pose().popMatrix();
    }

    public void initialize() {

        int keyCodeForward = ((KeyMappingAccessor) this.minecraft.options.keyUp).getKey().getValue();
        int keyCodeLeft = ((KeyMappingAccessor) this.minecraft.options.keyLeft).getKey().getValue();
        int keyCodeBack = ((KeyMappingAccessor) this.minecraft.options.keyDown).getKey().getValue();
        int keyCodeRight = ((KeyMappingAccessor) this.minecraft.options.keyRight).getKey().getValue();
        int boxSize = (Integer)this.boxSize.getValue();
        int gap = (Integer)this.gap.getValue();
        float fadeTime = (Integer) this.fadeTime.getValue();
        String w = Objects.requireNonNull(GLFW.glfwGetKeyName(keyCodeForward, 0)).toUpperCase();
        String a = Objects.requireNonNull(GLFW.glfwGetKeyName(keyCodeLeft, 0)).toUpperCase();
        String s = Objects.requireNonNull(GLFW.glfwGetKeyName(keyCodeBack, 0)).toUpperCase();
        String d = Objects.requireNonNull(GLFW.glfwGetKeyName(keyCodeRight, 0)).toUpperCase();
        float upKeyWidth = (float)this.minecraft.font.width(w) * (Float) this.scale.getValue();
        float leftKeyWidth = (float)this.minecraft.font.width(a) * (Float) this.scale.getValue();
        float downKeyWidth = (float)this.minecraft.font.width(s) * (Float) this.scale.getValue();
        float rightKeyWidth = (float)this.minecraft.font.width(d) * (Float) this.scale.getValue();

        int jump = ((KeyMappingAccessor) this.minecraft.options.keyJump).getKey().getValue();
        int attack = ((KeyMappingAccessor) this.minecraft.options.keyAttack).getKey().getValue();
        int use = ((KeyMappingAccessor) this.minecraft.options.keyUse).getKey().getValue();

        boolean bl = (Boolean)this.replaceNamesWithArrows.getValue();
        this.upKey = new Key(bl ? "▲" : (upKeyWidth > (float)boxSize ? w.substring(0, 1) : w), keyCodeForward, boxSize, boxSize, fadeTime);
        this.leftKey = new Key(bl ? "◀" : (leftKeyWidth > (float)boxSize ? a.substring(0, 1) : a), keyCodeLeft, boxSize, boxSize, fadeTime);
        this.downkey = new Key(bl ? "▼" : (downKeyWidth > (float)boxSize ? s.substring(0, 1) : s), keyCodeBack, boxSize, boxSize, fadeTime);
        this.rightKey = new Key(bl ? "▶" : (rightKeyWidth > (float)boxSize ? d.substring(0, 1) : d), keyCodeRight, boxSize, boxSize, fadeTime);
        float f5 = (this.leftKey.getWidth() + this.downkey.getWidth() + this.rightKey.getWidth() + gap) / 2.0f;
        this.leftMouseKey = new Key(boxSize < 14 ? "L" : "LMB", attack, f5, boxSize, fadeTime);
        this.rightMouseKey = new Key(boxSize < 14 ? "R" : "RMB", use, f5, boxSize, fadeTime);
        String name = ((KeyMappingAccessor) this.minecraft.options.keyJump).getKey().getName();
        this.spaceBarKey = new Key(name, jump, this.leftKey.getWidth() + this.downkey.getWidth() + this.rightKey.getWidth() + gap*2, (float)boxSize / 2, fadeTime);
    }
}
