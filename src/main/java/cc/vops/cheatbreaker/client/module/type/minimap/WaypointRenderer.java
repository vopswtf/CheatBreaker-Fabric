package cc.vops.cheatbreaker.client.module.type.minimap;


import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.mixin.GameRendererAccessor;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
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

    public void renderNames(DeltaTracker deltaTracker) {
        // TODO: update this to 26.1

//        MiniMapModule module = CheatBreaker.getInstance().getModuleManager().minmap;
//        if (!module.isEnabled()) return;
//        if (!module.showGameWaypoints.getAsBoolean()) return;
//        if (minecraft.level == null) return;
//        if (module.waypointStorage.getWaypoints().isEmpty()) return;
//
//        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
//        var stack = new PoseStack();
//        var cam = minecraft.gameRenderer.getMainCamera();
//
//        stack.pushPose();
//        stack.mulPose(cam.rotation().invert());
//        var camPos = minecraft.gameRenderer.getMainCamera().position();
//        float fov = (float) ((GameRendererAccessor) minecraft.gameRenderer).invokeGetFov(cam, deltaTracker.getGameTimeDeltaPartialTick(true), true);
//
//        GpuBufferSlice slice = ((GameRendererAccessor) minecraft.gameRenderer).getLevelProjectionMatrixBuffer().getBuffer(
//                minecraft.gameRenderer.getProjectionMatrix(fov)
//        );
//
//        RenderSystem.setProjectionMatrix(slice, ProjectionType.PERSPECTIVE);
//
//        worldRendererWaypoints.clear();
//
//        for (Waypoint waypoint : module.waypointStorage.getWaypoints()) {
//            renderWaypoint(waypoint, stack, camPos, cam, bufferSource, fov);
//        }
//
//        stack.popPose();
//        bufferSource.endLastBatch();
    }

    public static String abbreviateText(String text) {
        // "Hello World" -> "HW"
        return Arrays.stream(text.split(" "))
                .filter(s -> !s.isEmpty())
                .map(s -> s.substring(0, 1))
                .reduce("", String::concat);
    }

    private void renderWaypoint(Waypoint waypoint, PoseStack stack, Vec3 camPos, Camera cam, MultiBufferSource.BufferSource bufferSource, float fov) {
        String text = waypoint.name();

        if (CheatBreaker.getInstance().getModuleManager().minmap.abbreviateNames.getAsBoolean()) {
            text = abbreviateText(text);
        }

        int textWidth = minecraft.font.width(text);
        int width = textWidth + Waypoint.displayXOffset() * 2;
        int textHeight = minecraft.font.lineHeight;
        int height = textHeight + Waypoint.displayYOffset() * 2;
        var displayStart = projectToScreen(cam, fov, width, height, waypoint.x(), waypoint.y(), waypoint.z(), new Vector2f(-(width / 2f * 0.04f), (height / 2f * 0.04f)));
        if (displayStart == null) return;
        var displayEnd = projectToScreen(cam, fov, width, height, waypoint.x(), waypoint.y(), waypoint.z(), new Vector2f(width / 2f * 0.04f, -(height / 2f * 0.04f)));
        if (displayEnd == null) return;
        float projWidth = Math.abs(displayEnd.x() - displayStart.x());
        float projHeight = Math.abs(displayEnd.y() - displayStart.y());
        if (projWidth < 2 && projHeight < 2) {
            return;
        }
        worldRendererWaypoints.add(waypoint);

        stack.pushPose();

        // Move to waypoint relative to camera
        stack.translate(
                waypoint.x() - camPos.x(),
                waypoint.y() - camPos.y(),
                waypoint.z() - camPos.z()
        );


        double dist = waypoint.distTo(camPos.x(), camPos.y(), camPos.z());


        stack.mulPose(cam.rotation().invert(new Quaternionf()));
        float scale = computeDistanceScale(dist) * CheatBreaker.getInstance().getModuleManager().minmap.waypointNameScale.getAsFloat();
        stack.scale(scale * 0.04F, -scale * 0.04F, scale * 0.04F);

        drawFontBatch(text, -textWidth / 2f, -textHeight / 2f, stack.last().pose(), bufferSource);
        fillRect(stack, bufferSource, -width / 2f, -height / 2f, -0.1f, width / 2f, height / 2f, waypoint.colorInt());

        stack.popPose();
    }

    private void fillRect(PoseStack stack, MultiBufferSource.BufferSource source, float x, float y, float z, float x2, float y2, int color) {
        var buf = source.getBuffer(RenderTypes.textBackground());
        var matrix = stack.last().pose();
        buf.addVertex(matrix, x, y, z).setColor(color).setUv2(0, 0);
        buf.addVertex(matrix, x, y2, z).setColor(color).setUv2(0, 0);
        buf.addVertex(matrix, x2, y2, z).setColor(color).setUv2(0, 0);
        buf.addVertex(matrix, x2, y, z).setColor(color).setUv2(0, 0);
    }

    private void drawFontBatch(String text, float x, float y, Matrix4f matrix, MultiBufferSource bufferSource) {
        minecraft.font.drawInBatch(text, x, y, -1, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 0xF000F0);
    }

    private @Nullable Result projectToScreen(Camera camera, double fov, int width, int height, double x, double y, double z, Vector2f orthoOffset) {
//        viewProj.set(x, y, z, 1);
//        if (orthoOffset != null) {
//            var vec = new Matrix4f();
//            vec.rotate(camera.rotation().invert(new Quaternionf()));
//            vec.translate(orthoOffset.x(), orthoOffset.y(), 0);
//            vec.rotate(camera.rotation());
//            vec.transform(viewProj);
//        }
//        view.rotation(camera.rotation()).translate(camera.position().toVector3f().negate());
//
//        Matrix4f projection = minecraft.gameRenderer.getProjectionMatrix((float) fov);
//        projection.mul(view);
//        viewProj.mul(projection);
//
//        if (orthoOffset == null) {
//            viewProj.w = Math.max(Math.abs(viewProj.x()), Math.max(Math.abs(viewProj.y()), viewProj.w()));
//        }
//
//        if (viewProj.w() <= 0) {
//            return null;
//        }
//        viewProj.div(viewProj.w());
//
//        float projX = viewProj.x();
//        float projY = viewProj.y();
//
//        //float x = (graphics.guiWidth()/2f) + ((graphics.guiWidth() - width) * (viewProj.x() / 2f));
//        float resultX = 0.5f * (minecraft.getWindow().getGuiScaledWidth() * (projX + 1) - width * projX);
//        //float y = graphics.guiHeight() - (graphics.guiHeight()/2f + (graphics.guiHeight()-height) * (viewProj.y() / 2f));
//        float resultY = minecraft.getWindow().getGuiScaledHeight() * (0.5f - projY / 2) + (height * projY) / 2f;
//        return new Result(resultX, resultY);
        return null;
    }

    private float computeDistanceScale(double distance) {
        if (distance > 500) {
            return 0F;
        }

        float base = 1F;   // your original scale
        float growth = 0.045F; // how fast it gets bigger when far

        return (float)(base + distance * growth);
    }

    private record Result(float x, float y) {
    }
}