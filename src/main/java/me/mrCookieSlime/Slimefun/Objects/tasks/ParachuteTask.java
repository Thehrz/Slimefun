package me.mrCookieSlime.Slimefun.Objects.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

public class ParachuteTask
        implements Runnable {
    final UUID uuid;
    int id;

    public ParachuteTask(Player p) {
        this.uuid = p.getUniqueId();
    }

    public void setID(int id) {
        this.id = id;
    }


    public void run() {
        if (Bukkit.getPlayer(this.uuid) == null) {
            Bukkit.getScheduler().cancelTask(this.id);
        } else if (Bukkit.getPlayer(this.uuid).isDead()) {
            Bukkit.getScheduler().cancelTask(this.id);
        } else if (!Bukkit.getPlayer(this.uuid).isSneaking()) {
            Bukkit.getScheduler().cancelTask(this.id);
        } else {
            Player p = Bukkit.getPlayer(this.uuid);
            Vector vector = new Vector(0, 1, 0);
            vector.multiply(-0.1D);
            p.setVelocity(vector);
            p.setFallDistance(0.0F);
            if (!p.isSneaking()) Bukkit.getScheduler().cancelTask(this.id);
        }

    }
}


