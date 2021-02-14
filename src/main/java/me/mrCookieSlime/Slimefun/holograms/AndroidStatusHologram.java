package me.mrCookieSlime.Slimefun.holograms;

import me.mrCookieSlime.CSCoreLibPlugin.general.World.ArmorStandFactory;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.util.List;


public class AndroidStatusHologram {
    private static final double offset = 1.2D;

    public static void update(Block b, String name) {
        ArmorStand hologram = getArmorStand(b);
        hologram.setCustomName(name);
    }

    public static void remove(Block b) {
        ArmorStand hologram = getArmorStand(b);
        hologram.remove();
    }

    public static List<Entity> getNearbyEntities(Block b, double radius) {
        ArmorStand hologram = getArmorStand(b);
        return hologram.getNearbyEntities(radius, 1.0D, radius);
    }

    public static List<Entity> getNearbyEntities(Block b, double radius, double y) {
        ArmorStand hologram = getArmorStand(b);
        return hologram.getNearbyEntities(radius, y, radius);
    }

    private static ArmorStand getArmorStand(Block b) {
        Location l = new Location(b.getWorld(), b.getX() + 0.5D, b.getY() + 1.2D, b.getZ() + 0.5D);

        for (Entity n : l.getChunk().getEntities()) {
            if (n instanceof ArmorStand &&
                    n.getCustomName() == null && l.distanceSquared(n.getLocation()) < 0.4D) return (ArmorStand) n;

        }

        ArmorStand hologram = ArmorStandFactory.createHidden(l);
        hologram.setCustomNameVisible(false);
        hologram.setCustomName(null);
        return hologram;
    }
}


