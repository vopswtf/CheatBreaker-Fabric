package cc.vops.cheatbreaker.client.ui.mainmenu.cosmetics.element;


import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops. cheatbreaker.client.ui.element.AbstractModulesGuiElement;
import cc.vops.cheatbreaker.client.util.cosmetic.Cosmetic;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public class CosmeticListElement extends AbstractModulesGuiElement {
    private final Cosmetic cosmetic;
    private final Identifier checkmarkIcon = CheatBreaker.asset("icons/checkmark-32.png");

    public CosmeticListElement(Cosmetic cosmetic, float f) {
        super(f);
        this.height = 30;
        this.cosmetic = cosmetic;
    }

    @Override
    public void handleDrawElement(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float scaleFactor) {
        boolean bl;
        boolean bl2 = bl = mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + this.height;
        if (bl) {
            RenderUtil.drawRect(gfx, this.x, this.y, this.x + this.width, this.y + this.height, 0x2F000000);
        }
        if (this.cosmetic.getType() == Cosmetic.CosmeticType.CAPE) {
            gfx.pose().pushMatrix();
            gfx.pose().translate(this.x + 20, this.y + 5);
            gfx.pose().scale((float) (0.29591838f * 0.8448276f * 1.2), (float) (8.571428f * 0.015166666f * 1.2));
            RenderUtil.drawTexturedQuad(gfx, this.cosmetic.getLocation(), 0.0f, 0.0f, 2.0f, (float)7, 44, 160);
            gfx.pose().popMatrix();
        } else if (this.cosmetic.getType() == Cosmetic.CosmeticType.WINGS) {
            gfx.pose().pushMatrix();
            gfx.pose().translate(this.x + 30, this.y + 4);
            gfx.pose().scale(0.3f, 0.3f);
            gfx.pose().rotate((float) Math.toRadians(45.0));
            RenderUtil.drawTexturedQuad(gfx, this.cosmetic.getLocation(), 0.0f, 0.0f, 0.0f, (float)92, 54, 54);
            gfx.pose().popMatrix();
        } else {
            RenderUtil.drawIcon(gfx, this.cosmetic.getPreviewLocation(), (float)8, (float)(this.x + 20), (float)(this.y + 7));
        }
        RenderUtil.drawString(gfx, Fonts.playRegular14, this.cosmetic.getName().replace("_", " ").toUpperCase(), this.x + 42, (float) ((this.y + (float) this.height / 2) - 2.5), -1342177281);
        int color;
        if (this.cosmetic.isEquipped()) {
            color = CheatBreaker.getColor(0.0f, 0.65542173f * 1.2205882f, 0.0f, 0.48423913f * 0.9292929f);
        } else {
            color = CheatBreaker.getColor(0.0f, 0.0f, 0.0f, 0.07462687f * 3.35f);
        }
        RenderUtil.drawCircle(gfx, this.x + 8, this.y + (double) this.height / 2, 3, color);
    }

    @Override
    public void onClick(int mouseX, int mouseY, int button) {
        boolean bl;
        boolean bl2 = bl = mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + this.height;
        if (bl) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            if (this.cosmetic.isEquipped()) {
                this.cosmetic.setEquipped(false);
            } else {
                this.cosmetic.setEquipped(true);
                for (Cosmetic cosmetic : CheatBreaker.getInstance().getCosmetics()) {
                    if (cosmetic == this.cosmetic || !cosmetic.getType().equals(this.cosmetic.getType())) continue;
                    cosmetic.setEquipped(false);
                }
                this.cosmetic.setEquipped(true);
            }

            CheatBreaker.getInstance().getAssetsWebSocket().sendClientCosmetics();
        }
    }
}