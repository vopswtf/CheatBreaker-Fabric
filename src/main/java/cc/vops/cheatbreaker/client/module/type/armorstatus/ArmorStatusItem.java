package cc.vops.cheatbreaker.client.module.type.armorstatus;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.module.CBAnchorHelper;
import cc.vops.cheatbreaker.client.ui.module.CBPositionEnum;
import cc.vops.cheatbreaker.client.ui.module.GuiAnchor;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

public class ArmorStatusItem {
    public final ItemStack item;
    public final int iconW;
    public final int iconH;
    public final int padW;
    private int itemWidth;
    private int itemHeight;
    private String displayText = "";
    private int displayTextWidth;
    private String text = "";
    private int textWidth;
    private final boolean isArmor;
    private final Minecraft mc = Minecraft.getInstance();

    public static String stripColor(String string) {
        return string.replaceAll("(?i)§[0-9a-fklmnor]", "");
    }

    public ArmorStatusItem(ItemStack itemStack, int iconW, int iconH, int padW, boolean isArmor) {
        this.item = itemStack;
        this.iconW = iconW;
        this.iconH = iconH;
        this.padW = padW;
        this.isArmor = isArmor;
        this.initSize();
    }

    public int width() {
        return this.itemWidth;
    }

    public int height() {
        return this.itemHeight;
    }

    private void initSize() {
        int n = this.itemHeight = (Boolean) ArmorStatusModule.itemName.getValue() ?
                Math.max(this.mc.font.lineHeight * 2, this.iconH) :
                Math.max(this.mc.font.lineHeight , this.iconH);
        if (this.item != null) {
            int n2 = 1;
            int n3 = 1;
            if ((this.isArmor && (Boolean) ArmorStatusModule.showArmourDamage.getValue() || !this.isArmor && (Boolean) ArmorStatusModule.showItemDamage.getValue()) && this.item.isDamageableItem()) {
                n3 = this.item.getMaxDamage() + 1;
                n2 = n3 - this.item.getDamageValue();
                if (((String) ArmorStatusModule.damageDisplayType.getValue()).equalsIgnoreCase("value")) {
                    this.text = "§" + ArmorStatusDamageComparable.getDamageColor(ArmorStatusModule.damageColors, ((String) ArmorStatusModule.damageThreshold.getValue()).equalsIgnoreCase("percent") ? n2 * 100 / n3 : n2) + n2 + ((Boolean) ArmorStatusModule.showMaxDamage.getValue() ? "/" + n3 : "");
                } else if (((String) ArmorStatusModule.damageDisplayType.getValue()).equalsIgnoreCase("percent")) {
                    this.text = "§" + ArmorStatusDamageComparable.getDamageColor(ArmorStatusModule.damageColors, ((String) ArmorStatusModule.damageThreshold.getValue()).equalsIgnoreCase("percent") ? n2 * 100 / n3 : n2) + n2 * 100 / n3 + "%";
                }
            }
            this.textWidth = this.mc.font.width(stripColor(this.text));
            this.itemWidth = this.padW + this.iconW +
                    this.padW + this.textWidth;
            if ((Boolean) ArmorStatusModule.itemName.getValue()) {
                this.displayText = this.item.getDisplayName().getString();
                this.itemWidth = this.padW + this.iconW + this.padW + Math.max(this.mc.font.width(stripColor(this.displayText)), this.textWidth);
            }
            this.displayTextWidth = this.mc.font.width(stripColor(this.displayText));
        }
    }

    public void renderTo(GuiGraphicsExtractor gfx, float f, float f2) {
//        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
//        GL11.glEnable(32826);
//        RenderHelper.enableGUIStandardItemLighting();
//        ArmorStatusModule.renderItem.zLevel = -10;
        GuiAnchor cBGuiAnchor = CheatBreaker.getInstance().getModuleManager().armourStatus.getGuiAnchor();
        boolean bl = CBAnchorHelper.getHorizontalPositionEnum(cBGuiAnchor) == CBPositionEnum.RIGHT;
        if (bl) {
//            ArmorStatusModule.renderItem.renderItemAndEffectIntoGUI(this.mc.fontRenderer, this.mc.getTextureManager(), this.item,
//                    (int)(f - (float) (this.iconW + this.padW)), (int)f2);
//            HudUtil.renderItemOverlayIntoGUI(this.mc.fontRenderer, this.item, (int)(f - (float)(this.iconW +
//                            this.padW)), (int)f2, (Boolean) ArmorStatusModule.damageOverlay.getValue(),
//                    (Boolean) ArmorStatusModule.itemCount.getValue());
//            RenderHelper.disableStandardItemLighting();
//            GL11.glDisable(32826);
//            GL11.glDisable(3042);
            int x = (int)(f - (float) (this.iconW + this.padW));
            int y = (int)f2;

            gfx.item(item, x, y, 0);
            gfx.itemDecorations(mc.font, item, x, y, null);

//            this.mc.fontRenderer.drawStringWithShadow(this.displayText + "§r", f - (float)(this.padW + this.iconW + this.padW) - (float)this.displayTextWidth, f2, 0xFFFFFF);
//            this.mc.fontRenderer.drawStringWithShadow(this.text + "§r", f - (float)(this.padW + this.iconW + this.padW) - (float)this.textWidth, f2 + (float)((Boolean) ArmorStatusModule.itemName.getValue() ? this.itemHeight / 2 : this.itemHeight / 4), 0xFFFFFF);
            RenderUtil.drawStringWithShadow(gfx, mc.font, this.displayText + "§r", f - (float)(this.padW + this.iconW + this.padW) - (float)this.displayTextWidth, f2, -1);
            RenderUtil.drawStringWithShadow(gfx, mc.font, this.text, f - (float)(this.padW + this.iconW + this.padW) - (float)this.textWidth, f2 + (float)((Boolean) ArmorStatusModule.itemName.getValue() ? this.itemHeight / 2 : this.itemHeight / 4), -1);
        } else {
            int x = (int)(f);
            int y = (int)f2;

            gfx.item(item, (int)f, (int)f2, 0);
            gfx.itemDecorations(mc.font, item, x, y, null);

            RenderUtil.drawStringWithShadow(gfx, mc.font, this.displayText, f + (float)this.iconW + (float)this.padW, f2, -1);
            RenderUtil.drawStringWithShadow(gfx, mc.font, this.text + "§r", f + (float)this.iconW + (float)this.padW, f2 + (float)((Boolean) ArmorStatusModule.itemName.getValue() ? this.itemHeight / 2 : this.itemHeight / 4), -1);
        }
    }
}
