package cc.vops.cheatbreaker.client.module.type.minimap;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.event.type.GuiDrawEvent;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.module.GuiAnchor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class MiniMapModule extends AbstractModule {
    public Minimap minimap;

    public final Setting abbreviateNames;
    public final Setting showCardinalDirections;
    public final Setting enableBiomeBlending;
    public final Setting lockMapToNorth;
    public final Setting showGameWaypoints;
    public final Setting showMapWaypoints;
    public final Setting waypointNameScale;
    public final Setting showArrow;
    public final Setting arrowScale;

    public final WaypointStorage waypointStorage;
    public final WaypointRenderer waypointRenderer;

    public MiniMapModule() {
        super("Minimap");
        this.setDefaultState(false);
//        this.isEditable = false;
        this.minimap = new Minimap(this);
        this.setDefaultAnchor(GuiAnchor.RIGHT_TOP);
        this.setPreviewIcon(CheatBreaker.asset("icons/mods/zans.png"), 42, 42);
        this.waypointStorage = new WaypointStorage();
        this.waypointRenderer = new WaypointRenderer();

        new Setting(this, "label").setValue("General Options");
        {
            this.abbreviateNames = new Setting(this, "Abbreviate Names").setValue(true);
            this.waypointNameScale = new Setting(this, "Waypoint Name Scale").setValue(1.0f).setMinMax(0.5f, 2.0f);
        }

        new Setting(this, "label").setValue("Menu Options");
        {
            this.showCardinalDirections = new Setting(this, "Show Cardinal Directions").setValue(true);
            this.enableBiomeBlending = new Setting(this, "Enable Biome Blending").setValue(true);
            this.lockMapToNorth = new Setting(this, "Lock To North").setValue(false);
            this.showMapWaypoints = new Setting(this, "Show Map Waypoints").setValue(true);
            this.showArrow = new Setting(this, "Show Arrow").setValue(true);
            this.arrowScale = new Setting(this, "Arrow Scale").setValue(1.0f).setMinMax(0.5f, 2.0f);

        }
        new Setting(this, "label").setValue("Game Options");
        {
            this.showGameWaypoints = new Setting(this, "Show Game Waypoints").setValue(true);
        }


        this.addEvent(GuiDrawEvent.class, this::onDraw);

        ClientPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            waypointStorage.load();
        });
    }


    private void onDraw(GuiDrawEvent event) {
        GuiGraphicsExtractor gfx = event.getGraphics();
        gfx.pose().pushMatrix();
        gfx.pose().scale(CheatBreaker.getScaleFactor(), CheatBreaker.getScaleFactor());
        this.scaleAndTranslate(gfx);

        minimap.renderMap(gfx);

        gfx.pose().popMatrix();
    }


}
