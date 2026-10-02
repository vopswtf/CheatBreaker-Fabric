package cc.vops.cheatbreaker.client.module.type;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.event.type.RenderPreviewEvent;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.element.AbstractScrollableElement;
import cc.vops.cheatbreaker.client.ui.element.module.ModuleListElement;
import cc.vops.cheatbreaker.client.ui.module.CBModulesGui;
import cc.vops.cheatbreaker.client.util.bridge.GuiBridge;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

@Getter
public class OverlayModule extends AbstractModule {
    @Getter
    public static OverlayModule instance;

    private final Setting enchantmentGlint;
    private final Setting fireHeight;

    private final Setting vignette;
    private final Setting vignetteStrength;
    private final Setting vignetteColor;

    public OverlayModule() {
        super("Overlay");
        this.scale.setHidden(true);

        this.setDefaultState(false);
        this.setPreviewIcon(CheatBreaker.asset("icons/mods/overlay.png"), 32, 32);

        new Setting(this, "label").setValue("First Person Options");
        {
            enchantmentGlint = new Setting(this, "Show Enchantment Glint").setValue("Show").acceptedValues("Show", "Hide");
            fireHeight = new Setting(this, "Fire Height").setValue(1.0f).setMinMax(0f, 2f).setDelta(0.1f);
        }

        new Setting(this, "label").setValue("Vignette Options");
        {
            vignette = new Setting(this, "Vignette").setValue("Vanilla").acceptedValues("Vanilla", "Hide", "Forced");
            vignetteStrength = new Setting(this, "Vignette Strength").setValue(1.0f).setMinMax(0f, 1f).setDelta(0.01f);
            vignetteColor = new Setting(this, "Vignette Color").setValue(-1).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
        }

        instance = this;
        this.addEvent(RenderPreviewEvent.class, this::renderPreview);
    }

    public int getVignetteColor() {
        float strength = Math.max(0f, Math.min(1f, vignetteStrength.getAsFloat()));
        int rgb = vignetteColor.getColorValue() & 0xFFFFFF;

        float r = 1f - ((rgb >> 16) & 0xFF) / 255f;
        float g = 1f - ((rgb >> 8) & 0xFF) / 255f;
        float b = 1f - (rgb & 0xFF) / 255f;

        int ri = Math.round(r * strength * 255f);
        int gi = Math.round(g * strength * 255f);
        int bi = Math.round(b * strength * 255f);

        return 0xFF000000 | (ri << 16) | (gi << 8) | bi;
    }

    private void renderPreview(RenderPreviewEvent event) {
        if (!this.isRenderHud()) {
            return;
        }
        if (minecraft.level == null) return;
        if (!isEnabled() || !vignette.getAsString().equals("Forced")) return;
        if (!(GuiBridge.getScreen() instanceof CBModulesGui cbModulesGui)) return;
        AbstractScrollableElement open = cbModulesGui.focusedElement;
        if (!(open instanceof ModuleListElement mle)) return;
        if (mle.module != this) return;

        GuiGraphicsExtractor gfx = event.getGraphics();
        gfx.blit(RenderPipelines.VIGNETTE, Identifier.withDefaultNamespace("textures/misc/vignette.png"), 0, 0, 0.0F, 0.0F, gfx.guiWidth(), gfx.guiHeight(), gfx.guiWidth(), gfx.guiHeight(), getVignetteColor());
    }
}
