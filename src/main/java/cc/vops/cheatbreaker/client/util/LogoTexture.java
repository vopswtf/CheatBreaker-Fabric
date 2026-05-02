package cc.vops.cheatbreaker.client.util;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.MipmapStrategy;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class LogoTexture extends ReloadableTexture {
    public LogoTexture() {
        super(RenderUtil.LOGO);
    }

    @Override
    public @NotNull TextureContents loadContents(ResourceManager p_376459_) throws IOException {
        byte[] decoded = Base64.getDecoder().decode(RenderUtil.LOGO_BASE64);

        TextureContents texturecontents;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(decoded)) {
            texturecontents = new TextureContents(NativeImage.read(bais), new TextureMetadataSection(true, true, MipmapStrategy.MEAN, 0.0F));
        }

        return texturecontents;
    }
}