package cc.vops.cheatbreaker.client.util;

import net.minecraft.client.gui.Font;

public class ChatColor {
    public final static String BLACK = "§0";
    public final static String DARK_BLUE = "§1";
    public final static String DARK_GREEN = "§2";
    public final static String DARK_AQUA = "§3";
    public final static String DARK_RED = "§4";
    public final static String DARK_PURPLE = "§5";
    public final static String GOLD = "§6";
    public final static String GRAY = "§7";
    public final static String DARK_GRAY = "§8";
    public final static String BLUE = "§9";
    public final static String GREEN = "§a";
    public final static String AQUA = "§b";
    public final static String RED = "§c";
    public final static String LIGHT_PURPLE = "§d";
    public final static String YELLOW = "§e";
    public final static String WHITE = "§f";
    public final static String OBFUSCATED = "§k";
    public final static String BOLD = "§l";
    public final static String STRIKETHROUGH = "§m";
    public final static String UNDERLINE = "§n";
    public final static String ITALIC = "§o";
    public final static String RESET = "§r";


    public static String formatText(Font font, String text, double var2) {
        StringBuilder var4 = new StringBuilder();
        StringBuilder var5 = new StringBuilder();
        boolean var6 = false;
        for (char var10 : text.toCharArray()) {
            String var14;
            String var13;
            if (var6) {
                var4.append(var10);
                var6 = false;
                continue;
            }
            if (var10 == '§') {
                var4.append(var10);
                var6 = true;
                continue;
            }
            var4.append(var10);
            int var11 = font.width(var4.toString());
            if (!((double)var11 >= var2)) continue;
            String var12 = var4.toString();
            if (var12.contains(" ")) {
                var13 = var12.substring(0, var12.lastIndexOf(" "));
                var14 = var12.substring(var12.lastIndexOf(" "));
                if (var14.startsWith(" ")) {
                    var14 = var14.replaceFirst(" ", "");
                }
            } else {
                var13 = var12.substring(0, var12.length() - 1);
                var14 = var12.substring(var12.length() - 1);
            }
            var5.append(var13).append("\n");
            String var15 = formatColor(var4.toString());
            var4.setLength(0);
            var4.append(var14).append(var15);
        }
        var5.append(var4);
        return var5.isEmpty() ? text : var5.toString();
    }

    public static String formatColor(String text) {
        StringBuilder result = new StringBuilder();
        StringBuilder colorCodes = new StringBuilder();
        boolean isCode = false;
        for (char c : text.toCharArray()) {
            if (isCode) {
                colorCodes.setLength(0);
                colorCodes.append('§').append(c);
                isCode = false;
                continue;
            }
            if (c == '§') {
                isCode = true;
                continue;
            }
        }
        return colorCodes.toString();
    }


}
