package cc.vops.overlay;

import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import lombok.Getter;
import net.minecraft.resources.Identifier;

@Getter
public class OverlayModule extends AbstractModule {
    @Getter
    public static OverlayModule instance;

    private final Setting enchantmentGlint;
    private final Setting fireHeight;

    public OverlayModule() {
        super("Overlay");
        this.setDefaultState(false);
        this.setPreviewIcon(Identifier.fromNamespaceAndPath("overlay", "icon.png"), 32, 32);
        this.getSettingsList().clear();

        new Setting(this, "label").setValue("First Person Options");
        enchantmentGlint = new Setting(this, "Show Enchantment Glint").setValue("Show").acceptedValues("Show", "Hide");
        fireHeight = new Setting(this, "Fire Height").setValue(1.0f).setMinMax(0f, 2f).setDelta(0.1f);

        instance = this;
    }
}
