package me.mrCookieSlime.Slimefun.holograms;

import me.mrCookieSlime.CSCoreLibPlugin.general.Math.DoubleHandler;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.ArmorStandFactory;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

public class EnergyHologram {
    public static void update(Block b, double supply, double demand) {
        update(b, (demand > supply) ? ("&4&l- &c" + DoubleHandler.getFancyDouble(Math.abs(supply - demand)) + " &7J &e⚡") : ("&2&l+ &a" + DoubleHandler.getFancyDouble(supply - demand) + " &7J &e⚡"));
    }

    public static void update(final Block b, final String name) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
            ArmorStand hologram = EnergyHologram.getArmorStand(b);
            hologram.setCustomName(ChatColor.translateAlternateColorCodes('&', name));
        });
    }

    public static void remove(final Block b) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
            ArmorStand hologram = EnergyHologram.getArmorStand(b);
            hologram.remove();
        });
    }

    private static ArmorStand getArmorStand(Block b) {
        Location l = new Location(b.getWorld(), b.getX() + 0.5D, (b.getY() - 0.7F), b.getZ() + 0.5D);

        for (Entity n : l.getChunk().getEntities()) {
            if (n instanceof ArmorStand &&
                    n.getCustomName() != null && l.distanceSquared(n.getLocation()) < 0.4D) {
                return (ArmorStand) n;
            }

        }

        return ArmorStandFactory.createHidden(l);
    }
}


