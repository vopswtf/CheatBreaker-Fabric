package cc.vops.cheatbreaker.client.module.type;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.event.type.ClickEvent;
import cc.vops.cheatbreaker.client.event.type.GameTickEvent;
import cc.vops.cheatbreaker.client.event.type.GuiDrawEvent;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.module.GuiAnchor;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;

public class SaturationModule extends AbstractModule {
    private final Setting showBackground;
    private final Setting textColor;
    private final Setting backgroundColor;

    public SaturationModule() {
        super("Saturation");
        this.setDefaultAnchor(GuiAnchor.RIGHT_BOTTOM);
        this.setDefaultTranslations(0.0f, 0.0f);
        this.setState(false);
        this.showBackground = new Setting(this, "Show Background").setValue(true);
        this.textColor = new Setting(this, "Text Color").setValue(0xFFFF55).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.backgroundColor = new Setting(this, "Background Color").setValue(0x6F000000).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.setPreviewLabel(ChatFormatting.YELLOW + "20", 1.1030303f * 1.2692307f);
        this.addEvent(GuiDrawEvent.class, this::onDraw);
    }

    private void onDraw(GuiDrawEvent drawEvent) {
        if (!this.isRenderHud()) return;
        if (Minecraft.getInstance().player == null) return;
        GuiGraphicsExtractor gfx = drawEvent.getGraphics();
        gfx.pose().pushMatrix();

        gfx.pose().scale(CheatBreaker.getScaleFactor(), CheatBreaker.getScaleFactor());
        this.scaleAndTranslate(gfx);

        if ((Boolean) this.showBackground.getValue()) {
            this.setDimensions(56, 18);
            RenderUtil.drawRect(gfx, 0.0f, 0.0f, 56, 13, this.backgroundColor.getColorValue());
            String string = String.valueOf((int) Minecraft.getInstance().player.getFoodData().getSaturationLevel());
            RenderUtil.drawString(gfx, Minecraft.getInstance().font, string, (this.width / 2.0f - (float)(Minecraft.getInstance().font.width(string) / 2)), 3, this.textColor.getColorValue());
        } else {
            String string = "" + (int) Minecraft.getInstance().player.getFoodData().getSaturationLevel();
            RenderUtil.drawString(gfx, Minecraft.getInstance().font, string, (this.width / 2.0f - (float)(Minecraft.getInstance().font.width(string) / 2)), 3, this.textColor.getColorValue());
            this.setDimensions(Minecraft.getInstance().font.width(string), 18);
        }

        gfx.pose().popMatrix();
    }
}
