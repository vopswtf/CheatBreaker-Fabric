package cc.vops.cheatbreaker.client.util.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.NonNull;

public class FloatBlitRenderState extends RectRenderState {
    private final float u0, u1, v0, v1;

    public FloatBlitRenderState(
            Matrix3x2f pose,
            TextureSetup textureSetup,
            @Nullable ScreenRectangle scissorArea,
            float x0, float y0, float x1, float y1, int color,
            float u0, float u1, float v0, float v1
    ) {
        super(
                new Matrix3x2f(pose),
                textureSetup,
                scissorArea,
                x0, y0, x1, y1, color
        );

        this.u0 = u0;
        this.u1 = u1;
        this.v0 = v0;
        this.v1 = v1;
    }

    @Override
    public void buildVertices(@NonNull VertexConsumer vc) {
        vc.addVertexWith2DPose(pose, x0, y0).setUv(u0, v0).setColor(color);
        vc.addVertexWith2DPose(pose, x0, y1).setUv(u0, v1).setColor(color);
        vc.addVertexWith2DPose(pose, x1, y1).setUv(u1, v1).setColor(color);
        vc.addVertexWith2DPose(pose, x1, y0).setUv(u1, v0).setColor(color);
    }
}