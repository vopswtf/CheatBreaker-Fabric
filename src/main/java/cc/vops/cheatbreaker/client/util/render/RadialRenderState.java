package cc.vops.cheatbreaker.client.util.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.NonNull;

public class RadialRenderState extends CircleRenderState {
    protected final float innerRadius;
    protected final float outerRadius;
    protected final float startAngle;
    protected final float endAngle;

    public RadialRenderState(
            Matrix3x2f pose, @Nullable ScreenRectangle scissorArea,
            float cx, float cy, int color,
            float innerRadius, float outerRadius, float startAngle, float endAngle
    ) {
        super(
                new Matrix3x2f(pose), scissorArea,
                cx, cy, 0, color
        );
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
    }

    private static final int STEPS = 64;

    @Override
    public void buildVertices(@NonNull VertexConsumer vc) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;

        float angleRange = endAngle - startAngle;

        for (int i = 0; i < STEPS; i++) {
            float angle1 = startAngle + (angleRange * i / STEPS);
            float angle2 = startAngle + (angleRange * (i + 1) / STEPS);

            float rad1 = (float) Math.toRadians(angle1);
            float rad2 = (float) Math.toRadians(angle2);

            // outer radius
            float ox1 = cx + (float) Math.cos(rad1) * outerRadius;
            float oy1 = cy + (float) Math.sin(rad1) * outerRadius;
            float ox2 = cx + (float) Math.cos(rad2) * outerRadius;
            float oy2 = cy + (float) Math.sin(rad2) * outerRadius;

            // inner radius
            float ix1 = cx + (float) Math.cos(rad1) * innerRadius;
            float iy1 = cy + (float) Math.sin(rad1) * innerRadius;
            float ix2 = cx + (float) Math.cos(rad2) * innerRadius;
            float iy2 = cy + (float) Math.sin(rad2) * innerRadius;

            // triangle 1
            vc.addVertexWith2DPose(pose, ox1, oy1).setColor(r, g, b, a);
            vc.addVertexWith2DPose(pose, ox2, oy2).setColor(r, g, b, a);
            vc.addVertexWith2DPose(pose, ix1, iy1).setColor(r, g, b, a);

            // triangle 2
            vc.addVertexWith2DPose(pose, ox2, oy2).setColor(r, g, b, a);
            vc.addVertexWith2DPose(pose, ix2, iy2).setColor(r, g, b, a);
            vc.addVertexWith2DPose(pose, ix1, iy1).setColor(r, g, b, a);
        }
    }
}