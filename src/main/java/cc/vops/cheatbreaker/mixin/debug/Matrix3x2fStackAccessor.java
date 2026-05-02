package cc.vops.cheatbreaker.mixin.debug;

import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Matrix3x2fStack.class)
public interface Matrix3x2fStackAccessor {
    @Accessor("curr")
    int getCurr();
}