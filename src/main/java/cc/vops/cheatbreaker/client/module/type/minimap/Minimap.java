package cc.vops.cheatbreaker.client.module.type.minimap;


import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.util.PlayerHeads;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import com.mojang.blaze3d.platform.NativeImage;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import org.joml.Vector2f;

@SuppressWarnings("DataFlowIssue")
public class Minimap {
    private final MiniMapModule module;

    public Minimap(MiniMapModule module) {
        this.module = module;
    }

    @Getter
    @Setter
    protected float x,y;
    private static final int radius = 64;
    private static final int size = Mth.floor(radius * 2);

    private static final Identifier texLocation = Identifier.withDefaultNamespace("generated_minimap_texture");
    private NativeImage pixels;
    public long updateDuration = -1;
    private DynamicTexture tex;
    private int mapCenterX, mapCenterZ;

    private final Minecraft minecraft = Minecraft.getInstance();

    public void setup() {
        int viewDistance = 128;
        minecraft.getTextureManager().register(texLocation, tex = new DynamicTexture(() -> "generated_minimap_texture", viewDistance, viewDistance, false));
        pixels = tex.getPixels();
        pixels.fillRect(0, 0, pixels.getWidth(), pixels.getHeight(), net.minecraft.util.ARGB.opaque(0));
    }

    public void renderMap(GuiGraphicsExtractor gfx) {
        x = 1f;
        y = 1f;

        if (pixels == null || tex == null) {
            setup();
            return;
        }
        gfx.pose().pushMatrix();
        int fx = (int) (x);
        int fy = (int) (y);
        int fsize = size;

        gfx.pose().transformPosition(fx, fy, new Vector2f());
        gfx.pose().transformPosition(fx + fsize, fy + fsize, new Vector2f());
        gfx.enableScissor(fx, fy, fsize, fsize);
        gfx.pose().pushMatrix();
        gfx.pose().translate(fx, fy);
        gfx.pose().translate((float) radius, (float) radius);
        if (!module.lockMapToNorth.getAsBoolean()) {
            gfx.pose().rotate((float) -(((minecraft.player.getVisualRotationYInDegrees() + 180) / 180) * Math.PI));
        }
        gfx.pose().scale((float) Math.sqrt(2), (float) Math.sqrt(2));
        gfx.pose().translate(-(float) radius, -(float) radius);
        float offX = -(float) (minecraft.player.getX() - mapCenterX);
        float offZ = -(float) (minecraft.player.getZ() - mapCenterZ);
        gfx.pose().translate((float) (offX), (float) (offZ));
        gfx.blit(RenderPipelines.GUI_TEXTURED, texLocation, 0, 0, 0, 0, fsize, fsize, fsize, fsize);
        gfx.pose().popMatrix();

        module.setDimensions(fsize, fsize);

        gfx.disableScissor();

        if (module.showMapWaypoints.getAsBoolean()) {
            renderMapWaypoints(gfx);
        }
        if (module.showCardinalDirections.getAsBoolean()) {
            Vector2f pos = new Vector2f();
            gfx.pose().pushMatrix();
            var directions = new String[]{"N", "W", "E", "S"};
            for (int i : new int[]{-2, 1, 2, -1}) {
                var label = directions[i < 0 ? i + 2 : i + 1];
                var labelWidth = minecraft.font.width(label);
                var labelHeight = minecraft.font.lineHeight;
                gfx.pose().pushMatrix();
                gfx.pose().identity();
                gfx.pose().translate(fx + (float) radius, fy + (float) radius);
                if (!module.lockMapToNorth.getAsBoolean()) {
                    gfx.pose().rotate((float) -(((minecraft.player.getVisualRotationYInDegrees() + 180) / 180) * Math.PI));
                }
                gfx.pose().translate((float) ((i % 2) * fsize), (float) (((int) (i / 2f)) * fsize));
                pos.zero();
                gfx.pose().transformPosition(pos);
                gfx.pose().popMatrix();
                pos.x = Math.clamp(pos.x, fx, fx + fsize);
                pos.y = Math.clamp(pos.y, fy, fy + fsize);
                gfx.pose().pushMatrix();
                gfx.pose().translate(pos);
                gfx.pose().scale(0.5f, 0.5f);
                gfx.fill(-(labelWidth / 2 + 2), -(labelHeight / 2 + 2), labelWidth / 2 + 2, labelHeight / 2 + 2, 0x77888888);
                gfx.text(minecraft.font, label, -labelWidth / 2, -labelHeight / 2, -1);
                gfx.pose().popMatrix();
            }
            gfx.pose().popMatrix();
        }

        gfx.pose().pushMatrix();
        gfx.pose().translate(fx + (float) radius, fy + (float) radius);
        if (module.lockMapToNorth.getAsBoolean()) {
            gfx.pose().rotate((float) (((minecraft.player.getVisualRotationYInDegrees() + 180) / 180) * Math.PI));
        }

        if (module.showArrow.getAsBoolean()) {
            gfx.pose().scale(0.5f * module.arrowScale.getAsFloat(), 0.5f * module.arrowScale.getAsFloat());
            int arrowSize = 8;
            gfx.pose().translate(-arrowSize, -arrowSize);
            Identifier playerHead = PlayerHeads.getLocalPlayerHead();
            gfx.blit(playerHead, 0, 0, arrowSize, arrowSize, 1f, 1f, 1f, 1f);
            RenderUtil.drawIcon(gfx, playerHead, arrowSize, 0, 0);
        }
        gfx.pose().popMatrix();

        gfx.pose().popMatrix();
    }

    private void renderMapWaypoints(GuiGraphicsExtractor graphics) {
        if (!module.showMapWaypoints.getAsBoolean()) return;
        graphics.pose().pushMatrix();
        Vector2f pos = new Vector2f();
        for (Waypoint waypoint : CheatBreaker.getInstance().getModuleManager().minmap.waypointStorage.getWaypoints()) {
            graphics.pose().pushMatrix();
            float posX = (float) (waypoint.x() - minecraft.player.getX());
            float posY = (float) (waypoint.z() - minecraft.player.getZ());

            {
                pos.zero();
                graphics.pose().pushMatrix();
                graphics.pose().identity();
                graphics.pose().translate(x, y);
                graphics.pose().translate(radius, radius);
                graphics.pose().scale((float) Math.sqrt(2), (float) Math.sqrt(2));
                if (!module.lockMapToNorth.getAsBoolean()) {
                    graphics.pose().rotate((float) -Math.toRadians(minecraft.player.getVisualRotationYInDegrees() + 180));
                }
                graphics.pose().translate(posX, posY);
                graphics.pose().transformPosition(pos);
                graphics.pose().popMatrix();
            }

            {
                pos.x = Mth.clamp(pos.x, x, x + size);
                pos.y = Mth.clamp(pos.y, y, y + size);
                graphics.pose().translate(pos);
            }

            String text = waypoint.name();
            if (CheatBreaker.getInstance().getModuleManager().minmap.abbreviateNames.getAsBoolean()) {
                text = WaypointRenderer.abbreviateText(text);
            }

            float scale = CheatBreaker.getInstance().getModuleManager().minmap.waypointNameScale.getAsFloat();
            graphics.pose().scale(scale, scale);

            int textWidth = minecraft.font.width(text);
            int textHeight = minecraft.font.lineHeight;
            graphics.fill(-(textWidth / 2) - Waypoint.displayXOffset(), -(textHeight / 2) - Waypoint.displayYOffset(), (textWidth / 2) + Waypoint.displayXOffset(), (textHeight / 2) + Waypoint.displayYOffset(), waypoint.colorInt());
            graphics.text(minecraft.font, text, -(textWidth / 2), -textHeight / 2, -1, false);
            graphics.pose().popMatrix();
        }
        graphics.pose().popMatrix();
    }

    public void updateMapView() {
        if (tex == null) return;
        if (!module.isEnabled()) {
            updateDuration = -1;
            return;
        }
        long start = Util.getNanos();
        int centerX = minecraft.player.getBlockX();
        int centerZ = minecraft.player.getBlockZ();
        mapCenterX = centerX;
        mapCenterZ = centerZ;
        int size = pixels.getWidth();
        int texHalfWidth = size / 2;

        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos mutableBlockPos2 = new BlockPos.MutableBlockPos();

        var level = minecraft.level;
        var centerChunk = level.getChunk(SectionPos.blockToSectionCoord(centerX), SectionPos.blockToSectionCoord(centerZ));
        var surface = centerChunk.getHeight(Heightmap.Types.WORLD_SURFACE, centerX, centerZ);
        mutableBlockPos.set(centerX, surface, centerZ);
        int solidBlocksAbovePlayer = 0;
        boolean atSurface = false;
        if (level.dimensionType().hasCeiling()) {
            atSurface = minecraft.player.getBlockY() >= level.dimensionType().logicalHeight();
        } else if (surface + 1 <= minecraft.player.getBlockY()) {
            atSurface = true;
        } else {
            while (solidBlocksAbovePlayer <= 3 && surface > minecraft.player.getBlockY() && surface > level.getMinY()) {
                BlockState state = centerChunk.getBlockState(mutableBlockPos);
                mutableBlockPos.setY(surface--);
                if (!(state.propagatesSkylightDown() || !state.canOcclude() || !state.isViewBlocking(level, mutableBlockPos))) {
                    solidBlocksAbovePlayer++;
                }
            }
            if (solidBlocksAbovePlayer <= 2) {
                atSurface = true;
            }
        }

        boolean updated = false;
        for (int x = 0; x < size; x++) {
            double d = 0.0;
            for (int z = -1; z < size; z++) {
                int chunkX = (centerX + x - texHalfWidth);
                int chunkZ = (centerZ + z - texHalfWidth);
                ChunkAccess levelChunk = level.getChunk(SectionPos.blockToSectionCoord(chunkX), SectionPos.blockToSectionCoord(chunkZ), ChunkStatus.FULL, false);
                if (levelChunk != null) {
                    int fluidDepth = 0;
                    double e = 0.0;
                    mutableBlockPos.set(chunkX, 0, chunkZ);
                    int y = levelChunk.getHeight(Heightmap.Types.WORLD_SURFACE, mutableBlockPos.getX(), mutableBlockPos.getZ()) + 1;
                    if (!atSurface) {
                        y = Math.min(y, minecraft.player.getBlockY());
                    }
                    BlockState blockState;
                    if (y <= level.getMinY()) {
                        blockState = Blocks.AIR.defaultBlockState();
                    } else {
                        do {
                            mutableBlockPos.setY(--y);
                            blockState = levelChunk.getBlockState(mutableBlockPos);
                        } while (blockState.getMapColor(level, mutableBlockPos) == MapColor.NONE && y > level.getMinY());

                        if (y > level.getMinY() && !blockState.getFluidState().isEmpty()) {
                            int highestFullBlockY = y - 1;
                            mutableBlockPos2.set(mutableBlockPos);

                            BlockState blockState2;
                            do {
                                mutableBlockPos2.setY(highestFullBlockY--);
                                blockState2 = levelChunk.getBlockState(mutableBlockPos2);
                                fluidDepth++;
                            } while (highestFullBlockY > level.getMinY() && !blockState2.getFluidState().isEmpty());

                            FluidState fluidState = blockState.getFluidState();
                            blockState = !fluidState.isEmpty() && !blockState.isFaceSturdy(level, mutableBlockPos, Direction.UP) ? fluidState.createLegacyBlock() : blockState;
                        }
                    }

                    e += y;
                    var mapColor = blockState.getMapColor(level, mutableBlockPos);

                    int color;
                    if (mapColor == MapColor.WATER) {
                        var floorBlock = levelChunk.getBlockState(mutableBlockPos2);
                        var floorColor = floorBlock.getMapColor(level, mutableBlockPos2).col;
                        int biomeColor = module.enableBiomeBlending.getAsBoolean() ? BiomeColors.getAverageWaterColor(level, mutableBlockPos) : mapColor.col;
                        float shade = 0.9F;
                        int waterColor = biomeColor;
                        waterColor = ARGB.colorFromFloat(1f, ARGB.redFloat(waterColor) * shade, ARGB.greenFloat(waterColor) * shade, ARGB.blueFloat(waterColor) * shade);
                        waterColor = ARGB.average(waterColor, ARGB.scaleRGB(floorColor, 1f - fluidDepth / 15f));
                        color = waterColor;
                    } else {
                        double f = (e - d) * 4.0 / (1 + 4) + ((x + z & 1) - 0.5) * 0.4;
                        MapColor.Brightness brightness;
                        if (f > 0.6) {
                            brightness = MapColor.Brightness.HIGH;
                        } else if (f < -0.6) {
                            brightness = MapColor.Brightness.LOW;
                        } else {
                            brightness = MapColor.Brightness.NORMAL;
                        }
                        color = mapColor.calculateARGBColor(brightness);
                    }

                    d = e;


                    if (z >= 0 && Integer.rotateRight(pixels.getPixel(x, z), 4) != color) {
                        pixels.setPixel(x, z, ARGB.opaque(color));
                        updated = true;
                    }

                } else {
                    if (z >= 0 && Integer.rotateRight(pixels.getPixel(x, z), 4) != 0) {
                        pixels.setPixel(x, z, ARGB.opaque(0));
                        updated = true;
                    }
                }
            }
        }
        if (updated) {
            tex.upload();
        }
        updateDuration = Util.getNanos() - start;
    }
}