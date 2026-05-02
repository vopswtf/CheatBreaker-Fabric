package cc.vops.cheatbreaker.client.util.font;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.overlay.SocialOverlayScreen;
import cc.vops.cheatbreaker.mixin.FontResourceManagerAccessor;
import cc.vops.cheatbreaker.mixin.MinecraftAccessor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.EffectGlyph;
import net.minecraft.network.chat.FontDescription;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public final class Fonts {
    public static CBFontRenderer robotoRegular13 = createFont("roboto-regular", 13, -2);
    public static CBFontRenderer robotoRegular24 = createFont("roboto-regular", 24, -2);
    public static CBFontRenderer robotoBold14 = createFont("roboto-bold", 14, -2);
    public static CBFontRenderer playRegular18 = createFont("play-regular", 18, 0);
    public static CBFontRenderer playRegular16 = createFont("play-regular", 16, 0);
    public static CBFontRenderer playRegular14 = createFont("play-regular", 14, -2);
    public static CBFontRenderer playRegular12 = createFont("play-regular", 12, -2);
    public static CBFontRenderer playBold22 = createFont("play-bold", 22, -2);
    public static CBFontRenderer playBold18 = createFont("play-bold", 18, -4);
    public static CBFontRenderer ubuntuMedium16 = createFont("ubuntu-m", 16, -2);

    @Getter
    private static HashMap<String, CBFontRenderer> fonts;

    private static CBFontRenderer createFont(String assetName, float size, float yOffset) {
        Identifier identifier = CheatBreaker.asset("font/ttf/" + assetName + ".ttf");
        return createFont(identifier, size, yOffset);
    }

    private static CBFontRenderer createFont(Identifier identifier, float size, float yOffset) {
        if (fonts == null) fonts = new HashMap<>();
        CBFontRenderer fontRenderer = new CBFontRenderer(identifier, size, yOffset);
        fonts.put(fontRenderer.getName(), fontRenderer);
        return fontRenderer;
    }


    public static void reloadFonts() {
        for (Map.Entry<String, CBFontRenderer> entry : fonts.entrySet()) {
            entry.getValue().reload();
        }

        SocialOverlayScreen.resetInstance();
    }
}
