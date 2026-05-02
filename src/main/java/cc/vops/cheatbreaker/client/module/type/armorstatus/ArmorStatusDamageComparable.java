package cc.vops.cheatbreaker.client.module.type.armorstatus;

import java.util.List;

public class ArmorStatusDamageComparable
        implements Comparable<ArmorStatusDamageComparable> {
    public int percent;
    public String colorCode;

    public ArmorStatusDamageComparable(int n, String string) {
        this.percent = n;
        this.colorCode = string;
    }

    public String toString() {
        return this.percent + ", " + this.colorCode;
    }

    public int compare(ArmorStatusDamageComparable illlIlllIlIIIIllIlllIlIII) {
        return Integer.compare(this.percent, illlIlllIlIIIIllIlllIlIII.percent);
    }

    public static String getDamageColor(List<ArmorStatusDamageComparable> list, int percent) {
        for (ArmorStatusDamageComparable comparable : list) {
            if (percent > comparable.percent) continue;
            return comparable.colorCode;
        }
        return "f";
    }

    public int compareTo(ArmorStatusDamageComparable object) {
        return this.compare(object);
    }
}
