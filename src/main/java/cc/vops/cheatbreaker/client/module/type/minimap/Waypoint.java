package cc.vops.cheatbreaker.client.module.type.minimap;

import org.joml.Vector3f;

import java.awt.*;

public record Waypoint(String dimension, double x, double y, double z, Color color, String name, boolean preventRemoval, boolean hidden, boolean isServerWaypoint) {
    public double distTo(double x, double y, double z) {
        return Math.sqrt(squaredDistTo(x, y, z));
    }

    public double squaredDistTo(Vector3f vec) {
        return squaredDistTo(vec.x(), vec.y(), vec.z());
    }

    public double squaredDistTo(double x, double y, double z) {
        double x2 = x() - x;
        double y2 = y() - y;
        double z2 = z() - z;
        return x2 * x2 + y2 * y2 + z2 * z2;
    }

    public boolean closerToThan(double x, double y, double z, double distance) {
        return squaredDistTo(x, y, z) < distance * distance;
    }

    public static int displayXOffset() {
        return 2;
    }

    public static int displayYOffset() {
        return 2;
    }

    public int colorInt() {
        return color.getRGB();
    }
}