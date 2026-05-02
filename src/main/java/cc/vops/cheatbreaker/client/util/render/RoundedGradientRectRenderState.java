package cc.vops.cheatbreaker.client.util.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.NonNull;

public class RoundedGradientRectRenderState extends GradientRectRenderState {
    protected final double borderRadius;

    public RoundedGradientRectRenderState(
            TextureSetup textureSetup,
            Matrix3x2f pose,
            @Nullable ScreenRectangle scissorArea,
            float x0, float y0, float x1, float y1,
            int topColor, int bottomColor,
            double borderRadius
    ) {
        super(textureSetup, pose, scissorArea, x0, y0, x1, y1, topColor, bottomColor, false);
        this.borderRadius = borderRadius;
    }

    @Override
    public void buildVertices(@NonNull VertexConsumer vc) {
        float r = (float) borderRadius;

        // center bar
        vc.addVertexWith2DPose(pose, x0 + r, y0).setColor(topColor);
        vc.addVertexWith2DPose(pose, x0 + r, y1).setColor(bottomColor);
        vc.addVertexWith2DPose(pose, x1 - r, y1).setColor(bottomColor);
        vc.addVertexWith2DPose(pose, x1 - r, y0).setColor(topColor);

        // left bar
        vc.addVertexWith2DPose(pose, x0, y0 + r).setColor(lerpColor(y0 + r));
        vc.addVertexWith2DPose(pose, x0, y1 - r).setColor(lerpColor(y1 - r));
        vc.addVertexWith2DPose(pose, x0 + r, y1 - r).setColor(lerpColor(y1 - r));
        vc.addVertexWith2DPose(pose, x0 + r, y0 + r).setColor(lerpColor(y0 + r));

        // right bar
        vc.addVertexWith2DPose(pose, x1 - r, y0 + r).setColor(lerpColor(y0 + r));
        vc.addVertexWith2DPose(pose, x1 - r, y1 - r).setColor(lerpColor(y1 - r));
        vc.addVertexWith2DPose(pose, x1, y1 - r).setColor(lerpColor(y1 - r));
        vc.addVertexWith2DPose(pose, x1, y0 + r).setColor(lerpColor(y0 + r));

        // corners
        emitCorner(vc, x0 + r, y0 + r, r, -1, -1); // TL
        emitCorner(vc, x1 - r, y0 + r, r, +1, -1); // TR
        emitCorner(vc, x1 - r, y1 - r, r, +1, +1); // BR
        emitCorner(vc, x0 + r, y1 - r, r, -1, +1); // BL
    }

    private int lerpColor(float y) {
        float t = (y - y0) / (y1 - y0);
        t = Math.max(0f, Math.min(1f, t));

        int aA = (topColor >> 24) & 0xFF, aB = (bottomColor >> 24) & 0xFF;
        int rA = (topColor >> 16) & 0xFF, rB = (bottomColor >> 16) & 0xFF;
        int gA = (topColor >> 8) & 0xFF, gB = (bottomColor >>  8) & 0xFF;
        int bA =  topColor & 0xFF, bB = bottomColor & 0xFF;

        int a = (int)(aA + t * (aB - aA));
        int r = (int)(rA + t * (rB - rA));
        int g = (int)(gA + t * (gB - gA));
        int b = (int)(bA + t * (bB - bA));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static final int CORNER_STEPS = 12;

    private void emitCorner(VertexConsumer vc, float cx, float cy, float radius, float xSign, float ySign) {
        for (int i = 0; i < CORNER_STEPS; i++) {
            float ya = cy + ySign * radius *  i      / CORNER_STEPS;
            float yb = cy + ySign * radius * (i + 1) / CORNER_STEPS;

            float sy0 = Math.min(ya, yb);
            float sy1 = Math.max(ya, yb);

            float dy0 = sy0 - cy;
            float dy1 = sy1 - cy;

            float xArc0 = cx + xSign * (float) Math.sqrt(Math.max(0, radius * radius - dy0 * dy0));
            float xArc1 = cx + xSign * (float) Math.sqrt(Math.max(0, radius * radius - dy1 * dy1));

            float xL0 = xSign < 0 ? xArc0 : cx;
            float xR0 = xSign < 0 ? cx    : xArc0;
            float xL1 = xSign < 0 ? xArc1 : cx;
            float xR1 = xSign < 0 ? cx    : xArc1;

            vc.addVertexWith2DPose(pose, xL0, sy0).setColor(lerpColor(sy0));
            vc.addVertexWith2DPose(pose, xL1, sy1).setColor(lerpColor(sy1));
            vc.addVertexWith2DPose(pose, xR1, sy1).setColor(lerpColor(sy1));
            vc.addVertexWith2DPose(pose, xR0, sy0).setColor(lerpColor(sy0));
        }
    }
}
