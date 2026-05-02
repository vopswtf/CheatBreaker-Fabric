package cc.vops.cheatbreaker.client.ui.mainmenu.element;


import cc.vops.cheatbreaker.client.ui.fading.ColorFade;
import cc.vops.cheatbreaker.client.ui.mainmenu.AbstractElement;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class IconButtonElement extends AbstractElement {
    @Setter
    private Identifier icon;
    @Setter
    private String text;
    private boolean usesText;
    private final ColorFade outlineFade;
    private final ColorFade upperBackgroundFade;
    private final ColorFade lowerBackgroundFade;
    private float iconSize = 4;

    public IconButtonElement(Identifier icon) {
        this.icon = icon;
        this.outlineFade = new ColorFade(0x4FFFFFFF, 0xAF50A05C);
        this.upperBackgroundFade = new ColorFade(0x1A858585, 0x3F64B96E);
        this.lowerBackgroundFade = new ColorFade(0x1A858585, 0x3F55A562);
    }

    public IconButtonElement(float iconSize, Identifier icon) {
        this.icon = icon;
        this.iconSize = iconSize;
        this.outlineFade = new ColorFade(0x4FFFFFFF, 0xAF50A05C);
        this.upperBackgroundFade = new ColorFade(0x1A858585, 0x3F64B96E);
        this.lowerBackgroundFade = new ColorFade(0x1A858585, 0x3F55A562);
    }

    public IconButtonElement(String string) {
        this.text = string;
        this.usesText = true;
        this.outlineFade = new ColorFade(0x4FFFFFFF, 0xAF50A05C);
        this.upperBackgroundFade = new ColorFade(0x1A858585, 0x3F64B96E);
        this.lowerBackgroundFade = new ColorFade(0x1A858585, 0x3F55A562);
    }

    @Override
    protected void handleElementDraw(GuiGraphicsExtractor gfx, float mouseX, float mouseY, boolean enableMouse) {
        boolean useSecondary = enableMouse && this.isMouseInside(mouseX, mouseY);
        RenderUtil.drawCorneredGradientRectWithOutline(gfx, this.x, this.y,
                this.x + this.width, this.y + this.height,
                this.outlineFade.get(useSecondary).getRGB(),
                this.upperBackgroundFade.get(useSecondary).getRGB(),
                this.lowerBackgroundFade.get(useSecondary).getRGB());

        if (this.usesText) {
            RenderUtil.drawString(
                    gfx,
                    Fonts.robotoRegular13,
                    this.text,
                    (this.x + this.width / 2f - Fonts.robotoRegular13.width(this.text) / 2f),
                    (this.y + this.height / 2f - Fonts.robotoRegular13.height() / 2f),
                    -1
            );
        } else {
            RenderUtil.drawBlit(
                    gfx,
                    this.icon,
                    (this.x + this.width / 2f - this.iconSize),
                    (this.y + this.height / 2f - this.iconSize),
                    0f, 0f, // texture UV start
                    iconSize * 2, iconSize * 2, // UV width/height drawn
                    iconSize * 2, iconSize * 2, // full texture resolution
                    0xCCFFFFFF // 80% alpha
            );
        }
    }

    @Override
    public boolean handleElementMouseClicked(float mouseX, float mouseY, int mouseButton, boolean enableMouse) {
        return false;
    }
}