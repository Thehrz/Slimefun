package me.mrCookieSlime.Slimefun.listeners;

import io.izzel.taboolib.module.inject.TListener;
import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.Slimefun.Android.AndroidObject;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@TListener
public class AndroidKillingListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(final EntityDeathEvent e) {
        if (e.getEntity().hasMetadata("android_killer")) {
            Iterator<MetadataValue> iterator = e.getEntity().getMetadata("android_killer").iterator();
            if (iterator.hasNext()) {
                MetadataValue value = iterator.next();
                final AndroidObject obj = (AndroidObject) value.value();
                Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
                    List<ItemStack> items = new ArrayList<>();
                    for (Entity n : e.getEntity().getNearbyEntities(0.5D, 0.5D, 0.5D)) {
                        if (n instanceof Item && !n.hasMetadata("no_pickup")) {
                            items.add(((Item) n).getItemStack());
                            n.remove();
                        }
                    }

                    switch (e.getEntityType()) {
                        case BLAZE:
                            items.add(new ItemStack(Material.BLAZE_ROD, 1 + CSCoreLib.randomizer().nextInt(2)));
                            break;

                        case PIG_ZOMBIE:
                            items.add(new ItemStack(Material.GOLD_NUGGET, 1 + CSCoreLib.randomizer().nextInt(3)));
                            break;

                        case WITHER_SKELETON:
                            if (CSCoreLib.randomizer().nextInt(250) < 2) {
                                items.add((new MaterialData(Material.SKULL_ITEM, (byte) 1)).toItemStack(1));
                            }
                            break;

                        default:
                            break;
                    }


                    obj.getAndroid().addItems(obj.getBlock(), items.toArray(new ItemStack[0]));
                    ExperienceOrb exp = (ExperienceOrb) e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.EXPERIENCE_ORB);
                    exp.setExperience(1 + CSCoreLib.randomizer().nextInt(6));
                }, 1L);
            }

        }
    }
}


