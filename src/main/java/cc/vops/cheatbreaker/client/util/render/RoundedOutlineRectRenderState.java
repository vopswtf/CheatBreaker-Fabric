package cc.vops.cheatbreaker.client.util.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.NonNull;

public class RoundedOutlineRectRenderState extends RoundedRectRenderState {
    protected final float thickness;

    public RoundedOutlineRectRenderState(
            Matrix3x2f pose,
            @Nullable ScreenRectangle scissorArea,
            float x0, float y0, float x1, float y1,
            int color,
            float thickness,
            float borderRadius
) {
        super(
                new Matrix3x2f(pose), scissorArea,
                x0, y0, x1, y1,
                color,
                borderRadius
    );
        this.thickness = thickness;
    }

    @Override
    public void buildVertices(@NonNull VertexConsumer vc) {
        float borderRadius = (float) this.borderRadius;

        // top bar (between corners)
        vc.addVertexWith2DPose(pose, x0 + borderRadius, y0).setColor(color);
        vc.addVertexWith2DPose(pose, x0 + borderRadius, y0 + thickness).setColor(color);
        vc.addVertexWith2DPose(pose, x1 - borderRadius, y0 + thickness).setColor(color);
        vc.addVertexWith2DPose(pose, x1 - borderRadius, y0).setColor(color);

        // bottom bar
        vc.addVertexWith2DPose(pose, x0 + borderRadius, y1 - thickness).setColor(color);
        vc.addVertexWith2DPose(pose, x0 + borderRadius, y1).setColor(color);
        vc.addVertexWith2DPose(pose, x1 - borderRadius, y1).setColor(color);
        vc.addVertexWith2DPose(pose, x1 - borderRadius, y1 - thickness).setColor(color);

        // left bar
        vc.addVertexWith2DPose(pose, x0, y0 + borderRadius).setColor(color);
        vc.addVertexWith2DPose(pose, x0, y1 - borderRadius).setColor(color);
        vc.addVertexWith2DPose(pose, x0 + thickness, y1 - borderRadius).setColor(color);
        vc.addVertexWith2DPose(pose, x0 + thickness, y0 + borderRadius).setColor(color);

        // right bar
        vc.addVertexWith2DPose(pose, x1 - thickness, y0 + borderRadius).setColor(color);
        vc.addVertexWith2DPose(pose, x1 - thickness, y1 - borderRadius).setColor(color);
        vc.addVertexWith2DPose(pose, x1, y1 - borderRadius).setColor(color);
        vc.addVertexWith2DPose(pose, x1, y0 + borderRadius).setColor(color);

        // corners
        emitCorner(vc, x0 + borderRadius, y0 + borderRadius, borderRadius, thickness, -1, -1); // TL
        emitCorner(vc, x1 - borderRadius, y0 + borderRadius, borderRadius, thickness, +1, -1); // TR
        emitCorner(vc, x1 - borderRadius, y1 - borderRadius, borderRadius, thickness, +1, +1); // BR
        emitCorner(vc, x0 + borderRadius, y1 - borderRadius, borderRadius, thickness, -1, +1); // BL
    }

    private static final int CORNER_STEPS = 12;

    private void emitCorner(VertexConsumer vc, float cx, float cy,
                            float outerRadius, float thickness,
                            float xSign, float ySign) {
        float innerRadius = outerRadius - thickness;

        for (int i = 0; i < CORNER_STEPS; i++) {
            float ya = cy + ySign * outerRadius *  i      / CORNER_STEPS;
            float yb = cy + ySign * outerRadius * (i + 1) / CORNER_STEPS;

            float sy0 = Math.min(ya, yb);
            float sy1 = Math.max(ya, yb);

            float dy0 = sy0 - cy;
            float dy1 = sy1 - cy;

            // outer arc
            float xOut0 = cx + xSign * (float) Math.sqrt(Math.max(0, outerRadius * outerRadius - dy0 * dy0));
            float xOut1 = cx + xSign * (float) Math.sqrt(Math.max(0, outerRadius * outerRadius - dy1 * dy1));

            // inner arc
            float xIn0  = cx + xSign * (float) Math.sqrt(Math.max(0, innerRadius * innerRadius - dy0 * dy0));
            float xIn1  = cx + xSign * (float) Math.sqrt(Math.max(0, innerRadius * innerRadius - dy1 * dy1));

            // left/right assignment based on xSign
            float xL0 = xSign < 0 ? xOut0 : xIn0;
            float xR0 = xSign < 0 ? xIn0  : xOut0;
            float xL1 = xSign < 0 ? xOut1 : xIn1;
            float xR1 = xSign < 0 ? xIn1  : xOut1;

            vc.addVertexWith2DPose(pose, xL0, sy0).setColor(color);
            vc.addVertexWith2DPose(pose, xL1, sy1).setColor(color);
            vc.addVertexWith2DPose(pose, xR1, sy1).setColor(color);
            vc.addVertexWith2DPose(pose, xR0, sy0).setColor(color);
        }
    }
}