package cc.vops.cheatbreaker.client.module.type.minimap;


import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.GlobalSettings;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.mixin.GameRendererAccessor;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.awt.*;
import java.lang.Math;
import java.util.*;
import java.util.List;

public class WaypointRenderer {

    private static final double CUTOFF_DIST = 5;
    private final Minecraft minecraft = Minecraft.getInstance();
    private final Matrix4f view = new Matrix4f();
    private final Vector4f viewProj = new Vector4f();
    private final Set<Waypoint> worldRendererWaypoints = new HashSet<>();
    private static final double BEAM_MIN_DISTANCE = 12.0;

    public WaypointRenderer() {
        LevelRenderEvents.END_MAIN.register(context -> {
            var consumers = context.bufferSource();
            try {
                renderWaypoints(
                        context.poseStack(),
                        consumers
                );
            } catch (Exception e) {
                CheatBreaker.LOGGER.error("Error rendering waypoints", e);
            }
        });
    }

    public void renderWaypoints(PoseStack poseStack, MultiBufferSource.BufferSource consumers) {
        MiniMapModule module =
                CheatBreaker.getInstance().getModuleManager().minmap;

        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null) return;

        CameraRenderState camera =
                mc.gameRenderer
                        .getGameRenderState()
                        .levelRenderState
                        .cameraRenderState;

        Vec3 camPos = camera.pos;

        for (Waypoint waypoint : module.waypointStorage.getWaypoints()) {

            // Server waypoints always render.
            // Other waypoints require the module to be enabled.
            if (!waypoint.isServerWaypoint()) {
                if (!module.isEnabled()) continue;
                if (!module.showGameWaypoints.getAsBoolean()) continue;
            }

            renderWaypoint(
                    waypoint,
                    poseStack,
                    consumers,
                    camPos
            );
        }
    }

    public static String abbreviateText(String text) {
        // "Hello World" -> "HW"
        return Arrays.stream(text.split(" "))
                .filter(s -> !s.isEmpty())
                .map(s -> s.substring(0, 1))
                .reduce("", String::concat);
    }

    private void renderWaypoint(
            Waypoint waypoint,
            PoseStack poseStack,
            MultiBufferSource.BufferSource consumers,
            Vec3 camPos
    ) {
        Minecraft mc = Minecraft.getInstance();

        double x = waypoint.x() - camPos.x;
        double y = waypoint.y() - camPos.y;
        double z = waypoint.z() - camPos.z;

        double distSq = x * x + y * y + z * z;
        double dist = Math.sqrt(distSq);

        float scale =
                computeDistanceScale(dist)
                        * CheatBreaker.getInstance()
                        .getModuleManager()
                        .minmap
                        .waypointNameScale
                        .getAsFloat();

        if (scale <= 0) return;

        String text = waypoint.name();

        if (CheatBreaker.getInstance()
                .getModuleManager()
                .minmap
                .abbreviateNames
                .getAsBoolean()) {
            text = abbreviateText(text);
        }

        int textWidth = mc.font.width(text);
        int textHeight = mc.font.lineHeight;

        float width = textWidth + Waypoint.displayXOffset() * 2;
        float height = textHeight + Waypoint.displayYOffset() * 2;

        poseStack.pushPose();

        poseStack.translate(x, y, z);

        // Billboard toward the camera.
        poseStack.mulPose(
                mc.gameRenderer
                        .getMainCamera()
                        .rotation()
        );

        poseStack.scale(
                scale * 0.04F,
                -scale * 0.04F,
                scale * 0.04F
        );

        Matrix4f matrix = poseStack.last().pose();

        fillRect(
                poseStack,
                consumers,
                -width / 2f,
                -height / 2f,
                0.02f,
                width / 2f,
                height / 2f,
                waypoint.colorInt()
        );

        consumers.endBatch(RenderUtil.arrow());

        mc.font.drawInBatch(
                text,
                -textWidth / 2f + 0.5f,
                -textHeight / 2f + 1f,
                -1,
                false,
                matrix,
                consumers,
                Font.DisplayMode.SEE_THROUGH,
                0,
                0xF000F0
        );

        poseStack.popPose();
    }

    private void fillRect(
            PoseStack stack,
            MultiBufferSource source,
            float x,
            float y,
            float z,
            float x2,
            float y2,
            int color
    ) {
        VertexConsumer buffer = source.getBuffer(RenderUtil.arrow());
        Matrix4f matrix = stack.last().pose();

        buffer.addVertex(matrix, x,  y,  z).setColor(color);
        buffer.addVertex(matrix, x2, y,  z).setColor(color);
        buffer.addVertex(matrix, x2, y2, z).setColor(color);
        buffer.addVertex(matrix, x,  y2, z).setColor(color);
    }

    private void drawFontBatch(String text, float x, float y, Matrix4f matrix, MultiBufferSource bufferSource) {
        minecraft.font.drawInBatch(text, x, y, -1, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 0xF000F0);
    }

    private @Nullable Result projectToScreen(Camera camera, int width, int height, double x, double y, double z, Vector2f orthoOffset) {
        viewProj.set(x, y, z, 1);
        if (orthoOffset != null) {
            var vec = new Matrix4f();
            vec.rotate(camera.rotation().invert(new Quaternionf()));
            vec.translate(orthoOffset.x(), orthoOffset.y(), 0);
            vec.rotate(camera.rotation());
            vec.transform(viewProj);
        }
        view.rotation(camera.rotation()).translate(camera.position().toVector3f().negate());


        CameraRenderState cameraState = Minecraft.getInstance().gameRenderer.getGameRenderState().levelRenderState.cameraRenderState;
        Matrix4f projection = new Matrix4f(cameraState.projectionMatrix);
        projection.mul(view);
        viewProj.mul(projection);

        if (orthoOffset == null) {
            viewProj.w = Math.max(Math.abs(viewProj.x()), Math.max(Math.abs(viewProj.y()), viewProj.w()));
        }

        if (viewProj.w() <= 0) {
            return null;
        }
        viewProj.div(viewProj.w());

        float projX = viewProj.x();
        float projY = viewProj.y();

        //float x = (graphics.guiWidth()/2f) + ((graphics.guiWidth() - width) * (viewProj.x() / 2f));
        float resultX = 0.5f * (minecraft.getWindow().getGuiScaledWidth() * (projX + 1) - width * projX);
        //float y = graphics.guiHeight() - (graphics.guiHeight()/2f + (graphics.guiHeight()-height) * (viewProj.y() / 2f));
        float resultY = minecraft.getWindow().getGuiScaledHeight() * (0.5f - projY / 2) + (height * projY) / 2f;
        return new Result(resultX, resultY);
    }

    private float computeDistanceScale(double distance) {
        if (distance > 500) return 0F;
        float base = 1F;
        float growth = 0.045F;

        return (float)(base + distance * growth);
    }

    private record Result(float x, float y) {
    }
}