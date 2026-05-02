package cc.vops.cheatbreaker.client.util;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.util.font.CBFontRenderer;
import cc.vops.cheatbreaker.client.util.render.*;
import cc.vops.cheatbreaker.mixin.FontAccessor;
import cc.vops.cheatbreaker.mixin.TTFAccessor;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.font.TrueTypeGlyphProvider;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.DynamicUniforms;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.lwjgl.opengl.GL11;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Math;
import java.util.Base64;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class RenderUtil {
    public static void drawTexturedModalRect(GuiGraphicsExtractor gfx, Identifier texture, float x, float y, float u, float v, int width, int height, int color) {
        final float w = 256f;
        final float h = 256f;

        AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(texture);

        gfx.guiRenderState.addGuiElement(new FloatBlitRenderState(
                gfx.pose(),
                TextureSetup.singleTexture(tex.getTextureView(), tex.getSampler()),
                gfx.scissorStack.peek(),
                x, y,
                x + width, y + height,
                color,
                u / w, (u + width)  / w,
                v / h, (v + height) / h
        ));
    }

    public static void drawRectWithOutline(GuiGraphicsExtractor gfx, float x1, float y1, float x2, float y2, float thickness, int outlineColor, int fillColor) {
        gfx.guiRenderState.addGuiElement(
                new RectRenderState(
                        gfx.pose(),
                        TextureSetup.noTexture(),
                        gfx.scissorStack.peek(),
                        x1, y1, x2, y2,
                        fillColor
                )
        );

        if (thickness > 0) {
            gfx.guiRenderState.addGuiElement(
                    new OutlineRectRenderState(
                            gfx.pose(),
                            x1, y1, x2, y2,
                            outlineColor,
                            thickness / 2,
                            gfx.scissorStack.peek()
                    )
            );
        }
    }

    public static void drawTexturedQuad(
            GuiGraphicsExtractor gfx,
            Identifier texture,
            float x, float y,
            float u, float v,
            int width, int height
    ) {
        final float uvScale = 3.875f * 0.0010080645f;
        final float texSize = 256f;

        float u0 = (u * uvScale * texSize) / texSize;
        float u1 = (u * uvScale * texSize + width) / texSize;
        float v0 = (v * uvScale * texSize) / texSize;
        float v1 = (v * uvScale * texSize + height) / texSize;

        if (texture == null) return;
        AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(texture);

        gfx.guiRenderState.addGuiElement(new FloatBlitRenderState(
                gfx.pose(),
                TextureSetup.singleTexture(tex.getTextureView(), tex.getSampler()),
                gfx.scissorStack.peek(),
                x, y,
                x + width, y + height,
                -1,
                u0, u1,
                v0, v1
        ));
    }

    public static void drawBlit(
            GuiGraphicsExtractor gfx,
            Identifier texture,
            float x, float y,
            float u, float v,
            float uWidth, float vHeight,
            float texWidth, float texHeight,
            int color
    ) {
        AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(texture);

        gfx.guiRenderState.addGuiElement(new FloatBlitRenderState(
                new Matrix3x2f(gfx.pose()),
                TextureSetup.singleTexture(tex.getTextureView(), tex.getSampler()),
                gfx.scissorStack.peek(),
                x, y,
                x + uWidth, y + vHeight,
                color,
                u / texWidth, (u + uWidth) / texWidth,
                v / texHeight, (v + vHeight) / texHeight
        ));
    }

    public static void drawOutline(GuiGraphicsExtractor gfx, int x, int y, int width, int height, int color) {
        gfx.guiRenderState.addGuiElement(
                new OutlineRectRenderState(
                        gfx.pose(),
                        x, y, x + width, y + height,
                        color,
                        0.5f,
                        gfx.scissorStack.peek()
                )
        );
    }

    public static void drawRoundedRect(GuiGraphicsExtractor gfx, double x1, double y1, double x2, double y2, double radius, int color) {
        gfx.guiRenderState.addGuiElement(
                new RoundedRectRenderState(
                        gfx.pose(),
                        gfx.scissorStack.peek(),
                        (float)x1, (float)y1, (float)x2, (float)y2,
                        color,
                        radius / 2
                )
        );
    }

    public static void drawGradientRect(GuiGraphicsExtractor gfx, double x1, double y1, double x2, double y2, int topColor, int bottomColor) {
        gfx.guiRenderState.addGuiElement(
                new GradientRectRenderState(
                        TextureSetup.noTexture(),
                        gfx.pose(),
                        gfx.scissorStack.peek(),
                        (float)x1, (float)y1, (float)x2, (float)y2,
                        topColor, bottomColor, false
                )
        );
    }

    public static void drawCorneredGradientRectWithOutline(GuiGraphicsExtractor graphics, float left, float top, float right, float bottom, int outlineColour, int gradientStart, int gradientEnd) {
        graphics.guiRenderState.addGuiElement(
                new RoundedGradientRectRenderState(
                        TextureSetup.noTexture(),
                        graphics.pose(),
                        graphics.scissorStack.peek(),
                        left, top, right, bottom,
                        gradientStart, gradientEnd,
                        2.0f
                )
        );

        graphics.guiRenderState.addGuiElement(
                new RoundedOutlineRectRenderState(
                        graphics.pose(),
                        graphics.scissorStack.peek(),
                        left, top, right, bottom,
                        outlineColour,
                        0.5f,
                        2.0f
                )
        );
    }

    // Legacy rendering - Native MC
    public static boolean isFontUnloaded(Font font) {
        try {
            if (((FontAccessor) font).getProvider() instanceof TrueTypeGlyphProvider provider) {
                ((TTFAccessor) provider).invokeValidateFontOpen();
            }
        } catch (Exception e) {
            return true;
        }

        return false;
    }

    public static void drawCenteredString(GuiGraphicsExtractor gfx, Font font, String string, int i, int j, int k) {
        if (isFontUnloaded(font)) return;
        gfx.text(font, string, i - font.width(string) / 2, j, k, false);
    }

    public static void drawCenteredString(GuiGraphicsExtractor gfx, Font font, String string, float i, float j, int k) {
        if (isFontUnloaded(font)) return;
        gfx.pose().pushMatrix();
        gfx.pose().translate((i - (int)i), (j - (int)j));
        gfx.text(font, string, (int)i - font.width(string) / 2, (int)j, k, false);
        gfx.pose().popMatrix();
    }

    public static void drawCenteredStringWithShadow(GuiGraphicsExtractor gfx, Font font, String string, int i, int j, int k) {
        if (isFontUnloaded(font)) return;
        gfx.text(font, string, i - font.width(string) / 2, j, k, true);
    }

    public static void drawString(GuiGraphicsExtractor gfx, Font font, String string, int i, int j, int k) {
        if (isFontUnloaded(font)) return;
        gfx.text(font, string, i, j, k, false);
    }

    public static void drawString(GuiGraphicsExtractor gfx, Font font, String string, float i, float j, int k) {
        if (isFontUnloaded(font)) return;
        gfx.text(font, string, (int)i, (int)j, k, false);
    }

    public static void drawStringWithShadow(GuiGraphicsExtractor gfx, Font font, String string, float x, float y, int color) {
        if (isFontUnloaded(font)) return;
        gfx.text(font, string, (int)x, (int)y, color, true);
    }

    // CB Font Rendering
    public static void drawCenteredString(GuiGraphicsExtractor gfx, CBFontRenderer font, String string, float i, float j, int k) {
        font.drawCenteredString(gfx, string, i, j, k);
    }

    public static void drawCenteredStringWithShadow(GuiGraphicsExtractor gfx, CBFontRenderer font, String string, float i, float j, int k) {
        font.drawCenteredString(gfx, string, i, j, k);
    }

    public static void drawString(GuiGraphicsExtractor gfx, CBFontRenderer font, String string, float i, float j, int k) {
        font.drawString(gfx, string, i, j, k);
    }

    public static void drawString(GuiGraphicsExtractor gfx, CBFontRenderer font, String string, float i, float j, int k, boolean shadow) {
        if (shadow) {
            font.drawStringWithShadow(gfx, string, i, j, k);
        } else {
            font.drawString(gfx, string, i, j, k);
        }
    }

    public static void drawStringWithShadow(GuiGraphicsExtractor gfx, CBFontRenderer font, String string, float x, float y, int color) {
        font.drawStringWithShadow(gfx, string, x, y, color);
    }

    public static void drawIcon(GuiGraphicsExtractor gfx, Identifier texture, float size, float x, float y) {
        float imageSize = size * 2.0f;
        drawIcon(gfx, texture, x, y, imageSize, imageSize);
    }

    public static void drawIcon(GuiGraphicsExtractor gfx, Identifier texture, float size, float x, float y, int color) {
        float imageSize = size * 2.0f;
        drawIcon(gfx, texture, x, y, imageSize, imageSize, color);
    }

    public static void drawBoxWithOutLine(GuiGraphicsExtractor gfx, float f, float f2, float f3, float f4, float f5, int n, int n2) {
        drawRectWithOutline(gfx, f, f2, f3, f4, f5, n, n2);
    }

    public static void drawIcon(GuiGraphicsExtractor gfx, Identifier texture, float x, float y, float width, float height) {
//        gfx.blit(
//                RenderPipelines.GUI_TEXTURED,
//                texture,
//                (int) x, (int) y,
//                0f, 0f,
//                (int) width, (int) height,
//                (int) width, (int) height,
//                (int) width, (int) height,
//                -1
//        );
        AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(texture);

        gfx.guiRenderState.addGuiElement(new FloatBlitRenderState(
                gfx.pose(),
                TextureSetup.singleTexture(tex.getTextureView(), tex.getSampler()),
                gfx.scissorStack.peek(),
                x, y,
                x + width, y + height,
                -1,
                0f, 1f,
                0f, 1f
        ));
    }


    public static void drawIcon(GuiGraphicsExtractor gfx, Identifier texture, float x, float y, float width, float height, int color) {
        AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(texture);

        gfx.guiRenderState.addGuiElement(new FloatBlitRenderState(
                gfx.pose(),
                TextureSetup.singleTexture(tex.getTextureView(), tex.getSampler()),
                gfx.scissorStack.peek(),
                x, y,
                x + width, y + height,
                color,
                0f, 1f,
                0f, 1f
        ));
    }

    public static void drawCircle(GuiGraphicsExtractor gfx, double cx, double cy, double radius, int color) {
        gfx.guiRenderState.addGuiElement(new CircleRenderState(
                gfx.pose(),
                gfx.scissorStack.peek(),
                (float) cx, (float) cy,
                (float) radius,
                color
        ));
    }

    public static void drawCircleWithOutLine(GuiGraphicsExtractor gfx, double cx, double cy, double outerRadius, double innerRadius, int fillColor) {
        gfx.guiRenderState.addGuiElement(new RadialRenderState(
                gfx.pose(),
                gfx.scissorStack.peek(),
                (float) cx, (float) cy,
                fillColor,
                (float) outerRadius,
                (float) innerRadius,
                0.0f,
                360.0f
        ));
    }

    public static void drawRadial(GuiGraphicsExtractor gfx, double centerX, double centerY, double innerRadius, double outerRadius, double startAngle, double endAngle, int color) {
        gfx.guiRenderState.addGuiElement(
                new RadialRenderState(
                        gfx.pose(),
                        gfx.scissorStack.peek(),
                        (float) centerX, (float) centerY,
                        color,
                        (float) innerRadius, (float) outerRadius,
                        (float) startAngle, (float) endAngle
                )
        );
    }


    public static void drawRect(GuiGraphicsExtractor gfx, float x1, float y1, float x2, float y2, int color) {
        gfx.guiRenderState.addGuiElement(
                new RectRenderState(
                        gfx.pose(),
                        TextureSetup.noTexture(),
                        gfx.scissorStack.peek(),
                        Math.min(x1, x2), Math.min(y1, y2),
                        Math.max(x1, x2), Math.max(y1, y2),
                        color
                )
        );
    }

    public static final RenderPipeline ICON_PIPELINE =
            RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
                    .withLocation("pipeline/cb_icon")
                    .withVertexShader("core/position_tex_color")
                    .withFragmentShader("core/position_tex_color")
                    .withSampler("Sampler0")
                    .withCull(false)
                    .withDepthStencilState(DepthStencilState.DEFAULT)
                    .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
                    .build();

    static {
        RenderPipelines.register(ICON_PIPELINE);
    }

    public static RenderType icon(Identifier texture) {
        return RenderType.create(
                "cb_icon",
                RenderSetup.builder(ICON_PIPELINE)
                        .bufferSize(256)
                        .withTexture("cheatbreaker_team_arrow", texture)
                        .createRenderSetup()
        );
    }

    public static Identifier LOGO = CheatBreaker.asset("logo_108.png");
    public static final String LOGO_BASE64 = "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/GMzLdsAADtmaVRYdFhNTDpjb20uYWRvYmUueG1wAAAAAAA8P3hwYWNrZXQgYmVnaW49Iu+7vyIgaWQ9Ilc1TTBNcENlaGlIenJlU3pOVGN6a2M5ZCI/Pgo8eDp4bXBtZXRhIHhtbG5zOng9ImFkb2JlOm5zOm1ldGEvIiB4OnhtcHRrPSJBZG9iZSBYTVAgQ29yZSA1LjYtYzEzOCA3OS4xNTk4MjQsIDIwMTYvMDkvMTQtMDE6MDk6MDEgICAgICAgICI+CiAgIDxyZGY6UkRGIHhtbG5zOnJkZj0iaHR0cDovL3d3dy53My5vcmcvMTk5OS8wMi8yMi1yZGYtc3ludGF4LW5zIyI+CiAgICAgIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiCiAgICAgICAgICAgIHhtbG5zOnhtcD0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wLyIKICAgICAgICAgICAgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iCiAgICAgICAgICAgIHhtbG5zOnN0RXZ0PSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VFdmVudCMiCiAgICAgICAgICAgIHhtbG5zOnBob3Rvc2hvcD0iaHR0cDovL25zLmFkb2JlLmNvbS9waG90b3Nob3AvMS4wLyIKICAgICAgICAgICAgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIgogICAgICAgICAgICB4bWxuczp0aWZmPSJodHRwOi8vbnMuYWRvYmUuY29tL3RpZmYvMS4wLyIKICAgICAgICAgICAgeG1sbnM6ZXhpZj0iaHR0cDovL25zLmFkb2JlLmNvbS9leGlmLzEuMC8iPgogICAgICAgICA8eG1wOkNyZWF0b3JUb29sPkFkb2JlIFBob3Rvc2hvcCBDQyAyMDE3IChXaW5kb3dzKTwveG1wOkNyZWF0b3JUb29sPgogICAgICAgICA8eG1wOkNyZWF0ZURhdGU+MjAxOC0wMy0wNFQxNToxNDoxMiswMTowMDwveG1wOkNyZWF0ZURhdGU+CiAgICAgICAgIDx4bXA6TWV0YWRhdGFEYXRlPjIwMTgtMDMtMDRUMTU6MTQ6MTIrMDE6MDA8L3htcDpNZXRhZGF0YURhdGU+CiAgICAgICAgIDx4bXA6TW9kaWZ5RGF0ZT4yMDE4LTAzLTA0VDE1OjE0OjEyKzAxOjAwPC94bXA6TW9kaWZ5RGF0ZT4KICAgICAgICAgPHhtcE1NOkluc3RhbmNlSUQ+eG1wLmlpZDpkMmVmZjg1My1kYWUwLTAzNGEtODIzMy1lMzAyMmI0YzdlZDQ8L3htcE1NOkluc3RhbmNlSUQ+CiAgICAgICAgIDx4bXBNTTpEb2N1bWVudElEPmFkb2JlOmRvY2lkOnBob3Rvc2hvcDo0ZmVjNDAzMS0xZmI2LTExZTgtOGM3NS1iMTc2NGE3YjI2NGY8L3htcE1NOkRvY3VtZW50SUQ+CiAgICAgICAgIDx4bXBNTTpPcmlnaW5hbERvY3VtZW50SUQ+eG1wLmRpZDphNGNkMDNjYi1hY2FjLTc1NGQtYTk1YS0yZGQxNGRhZDcwNGY8L3htcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD4KICAgICAgICAgPHhtcE1NOkhpc3Rvcnk+CiAgICAgICAgICAgIDxyZGY6U2VxPgogICAgICAgICAgICAgICA8cmRmOmxpIHJkZjpwYXJzZVR5cGU9IlJlc291cmNlIj4KICAgICAgICAgICAgICAgICAgPHN0RXZ0OmFjdGlvbj5jcmVhdGVkPC9zdEV2dDphY3Rpb24+CiAgICAgICAgICAgICAgICAgIDxzdEV2dDppbnN0YW5jZUlEPnhtcC5paWQ6YTRjZDAzY2ItYWNhYy03NTRkLWE5NWEtMmRkMTRkYWQ3MDRmPC9zdEV2dDppbnN0YW5jZUlEPgogICAgICAgICAgICAgICAgICA8c3RFdnQ6d2hlbj4yMDE4LTAzLTA0VDE1OjE0OjEyKzAxOjAwPC9zdEV2dDp3aGVuPgogICAgICAgICAgICAgICAgICA8c3RFdnQ6c29mdHdhcmVBZ2VudD5BZG9iZSBQaG90b3Nob3AgQ0MgMjAxNyAoV2luZG93cyk8L3N0RXZ0OnNvZnR3YXJlQWdlbnQ+CiAgICAgICAgICAgICAgIDwvcmRmOmxpPgogICAgICAgICAgICAgICA8cmRmOmxpIHJkZjpwYXJzZVR5cGU9IlJlc291cmNlIj4KICAgICAgICAgICAgICAgICAgPHN0RXZ0OmFjdGlvbj5zYXZlZDwvc3RFdnQ6YWN0aW9uPgogICAgICAgICAgICAgICAgICA8c3RFdnQ6aW5zdGFuY2VJRD54bXAuaWlkOmQyZWZmODUzLWRhZTAtMDM0YS04MjMzLWUzMDIyYjRjN2VkNDwvc3RFdnQ6aW5zdGFuY2VJRD4KICAgICAgICAgICAgICAgICAgPHN0RXZ0OndoZW4+MjAxOC0wMy0wNFQxNToxNDoxMiswMTowMDwvc3RFdnQ6d2hlbj4KICAgICAgICAgICAgICAgICAgPHN0RXZ0OnNvZnR3YXJlQWdlbnQ+QWRvYmUgUGhvdG9zaG9wIENDIDIwMTcgKFdpbmRvd3MpPC9zdEV2dDpzb2Z0d2FyZUFnZW50PgogICAgICAgICAgICAgICAgICA8c3RFdnQ6Y2hhbmdlZD4vPC9zdEV2dDpjaGFuZ2VkPgogICAgICAgICAgICAgICA8L3JkZjpsaT4KICAgICAgICAgICAgPC9yZGY6U2VxPgogICAgICAgICA8L3htcE1NOkhpc3Rvcnk+CiAgICAgICAgIDxwaG90b3Nob3A6RG9jdW1lbnRBbmNlc3RvcnM+CiAgICAgICAgICAgIDxyZGY6QmFnPgogICAgICAgICAgICAgICA8cmRmOmxpPmFkb2JlOmRvY2lkOnBob3Rvc2hvcDpkZTA3YTk0ZS0xOTU5LTExZTgtOTJiMC1mZjljYTg3N2UzZGI8L3JkZjpsaT4KICAgICAgICAgICAgICAgPHJkZjpsaT5hZG9iZTpkb2NpZDpwaG90b3Nob3A6ZmVlM2MwZGEtMTk1NS0xMWU4LTk3YTctYjI5OThlMTM3ZDAyPC9yZGY6bGk+CiAgICAgICAgICAgIDwvcmRmOkJhZz4KICAgICAgICAgPC9waG90b3Nob3A6RG9jdW1lbnRBbmNlc3RvcnM+CiAgICAgICAgIDxwaG90b3Nob3A6Q29sb3JNb2RlPjM8L3Bob3Rvc2hvcDpDb2xvck1vZGU+CiAgICAgICAgIDxwaG90b3Nob3A6SUNDUHJvZmlsZT5zUkdCIElFQzYxOTY2LTIuMTwvcGhvdG9zaG9wOklDQ1Byb2ZpbGU+CiAgICAgICAgIDxkYzpmb3JtYXQ+aW1hZ2UvcG5nPC9kYzpmb3JtYXQ+CiAgICAgICAgIDx0aWZmOk9yaWVudGF0aW9uPjE8L3RpZmY6T3JpZW50YXRpb24+CiAgICAgICAgIDx0aWZmOlhSZXNvbHV0aW9uPjExODExMDAvMTAwMDA8L3RpZmY6WFJlc29sdXRpb24+CiAgICAgICAgIDx0aWZmOllSZXNvbHV0aW9uPjExODExMDAvMTAwMDA8L3RpZmY6WVJlc29sdXRpb24+CiAgICAgICAgIDx0aWZmOlJlc29sdXRpb25Vbml0PjM8L3RpZmY6UmVzb2x1dGlvblVuaXQ+CiAgICAgICAgIDxleGlmOkNvbG9yU3BhY2U+MTwvZXhpZjpDb2xvclNwYWNlPgogICAgICAgICA8ZXhpZjpQaXhlbFhEaW1lbnNpb24+MTA4PC9leGlmOlBpeGVsWERpbWVuc2lvbj4KICAgICAgICAgPGV4aWY6UGl4ZWxZRGltZW5zaW9uPjEwODwvZXhpZjpQaXhlbFlEaW1lbnNpb24+CiAgICAgIDwvcmRmOkRlc2NyaXB0aW9uPgogICA8L3JkZjpSREY+CjwveDp4bXBtZXRhPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgIAo8P3hwYWNrZXQgZW5kPSJ3Ij8+0WvuLAAAACBjSFJNAAB6JQAAgIMAAPn/AACA6QAAdTAAAOpgAAA6mAAAF2+SX8VGAAANBElEQVR42uyde7wVVRXHv3e4CKKoaAgKpCYHQTEV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";
}
