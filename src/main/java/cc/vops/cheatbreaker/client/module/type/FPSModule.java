package cc.vops.cheatbreaker.client.module.type;


import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.event.type.GuiDrawEvent;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.module.GuiAnchor;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class FPSModule extends AbstractModule {
    private final Setting showBackground;
    private final Setting textColor;
    private final Setting backgroundColor;

    public FPSModule() {
        super("FPS");
        this.setDefaultAnchor(GuiAnchor.MIDDLE_TOP);
        this.setDefaultTranslations(0.0f, 0.0f);
        this.setDefaultState(false);
        this.showBackground = new Setting(this, "Show Background").setValue(true);
        this.textColor = new Setting(this, "Text Color").setValue(-1).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.backgroundColor = new Setting(this, "Background Color").setValue(0x6F000000).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.setPreviewLabel("[144 FPS]", 1.6978723f * 0.8245614f);
        this.addEvent(GuiDrawEvent.class, this::onRender);
    }
    private void onRender(GuiDrawEvent drawEvent) {
        if (!this.isRenderHud()) return;
        GuiGraphicsExtractor gfx = drawEvent.getGraphics();
        gfx.pose().pushMatrix();

        gfx.pose().scale(CheatBreaker.getScaleFactor(), CheatBreaker.getScaleFactor());
        this.scaleAndTranslate(gfx);

        if ((Boolean) this.showBackground.getValue()) {
            this.setDimensions(56, 18);
            RenderUtil.drawRect(gfx, 0.0f, 0.0f, 56, 13, this.backgroundColor.getColorValue());
            String string = Minecraft.getInstance().getFps() + " FPS";
            RenderUtil.drawString(gfx, Minecraft.getInstance().font, string, (this.width / 2.0f - (float)(Minecraft.getInstance().font.width(string) / 2)), 3, this.textColor.getColorValue());
        } else {
            String string = "[" + Minecraft.getInstance().getFps() + " FPS]";
            RenderUtil.drawString(gfx, Minecraft.getInstance().font, string, (this.width / 2.0f - (float)(Minecraft.getInstance().font.width(string) / 2)), 3, this.textColor.getColorValue());
            this.setDimensions(Minecraft.getInstance().font.width(string), 18);
        }
        gfx.pose().popMatrix();
    }
}