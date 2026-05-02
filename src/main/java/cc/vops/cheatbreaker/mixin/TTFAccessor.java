package cc.vops.cheatbreaker.mixin;

import com.mojang.blaze3d.font.TrueTypeGlyphProvider;
import org.lwjgl.util.freetype.FT_Face;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TrueTypeGlyphProvider.class)
public interface TTFAccessor {
    @Invoker("validateFontOpen")
    FT_Face invokeValidateFontOpen();
}
