package cc.vops.cheatbreaker.client.util.dash;


import cc.vops.cheatbreaker.CheatBreaker;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javazoom.jl.decoder.JavaLayerUtils;
import javazoom.jl.player.Player;
import org.lwjgl.system.linux.Stat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class DashUtil {
    private static final String apiURL = "https://dash-api.com/api/v3/allData.php";
    private static Player player;
    private static boolean IlllIIIlIlllIllIlIIlllIlI;
    private static DashPlayer dashPlayer;

    public static List<Station> get() {
        JavaLayerUtils.setHook(new DashHook());
        ArrayList<Station> arrayList = new ArrayList<>();
        try {
            JsonObject jsonObject = new JsonParser().parse(DashUtil.get(apiURL)).getAsJsonObject();
            if (jsonObject.has("stations")) {
                JsonArray jsonArray = jsonObject.getAsJsonArray("stations");
                for (JsonElement jsonElement : jsonArray) {
                    JsonObject jsonObject2 = jsonElement.getAsJsonObject();
                    String string = jsonObject2.get("name").getAsString();
                    String string2 = jsonObject2.get("genre").getAsString();
                    String string3 = jsonObject2.get("square_logo_url").getAsString();
                    String string4 = jsonObject2.get("current_song_url").getAsString();
                    String string5 = jsonObject2.get("stream_url").getAsString();
                    Station illlIllIlIIIIlIIlIIllIIIl = new Station(string, string3, string2, string4, string5);
                    arrayList.add(illlIllIlIIIIlIIlIIllIIIl);
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return arrayList;
    }

    public static String get(String string) {
        try {
            URLConnection uRLConnection = new URL(string).openConnection();
            uRLConnection.setConnectTimeout(5000);
            uRLConnection.setReadTimeout(5000);
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uRLConnection.getInputStream()))) {
                return bufferedReader.readLine();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static void end() {
        if (CheatBreaker.getInstance().getRadioManager().getCurrentStation() instanceof LocalStation ls) {
            ls.endStream();
        }
        if (player != null) {
            player.close();
            dashPlayer.closeImpl();
            player = null;
        }
        IlllIIIlIlllIllIlIIlllIlI = false;
    }

    public static boolean isPlayerNotNull() {
        // lol
        if (CheatBreaker.getInstance() == null) return false;
        if (CheatBreaker.getInstance().getRadioManager() == null) return false;
        if (CheatBreaker.getInstance().getRadioManager().getCurrentStation() instanceof LocalStation ls) {
            return ls.isPlaying();
        }

        return player != null;
    }

    public static void play(String string) {
        if (IlllIIIlIlllIllIlIIlllIlI) {
            return;
        }
        IlllIIIlIlllIllIlIIlllIlI = true;
        if (player != null) {
            player.close();
            CheatBreaker.LOGGER.info("Stopping current stream...");
            player = null;
            return;
        }
        Station station = CheatBreaker.getInstance().getRadioManager().getCurrentStation();
        new Thread(() -> {
            try {
                URL uRL = new URL(string);
                InputStream inputStream = uRL.openStream();
                dashPlayer = new DashPlayer();
                player = new Player(inputStream, dashPlayer);
                if (station == CheatBreaker.getInstance().getRadioManager().getCurrentStation()) {
                    player.play();
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }).start();
    }

    public static DashPlayer getDashPlayer() {
        return dashPlayer;
    }

    static {
        dashPlayer = new DashPlayer();
    }
}
