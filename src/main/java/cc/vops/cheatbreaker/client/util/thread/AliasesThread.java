package cc.vops.cheatbreaker.client.util.thread;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.overlay.element.AliasesElement;
import cc.vops.cheatbreaker.client.util.ChatColor;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class AliasesThread extends Thread {
    private final AliasesElement parent;
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public AliasesThread(AliasesElement parent) {
        this.parent = parent;
    }

    @Override
    public void run() {
        try {
            URL url = new URL("https://liforra.de/api/namehistory?uuid=" + this.parent.getFriend().getPlayerId().replaceAll("-", ""));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int code = connection.getResponseCode();
            CheatBreaker.LOGGER.info("HTTP CODE = " + code);

            BufferedReader reader;

            if (code >= 200 && code < 300) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();

            JsonElement jsonElement = JsonParser.parseString(stringBuilder.toString());

            for (Map.Entry entry : jsonElement.getAsJsonObject().entrySet()) {
                if (!((String) entry.getKey()).equalsIgnoreCase("history")) continue;
                Iterator iterator = ((JsonElement) entry.getValue()).getAsJsonArray().iterator();
                while (iterator.hasNext()) {
                    JsonElement jsonElement2 = (JsonElement) iterator.next();
                    String string2 = jsonElement2.getAsJsonObject().get("name").getAsString();
                    if (jsonElement2.getAsJsonObject().has("changed_at")) {
                        String changedAt = jsonElement2.getAsJsonObject().get("changed_at").getAsString();
                        LocalDateTime localDateTime = LocalDateTime.parse(changedAt.replaceAll("Z", ""));
                        this.parent.getAliases().add(ChatColor.GRAY + localDateTime.format(this.format) + ChatColor.RESET + " " + string2);
                        continue;
                    }
                    this.parent.getAliases().add(string2);
                }
            }
            Collections.reverse(this.parent.getAliases());

            if (this.parent.getAliases().isEmpty()) {
                this.parent.getAliases().add(ChatColor.RED + "No aliases found.");
            }

            this.parent.setElementSize(
                    this.parent.getX(),
                    this.parent.getY(),
                    this.parent.getWidth(),
                    this.parent.getHeight() + (float)(this.parent.getAliases().size() * 10) - (float)10);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}