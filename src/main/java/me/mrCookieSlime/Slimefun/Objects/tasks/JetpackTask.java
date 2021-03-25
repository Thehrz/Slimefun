package me.mrCookieSlime.Slimefun.Objects.tasks;

import me.mrCookieSlime.CSCoreLibPlugin.general.Player.PlayerInventory;
import me.mrCookieSlime.Slimefun.api.energy.ItemEnergy;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;


public class JetpackTask
        implements Runnable {
    final UUID uuid;
    final double thrust;
    int id;

    public JetpackTask(Player p, double thrust) {
        this.uuid = p.getUniqueId();
        this.thrust = thrust;
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
            Player p = Bukkit.getPlayer(this.uuid);
            float cost = 0.08F;
            float charge = ItemEnergy.getStoredEnergy(p.getInventory().getChestplate());
            if (charge >= cost) {
                p.getInventory().setChestplate(ItemEnergy.chargeItem(p.getInventory().getChestplate(), -cost));
                PlayerInventory.update(p);

                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.25F, 1.0F);
                p.getWorld().playEffect(p.getLocation(), Effect.SMOKE, 1, 1);
                p.setFallDistance(0.0F);
                Vector vector = new Vector(0, 1, 0);
                vector.multiply(this.thrust);
                vector.add(p.getEyeLocation().getDirection().multiply(0.2F));

                p.setVelocity(vector);
            } else {
                Bukkit.getScheduler().cancelTask(this.id);
            }
        }

    }
}


