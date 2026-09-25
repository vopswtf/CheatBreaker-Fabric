package cc.vops.cheatbreaker.client.util;

import cc.vops.cheatbreaker.CheatBreaker;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Base64;
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
            try (InputStream in = inputStream(url)) {
                NativeImage img = readAnyImage(in);

                Minecraft.getInstance().execute(() -> {
                    DynamicTexture newTex = new DynamicTexture(() -> id, img);
                    textures.put(id, newTex);
                    Minecraft.getInstance().getTextureManager().register(location, newTex);
                });

            } catch (Exception e) {
                CheatBreaker.LOGGER.info("Image download failed (" + id + "): " + e);
            }
        }
    }

    private static InputStream inputStream(String source) throws IOException {
        if (source == null || source.isBlank()) throw new IOException("Empty image source");

        if (source.startsWith("http://") || source.startsWith("https://")) {
            return URI.create(source).toURL().openStream();
        }

        // assuming b64 here
        if (source.startsWith("data:image/")) {
            int comma = source.indexOf(',');
            if (comma == -1) throw new IOException("Invalid data string");
            source = source.substring(comma + 1);
        }

        try {
            byte[] bytes = Base64.getDecoder().decode(source);
            return new ByteArrayInputStream(bytes);
        } catch (IllegalArgumentException e) {
            throw new IOException("not real wtf", e);
        }
    }

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
