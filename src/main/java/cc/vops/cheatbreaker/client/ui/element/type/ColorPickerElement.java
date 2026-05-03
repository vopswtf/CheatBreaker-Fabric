package cc.vops.cheatbreaker.client.ui.element.type;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.ui.element.AbstractModuleTypeElement;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.Mouse;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.client.util.render.GradientRectRenderState;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ColorPickerElement extends AbstractModuleTypeElement {
    private Setting setting;
    private List<ColorPickerColorElement> colors;
    private boolean lIIIIllIIlIlIllIIIlIllIlI = false;
    private boolean IlllIllIlIIIIlIIlIIllIIIl = false;
    private boolean IlIlllIIIIllIllllIllIIlIl = false;
    private boolean llIIlllIIIIlllIllIlIlllIl = false;
    private ColorPickerColorElement colorPickerColorElement;
    private float IIIlllIIIllIllIlIIIIIIlII = 1.0f;
    private float llIlIIIlIIIIlIlllIlIIIIll = 1.0f;
    private float pickerX;
    private float pickerY;
    private float lIIIIIllllIIIIlIlIIIIlIlI;
    private float IIIIIIlIlIlIllllllIlllIlI;
    private float pickerWidth;
    private float pickerHeight;
    private int lIIlIIllIIIIIlIllIIIIllII;
    private int lIIlllIIlIlllllllllIIIIIl;
    private int lIllIllIlIIllIllIlIlIIlIl;
    private int llIlIIIllIIIIlllIlIIIIIlI;

    private DynamicTexture pickerTexture;
    private Identifier pickerLocation;
    private float cachedHue = -1f;

    public ColorPickerElement(Setting setting, float f) {
        super(f);
        this.setting = setting;
        this.colorPickerColorElement = new ColorPickerColorElement(f, (Integer)setting.getValue());
        this.colors = new ArrayList<>();
        for (ChatFormatting value : ChatFormatting.values()) {
            if (!value.isColor()) continue;
            this.colors.add(new ColorPickerColorElement(f, value.getColor()));
        }
    }

    @Override
    public void drawElement(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTicks) {
        String string;
        this.height = this.lIIIIllIIlIlIllIIIlIllIlI ? 130 : 18;
        this.pickerX = this.x + 56;
        this.lIIIIIllllIIIIlIlIIIIlIlI = this.x + 176;
        this.pickerY = this.y + 25;
        this.IIIIIIlIlIlIllllllIlllIlI = this.y + 119;
        this.pickerWidth = this.lIIIIIllllIIIIlIlIIIIlIlI - this.pickerX;
        this.pickerHeight = this.IIIIIIlIlIlIllllllIlllIlI - this.pickerY;
//        CheatBreaker.getInstance().ubuntuMedium16px.drawString(this.setting.getLabel().toUpperCase(), this.x + 10, (float)(this.y + 4), -1895825408);
        RenderUtil.drawString(gui, Fonts.ubuntuMedium16, this.setting.getLabel().toUpperCase(), this.x + 10, (float)(this.y + 6), -1895825408);
        this.colorPickerColorElement.color = this.setting.getColorValue();
        this.colorPickerColorElement.setDimensions(this.x + 160, this.y + 3, 14, 14);
        this.colorPickerColorElement.yOffset = this.yOffset;
        this.colorPickerColorElement.handleDrawElement(gui, mouseX, mouseY, partialTicks);
        RenderUtil.drawRect(gui, this.x + 186, this.y + 16, this.x + this.width - 16, this.y + 17, 0x7F000000);
//        CheatBreaker.getInstance().playBold18px.drawString("#", this.x + 188, (float)(this.y + 4), -1358954496);
//        CheatBreaker.getInstance().playBold18px.drawString(Integer.toHexString(this.setting.getColorValue()), this.x + 194, (float)(this.y + 4), -1358954496);
        RenderUtil.drawString(gui, Fonts.playBold18, "#", this.x + 188, (float)(this.y + 6), -1358954496);
        RenderUtil.drawString(gui, Fonts.playBold18, Integer.toHexString(this.setting.getColorValue()), this.x + 194, (float)(this.y + 6), -1358954496);
        boolean bl = (float) mouseX > (float)(this.x + this.width - 40) * this.scale && (float) mouseX < (float)(this.x + this.width - 12) * this.scale && (float) mouseY > (float)(this.y + this.yOffset) * this.scale && (float) mouseY < (float)(this.y + 18 + this.yOffset) * this.scale;
        string = bl ? "(Favorite)" : "(+)";
        if (CheatBreaker.getInstance().getGlobalSettings().isFavouriteColor((Integer)this.setting.getValue())) {
            string = bl ? "(Un-favorite)" : "(-)";
        }
//        CheatBreaker.getInstance().playBold18px.drawString(string, this.x + this.width - 16 - CheatBreaker.getInstance().playBold18px.getStringWidth(string), (float)(this.y + 4), bl ? -822083584 : -1358954496);
        RenderUtil.drawString(gui, Fonts.playBold18, string, this.x + this.width - 16 - Fonts.playBold18.width(string), (float)(this.y + 6), bl ? -822083584 : -1358954496);
        if (this.lIIIIllIIlIlIllIIIlIllIlI) {
            if (this.IlllIllIlIIIIlIIlIIllIIIl && !Mouse.isButtonDown(0)) {
                this.IlllIllIlIIIIlIIlIIllIIIl = false;
                this.lIIIIIIIIIlIllIIllIlIIlIl();
            }
            if (this.IlIlllIIIIllIllllIllIIlIl && !Mouse.isButtonDown(0)) {
                this.IlIlllIIIIllIllllIllIIlIl = false;
                this.lIIIIIIIIIlIllIIllIlIIlIl();
            }
            if (this.llIIlllIIIIlllIllIlIlllIl && !Mouse.isButtonDown(0)) {
                this.llIIlllIIIIlllIllIlIlllIl = false;
                this.lIIIIIIIIIlIllIIllIlIIlIl();
            }
            RenderUtil.drawRect(gui, this.x + 55, this.y + 24, this.x + 177, this.y + 120, -822083584);

            populateColorDisplay(gui, mouseX, mouseY);

            RenderUtil.drawRect(gui, this.pickerX - 51, this.pickerY + 1.0f, this.pickerX - 43, this.pickerY + 9.0f, -16777216);
            RenderUtil.drawRect(gui, this.pickerX - 50, this.pickerY + 2.0f, this.pickerX - 44, this.pickerY + 8.0f, this.setting.rainbow ? -13369549 : -1);
            RenderUtil.drawString(gui, Fonts.playRegular16, "CHROMA", this.pickerX - (float)40, this.pickerY, -1358954496);

            if (setting.hasDefaultValue()) {
                RenderUtil.drawRect(gui, this.pickerX - 51, this.pickerY + 16.0f, this.pickerX - 43, this.pickerY + 24.0f, -16777216);
                RenderUtil.drawRect(gui, this.pickerX - 50, this.pickerY + 17.0f, this.pickerX - 44, this.pickerY + 23.0f, -1);
                RenderUtil.drawString(gui, Fonts.playRegular16, "RESET", this.pickerX - (float) 40, this.pickerY + 15.0f, -1358954496);
            }

            this.IlllIIIlIlllIllIlIIlllIlI(gui, mouseX, mouseY);
            this.lIIIIIIIIIlIllIIllIlIIlIl(gui, mouseY);
            this.lIIlIIllIIIIIlIllIIIIllII = (int)(this.pickerX + this.pickerWidth + (float)64);
            this.lIIlllIIlIlllllllllIIIIIl = (int)this.pickerY;
            this.drawColorList(gui, CheatBreaker.getInstance().getGlobalSettings().IlIIlIIlIllIIIIllIIllIlIl, this.lIIlIIllIIIIIlIllIIIIllII, this.lIIlllIIlIlllllllllIIIIIl, mouseX, mouseY, (int) partialTicks);
            this.lIllIllIlIIllIllIlIlIIlIl = (int)(this.pickerX + this.pickerWidth + (float)94);
            this.llIlIIIllIIIIlllIlIIIIIlI = (int)this.pickerY;
            this.drawColorList(gui, CheatBreaker.getInstance().getGlobalSettings().favouriteColors, this.lIllIllIlIIllIllIlIlIIlIl, this.llIlIIIllIIIIlllIlIIIIIlI, mouseX, mouseY, (int) partialTicks);
            this.drawColorList(gui, this.colors, (int)(this.pickerX + this.pickerWidth + (float)34), (int)this.pickerY, mouseX, mouseY, (int) partialTicks);
        }

        if (this.IlllIllIlIIIIlIIlIIllIIIl && Mouse.isButtonDown(0)) {
            this.IlllIllIlIIIIlIIlIIllIIIl = true;
            float relX = ((mouseX / this.scale) - this.pickerX);
            float relY = ((mouseY / this.scale) - this.pickerY - this.yOffset);

            relX = Mth.clamp(relX, 0f, this.pickerWidth - 0.001f);
            relY = Mth.clamp(relY, 0f, this.pickerHeight - 0.001f);

            this.setting.colorArray = new int[]{
                    (int) relX,
                    (int) relY
            };

            float sat = Math.min(1f, Math.max(0f, relX / (this.pickerWidth - 1f)));
            float bri = Math.min(1f, Math.max(0f, 1f - (relY / (this.pickerHeight - 1f))));
            int pickedColor = Color.HSBtoRGB(this.IIIlllIIIllIllIlIIIIIIlII, sat, bri);
            this.setting.setValue(pickedColor);
        } else if (!Mouse.isButtonDown(0)) {
            // stop dragging once mouse released
            this.IlllIllIlIIIIlIIlIIllIIIl = false;
        }
    }

    private void populateColorDisplay(GuiGraphicsExtractor gfx, int mouseX, int mouseY) {
        // draw base hue gradient (left = low sat, right = full hue)
        int fullHue = Color.HSBtoRGB(this.IIIlllIIIllIllIlIIIIIIlII, 1.0f, 1.0f) | 0xFF000000;

        gfx.guiRenderState.addGuiElement(new GradientRectRenderState(
                TextureSetup.noTexture(),
                gfx.pose(),
                gfx.scissorStack.peek(),
                pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight,
                0xFFFFFFFF, // sat=0 is white
                fullHue, // sat=1 is full hue
                true
        ));

        RenderUtil.drawGradientRect(gfx, (int) pickerX, (int) pickerY, (int) (pickerX + pickerWidth), (int) (pickerY + pickerHeight), 0x00000000, 0xFF000000);

// draw selection cursor
        if (this.setting.colorArray != null) {
            int selX = (int) (this.pickerX + this.setting.colorArray[0]);
            int selY = (int) (this.pickerY + this.setting.colorArray[1]);
            RenderUtil.drawCircle(gfx, selX, selY, 3, 0xFF000000);
            RenderUtil.drawCircle(gfx, selX, selY, 2, 0xFFFFFFFF);
        }
    }

    private void lIIIIIIIIIlIllIIllIlIIlIl() {
        if (CheatBreaker.getInstance().getGlobalSettings().IlIIlIIlIllIIIIllIIllIlIl.size() >= 16) {
            CheatBreaker.getInstance().getGlobalSettings().IlIIlIIlIllIIIIllIIllIlIl.removeFirst();
        }
        CheatBreaker.getInstance().getGlobalSettings().IlIIlIIlIllIIIIllIIllIlIl.add(new ColorPickerColorElement(this.scale, (Integer)this.setting.getValue()));
        CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
    }

    private void lIIIIIIIIIlIllIIllIlIIlIl(GuiGraphicsExtractor gui, int n2) {
//        RenderUtil.drawRect(gfx, this.pickerX + this.pickerWidth + (float)18, this.pickerY - 1.0f, this.pickerX + this.pickerWidth + (float)28, this.pickerY + 1.0f + this.pickerHeight, -822083584);
        RenderUtil.drawRect(gui, (int) (this.pickerX + this.pickerWidth + (float)18), (int)(this.pickerY - 1.0f), (int)(this.pickerX + this.pickerWidth + (float)28), (int)(this.pickerY + 1.0f + this.pickerHeight), -822083584);
        this.IlllIIIlIlllIllIlIIlllIlI(gui);
        int n3 = 0;
        while ((float)n3 < this.pickerHeight) {
            int n4 = (Integer)this.setting.getValue();
            int n5 = new Color(n4 >> 16 & 0xFF, n4 >> 8 & 0xFF, n4 & 0xFF, Math.round((float)255 - (float)n3 / this.pickerHeight * (float)255)).getRGB();
            if (this.llIIlllIIIIlllIllIlIlllIl && (float)n2 >= ((float)this.yOffset + this.pickerY + (float)n3) * this.scale && (float)n2 <= ((float)this.yOffset + this.pickerY + (float)n3 + 1.0f) * this.scale) {
                this.llIlIIIlIIIIlIlllIlIIIIll = (float)n3 / this.pickerHeight;
                this.setting.setValue(n5);
            }
//            RenderUtil.drawRect(gfx, this.pickerX + this.pickerWidth + (float)19, this.pickerY + (float)n3, this.pickerX + this.pickerWidth + (float)27, this.pickerY + (float)n3 + 1.0f, n5);
            RenderUtil.drawRect(gui, (int)(this.pickerX + this.pickerWidth + (float)19), (int)(this.pickerY + (float)n3), (int)(this.pickerX + this.pickerWidth + (float)27), (int)(this.pickerY + (float)n3 + 1.0f), n5);
            ++n3;
        }
        float f = (float)-1 + this.pickerHeight * this.llIlIIIlIIIIlIlllIlIIIIll;
//        RenderUtil.drawRect(gfx, this.pickerX + this.pickerWidth + (float)18, this.pickerY + f, this.pickerX + this.pickerWidth + (float)28, this.pickerY + f + (float)3, -822083584);
//        RenderUtil.drawRect(gfx, this.pickerX + this.pickerWidth + (float)18, this.pickerY + f + 1.0f, this.pickerX + this.pickerWidth + (float)28, this.pickerY + f + 2.0f, -805306369);
        RenderUtil.drawRect(gui, (int)(this.pickerX + this.pickerWidth + (float)18), (int)(this.pickerY + f), (int)(this.pickerX + this.pickerWidth + (float)28), (int)(this.pickerY + f + (float)3), -822083584);
        RenderUtil.drawRect(gui, (int)(this.pickerX + this.pickerWidth + (float)18), (int)(this.pickerY + f + 1.0f), (int)(this.pickerX + this.pickerWidth + (float)28), (int)(this.pickerY + f + 2.0f), -805306369);
    }

    private void IlllIIIlIlllIllIlIIlllIlI(GuiGraphicsExtractor gui, int n, int n2) {
//        RenderUtil.drawRect(gfx, this.pickerX + this.pickerWidth + (float)4, this.pickerY - 1.0f, this.pickerX + this.pickerWidth + (float)14, this.pickerY + 1.0f + this.pickerHeight, -822083584);
        RenderUtil.drawRect(gui, (int)(this.pickerX + this.pickerWidth + (float)4), (int)(this.pickerY - 1.0f), (int)(this.pickerX + this.pickerWidth + (float)14), (int)(this.pickerY + 1.0f + this.pickerHeight), -822083584);
        int n3 = 0;
        while ((float)n3 < this.pickerHeight) {
            int n4;
            if (this.IlIlllIIIIllIllllIllIIlIl && (float)n2 >= ((float)this.yOffset + this.pickerY + (float)n3) * this.scale && (float)n2 <= ((float)this.yOffset + this.pickerY + (float)n3 + 1.0f) * this.scale) {
                n4 = (Integer)this.setting.getValue();
                float[] arrf = Color.RGBtoHSB(n4 >> 16 & 0xFF, n4 >> 8 & 0xFF, n4 & 0xFF, null);
                this.setting.setValue(Color.HSBtoRGB(this.IIIlllIIIllIllIlIIIIIIlII, arrf[1], arrf[2]));
                this.IIIlllIIIllIllIlIIIIIIlII = (float)n3 / this.pickerHeight;
            }
            n4 = Color.HSBtoRGB((float)n3 / this.pickerHeight, 1.0f, 1.0f);
//            RenderUtil.drawRect(gfx, this.pickerX + this.pickerWidth + (float)5, this.pickerY + (float)n3, this.pickerX + this.pickerWidth + (float)13, this.pickerY + (float)n3 + 1.0f, n4);
            RenderUtil.drawRect(gui, (int)(this.pickerX + this.pickerWidth + (float)5), (int)(this.pickerY + (float)n3), (int)(this.pickerX + this.pickerWidth + (float)13), (int)(this.pickerY + (float)n3 + 1.0f), n4);
            ++n3;
        }
        float f = (float)-1 + this.pickerHeight * this.IIIlllIIIllIllIlIIIIIIlII;
//        RenderUtil.drawRect(gfx, this.pickerX + this.pickerWidth + (float)4, this.pickerY + f, this.pickerX + this.pickerWidth + (float)14, this.pickerY + f + (float)3, -822083584);
//        RenderUtil.drawRect(gfx, this.pickerX + this.pickerWidth + (float)4, this.pickerY + f + 1.0f, this.pickerX + this.pickerWidth + (float)14, this.pickerY + f + 2.0f, -805306369);
        RenderUtil.drawRect(gui, (int)(this.pickerX + this.pickerWidth + (float)4), (int)(this.pickerY + f), (int)(this.pickerX + this.pickerWidth + (float)14), (int)(this.pickerY + f + (float)3), -822083584);
        RenderUtil.drawRect(gui, (int)(this.pickerX + this.pickerWidth + (float)4), (int)(this.pickerY + f + 1.0f), (int)(this.pickerX + this.pickerWidth + (float)14), (int)(this.pickerY + f + 2.0f), -805306369);
    }

    private void IlllIIIlIlllIllIlIIlllIlI(GuiGraphicsExtractor gui) {
        boolean bl = true;
        int n = 2;
        while ((float)n < this.pickerHeight - (float)4) {
            if (!bl) {
                RenderUtil.drawRect(gui, (int)(this.pickerX + this.pickerWidth + (float)19), (int)(this.pickerY + (float)n), (int)(this.pickerX + this.pickerWidth + (float)23), (int)(this.pickerY + (float)n + (float)4), -1);
                RenderUtil.drawRect(gui, (int)(this.pickerX + this.pickerWidth + (float)23), (int)(this.pickerY + (float)n + (float)4), (int)(this.pickerX + this.pickerWidth + (float)27), (int)(this.pickerY + (float)n + (float)8), -1);
                RenderUtil.drawRect(gui, (int)(this.pickerX + this.pickerWidth + (float)23), (int)(this.pickerY + (float)n), (int)(this.pickerX + this.pickerWidth + (float)27), (int)(this.pickerY + (float)n + (float)4), -7303024);
                RenderUtil.drawRect(gui, (int)(this.pickerX + this.pickerWidth + (float)19), (int)(this.pickerY + (float)n + (float)4), (int)(this.pickerX + this.pickerWidth + (float)23), (int)(this.pickerY + (float)n + (float)8), -7303024);
            }
            bl = !bl;
            n += 4;
        }
    }

    private void drawColorList(GuiGraphicsExtractor gui, List<ColorPickerColorElement> list, int n, int n2, int n3, int n4, int n5) {
        int n6 = 0;
        int n7 = 0;
        int n8 = 8;
        for (ColorPickerColorElement colorPickerColorElement : list) {
            colorPickerColorElement.scale = this.scale;
            if (n6 == n8) {
                ++n7;
                n6 = 0;
            }
            if (list == this.colors) {
                int n9 = n8 * 2 / 8 * 12;
                int var12_12 = n + n9 - n7 * 12 - 12;
                int var13_13 = n2 + n6 * n9 - n6 * 12;
                colorPickerColorElement.yOffset = this.yOffset;
                colorPickerColorElement.setDimensions(var12_12, var13_13, 10, 10);
                String string = "0123456789abcdefklmnor";
                int n10 = n6 + n7 * n8;
                String string2 = string.substring(n10, n10 + 1);
                if (colorPickerColorElement.isMouseInside(n3, n4, false)) {
                    RenderUtil.drawRect(gui, var12_12 + 12, var13_13 - 1, var12_12 + 26, var13_13 + 11, -1087492562);
                    RenderUtil.drawString(gui, Fonts.ubuntuMedium16, "&" + string2, (float) (var12_12 + 14), (float)var13_13, -1);
                }
            } else {
                int var12_12 = n + n7 * 12;
                int var13_13 = n2 + n6 * 12;
                colorPickerColorElement.yOffset = this.yOffset;
                colorPickerColorElement.setDimensions(var12_12, var13_13, 10, 10);
            }
            colorPickerColorElement.handleDrawElement(gui, n3, n4, (float)n5);
            ++n6;
        }
    }

    private void lIIIIlIIllIIlIIlIIIlIIllI(List<ColorPickerColorElement> list, int originX, int originY, int mouseX, int mouseY) {
        int col = 0;
        int row = 0;
        int perCol = 8;

        for (ColorPickerColorElement swatch : list) {
            if (col == perCol) { row++; col = 0; }

            int sx, sy;

            if (list == this.colors) {
                int n9 = perCol * 2 / 8 * 12;
                sx = originX + n9 - row * 12 - 12;
                sy = originY + col * n9 - col * 12;
            } else {
                sx = originX + row * 12;
                sy = originY + col * 12;
            }

            swatch.yOffset = this.yOffset;
            swatch.scale = this.scale;
            swatch.setDimensions(sx, sy, 10, 10);

            if (swatch.isMouseInside(mouseX / this.scale, mouseY / this.scale, true)) {
                int picked = (list == this.colors)
                        ? new Color(swatch.color).getRGB()
                        : new Color(swatch.color, true).getRGB();

                this.setting.setValue(picked);
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);

                // sync hue + cursor to selected color
                float[] hsb = Color.RGBtoHSB(
                        (picked >> 16) & 0xFF,
                        (picked >> 8) & 0xFF,
                        picked & 0xFF,
                        null
                );
                this.IIIlllIIIllIllIlIIIIIIlII = hsb[0];
                int arrX = (int) (hsb[1] * this.pickerWidth);
                int arrY = (int) (this.pickerHeight - hsb[2] * this.pickerHeight);
                this.setting.colorArray = new int[]{arrX, arrY};
                this.cachedHue = -1f;
                break;
            }

            col++;
        }
    }

    @Override
    public void handleElementClick(int mouseX, int mouseY, int button) {
        boolean bl;
        boolean bl2 = (float) mouseX > (float)(this.x + this.width - 40) * this.scale && (float) mouseX < (float)(this.x + this.width - 12) * this.scale && (float) mouseY > (float)(this.y + this.yOffset) * this.scale && (float) mouseY < (float)(this.y + 18 + this.yOffset) * this.scale;
        boolean bl3 = bl = (float) mouseX > (float)this.x * this.scale && (float) mouseX < (float)(this.x + this.width - 40) * this.scale && (float) mouseY > (float)(this.y + this.yOffset) * this.scale && (float) mouseY < (float)(this.y + 18 + this.yOffset) * this.scale;

        if (bl) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            float[] arrf = Color.RGBtoHSB((Integer)this.setting.getValue() >> 16 & 0xFF, (Integer)this.setting.getValue() >> 8 & 0xFF, (Integer)this.setting.getValue() & 0xFF, null);
            this.IIIlllIIIllIllIlIIIIIIlII = arrf[0];
            int n4 = (int)(arrf[1] * this.pickerWidth);
            int n5 = (int)(this.pickerHeight - arrf[2] * this.pickerHeight);
            this.setting.colorArray = new int[]{n4, n5};
            this.lIIIIllIIlIlIllIIIlIllIlI = !this.lIIIIllIIlIlIllIIIlIllIlI;
        } else if (bl2) {
            if (CheatBreaker.getInstance().getGlobalSettings().isFavouriteColor((Integer)this.setting.getValue())) {
                CheatBreaker.getInstance().getGlobalSettings().removeFavouriteColor((Integer)this.setting.getValue());
            } else {
                if (CheatBreaker.getInstance().getGlobalSettings().favouriteColors.size() >= 16) {
                    CheatBreaker.getInstance().getGlobalSettings().favouriteColors.removeFirst();
                }
                CheatBreaker.getInstance().getGlobalSettings().favouriteColors.add(new ColorPickerColorElement(this.scale, (Integer)this.setting.getValue()));
            }
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
        } else if (this.lIIIIllIIlIlIllIIIlIllIlI) {
            boolean bl4;
            this.lIIIIlIIllIIlIIlIIIlIIllI(
                    this.colors,
                    (int) (this.pickerX + this.pickerWidth + 34),
                    (int) this.pickerY,
                    mouseX,
                    mouseY
            );
            this.lIIIIlIIllIIlIIlIIIlIIllI(CheatBreaker.getInstance().getGlobalSettings().IlIIlIIlIllIIIIllIIllIlIl, this.lIIlIIllIIIIIlIllIIIIllII, this.lIIlllIIlIlllllllllIIIIIl, mouseX, mouseY);
            this.lIIIIlIIllIIlIIlIIIlIIllI(CheatBreaker.getInstance().getGlobalSettings().favouriteColors, this.lIllIllIlIIllIllIlIlIIlIl, this.llIlIIIllIIIIlllIlIIIIIlI, mouseX, mouseY);
            boolean isHovingChroma = (float) mouseX > (this.pickerX - (float)51) * this.scale && (float) mouseY > (this.pickerY + 1.0f + (float)this.yOffset) * this.scale && (float) mouseX < (this.pickerX - (float)43) * this.scale && (float) mouseY < (this.pickerY + (float)9 + (float)this.yOffset) * this.scale;
            boolean isHovingReset = (float) mouseX > (this.pickerX - (float)51) * this.scale && (float) mouseY > (this.pickerY + 16.0f + (float)this.yOffset) * this.scale && (float) mouseX < (this.pickerX - (float)43) * this.scale && (float) mouseY < (this.pickerY + 24.0f + (float)this.yOffset) * this.scale;

            if ((float) mouseX > this.pickerX * this.scale && (float) mouseX < (this.pickerX + this.pickerWidth) * this.scale && (float) mouseY > (this.pickerY + (float)this.yOffset) * this.scale && (float) mouseY < (this.pickerY + this.pickerHeight + (float)this.yOffset) * this.scale) {
                this.IlllIllIlIIIIlIIlIIllIIIl = true;
                float relX = ((mouseX / this.scale) - this.pickerX);
                float relY = ((mouseY / this.scale) - this.pickerY - this.yOffset);

                relX = Mth.clamp(relX, 0f, this.pickerWidth - 0.001f);
                relY = Mth.clamp(relY, 0f, this.pickerHeight - 0.001f);

                this.setting.colorArray = new int[]{
                        (int) relX,
                        (int) relY
                };

                float sat = Math.min(1f, Math.max(0f, relX / (this.pickerWidth - 1f)));
                float bri = Math.min(1f, Math.max(0f, 1f - (relY / (this.pickerHeight - 1f))));
                int pickedColor = Color.HSBtoRGB(this.IIIlllIIIllIllIlIIIIIIlII, sat, bri);
                this.setting.setValue(pickedColor);
            }
            if ((float) mouseX > (this.pickerX + this.pickerWidth + (float)4) * this.scale && (float) mouseX < (this.pickerX + this.pickerWidth + (float)14) * this.scale && (float) mouseY > (this.pickerY - 1.0f + (float)this.yOffset) * this.scale && (float) mouseY < (this.pickerY + 1.0f + this.pickerHeight + (float)this.yOffset) * this.scale) {
                this.IlIlllIIIIllIllllIllIIlIl = true;
            }
            if ((float) mouseX > (this.pickerX + this.pickerWidth + (float)18) * this.scale && (float) mouseX < (this.pickerX + this.pickerWidth + (float)28) * this.scale && (float) mouseY > (this.pickerY - 1.0f + (float)this.yOffset) * this.scale && (float) mouseY < (this.pickerY + 1.0f + this.pickerHeight + (float)this.yOffset) * this.scale) {
                this.llIIlllIIIIlllIllIlIlllIl = true;
            }
            if (isHovingChroma) {
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
                this.setting.rainbow = !this.setting.rainbow;
            }

            if (isHovingReset && setting.hasDefaultValue()) {
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
                int defaultColor = (Integer) this.setting.getDefaultValue();
                this.setting.setValue(defaultColor, false);
//                CheatBreaker.LOGGER.info("Reset color to default: " + Integer.toHexString(defaultColor));
            }
        }
    }
}