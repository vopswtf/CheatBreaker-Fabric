package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.CheatBreaker;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.gizmos.GizmoProperties;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityHitboxDebugRenderer.class)
public class HitboxesMixin {
    @WrapOperation(
            method = "showHitboxes",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/gizmos/Gizmos;arrow(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;I)Lnet/minecraft/gizmos/GizmoProperties;"
            )
    )
    private GizmoProperties disableArrows(Vec3 start, Vec3 end, int color, Operation<GizmoProperties> original) {
        if (!CheatBreaker.getInstance().getGlobalSettings().showHitboxArrows.getAsBoolean()) return null;
        return original.call(start, end, color);
    }

    @WrapOperation(
            method = "showHitboxes",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/gizmos/Gizmos;cuboid(Lnet/minecraft/world/phys/AABB;Lnet/minecraft/gizmos/GizmoStyle;)Lnet/minecraft/gizmos/GizmoProperties;",
                    ordinal = 2
            )
    )
    private GizmoProperties disableEyes(AABB box, GizmoStyle style, Operation<GizmoProperties> original) {
        if (!CheatBreaker.getInstance().getGlobalSettings().showHitboxEyeHeight.getAsBoolean()) return null;
        return original.call(box, style);
    }
}
