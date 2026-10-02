package cc.vops.cheatbreaker.client.module.type.cooldown;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.event.type.GuiDrawEvent;
import cc.vops.cheatbreaker.client.event.type.RenderPreviewEvent;
import cc.vops.cheatbreaker.client.event.type.WindowTickEvent;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.module.CBModulePlaceGui;
import cc.vops.cheatbreaker.client.ui.module.CBModulesGui;
import cc.vops.cheatbreaker.client.ui.module.GuiAnchor;
import cc.vops.cheatbreaker.client.util.bridge.GuiBridge;
import com.lunarclient.apollo.cooldown.v1.DisplayCooldownMessage;
import com.lunarclient.apollo.cooldown.v1.RemoveCooldownMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CooldownsModule extends AbstractModule {

    private static final List<CooldownRenderer> real = new ArrayList<>();
    public final Setting colorTheme;
    private final Setting listMode;
    private final Setting coloredColor;
    private final List<CooldownRenderer> fake = new ArrayList<>();

    public CooldownsModule() {
        super("Cooldowns");
        this.setDefaultAnchor(GuiAnchor.MIDDLE_TOP);
        this.setDefaultTranslations(0.0f, 5);
        this.colorTheme = new Setting(this, "Color Theme").setValue("Bright").acceptedValues("Bright", "Dark", "Colored");
        this.listMode = new Setting(this, "List Mode").setValue("horizontal").acceptedValues("vertical", "horizontal");
        this.coloredColor = new Setting(this, "Colored color").setValue(-1).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.addEvent(WindowTickEvent.class, this::onTick);
        this.addEvent(RenderPreviewEvent.class, this::renderPreview);
        this.addEvent(GuiDrawEvent.class, this::renderReal);
        this.setDefaultState(true);
    }

    public static void create(String name, long duration, String itemId) {
        for (CooldownRenderer renderer : real) {
            if (!renderer.getName().equalsIgnoreCase(name) || !Objects.equals(renderer.getItemId(), itemId)) continue;
            renderer.updateTime();
            renderer.setDuration(duration);
            return;
        }
        real.add(new CooldownRenderer(name, itemId, duration));
    }

    public void onTick(WindowTickEvent cBTickEvent) {
        if (!real.isEmpty()) {
            real.removeIf(CooldownRenderer::isTimeOver);
        }
        if (!fake.isEmpty()) {
            fake.removeIf(CooldownRenderer::isTimeOver);
        }
    }

    public void renderPreview(GuiDrawEvent guiDrawEvent) {
        if (!this.isRenderHud()) {
            return;
        }
        var gfx = guiDrawEvent.getGraphics();
        if (real.isEmpty()) {
            gfx.pose().pushMatrix();
            if (this.fake.isEmpty()) {
                fake.add(new CooldownRenderer("CombatTag", "diamond_sword", 30000L));
                fake.add(new CooldownRenderer("EnderPearl", "ender_pearl", 12000L));
            }
            float f = CheatBreaker.getScaleFactor();
            gfx.pose().scale(f, f);
            this.scaleAndTranslate(gfx);
            boolean bl = ((String) listMode.getValue()).equalsIgnoreCase("vertical");
            int n = 36;
            int n2 = 36;
            int n3 = bl ? n : fake.size() * n;
            int n4 = bl ? fake.size() * n2 : n2;
            this.setDimensions((int) ((float) n3), (int) ((float) n4));
            for (int i = 0; i < fake.size(); ++i) {
                CooldownRenderer cooldownRenderer2 = fake.get(i);
                if (((String) listMode.getValue()).equalsIgnoreCase("vertical")) {
                    cooldownRenderer2.render(gfx, this.colorTheme, this.width / 2.0f - (float) (n / 2), i * n2, this.coloredColor.getColorValue());
                    continue;
                }
                cooldownRenderer2.render(gfx, this.colorTheme, i * n, 0.0f, this.coloredColor.getColorValue());
            }
            gfx.pose().popMatrix();
        }
    }

    public void renderReal(GuiDrawEvent guiDrawEvent) {
        if (!this.isRenderHud()) {
            return;
        }
        var gfx = guiDrawEvent.getGraphics();
        gfx.pose().pushMatrix();
        if (!real.isEmpty()) {
            gfx.pose().pushMatrix();
            float f = CheatBreaker.getScaleFactor();
            gfx.pose().scale(f, f);
            this.scaleAndTranslate(gfx);
            boolean bl = ((String) listMode.getValue()).equalsIgnoreCase("vertical");
            int n = 36;
            int n2 = 36;
            int n3 = bl ? n : real.size() * n;
            int n4 = bl ? real.size() * n2 : n2;
            this.setDimensions((int) ((float) n3), (int) ((float) n4));
            for (int i = 0; i < real.size(); ++i) {
                CooldownRenderer cooldownRenderer2 = real.get(i);
                if (((String) listMode.getValue()).equalsIgnoreCase("vertical")) {
                    cooldownRenderer2.render(gfx, this.colorTheme, this.width / 2.0f - (float) (n / 2), i * n2, this.coloredColor.getColorValue());
                    continue;
                }
                cooldownRenderer2.render(gfx, this.colorTheme, i * n, 0.0f, this.coloredColor.getColorValue());
            }
            gfx.pose().popMatrix();
        } else if (!(GuiBridge.getScreen() instanceof CBModulesGui) && !(GuiBridge.getScreen() instanceof CBModulePlaceGui)) {
            this.setDimensions(50, 24);
            this.scaleAndTranslate(gfx);
        }
        gfx.pose().popMatrix();
    }

    public void addCooldown(DisplayCooldownMessage msg) {
        try {
            String name = msg.getName();
            long duration = msg.getDuration().getSeconds() * 1000L;
            String itemId = msg.getIcon().getItemStack().getItemName();
            create(name, duration, itemId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeCooldown(RemoveCooldownMessage msg) {
        try {
            String name = msg.getName();
            real.removeIf(renderer -> renderer.getName().equalsIgnoreCase(name));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetCooldowns() {
        real.clear();
    }
}
