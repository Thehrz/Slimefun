package me.mrCookieSlime.Slimefun.Objects.tasks;

import me.mrCookieSlime.CSCoreLibPlugin.general.Player.PlayerInventory;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import me.mrCookieSlime.Slimefun.api.energy.ItemEnergy;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.UUID;


public class JetBootsTask
        implements Runnable {
    final UUID uuid;
    final double speed;
    int id;

    public JetBootsTask(Player p, double speed) {
        this.uuid = p.getUniqueId();
        this.speed = speed;
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
            float cost = 0.075F;
            float charge = ItemEnergy.getStoredEnergy(p.getInventory().getBoots());
            double accuracy = Double.parseDouble((new DecimalFormat("##.##")).format(this.speed - 0.7D).replace(",", "."));
            if (charge >= cost) {
                p.getInventory().setBoots(ItemEnergy.chargeItem(p.getInventory().getBoots(), -cost));
                PlayerInventory.update(p);

                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_TNT_PRIMED, 0.25F, 1.0F);
                p.getWorld().playEffect(p.getLocation(), Effect.SMOKE, 1, 1);
                p.setFallDistance(0.0F);
                double gravity = 0.04D;
                double offset = SlimefunStartup.chance(100, 50) ? accuracy : -accuracy;
                Vector vector = new Vector(p.getEyeLocation().getDirection().getX() * this.speed + offset, gravity, p.getEyeLocation().getDirection().getZ() * this.speed - offset);

                p.setVelocity(vector);
            } else {
                Bukkit.getScheduler().cancelTask(this.id);
            }
        }

    }
}


