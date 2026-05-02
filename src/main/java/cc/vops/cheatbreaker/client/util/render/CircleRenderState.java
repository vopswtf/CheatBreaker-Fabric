package cc.vops.cheatbreaker.client.util.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.NonNull;

public class CircleRenderState extends CBRenderState {
    protected final float cx;
    protected final float cy;
    protected final float radius;
    protected final int color;

    public CircleRenderState(
            Matrix3x2f pose,
            @Nullable ScreenRectangle scissorArea,
            float cx, float cy, float radius, int color
    ) {
        super(
                TextureSetup.noTexture(),
                new Matrix3x2f(pose),
                scissorArea
        );
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        this.color = color;
    }

    private static final int STEPS = 24;

    @Override
    public void buildVertices(@NonNull VertexConsumer vc) {
        int r = (color >> 16) & 0xFF;
        int g = (color >>  8) & 0xFF;
        int b =  color        & 0xFF;
        int a = (color >> 24) & 0xFF;

        for (int i = 0; i < STEPS; i++) {
            float ya = cy + radius *  i      / STEPS;
            float yb = cy + radius * (i + 1) / STEPS;

            // top half (going upward from center)
            float ty0 = cy - (yb - cy);
            float ty1 = cy - (ya - cy);

            strip(vc, ty0, ty1, r, g, b, a);
            // bottom half
            strip(vc, ya, yb, r, g, b, a);
        }
    }

    private void strip(VertexConsumer vc, float sy0, float sy1,
                           int r, int g, int b, int a) {
        float dy0 = sy0 - cy;
        float dy1 = sy1 - cy;

        float xHalf0 = (float) Math.sqrt(Math.max(0, radius * radius - dy0 * dy0));
        float xHalf1 = (float) Math.sqrt(Math.max(0, radius * radius - dy1 * dy1));

        vc.addVertexWith2DPose(pose, cx - xHalf0, sy0).setColor(r, g, b, a);
        vc.addVertexWith2DPose(pose, cx - xHalf1, sy1).setColor(r, g, b, a);
        vc.addVertexWith2DPose(pose, cx + xHalf1, sy1).setColor(r, g, b, a);
        vc.addVertexWith2DPose(pose, cx + xHalf0, sy0).setColor(r, g, b, a);
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