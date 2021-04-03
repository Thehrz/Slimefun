package me.mrCookieSlime.Slimefun.AncientAltar;

import me.mrCookieSlime.CSCoreLibPlugin.general.Particles.MC_1_8.ParticleEffect;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import me.mrCookieSlime.Slimefun.Variables;
import me.mrCookieSlime.Slimefun.listeners.AncientAltarListener;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RitualAnimation implements Runnable {
    final List<Block> altars;
    final Block altar;
    final Location l;
    final ItemStack output;
    final List<Block> pedestals;
    final List<ItemStack> items;
    final List<Location> particles;
    boolean running;
    int stage;

    public RitualAnimation(List<Block> altars, Block altar, Location drop, ItemStack output, List<Block> pedestals, List<ItemStack> items) {
        this.l = drop;
        this.altar = altar;
        this.altars = altars;
        this.output = output;
        this.pedestals = pedestals;
        this.items = items;
        this.particles = new ArrayList<>();

        this.running = true;
        this.stage = 0;
    }

    @Override
    public void run() {
        idle();
        if (this.stage == 36) {
            finish();
            return;
        }
        if (this.stage > 0 && this.stage % 4 == 0) {
            checkPedestal(this.pedestals.get(this.stage / 4 - 1));
        }
        this.stage++;
        SlimefunStartup.instance.getServer().getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, this, 8L);
    }

    private void idle() {
        try {
            ParticleEffect.SPELL_WITCH.display(this.l, 1.2F, 0.0F, 1.2F, 0.0F, 16);
            ParticleEffect.FIREWORKS_SPARK.display(this.l, 0.2F, 0.0F, 0.2F, 0.0F, 8);
            for (Location l2 : this.particles) {
                ParticleEffect.ENCHANTMENT_TABLE.display(l2, 0.3F, 0.2F, 0.3F, 0.0F, 16);
                ParticleEffect.CRIT_MAGIC.display(l2, 0.3F, 0.2F, 0.3F, 0.0F, 8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkPedestal(Block pedestal) {
        Item item = AncientAltarListener.findItem(pedestal);
        if (item == null) {
            abort();
        } else {
            this.particles.add(pedestal.getLocation().add(0.5D, 1.5D, 0.5D));
            this.items.add(AncientAltarListener.fixItemStack(item.getItemStack(), item.getCustomName()));
            pedestal.getWorld().playSound(pedestal.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 5.0F, 2.0F);

            try {
                ParticleEffect.ENCHANTMENT_TABLE.display(pedestal.getLocation().add(0.5D, 1.5D, 0.5D), 0.3F, 0.2F, 0.3F, 0.0F, 16);
                ParticleEffect.CRIT_MAGIC.display(pedestal.getLocation().add(0.5D, 1.5D, 0.5D), 0.3F, 0.2F, 0.3F, 0.0F, 8);
            } catch (Exception e) {
                e.printStackTrace();
            }

            item.remove();
            pedestal.removeMetadata("item_placed", SlimefunStartup.instance);
        }

    }

    private void abort() {
        this.running = false;

        this.pedestals.forEach(pblock -> Variables.altarinuse.remove(pblock.getLocation()));


        Variables.altarinuse.remove(this.altar.getLocation());
        this.l.getWorld().playSound(this.l, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 5.0F, 1.0F);
        this.altars.remove(this.altar);
    }

    private void finish() {
        if (this.running) {
            this.l.getWorld().playSound(this.l, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F, 1.0F);
            this.l.getWorld().playEffect(this.l, Effect.STEP_SOUND, Material.EMERALD_BLOCK);
            this.l.getWorld().dropItemNaturally(this.l.add(0.0D, 1.0D, 0.0D), this.output);

            this.pedestals.forEach(pblock -> Variables.altarinuse.remove(pblock.getLocation()));


            Variables.altarinuse.remove(this.altar.getLocation());
            this.altars.remove(this.altar);
        } else {

            this.l.getWorld().playSound(this.l, Sound.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, 1.0F, 1.0F);
        }
    }
}


