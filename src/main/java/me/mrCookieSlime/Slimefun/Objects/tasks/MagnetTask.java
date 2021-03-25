package me.mrCookieSlime.Slimefun.Objects.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MagnetTask
        implements Runnable {
    final UUID uuid;
    int id;

    public MagnetTask(Player p) {
        this.uuid = p.getUniqueId();
    }

    public void setID(int id) {
        this.id = id;
    }


    @Override
    public void run() {
        if (Bukkit.getPlayer(this.uuid) == null) {
            Bukkit.getScheduler().cancelTask(this.id);
        } else if (Bukkit.getPlayer(this.uuid).isDead()) {
            Bukkit.getScheduler().cancelTask(this.id);
        } else if (!Bukkit.getPlayer(this.uuid).isSneaking()) {
            Bukkit.getScheduler().cancelTask(this.id);
        } else {
            for (Entity item : Bukkit.getPlayer(this.uuid).getNearbyEntities(6.0D, 6.0D, 6.0D)) {
                if (item instanceof Item &&
                        !item.hasMetadata("no_pickup") && ((Item) item).getPickupDelay() <= 0) {
                    item.teleport(Bukkit.getPlayer(this.uuid).getEyeLocation());
                    Bukkit.getPlayer(this.uuid).getWorld().playSound(Bukkit.getPlayer(this.uuid).getEyeLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 5.0F, 2.0F);
                }
            }
        }

    }
}


