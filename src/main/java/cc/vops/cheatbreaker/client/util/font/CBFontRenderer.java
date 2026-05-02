package cc.vops.cheatbreaker.client.util.font;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.UUID;

// credit claude LOL
public class CBFontRenderer {

    private static final int IMG_SIZE = 1024;
    private static final int CHAR_COUNT = 256;
    private static final int CHAR_PADDING = 8;

    private final CharData[] charData = new CharData[CHAR_COUNT];
    private final CharData[] boldCharData = new CharData[CHAR_COUNT];
    private final CharData[] italicCharData = new CharData[CHAR_COUNT];
    private final CharData[] boldItalicCharData = new CharData[CHAR_COUNT];

    private DynamicTexture tex;
    private DynamicTexture texBold;
    private DynamicTexture texItalic;
    private DynamicTexture texBoldItalic;

    private Font font;
    private int fontHeight;
    private final int charOffset = 0;

    @Getter
    private final String name;
    private final Identifier resourceLocation;
    private final float size;
    private final float offsetY;

    public CBFontRenderer(Identifier resourceLocation, float size, float offsetY) {
        this.name = resourceLocation.toDebugFileName();
        this.resourceLocation = resourceLocation;
        this.size = size;
        this.offsetY = offsetY;
        reload();
    }

    public void reload() {
        Font loaded;
        try {
            InputStream is = Minecraft.getInstance().getResourceManager()
                    .open(resourceLocation);
            loaded = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
        } catch (Exception e) {
            loaded = new Font("Arial", Font.PLAIN, (int) size);
            e.printStackTrace();
        }
        this.font = loaded;
        bakeAll();
    }

    private void bakeAll() {
        tex          = bake(font.deriveFont(Font.PLAIN),       charData);
        texBold      = bake(font.deriveFont(Font.BOLD),        boldCharData);
        texItalic    = bake(font.deriveFont(Font.ITALIC),      italicCharData);
        texBoldItalic = bake(font.deriveFont(Font.BOLD | Font.ITALIC), boldItalicCharData);
    }

    private DynamicTexture bake(Font f, CharData[] chars) {
        BufferedImage img = new BufferedImage(IMG_SIZE, IMG_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setFont(f);
        g.setColor(new Color(255, 255, 255, 0));
        g.fillRect(0, 0, IMG_SIZE, IMG_SIZE);
        g.setColor(Color.WHITE);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        FontMetrics fm = g.getFontMetrics();
        int posX = 0, posY = 1, rowHeight = 0;

        for (int i = 0; i < CHAR_COUNT; i++) {
            char ch = (char) i;
            Rectangle2D bounds = fm.getStringBounds(String.valueOf(ch), g);
            CharData cd = new CharData();
            cd.width  = bounds.getBounds().width + CHAR_PADDING;
            cd.height = bounds.getBounds().height;

            if (posX + cd.width >= IMG_SIZE) {
                posX = 0;
                posY += rowHeight;
                rowHeight = 0;
            }
            if (cd.height > rowHeight) rowHeight = cd.height;
            if (cd.height > fontHeight) fontHeight = cd.height;

            cd.storedX = posX;
            cd.storedY = posY;
            chars[i] = cd;

            g.drawString(String.valueOf(ch), posX + 2, posY + fm.getAscent());
            posX += cd.width;
        }

        g.dispose();
        return uploadTexture(img);
    }

    private DynamicTexture uploadTexture(BufferedImage img) {
        DynamicTexture texture = new DynamicTexture(() -> UUID.randomUUID().toString(), IMG_SIZE, IMG_SIZE, false);

        NativeImage nativeImage = texture.getPixels();
        if (nativeImage == null) return texture;

        for (int y = 0; y < IMG_SIZE; y++) {
            for (int x = 0; x < IMG_SIZE; x++) {
                int argb = img.getRGB(x, y);
                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >>  8) & 0xFF;
                int b =  argb        & 0xFF;
                // NativeImage expects ABGR
                nativeImage.setPixel(x, y, (a << 24) | (b << 16) | (g << 8) | r);
            }
        }

        texture.upload();
        return texture;
    }

    // -------------------------------------------------------------------------
    // Public draw API
    // -------------------------------------------------------------------------

    public void drawString(GuiGraphicsExtractor gfx, String text, float x, float y, int color) {
        gfx.guiRenderState.addGuiElement(buildState(gfx, text, x, y, color, false));
    }

    public void drawCenteredString(GuiGraphicsExtractor gfx, String text, float x, float y, int color) {
        drawString(gfx, text, x - getStringWidth(text) / 2f, y, color);
    }

    public void drawStringWithShadow(GuiGraphicsExtractor gfx,
                                     String text, float x, float y, int color) {
        gfx.guiRenderState.addGuiElement(buildState(gfx, text, x + 1, y + 1, shadowColor(color), false));
        gfx.guiRenderState.addGuiElement(buildState(gfx, text, x,     y,     color,              false));
    }

    public void drawCenteredStringWithShadow(GuiGraphicsExtractor gfx,
                                             String text, float x, float y, int color) {
        float cx = x - getStringWidth(text) / 2f;
        gfx.guiRenderState.addGuiElement(buildState(gfx, text, cx + 1, y + 1, shadowColor(color), false));
        gfx.guiRenderState.addGuiElement(buildState(gfx, text, cx,     y,     color,              false));
    }

    // -------------------------------------------------------------------------
    // Render state factory
    // -------------------------------------------------------------------------

    public StringRenderState buildState(GuiGraphicsExtractor gfx, String text, float x, float y, int color, boolean shadow) {
        return new StringRenderState(gfx.pose(), text, x, y, color, shadow, gfx.scissorStack.peek());
    }

    // -------------------------------------------------------------------------
    // Metrics
    // -------------------------------------------------------------------------

    public int getStringWidth(String text) {
        if (text == null) return 0;
        int width = 0;
        for (char c : text.toCharArray()) {
            if (c < charData.length && charData[c] != null)
                width += charData[c].width - CHAR_PADDING + charOffset;
        }
        return width / 2;
    }

    public int width(String text) {
        return getStringWidth(text);
    }

    public int height() {
        return (fontHeight - CHAR_PADDING) / 2;
    }

    public String plainSubstrByWidth(String text, int width) {
        return plainSubstrByWidth(text, width, false);
    }

    public String plainSubstrByWidth(String text, int width, boolean fromEnd) {
        if (fromEnd) {
            int start = text.length();
            int w = 0;
            while (start > 0) {
                char ch = text.charAt(start - 1);
                int cw = ch < charData.length && charData[ch] != null
                        ? (charData[ch].width - CHAR_PADDING + charOffset) / 2
                        : 0;
                if (w + cw > width) break;
                w += cw;
                start--;
            }
            return text.substring(start);
        } else {
            int end = 0;
            int w = 0;
            while (end < text.length()) {
                char ch = text.charAt(end);
                int cw = ch < charData.length && charData[ch] != null
                        ? (charData[ch].width - CHAR_PADDING + charOffset) / 2
                        : 0;
                if (w + cw > width) break;
                w += cw;
                end++;
            }
            return text.substring(0, end);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static int shadowColor(int color) {
        return (color & 0xFCFCFC) >> 2 | (color & 0xFF000000);
    }

    // -------------------------------------------------------------------------
    // CharData
    // -------------------------------------------------------------------------

    private static class CharData {
        int width, height, storedX, storedY;
    }

    // =========================================================================
    // StringRenderState - implements GuiElementRenderState
    // =========================================================================

    private static final int[] COLOR_CODES = new int[32];

    static {
        for (int index = 0; index < 32; index++) {
            int alpha = (index >> 3 & 1) * 85;
            int red   = (index >> 2 & 1) * 170 + alpha;
            int green = (index >> 1 & 1) * 170 + alpha;
            int blue  = (index      & 1) * 170 + alpha;
            if (index == 6)  red += 85;  // gold fix
            if (index >= 16) { red /= 4; green /= 4; blue /= 4; } // shadow variants
            COLOR_CODES[index] = 0xFF000000 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF);
        }
    }

    public class StringRenderState implements GuiElementRenderState {

        private final Matrix3x2f pose;
        private final String text;
        private final float x, y;
        private final int color;
        private final boolean shadow;
        @Nullable private final ScreenRectangle scissorArea;
        @Nullable private final ScreenRectangle bounds;

        private StringRenderState(Matrix3x2f pose, String text, float x, float y,
                                  int color, boolean shadow,
                                  @Nullable ScreenRectangle scissor) {
            this.pose        = new Matrix3x2f(pose);
            this.text        = text;
            this.x           = x;
            this.y           = y;
            this.color       = ensureAlpha(color);
            this.shadow      = shadow;
            this.scissorArea = scissor;

            ScreenRectangle rect = new ScreenRectangle(
                    Mth.floor(x), Mth.floor(y),
                    (int) Math.ceil(getStringWidth(text)),
                    (int) Math.ceil(height())
            ).transformMaxBounds(pose);
            this.bounds = scissor != null ? scissor.intersection(rect) : rect;
        }

        @Override
        public void buildVertices(VertexConsumer vc) {

            if (shadow) emitText(vc, text, x + 0.5f, y + 0.5f, shadowColor(color), charData);
            emitText(vc, text, x, y, color, charData);
        }

        private void emitText(VertexConsumer vc, String text, float cx, float cy, int col, CharData[] data) {
            boolean bold = false, italic = false;
            CharData[] current = data;

            int origR = (col >> 16) & 0xFF;
            int origG = (col >>  8) & 0xFF;
            int origB =  col        & 0xFF;
            int a     = (col >> 24) & 0xFF;

            int r = origR, g = origG, b = origB;

            float drawX = cx;
            float drawY = cy;

            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);

                if (ch == '\u00a7' && i + 1 < text.length()) {
                    int idx = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
                    i++;

                    if (idx >= 0 && idx < 16) {
                        bold = false; italic = false;
                        current = data;
                        int cc = COLOR_CODES[shadow ? idx + 16 : idx];
                        r = (cc >> 16) & 0xFF;
                        g = (cc >>  8) & 0xFF;
                        b =  cc        & 0xFF;
                    } else if (idx == 17) { // §l bold
                        bold = true;
                        current = italic ? boldItalicCharData : boldCharData;
                    } else if (idx == 20) { // §o italic
                        italic = true;
                        current = bold ? boldItalicCharData : italicCharData;
                    } else if (idx == 21) { // §r reset
                        bold = false; italic = false;
                        current = data;
                        r = origR; g = origG; b = origB;
                    }
                    // §k obfuscated, §m strikethrough, §n underline - skipped for now
                    continue;
                }

                if (ch >= current.length || current[ch] == null) continue;

                CharData cd = current[ch];
                emitQuad(vc, drawX, drawY + offsetY, cd, r, g, b, a);
                drawX += (cd.width - CHAR_PADDING + charOffset) / 2f;
            }
        }

        private void emitQuad(VertexConsumer vc, float x, float y, CharData cd, int r, int g, int b, int a) {
            float u0 = (float)  cd.storedX              / IMG_SIZE;
            float v0 = (float)  cd.storedY              / IMG_SIZE;
            float u1 = (float) (cd.storedX + cd.width)  / IMG_SIZE;
            float v1 = (float) (cd.storedY + cd.height) / IMG_SIZE;

            float w = cd.width  / 2f;
            float h = cd.height / 2f;

            vc.addVertexWith2DPose(pose, x,     y    ).setColor(r, g, b, a).setUv(u0, v0);
            vc.addVertexWith2DPose(pose, x,     y + h).setColor(r, g, b, a).setUv(u0, v1);
            vc.addVertexWith2DPose(pose, x + w, y + h).setColor(r, g, b, a).setUv(u1, v1);
            vc.addVertexWith2DPose(pose, x + w, y    ).setColor(r, g, b, a).setUv(u1, v0);
        }

        private DynamicTexture getTexForData(CharData[] data) {
            if (data == boldCharData)      return texBold;
            if (data == italicCharData)    return texItalic;
            if (data == boldItalicCharData) return texBoldItalic;
            return tex;
        }

        @Override
        public RenderPipeline pipeline() {
            return RenderPipelines.GUI_TEXTURED; // textured pipeline for atlas quads
        }

        @Override
        public TextureSetup textureSetup() {
            return TextureSetup.singleTexture(tex.getTextureView(), tex.getSampler());
        }

        @Override
        public @Nullable ScreenRectangle scissorArea() { return scissorArea; }

        @Override
        public @Nullable ScreenRectangle bounds() { return bounds; }

        private static int ensureAlpha(int color) {
            return (color & 0xFC000000) == 0 ? color | 0xFF000000 : color;
        }
    }
}