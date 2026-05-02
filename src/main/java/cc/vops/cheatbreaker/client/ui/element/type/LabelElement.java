package cc.vops.cheatbreaker.client.ui.element.type;

import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.ui.element.AbstractModuleTypeElement;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class LabelElement extends AbstractModuleTypeElement {
    private Setting setting;

    public LabelElement(Setting cBSetting, float f) {
        super(f);
        this.setting = cBSetting;
        this.height = 12;
    }

    @Override
    public void drawElement(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawString(
                gui,
                Fonts.ubuntuMedium16,
                ((String)this.setting.getAsString()).toUpperCase(),
                this.x + 2,
                this.y + 4,
                0x6F000000
        );

        RenderUtil.drawRect(gui, this.x + 2, this.y + this.height - 1, this.x + (float) this.width / 2 - 20, this.y + this.height, 0x1F2F2F2F);
    }

    @Override
    public void handleElementClick(int mouseX, int mouseY, int button) {
    }
}