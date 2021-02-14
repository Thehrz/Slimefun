package me.mrCookieSlime.Slimefun.holograms;

import me.mrCookieSlime.CSCoreLibPlugin.general.World.ArmorStandFactory;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

public class CargoHologram {
    public static void update(final Block b, final String name) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
            ArmorStand hologram = CargoHologram.getArmorStand(b);
            hologram.setCustomName(ChatColor.translateAlternateColorCodes('&', name));
        });
    }

    public static void remove(Block b) {
        ArmorStand hologram = getArmorStand(b);
        hologram.remove();
    }

    private static ArmorStand getArmorStand(Block b) {
        Location l = new Location(b.getWorld(), b.getX() + 0.5D, (b.getY() - 0.7F), b.getZ() + 0.5D);

        for (Entity n : l.getChunk().getEntities()) {
            if (n instanceof ArmorStand &&
                    n.getCustomName() != null && l.distanceSquared(n.getLocation()) < 0.4D) return (ArmorStand) n;

        }

        ArmorStand hologram = ArmorStandFactory.createHidden(l);
        return hologram;
    }
}


