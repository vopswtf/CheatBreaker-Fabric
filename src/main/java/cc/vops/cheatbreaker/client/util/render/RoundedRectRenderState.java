package cc.vops.cheatbreaker.client.util.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.NonNull;

public class RoundedRectRenderState extends RectRenderState {
    protected final double borderRadius;

    public RoundedRectRenderState(
            Matrix3x2f pose,
            @Nullable ScreenRectangle scissorArea,
            float x0, float y0, float x1, float y1,
            int color,
            double borderRadius
    ) {
        super(
                new Matrix3x2f(pose), TextureSetup.noTexture(), scissorArea,
                x0, y0, x1, y1,
                color
        );
        this.borderRadius = borderRadius;
    }

    @Override
    public void buildVertices(@NonNull VertexConsumer vc) {
        float r = (float) borderRadius;
        int red   = (color >> 16) & 0xFF;
        int green = (color >>  8) & 0xFF;
        int blue  =  color        & 0xFF;
        int alpha = (color >> 24) & 0xFF;

        // center bar
        vc.addVertexWith2DPose(pose, x0 + r, y0).setColor(color);
        vc.addVertexWith2DPose(pose, x0 + r, y1).setColor(color);
        vc.addVertexWith2DPose(pose, x1 - r, y1).setColor(color);
        vc.addVertexWith2DPose(pose, x1 - r, y0).setColor(color);

        // left bar
        vc.addVertexWith2DPose(pose, x0,     y0 + r).setColor(color);
        vc.addVertexWith2DPose(pose, x0,     y1 - r).setColor(color);
        vc.addVertexWith2DPose(pose, x0 + r, y1 - r).setColor(color);
        vc.addVertexWith2DPose(pose, x0 + r, y0 + r).setColor(color);

        // right bar
        vc.addVertexWith2DPose(pose, x1 - r, y0 + r).setColor(color);
        vc.addVertexWith2DPose(pose, x1 - r, y1 - r).setColor(color);
        vc.addVertexWith2DPose(pose, x1,     y1 - r).setColor(color);
        vc.addVertexWith2DPose(pose, x1,     y0 + r).setColor(color);

        // corners
        emitCorner(vc, x0 + r, y0 + r, r, -1, -1, red, green, blue, alpha); // TL
        emitCorner(vc, x1 - r, y0 + r, r, +1, -1, red, green, blue, alpha); // TR
        emitCorner(vc, x1 - r, y1 - r, r, +1, +1, red, green, blue, alpha); // BR
        emitCorner(vc, x0 + r, y1 - r, r, -1, +1, red, green, blue, alpha); // BL
    }

    private static final int CORNER_STEPS = 12;

    private void emitCorner(VertexConsumer vc, float cx, float cy, float radius,
                            float xSign, float ySign,
                            int r, int g, int b, int a) {
        for (int i = 0; i < CORNER_STEPS; i++) {
            float ya = cy + ySign * radius *  i      / CORNER_STEPS;
            float yb = cy + ySign * radius * (i + 1) / CORNER_STEPS;

            // always keep y0 < y1 for consistent CCW winding
            float y0 = Math.min(ya, yb);
            float y1 = Math.max(ya, yb);

            float dy0 = y0 - cy;
            float dy1 = y1 - cy;

            float xArc0 = cx + xSign * (float) Math.sqrt(Math.max(0, radius * radius - dy0 * dy0));
            float xArc1 = cx + xSign * (float) Math.sqrt(Math.max(0, radius * radius - dy1 * dy1));

            float xL0 = xSign < 0 ? xArc0 : cx;
            float xR0 = xSign < 0 ? cx     : xArc0;
            float xL1 = xSign < 0 ? xArc1 : cx;
            float xR1 = xSign < 0 ? cx     : xArc1;

            vc.addVertexWith2DPose(pose, xL0, y0).setColor(r, g, b, a);
            vc.addVertexWith2DPose(pose, xL1, y1).setColor(r, g, b, a);
            vc.addVertexWith2DPose(pose, xR1, y1).setColor(r, g, b, a);
            vc.addVertexWith2DPose(pose, xR0, y0).setColor(r, g, b, a);
        }
    }
}
