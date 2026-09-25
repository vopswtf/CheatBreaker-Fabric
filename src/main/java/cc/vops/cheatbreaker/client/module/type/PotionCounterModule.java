package cc.vops.cheatbreaker.client.module.type;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.event.type.ClickEvent;
import cc.vops.cheatbreaker.client.event.type.GameTickEvent;
import cc.vops.cheatbreaker.client.event.type.GuiDrawEvent;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.module.GuiAnchor;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

import java.util.ArrayList;
import java.util.List;

public class PotionCounterModule extends AbstractModule {
    private int pots = 0;
    private int soups = 0;

    private final Setting showBackground;
    private final Setting textColor;
    private final Setting backgroundColor;
    private final Setting healingType;

    public PotionCounterModule() {
        super("Potion Counter");
        this.setDefaultAnchor(GuiAnchor.RIGHT_TOP);
        this.setDefaultTranslations(0.0f, 0.0f);
        this.setState(false);
        this.showBackground = new Setting(this, "Show Background").setValue(true);
        this.textColor = new Setting(this, "Text Color").setValue(-1).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.backgroundColor = new Setting(this, "Background Color").setValue(0x6F000000).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.healingType = new Setting(this, "Healing Type").setValue("Potion").acceptedValues("Potion", "Soup");
        this.setPreviewLabel("[12 pots]", 1.1030303f * 1.2692307f);
        this.addEvent(GuiDrawEvent.class, this::onDraw);
        this.addEvent(GameTickEvent.class, this::onTick);
    }

    private void onDraw(GuiDrawEvent drawEvent) {
        if (!this.isRenderHud()) return;
        GuiGraphicsExtractor gfx = drawEvent.getGraphics();
        gfx.pose().pushMatrix();

        gfx.pose().scale(CheatBreaker.getScaleFactor(), CheatBreaker.getScaleFactor());
        this.scaleAndTranslate(gfx);

        int amount = this.healingType.getValue().equals("Potion") ? this.pots : this.soups;
        String type = this.healingType.getValue().equals("Potion") ? "pot" : "soup";

        if ((Boolean) this.showBackground.getValue()) {
            this.setDimensions(56, 18);
            RenderUtil.drawRect(gfx, 0.0f, 0.0f, 56, 13, this.backgroundColor.getColorValue());
            String string = amount + " " + type + (amount == 1 ? "" : "s");
            RenderUtil.drawString(gfx, Minecraft.getInstance().font, string, (this.width / 2.0f - (float)(Minecraft.getInstance().font.width(string) / 2)), 3, this.textColor.getColorValue());
        } else {
            String string = "[" + amount + " " + type + (amount == 1 ? "" : "s") + "]";
            RenderUtil.drawString(gfx, Minecraft.getInstance().font, string, (this.width / 2.0f - (float)(Minecraft.getInstance().font.width(string) / 2)), 3, this.textColor.getColorValue());
            this.setDimensions(Minecraft.getInstance().font.width(string), 18);
        }

        gfx.pose().popMatrix();
    }

    private void onTick(GameTickEvent event) {
        if (!this.isEnabled()) return;
        if (Minecraft.getInstance().player == null) return;

        boolean isPotion = this.healingType.getValue().equals("Potion");

        int pots = 0;
        int soups = 0;

        for (ItemStack stack : Minecraft.getInstance().player.getInventory()) {
            if (isPotion && stack.getItem() instanceof SplashPotionItem) {
                var comp = stack.get(DataComponents.POTION_CONTENTS);

                if (comp != null && comp.potion().isPresent()) {
                    Potion potion = comp.potion().get().value();
                    if (potion.name().equals(Potions.HEALING.value().name()) || potion.name().equals(Potions.STRONG_HEALING.value().name())) {
                        pots += stack.getCount();
                    }
                }
            }

            if (!isPotion && stack.getItem() == Items.MUSHROOM_STEW) {
                this.soups += stack.getCount();
            }
        }

        this.pots = pots;
        this.soups = soups;
    }
}
