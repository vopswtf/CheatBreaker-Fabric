package cc.vops.cheatbreaker.client.util.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.NonNull;

public class OutlineRectRenderState extends RectRenderState {
    protected final float thickness;

    public OutlineRectRenderState(
            Matrix3x2f pose,
            float x0, float y0, float x1, float y1,
            int color,
            float thickness,
            @Nullable ScreenRectangle scissorArea
    ) {
        super(
                new Matrix3x2f(pose), TextureSetup.noTexture(), scissorArea,
                x0, y0, x1, y1, color
        );
        this.thickness = thickness;
    }

    @Override
    public void buildVertices(@NonNull VertexConsumer vc) {
        // top line
        vc.addVertexWith2DPose(pose, x0, y0).setColor(color);
        vc.addVertexWith2DPose(pose, x0, y0 + thickness).setColor(color);
        vc.addVertexWith2DPose(pose, x1, y0 + thickness).setColor(color);
        vc.addVertexWith2DPose(pose, x1, y0).setColor(color);

        // bottom line
        vc.addVertexWith2DPose(pose, x0, y1 - thickness).setColor(color);
        vc.addVertexWith2DPose(pose, x0, y1).setColor(color);
        vc.addVertexWith2DPose(pose, x1, y1).setColor(color);
        vc.addVertexWith2DPose(pose, x1, y1 - thickness).setColor(color);

        // left line
        vc.addVertexWith2DPose(pose, x0, y0 + thickness).setColor(color);
        vc.addVertexWith2DPose(pose, x0, y1 - thickness).setColor(color);
        vc.addVertexWith2DPose(pose, x0 + thickness, y1 - thickness).setColor(color);
        vc.addVertexWith2DPose(pose, x0 + thickness, y0 + thickness).setColor(color);

        // right line
        vc.addVertexWith2DPose(pose, x1 - thickness, y0 + thickness).setColor(color);
        vc.addVertexWith2DPose(pose, x1 - thickness, y1 - thickness).setColor(color);
        vc.addVertexWith2DPose(pose, x1, y1 - thickness).setColor(color);
        vc.addVertexWith2DPose(pose, x1, y0 + thickness).setColor(color);
    }
}