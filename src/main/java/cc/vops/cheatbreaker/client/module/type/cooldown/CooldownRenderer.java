package cc.vops.cheatbreaker.client.module.type.cooldown;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Getter
public class CooldownRenderer {
    private final String name;
    private final String itemId;
    private long duration;
    private long time;
    private final ItemStack item;

    public CooldownRenderer(String name, String item, long duration) {
        this.name = name;
        this.itemId = item;
        this.duration = duration;
        this.time = System.currentTimeMillis();
        this.item = resolveItemStack(item);
    }


    public static ItemStack resolveItemStack(String name) {
        Identifier id = Identifier.withDefaultNamespace(name);
        Item item = BuiltInRegistries.ITEM.getOptional(id).orElse(Items.AIR);
        return new ItemStack(item);
    }

    public void render(GuiGraphicsExtractor gfx, Setting setting, float f, float f2, int n) {
        float f3;
        int n2 = 17;
        gfx.pose().pushMatrix();

        float f5 = 1.35f;
        gfx.pose().translate(-0.5f, -1);
        gfx.pose().scale(f5, f5);

        gfx.item(item, (int)((f + (float)(n2 / 2)) / f5) - 1, (int)((f2 + (float)(n2 / 2)) / f5));

        gfx.pose().popMatrix();
        double d = this.duration - (System.currentTimeMillis() - this.time);
        if (d <= 0.0) {
            return;
        }

        if (((String)setting.getValue()).equalsIgnoreCase("Bright")) {
            RenderUtil.drawCircleWithOutLine(gfx, f + (float)n2, f2 + (float)n2, n2, 0.0, (float)this.duration / (0.9574468f * 4.1255555f), (int)this.duration, d, CheatBreaker.getColor(0.0f, 0.0f, 0.0f, 0.509434f * 0.39259258f));
            RenderUtil.drawCircleWithOutLine(gfx, f + (float)n2, f2 + (float)n2, (float)n2 + 1.1688311f * 0.08555556f, n2 - 2, (float)this.duration / (0.625f * 6.32f), (int)this.duration, this.duration, CheatBreaker.getColor(1.5945946f * 0.56440675f, 0.275f * 3.272727f, 0.7340425f * 1.226087f, 1.0f));
            RenderUtil.drawCircleWithOutLine(gfx, f + (float)n2, f2 + (float)n2, (float)n2 + 0.886076f * 0.11285714f, n2 - 2, (float)this.duration / (2.5510418f * 1.548387f), (int)this.duration, d, CheatBreaker.getColor(2.6249998f * 0.13333334f, 0.16578947f * 2.1111112f, 0.62999994f * 0.5555556f, 1.6315789f * 0.36774194f));
        } else if (((String)setting.getValue()).equalsIgnoreCase("Dark")) {
            RenderUtil.drawCircleWithOutLine(gfx, f + (float)n2, f2 + (float)n2, n2, 0.0, CheatBreaker.getColor(0.0F, 0.0F, 0.0F, 0.2F));
            RenderUtil.drawCircleWithOutLine(gfx, f + (float)n2, f2 + (float)n2, n2, 0.0, (float)this.duration / (31.161114f * 0.12676056f), (int)this.duration, d, CheatBreaker.getColor(0.0F, 0.0F, 0.0F, 0.2F));

            RenderUtil.drawCircleWithOutLine(gfx, f + (float)n2, f2 + (float)n2, (float)n2 + 0.19f * 0.5263158f, n2 - 2, (float)this.duration / (0.24074075f * 16.407692f), (int)this.duration, this.duration, CheatBreaker.getColor(0.25F, 0.25F, 0.25F, 1.0F));

            RenderUtil.drawCircleWithOutLine(gfx, f + (float)n2, f2 + (float)n2, (float)n2 + 0.315f * 0.31746033f, n2 - 2, (float)this.duration / (55.3f * 0.071428575f), (int)this.duration, d, CheatBreaker.getColor(0.18F, 0.18F, 0.18F, 1.0F));
        } else if (((String)setting.getValue()).equalsIgnoreCase("Colored")) {
            float f6 = (float)(n >> 24 & 0xFF) / (float)255;
            f3 = (float)(n >> 16 & 0xFF) / (float)255;
            float f7 = (float)(n >> 8 & 0xFF) / (float)255;
            float f8 = (float)(n & 0xFF) / (float)255;
            RenderUtil.drawCircleWithOutLine(gfx, f + (float)n2, f2 + (float)n2, n2, 0.0, CheatBreaker.getColor(f3, f7, f8, 0.26086956f * 0.57500005f * f6));
            RenderUtil.drawCircleWithOutLine(gfx, f + (float)n2, f2 + (float)n2, n2, 0.0, (float)this.duration / (7.0413046f * 0.5609756f), (int)this.duration, d, CheatBreaker.getColor(f3, f7, f8, 0.060606062f * 4.125f * f6));
            RenderUtil.drawCircleWithOutLine(gfx, f + (float)n2, f2 + (float)n2, (float)n2 + 0.10759494f * 0.92941177f, n2 - 2, (float)this.duration / (3.2223685f * 1.2258065f), (int)this.duration, this.duration, CheatBreaker.getColor(f3, f7, f8, f6));
            RenderUtil.drawCircleWithOutLine(gfx, f + (float)n2, f2 + (float)n2, (float)n2 + 0.31707317f * 0.31538463f, n2 - 2, (float)this.duration / (4.761644f * 0.82954544f), (int)this.duration, d, CheatBreaker.getColor(f3, f7, f8, 0.058333337f * 2.5714285f * f6));
        }

        String string = String.format("%.1f", d / (double)1000);
        RenderUtil.drawCenteredStringWithShadow(gfx, Fonts.ubuntuMedium16, string, f + (float)n2 - 1, f2 + (float)(n2 / 2) + 6, -1);
    }

    public boolean isTimeOver() {
        return this.time < System.currentTimeMillis() - this.duration;
    }

    public void setDuration(long l) {
        this.duration = l;
    }

    public void updateTime() {
        this.time = System.currentTimeMillis();
    }

    public long IIIIllIlIIIllIlllIlllllIl() {
        return this.duration;
    }

    public ItemStack IIIIllIIllIIIIllIllIIIlIl() {
        return this.item;
    }
}
