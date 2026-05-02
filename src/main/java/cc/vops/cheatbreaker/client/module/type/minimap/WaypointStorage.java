package cc.vops.cheatbreaker.client.module.type.minimap;

import cc.vops.cheatbreaker.CheatBreaker;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.Getter;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class WaypointStorage {

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Waypoint.class, new WaypointTypeAdapter()).create();
    private static final TypeToken<ArrayList<Waypoint>> WAYPOINT_LIST_TYPE = new TypeToken<>() {
    };

    @Getter
    private final ArrayList<Waypoint> waypoints = new ArrayList<>();

    public int getWaypointCount() {
        return waypoints.size();
    }

    public Stream<Waypoint> getCurrentlyAvailableWaypoints(String dimension) {
        if (dimension == null) return waypoints.stream();
        return waypoints.stream().filter(w -> w.dimension().equals(dimension));
    }

    private Path getCurrentPath() {
        if (Minecraft.getInstance().getCurrentServer() != null) {
            return Minecraft.getInstance().gameDirectory.toPath()
                    .resolve("cheatbreaker")
                    .resolve("waypoints")
                    .resolve("multiplayer")
                    .resolve(Minecraft.getInstance().getCurrentServer().ip.replace(":", "_") + ".json");
        }

        if (Minecraft.getInstance().getSingleplayerServer() == null) return null;

        return Minecraft.getInstance().gameDirectory.toPath()
                .resolve("cheatbreaker")
                .resolve("waypoints")
                .resolve("singleplayer")
                .resolve(Minecraft.getInstance().getSingleplayerServer().getWorldData().getLevelName().replace(" ", "_") + ".json");
    }

    private Path currentSave = null;

    public void load() {
        currentSave = getCurrentPath();
        CheatBreaker.LOGGER.info("Loading waypoints from: " + currentSave);
        waypoints.clear();
        if (currentSave == null) return;
        if (Files.exists(currentSave)) {
            try (var reader = Files.newBufferedReader(currentSave)) {
                List<Waypoint> loaded = GSON.fromJson((Reader) reader, WAYPOINT_LIST_TYPE.getType());
                waypoints.addAll(loaded);
                CheatBreaker.LOGGER.info("Loaded {} waypoints!", waypoints.size());
            } catch (IOException e) {
                CheatBreaker.LOGGER.warn("Failed to load waypoints!", e);
            }
        } else {
            save();
        }

        // testing
        if (waypoints.isEmpty()) {
//            waypoints.add(new Waypoint("overworld", 100, 94, 100, new Color(255, 0, 0, 128), "Sample Point", false, false, false));
        }
    }

    public void save() {
        try {
            Files.createDirectories(currentSave.getParent());
            var writer = Files.newBufferedWriter(currentSave);
            List<Waypoint> clientOnlyWaypoints = waypoints.stream().filter(wp -> !wp.isServerWaypoint()).toList();
            GSON.toJson(clientOnlyWaypoints, writer);
            writer.close();
        } catch (IOException e) {
            CheatBreaker.LOGGER.warn("Failed to save waypoints!", e);
        }
    }

    public void create(Waypoint waypoint) {
        waypoints.add(waypoint);
        save();
    }

    public void replace(Waypoint oldPoint, Waypoint newPoint) {
        waypoints.set(waypoints.indexOf(oldPoint), newPoint);
        save();
    }

    public void remove(Waypoint waypoint) {
        waypoints.remove(waypoint);
        save();
    }

    public static class WaypointTypeAdapter extends TypeAdapter<Waypoint> {

        @Override
        public void write(JsonWriter out, Waypoint value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.beginObject();
            out.name("dimension").value(value.dimension());
            out.name("x").value(value.x());
            out.name("y").value(value.y());
            out.name("z").value(value.z());
            out.name("color").value(String.format("#%02x%02x%02x%02x", value.color().getAlpha(), value.color().getRed(), value.color().getGreen(), value.color().getBlue()));
            out.name("name").value(value.name());
            out.name("preventRemoval").value(value.preventRemoval());
            out.name("hidden").value(value.hidden());
            out.endObject();
        }

        @Override
        public Waypoint read(JsonReader in) throws IOException {
            double x = 0, y = 0, z = 0;
            Color color = new Color(0, 0, 0, 0);
            String dimension = "", name = "";
            boolean preventRemoval = false, hidden = false;
            in.beginObject();
            while (in.peek() != JsonToken.END_OBJECT) {
                String jsonName = in.nextName();
                switch (jsonName) {
                    case "dimension" -> dimension = in.nextString();
                    case "x" -> x = in.nextDouble();
                    case "y" -> y = in.nextDouble();
                    case "z" -> z = in.nextDouble();
                    case "color" -> color = Color.decode(in.nextString());
                    case "name" -> name = in.nextString();
                    case "preventRemoval" -> preventRemoval = in.nextBoolean();
                    case "hidden" -> hidden = in.nextBoolean();

                    default -> in.skipValue();
                }
            }
            in.endObject();
            return new Waypoint(dimension, x, y, z, color, name, preventRemoval, hidden, false);
        }
    }
}