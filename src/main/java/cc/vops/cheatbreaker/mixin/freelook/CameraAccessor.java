package cc.vops.cheatbreaker.mixin.freelook;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraAccessor {
    @Accessor("yRot")
    void setYRot(float paramFloat);

    @Accessor("xRot")
    void setXRot(float paramFloat);

    @Accessor("position")
    void setCameraPosition(Vec3 position);

    @Accessor("rotation")
    Quaternionf getRotation();

    @Accessor("FORWARDS")
    Vector3f FORWARDS();

    @Accessor("UP")
    Vector3f UP();

    @Accessor("LEFT")
    Vector3f LEFT();

    @Accessor
    void setMatrixPropertiesDirty(int dirty);
}