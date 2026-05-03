package cc.vops.cheatbreaker.mixin.freelook;

import cc.vops.cheatbreaker.CheatBreaker;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Unique
    private float previousCameraYRot = -1;

    @Unique
    private float previousCameraXRot = -1;

    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    public void turn(double p_19885_, double p_19886_, CallbackInfo ci) {
        if (CheatBreaker.getInstance().getGlobalSettings().isFreeLooking) {
            ci.cancel();

            if (Minecraft.getInstance().gameRenderer.getMainCamera() instanceof Camera camera) {
                float f = (float)p_19886_ * 0.15F;
                float f1 = (float)p_19885_ * 0.15F;

                if (previousCameraXRot == -1) {
                    previousCameraXRot = camera.xRot();
                    previousCameraYRot = camera.yRot();
                }

                previousCameraXRot = Mth.clamp(previousCameraXRot + f, -90.0F, 90.0F);
                previousCameraYRot += f1;

                CameraAccessor cameraAccessor = (CameraAccessor) camera;
                cameraAccessor.setXRot(previousCameraXRot);
                cameraAccessor.setYRot(previousCameraYRot);
                cameraAccessor.getRotation().rotationYXZ((float) ((float) Math.PI - Math.toRadians(previousCameraYRot)), (float) -Math.toRadians(previousCameraXRot), 0.0F);
                cameraAccessor.FORWARDS().rotate(cameraAccessor.getRotation(), new Vector3f(camera.forwardVector().x(), camera.forwardVector().y(), camera.forwardVector().z()));
                cameraAccessor.UP().rotate(cameraAccessor.getRotation(), new Vector3f(camera.upVector().x(), camera.upVector().y(), camera.upVector().z()));
                cameraAccessor.LEFT().rotate(cameraAccessor.getRotation(), new Vector3f(camera.leftVector().x(), camera.leftVector().y(), camera.leftVector().z()));

                cameraAccessor.setMatrixPropertiesDirty(3);
            }
        } else {
            previousCameraXRot = -1;
            previousCameraYRot = -1;
        }
    }
}
