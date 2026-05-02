package cc.vops.cheatbreaker.client.module.type.armorstatus;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.event.type.GuiDrawEvent;
import cc.vops.cheatbreaker.client.event.type.RenderPreviewEvent;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.module.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class ArmorStatusModule extends AbstractModule {
    private Setting generalOptionsLabel;
    public static Setting listMode;
    public static Setting itemName;
    public static Setting itemCount;
    public static Setting showWhileTying;
    public static Setting equippedItem;
    private Setting damageOptionsLabel;
    public static Setting damageOverlay;
    public static Setting showItemDamage;
    public static Setting showArmourDamage;
    public static Setting showMaxDamage;
    private Setting damageDisplay;
    public static Setting damageDisplayType;
    public static Setting damageThreshold;
    public static final List<ArmorStatusDamageComparable> damageColors;
    private static List<ArmorStatusItem> items;

    public ArmorStatusModule() {
        super("Armor Status");
        this.setDefaultAnchor(GuiAnchor.RIGHT_BOTTOM);
        this.setDefaultState(false);
        this.generalOptionsLabel = new Setting(this, "label").setValue("General Options");
        listMode = new Setting(this, "List Mode").setValue("vertical").acceptedValues("vertical", "horizontal");
        itemName = new Setting(this, "Item Name").setValue(false);
        itemCount = new Setting(this, "Item Count").setValue(true);
        equippedItem = new Setting(this, "Equipped Item").setValue(true);
        showWhileTying = new Setting(this, "Show While Typing").setValue(false);
        this.damageOptionsLabel = new Setting(this, "label").setValue("Damage Options");
        damageOverlay = new Setting(this, "Damage Overlay").setValue(true);
        showItemDamage = new Setting(this, "Show Item Damage").setValue(true);
        showArmourDamage = new Setting(this, "Show Armor Damage").setValue(true);
        showMaxDamage = new Setting(this, "Show Max Damage").setValue(false);
        this.damageDisplay = new Setting(this, "label").setValue("Damage Display");
        damageDisplayType = new Setting(this, "Damage Display Type").setValue("value").acceptedValues("value", "percent", "none");
        damageThreshold = new Setting(this, "Damage Threshold Type").setValue("percent").acceptedValues("percent", "value");
        damageColors.add(new ArmorStatusDamageComparable(10, "4"));
        damageColors.add(new ArmorStatusDamageComparable(25, "c"));
        damageColors.add(new ArmorStatusDamageComparable(40, "6"));
        damageColors.add(new ArmorStatusDamageComparable(60, "e"));
        damageColors.add(new ArmorStatusDamageComparable(80, "7"));
        damageColors.add(new ArmorStatusDamageComparable(100, "f"));
        this.setPreviewIcon(CheatBreaker.asset("icons/mods/diamond_chestplate.png"), 34, 34);
        this.addEvent(RenderPreviewEvent.class, this::renderPreview);
        this.addEvent(GuiDrawEvent.class, this::renderReal);
    }

    private void renderPreview(GuiDrawEvent event) {
        if (!this.isRenderHud()) return;
        List<ArmorStatusItem> arrayList = new ArrayList<>();

        if (minecraft.player != null) {
            for (EquipmentSlot equipmentSlot : EquipmentSlotGroup.ARMOR) {
                if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                    ItemStack itemStack = this.minecraft.player.getItemBySlot(equipmentSlot);
                    arrayList.add(new ArmorStatusItem(itemStack, 16, 16, 2, true));
                }
            }
        }
        if (arrayList.isEmpty()) {
            arrayList.add(new ArmorStatusItem(new ItemStack(Items.DIAMOND_BOOTS), 16, 16, 2, true));
            arrayList.add(new ArmorStatusItem(new ItemStack(Items.DIAMOND_LEGGINGS), 16, 16, 2, true));
            arrayList.add(new ArmorStatusItem(new ItemStack(Items.DIAMOND_CHESTPLATE), 16, 16, 2, true));
            arrayList.add(new ArmorStatusItem(new ItemStack(Items.DIAMOND_HELMET), 16, 16, 2, true));
        }

        arrayList = arrayList.reversed();

        if ((Boolean) equippedItem.getValue() && this.minecraft.player != null && !this.minecraft.player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            arrayList.addFirst(new ArmorStatusItem(this.minecraft.player.getItemInHand(InteractionHand.MAIN_HAND),16, 16, 2, false));
        } else if ((Boolean) equippedItem.getValue()) {
            arrayList.addFirst(new ArmorStatusItem(new ItemStack(Items.DIAMOND_SWORD), 16, 16, 2, false));
        }

        GuiGraphicsExtractor gfx = event.getGraphics();
        gfx.pose().pushMatrix();
        gfx.pose().scale(CheatBreaker.getScaleFactor(), CheatBreaker.getScaleFactor());
        this.scaleAndTranslate(gfx);
        this.renderArmorStatus(gfx, arrayList);
        gfx.pose().popMatrix();
    }

    private void renderReal(GuiDrawEvent event) {
        if (!this.isRenderHud()) {
            return;
        }
        if (!(this.minecraft.screen instanceof CBModulesGui || this.minecraft.screen instanceof CBModulePlaceGui || this.minecraft.screen instanceof ChatScreen && !(Boolean) showWhileTying.getValue())) {
            this.updateItems(this.minecraft);
            if (!items.isEmpty()) {
                GuiGraphicsExtractor gfx = event.getGraphics();
                gfx.pose().pushMatrix();
                gfx.pose().scale(CheatBreaker.getScaleFactor(), CheatBreaker.getScaleFactor());
                this.scaleAndTranslate(gfx);
                this.renderArmorStatus(gfx, items);
                gfx.pose().popMatrix();
            }
        }
    }

    private void updateItems(Minecraft minecraft) {
        items.clear();
        if (minecraft.player == null) return;
        for (EquipmentSlot equipmentSlot : EquipmentSlotGroup.ARMOR) {
            if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                ItemStack itemStack = minecraft.player.getItemBySlot(equipmentSlot);
                items.add(new ArmorStatusItem(itemStack, 16, 16, 2, true));
            }
        }

        if ((Boolean) equippedItem.getValue() && !minecraft.player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            items.add(new ArmorStatusItem(minecraft.player.getItemInHand(InteractionHand.MAIN_HAND),16, 16, 2, false));
        }

        items = items.reversed();
    }

    private void renderArmorStatus(GuiGraphicsExtractor gfx, List<ArmorStatusItem> list) {
        if (!list.isEmpty()) {
            int n = (Boolean) itemName.getValue() ? 18 : 16;
            if (((String) listMode.getValue()).equalsIgnoreCase("vertical")) {
                int n3 = 0;
                int n4 = 0;
                boolean bl = CBAnchorHelper.getHorizontalPositionEnum(this.getGuiAnchor()) == CBPositionEnum.RIGHT;
                for (ArmorStatusItem armorStatusItem : list) {
                    armorStatusItem.renderTo(gfx, bl ? this.width : 0.0f, n3);
                    n3 += n;
                    if (armorStatusItem.width() <= n4) continue;
                    n4 = armorStatusItem.width();
                }
                this.height = n3;
                this.width = n4;
            } else if (((String) listMode.getValue()).equalsIgnoreCase("horizontal")) {
                boolean bl = false;
                int width = 0;
                int height = 0;
                boolean bl2 = CBAnchorHelper.getHorizontalPositionEnum(this.getGuiAnchor()) == CBPositionEnum.RIGHT;
                for (ArmorStatusItem armorStatusItem : list) {
                    if (bl2) {
                        width += armorStatusItem.width();
                    }
                    armorStatusItem.renderTo(gfx, width, 0.0f);
                    if (!bl2) {
                        width += armorStatusItem.width();
                    }
                    if (armorStatusItem.height() <= height) continue;
                    height += armorStatusItem.height();
                }
                this.height = height;
                this.width = width;
            }
        }
    }

    static {
        damageColors = new ArrayList();
        items = new ArrayList();
    }

}
