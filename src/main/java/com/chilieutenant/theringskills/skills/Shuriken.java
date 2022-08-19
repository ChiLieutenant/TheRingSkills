package com.chilieutenant.theringskills.skills;

import com.chilieutenant.theringskillsapi.DamageHandler;
import com.chilieutenant.theringskillsapi.GeneralMethods;
import com.chilieutenant.theringskillsapi.MainAbility;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Shuriken extends MainAbility {
    private Location origin;
    private Location location;
    private Vector direction;
    private int angle = 0;
    private Vector dir;
    private Location targetLocation;
    private int rotationAngle = 0;

    private static final String[] colors = { "505050", "505050", "505050", "505050", "505050", "646464", "787878", "8c8c8c" };

    public Shuriken(Player player) {
        super(player);

        if(aPlayer.isOnCooldown(this)) {
            return;
        }
        if(hasAbility(player, Shuriken.class)) {
            return;
        }

        setFields();
        start();

    }
    public void setFields() {
        location = player.getEyeLocation();
        origin = player.getLocation().clone();
        dir = player.getEyeLocation().getDirection();
        dir.multiply(29);
        targetLocation = player.getEyeLocation().add(dir);
        direction = GeneralMethods.getDirection(location, targetLocation);
    }
    static final double[] sin = new double[360];

    static {
        for (int i = 0; i < sin.length; i++) {
            sin[i] = Math.sin(Math.toRadians(i));
        }
    }

    public static double sin(int angle) {
        double result = sin[Math.abs(angle) % 360];
        return angle >= 0 ? result : -result;
    }

    public static double cos(int angle) {
        return sin(angle + 90);
    }
    public static void rotateAroundAxisX(Vector v, double cos, double sin) {
        double y = v.getY() * cos - v.getZ() * sin;
        double z = v.getY() * sin + v.getZ() * cos;
        v.setY(y).setZ(z);
    }
    public static void rotateAroundAxisY(Vector v, double cos, double sin) {
        double x = v.getX() * cos + v.getZ() * sin;
        double z = v.getX() * -sin + v.getZ() * cos;
        v.setX(x).setZ(z);
    }
    public static void displayColoredParticle(String hexVal, final Location loc, final int amount, final double offsetX, final double offsetY, final double offsetZ, float size) {
        int r = 0;
        int g = 0;
        int b = 0;
        if (hexVal.length() <= 6) {
            r = Integer.valueOf(hexVal.substring(0, 2), 16);
            g = Integer.valueOf(hexVal.substring(2, 4), 16);
            b = Integer.valueOf(hexVal.substring(4, 6), 16);
        }
        new ColoredParticle(Color.fromRGB(r, g, b), size).display(loc, amount, offsetX, offsetY, offsetZ);
    }

    @Override
    public String getName() {
        return "Shuriken";
    }

    @Override
    public long getCooldown() {
        return 2000;
    }

    @Override
    public void progress() {
        location.add(direction.clone().normalize().multiply(0.6));
        int angle2 = (int) player.getLocation().getYaw() + 90;
        double cos = cos(angle);
        double sin = sin(angle);
        double cos2 = cos(-angle2);
        double sin2 = sin(-angle2);
        int offset = 0;
        int index = 0;
        float size = 0.8f;
        for (double pos = 0.1; pos <= 0.8; pos += 0.1) {
            for (int j = 0; j <= 288; j += 72) {
                final Vector temp = new Vector(pos * cos(rotationAngle + j + offset), 0, pos * sin(rotationAngle + j + offset));
                if (angle != 0) rotateAroundAxisX(temp, cos, sin);
                if (angle2 != 0) rotateAroundAxisY(temp, cos2, sin2);
                displayColoredParticle(colors[index], location.clone().add(temp), 1, 0, 0, 0, size);

            }
            offset += 4;
            index = Math.max(0, Math.min(colors.length - 1, ++index));
            size -= 0.05;
        }
        if(location.distance(origin) > 23) {
            aPlayer.addCooldown(this);
            remove();
            return;
        }

        for(Entity e : GeneralMethods.getEntitiesAroundPoint(location, 2)) {
            if(e.getUniqueId() != player.getUniqueId()) {
                DamageHandler.damageEntity(e, 4, this);
                aPlayer.addCooldown(this);
                remove();
                return;
            }
        }


    }
}
