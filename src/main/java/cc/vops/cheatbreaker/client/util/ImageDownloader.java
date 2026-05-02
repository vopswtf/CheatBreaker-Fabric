package cc.vops.cheatbreaker.client.util;

import cc.vops.cheatbreaker.CheatBreaker;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
public class ImageDownloader {

    private static final Map<String, Identifier> locations = new HashMap<>();
    private static final Map<String, DynamicTexture> textures = new HashMap<>();

    public static Identifier getImage(String id, String url) {
        Identifier loc = locations.computeIfAbsent(id,
                k -> CheatBreaker.asset("downloaded-images/" + id)
        );

        if (!textures.containsKey(id)) {
            // Create tiny placeholder just to register something
            DynamicTexture placeholder = new DynamicTexture(() -> id, new NativeImage(1, 1, false));
            textures.put(id, placeholder);
            Minecraft.getInstance().getTextureManager().register(loc, placeholder);

            new Thread(new DownloadTask(id, url, loc)).start();
        }

        return loc;
    }

    private static class DownloadTask implements Runnable {
        private final String id;
        private final String url;
        private final Identifier location;

        DownloadTask(String id, String url, Identifier loc) {
            this.id = id;
            this.url = url;
            this.location = loc;
        }

        @Override
        public void run() {
            try (InputStream in = URI.create(url).toURL().openStream()) {

                // decode ANY image type (PNG/JPG/GIF/BMP)
                NativeImage img = readAnyImage(in);

                // Must register NEW DynamicTexture with correct dimensions
                Minecraft.getInstance().execute(() -> {
                    DynamicTexture newTex = new DynamicTexture(() -> id, img);
                    textures.put(id, newTex);

                    // re-register with the SAME Identifier
                    Minecraft.getInstance().getTextureManager().register(location, newTex);
                });

            } catch (Exception e) {
                CheatBreaker.LOGGER.info("Image download failed (" + id + "): " + e);
            }
        }
    }

    // BufferedImage → NativeImage converter
    private static NativeImage readAnyImage(InputStream in) throws IOException {
        BufferedImage src = ImageIO.read(in);
        if (src == null) {
            throw new IOException("Unsupported image");
        }

        NativeImage out = new NativeImage(src.getWidth(), src.getHeight(), false);

        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                out.setPixel(x, y, src.getRGB(x, y));
            }
        }

        return out;
    }
}
