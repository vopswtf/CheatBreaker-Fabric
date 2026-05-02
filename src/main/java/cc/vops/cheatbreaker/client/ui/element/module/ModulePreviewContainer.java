package cc.vops.cheatbreaker.client.ui.element.module;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.element.AbstractScrollableElement;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;

public class ModulePreviewContainer extends AbstractScrollableElement {
    private final List<ModulePreviewElement> elements = new ArrayList<>();

    public ModulePreviewContainer(float scale, int x, int y, int width, int height) {
        super(scale, x, y, width, height);
        for (AbstractModule module : CheatBreaker.getInstance().getModuleManager().modules) {
            if (module == CheatBreaker.getInstance().getModuleManager().notifications) continue;
            ModulePreviewElement element = new ModulePreviewElement(this, module, scale);
            this.elements.add(element);
        }
    }

    @Override
    public boolean hasSettings(AbstractModule module) {
        return false;
    }

    @Override
    public void onClick(int mouseX, int mouseY, int button) {
        super.onClick(mouseX, mouseY, button);
        for (ModulePreviewElement element : this.elements) {
            if (!element.isMouseInside(mouseX, mouseY, false)) continue;
            element.onClick(mouseX, mouseY, button);
        }
    }

    @Override
    public void handleModuleMouseClick(AbstractModule module) {
    }

    @Override
    public void handleDrawElement(GuiGraphicsExtractor gfx, int mouseX, int my, float partialTicks) {
        int mouseY = my - this.yOffset;
        RenderUtil.drawRoundedRect(gfx, this.x, this.y, this.x + this.width, this.y + this.height + 2, 8.0, -657931);

        this.preDraw(gfx, mouseX, mouseY);

        int n3 = 0;
        int n4 = 0;
        for (ModulePreviewElement element : this.elements) {
            element.yOffset = this.scrollAmount;
            element.setDimensions(this.x + 4 + n3 * 120, this.y + 4 + n4 * 112, 116, 108);
            element.handleDrawElement(gfx, mouseX, mouseY, partialTicks);

            if (++n3 == 3) {
                n3 = 0;
                ++n4;
            }
        }

        this.scrollHeight = 4 + n4 * 112;

        if (n3 > 0) {
            this.scrollHeight += 112;
        }

        this.postDraw(gfx, mouseX, mouseY);
    }
}
