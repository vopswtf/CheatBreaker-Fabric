package cc.vops.cheatbreaker.client.module.type;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.event.type.GameTickEvent;
import cc.vops.cheatbreaker.client.event.type.GuiDrawEvent;
import cc.vops.cheatbreaker.client.event.type.RenderPreviewEvent;
import cc.vops.cheatbreaker.client.event.type.WindowTickEvent;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.module.CBPositionEnum;
import cc.vops.cheatbreaker.client.ui.module.GuiAnchor;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import com.google.common.collect.Ordering;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;

import java.util.ArrayList;
import java.util.Collection;

public class PotionStatusModule extends AbstractModule {

    //public final Setting showInInventory;
    private final Setting showWhileTying;
    private final Setting showEffectName;
    private final Setting colorOptionsLabel;
    private final Setting nameColor;
    private final Setting durationColor;
    private final Setting blink;
    private final Setting blinkDuration;
    private final Identifier location = CheatBreaker.asset("gui/container/inventory.png");
    private int ticks = 0;

    public PotionStatusModule() {
        super("Potion Effects");
        this.setDefaultState(false);
        this.setDefaultAnchor(GuiAnchor.LEFT_MIDDLE);

        new Setting(this, "label").setValue("General Options");
        {
            this.showWhileTying = new Setting(this, "Show While Typing").setValue(true);
            this.showEffectName = new Setting(this, "Effect Name").setValue(true);
            //this.showInInventory = new Setting(this, "Show Potion info in inventory").setValue(false);
            // commented out due to there being two of the same option.
        }
        new Setting(this, "label").setValue("Blink Options");
        {
            this.blink = new Setting(this, "Blink").setValue(true);
            this.blinkDuration = new Setting(this, "Blink Duration").setValue(10).setMinMax(2, 20);
            this.colorOptionsLabel = new Setting(this, "label").setValue("Color Options");
            this.nameColor = new Setting(this, "Name Color").setValue(-1).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
            this.durationColor = new Setting(this, "Duration Color").setValue(-1).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
        }

        this.setPreviewIcon(CheatBreaker.asset("icons/mods/speed_icon.png"), 28, 28);

        this.addEvent(RenderPreviewEvent.class, this::renderPreview);
        this.addEvent(GuiDrawEvent.class, this::renderReal);
        this.addEvent(GameTickEvent.class, this::onTick);
    }

    private void onTick(GameTickEvent windowTickEvent) {
        this.ticks++;
    }

    private void renderReal(GuiDrawEvent guiDrawEvent) {
        if (this.minecraft.player == null) return;
        GuiGraphicsExtractor gfx = guiDrawEvent.getGraphics();
        gfx.pose().pushMatrix();
        if ((Boolean) this.showWhileTying.getValue() || !(this.minecraft.screen instanceof ChatScreen)) {
            gfx.pose().pushMatrix();
            Collection<MobEffectInstance> collection = this.minecraft.player.getActiveEffects();
            if (collection.isEmpty()) {
                gfx.pose().popMatrix();
                gfx.pose().popMatrix();
                return;
            }
            gfx.pose().scale(CheatBreaker.getScaleFactor(), CheatBreaker.getScaleFactor());
            this.scaleAndTranslate(guiDrawEvent.getGraphics());
            renderEnchants(collection, gfx);
            gfx.pose().popMatrix();
        }
        gfx.pose().popMatrix();
    }

    private void renderPreview(RenderPreviewEvent event) {
        if (!this.isRenderHud()) {
            return;
        }
        if (minecraft.level == null) return;
        GuiGraphicsExtractor gfx = event.getGraphics();
        gfx.pose().pushMatrix();
        Collection<MobEffectInstance> collection = this.minecraft.player != null ? new ArrayList<>(this.minecraft.player.getActiveEffects()) : new ArrayList<>();
        if (collection.isEmpty()) {
            gfx.pose().pushMatrix();
            gfx.pose().scale(CheatBreaker.getScaleFactor(), CheatBreaker.getScaleFactor());
            this.scaleAndTranslate(gfx);
            collection.add(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 3));
            collection.add(new MobEffectInstance(MobEffects.SPEED, 30, 3));
            renderEnchants(collection, gfx);
            gfx.pose().popMatrix();
        }
        gfx.pose().popMatrix();
    }

    private void renderEnchants(Collection<MobEffectInstance> collection, GuiGraphicsExtractor gfx) {
        int n = 0;
        int n2 = 0;
        int n3 = 22;
        CBPositionEnum position = this.getPosition();
        collection = Ordering.natural().sortedCopy(collection);
        for (int n6 = 0; n6 < collection.size(); ++n6) {
            MobEffectInstance potionEffect = collection.toArray(new MobEffectInstance[0])[n6];
            String string;
            boolean shouldBlink = this.shouldBlink(potionEffect.getDuration());
            int n4 = 0;
            if ((Boolean) this.showEffectName.getValue()) {
                string = this.getEffectName(potionEffect).getString();
                n4 = this.minecraft.font.width(string) + 20;
                if (position == CBPositionEnum.RIGHT) {
                    RenderUtil.drawStringWithShadow(gfx, minecraft.font, string + "§r", width - n4, n, this.nameColor.getColorValue());
                } else if (position == CBPositionEnum.LEFT) {
                    RenderUtil.drawStringWithShadow(gfx, minecraft.font, string + "§r", 20, n, this.nameColor.getColorValue());
                } else if (position == CBPositionEnum.CENTER) {
                    RenderUtil.drawStringWithShadow(gfx, minecraft.font, string + "§r", (float) width / 2 - ((float) n4 / 2) + 20, n, this.nameColor.getColorValue());
                }
                if (n4 > n2) {
                    n2 = n4;
                }
            }
            string = MobEffectUtil.formatDuration(potionEffect, 1.0F, minecraft.level.tickRateManager().tickrate()).getString();
            int n5 = this.minecraft.font.width(string) + 20;
            if (shouldBlink) {
                if (position == CBPositionEnum.RIGHT) {
                    RenderUtil.drawStringWithShadow(gfx, minecraft.font, string + "§r", (int) width - n5, n + ((Boolean) this.showEffectName.getValue() ? 10 : 5), this.durationColor.getColorValue());
                } else if (position == CBPositionEnum.LEFT) {
                    RenderUtil.drawStringWithShadow(gfx, minecraft.font, string + "§r", 20, n + ((Boolean) this.showEffectName.getValue() ? 10 : 5), this.durationColor.getColorValue());
                } else if (position == CBPositionEnum.CENTER) {
                    RenderUtil.drawStringWithShadow(gfx, minecraft.font, string + "§r", (float) (int) width / 2 - ((float) n5 / 2) + 20, n + ((Boolean) this.showEffectName.getValue() ? 10 : 5), this.durationColor.getColorValue());
                }
            }

            Identifier icon = Gui.getMobEffectSprite(potionEffect.getEffect());
            if (position == CBPositionEnum.RIGHT) {
                gfx.blitSprite(RenderPipelines.GUI_TEXTURED, icon, (int) (width - 20), (n6 * 22), 18, 18);
            } else if (position == CBPositionEnum.LEFT) {
                gfx.blitSprite(RenderPipelines.GUI_TEXTURED, icon, 0, (n6 * 22), 18, 18);
            } else if (position == CBPositionEnum.CENTER) {
                gfx.blitSprite(RenderPipelines.GUI_TEXTURED, icon, (int) (width / 2.0f - (n4 / 2)), (n6 * 22), 18, 18);
            }

            if (n5 > n2) {
                n2 = n5;
            }
            n += n3;
        }

        this.setDimensions(n2, n);
    }

    private Component getEffectName(MobEffectInstance p_368169_) {
        MutableComponent mutablecomponent = p_368169_.getEffect().value().getDisplayName().copy();
        if (p_368169_.getAmplifier() >= 1 && p_368169_.getAmplifier() <= 9) {
            mutablecomponent.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + (p_368169_.getAmplifier() + 1)));
        }

        return mutablecomponent;
    }

    private boolean shouldBlink(float f) {
        if ((Boolean) this.blink.getValue() && f <= (float) ((Integer) this.blinkDuration.getValue() * 22)) {
            if (this.ticks > 20) {
                this.ticks = 0;
            }
            return this.ticks <= 10;
        }
        return true;
    }
}
