package cc.vops.cheatbreaker.client.util.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public abstract class CBRenderState implements GuiElementRenderState {
    protected final TextureSetup textureSetup;
    protected final Matrix3x2f pose;
    protected final @Nullable ScreenRectangle scissorArea;

    @Override
    public void buildVertices(@NonNull VertexConsumer vertexConsumer) {

    }

    public abstract ScreenRectangle getBounds();

    @Override
    public @NonNull RenderPipeline pipeline() {
        return textureSetup.equals(TextureSetup.noTexture()) ? RenderPipelines.GUI : RenderPipelines.GUI_TEXTURED;
    }

    @Override
    public @NonNull TextureSetup textureSetup() {
        return textureSetup;
    }

    @Override
    public @Nullable ScreenRectangle scissorArea() {
        return scissorArea;
    }

    @Override
    public @Nullable ScreenRectangle bounds() {
        return getBounds();
    }
}
