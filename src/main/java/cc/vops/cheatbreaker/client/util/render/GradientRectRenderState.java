package cc.vops.cheatbreaker.client.util.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.NonNull;

public class GradientRectRenderState extends RectRenderState {
    protected final int topColor;
    protected final int bottomColor;
    protected final boolean horizontal;

    public GradientRectRenderState(
            TextureSetup textureSetup,
            Matrix3x2f pose,
            @Nullable ScreenRectangle scissorArea,
            float x0, float y0, float x1, float y1,
            int topColor,
            int bottomColor,
            boolean horizontal
    ) {
        super(
            new Matrix3x2f(pose), textureSetup, scissorArea,
            x0, y0, x1, y1, 0
        );

        this.topColor = topColor;
        this.bottomColor = bottomColor;
        this.horizontal = horizontal;
    }

    @Override
    public void buildVertices(@NonNull VertexConsumer vc) {
        if (horizontal) {
            vc.addVertexWith2DPose(pose, x0, y0).setColor(topColor);
            vc.addVertexWith2DPose(pose, x0, y1).setColor(topColor);
            vc.addVertexWith2DPose(pose, x1, y1).setColor(bottomColor);
            vc.addVertexWith2DPose(pose, x1, y0).setColor(bottomColor);
        } else {
            vc.addVertexWith2DPose(pose, x0, y0).setColor(topColor);
            vc.addVertexWith2DPose(pose, x0, y1).setColor(bottomColor);
            vc.addVertexWith2DPose(pose, x1, y1).setColor(bottomColor);
            vc.addVertexWith2DPose(pose, x1, y0).setColor(topColor);
        }
    }
}
