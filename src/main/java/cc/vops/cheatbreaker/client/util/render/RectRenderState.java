package cc.vops.cheatbreaker.client.util.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.NonNull;

public class RectRenderState extends CBRenderState {
    protected final float x0, y0, x1, y1;
    protected final int color;

    public RectRenderState(
            Matrix3x2f pose, TextureSetup textureSetup, @Nullable ScreenRectangle scissorArea,
            float x0, float y0, float x1, float y1, int color
    ) {
        super(
                textureSetup,
                new Matrix3x2f(pose),
                scissorArea
        );
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.color = color;
    }

    @Override
    public void buildVertices(@NonNull VertexConsumer vc) {
        vc.addVertexWith2DPose(pose, x0, y0).setColor(color);
        vc.addVertexWith2DPose(pose, x0, y1).setColor(color);
        vc.addVertexWith2DPose(pose, x1, y1).setColor(color);
        vc.addVertexWith2DPose(pose, x1, y0).setColor(color);
    }

    @Override
    public ScreenRectangle getBounds() {
        ScreenRectangle rect = new ScreenRectangle(
                Mth.floor(x0),
                Mth.floor(y0),
                Mth.ceil(x1) - Mth.floor(x0),
                Mth.ceil(y1) - Mth.floor(y0)
        ).transformMaxBounds(pose);

        return scissorArea != null ? scissorArea.intersection(rect) : rect;
    }
}
