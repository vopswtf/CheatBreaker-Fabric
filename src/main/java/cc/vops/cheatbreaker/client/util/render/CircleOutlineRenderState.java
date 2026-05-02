package cc.vops.cheatbreaker.client.util.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.NonNull;

public class CircleOutlineRenderState extends CBRenderState {
    private final float cx;
    private final float cy;
    private final float radius;
    private final float thickness;
    private final int color;

    public CircleOutlineRenderState(
            Matrix3x2f pose, @Nullable ScreenRectangle scissorArea,
            float cx, float cy, float radius, float thickness, int color
    ) {
        super(
                TextureSetup.noTexture(),
                new Matrix3x2f(pose),
                scissorArea
        );
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        this.thickness = thickness;
        this.color = color;
    }

    private static final int STEPS = 64;

    @Override
    public void buildVertices(@NonNull VertexConsumer vc) {
        float inner = radius - thickness;
        int r = (color >> 16) & 0xFF;
        int g = (color >>  8) & 0xFF;
        int b =  color        & 0xFF;
        int a = (color >> 24) & 0xFF;

        for (int i = 0; i < STEPS; i++) {
            float ya = cy + radius *  i      / STEPS;
            float yb = cy + radius * (i + 1) / STEPS;

            // top half
            emitStrip(vc, cy - (yb - cy), cy - (ya - cy), inner, r, g, b, a);
            // bottom half
            emitStrip(vc, ya, yb, inner, r, g, b, a);
        }
    }

    private void emitStrip(VertexConsumer vc, float sy0, float sy1, float innerRadius,
                           int r, int g, int b, int a) {
        float dy0 = sy0 - cy;
        float dy1 = sy1 - cy;

        float xOuter0 = (float) Math.sqrt(Math.max(0, radius      * radius      - dy0 * dy0));
        float xOuter1 = (float) Math.sqrt(Math.max(0, radius      * radius      - dy1 * dy1));
        float xInner0 = (float) Math.sqrt(Math.max(0, innerRadius * innerRadius - dy0 * dy0));
        float xInner1 = (float) Math.sqrt(Math.max(0, innerRadius * innerRadius - dy1 * dy1));

        // left ring strip
        vc.addVertexWith2DPose(pose, cx - xOuter0, sy0).setColor(r, g, b, a);
        vc.addVertexWith2DPose(pose, cx - xOuter1, sy1).setColor(r, g, b, a);
        vc.addVertexWith2DPose(pose, cx - xInner1, sy1).setColor(r, g, b, a);
        vc.addVertexWith2DPose(pose, cx - xInner0, sy0).setColor(r, g, b, a);

        // right ring strip
        vc.addVertexWith2DPose(pose, cx + xInner0, sy0).setColor(r, g, b, a);
        vc.addVertexWith2DPose(pose, cx + xInner1, sy1).setColor(r, g, b, a);
        vc.addVertexWith2DPose(pose, cx + xOuter1, sy1).setColor(r, g, b, a);
        vc.addVertexWith2DPose(pose, cx + xOuter0, sy0).setColor(r, g, b, a);
    }

    @Override
    public ScreenRectangle getBounds() {
        ScreenRectangle rect = new ScreenRectangle(
                Mth.floor(cx - radius),
                Mth.floor(cy - radius),
                Mth.ceil(radius * 2),
                Mth.ceil(radius * 2)
        ).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(rect) : rect;
    }
}