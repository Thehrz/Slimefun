package me.mrCookieSlime.Slimefun.listeners;

import io.izzel.taboolib.module.inject.TListener;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

@TListener
public class ItemPickupListener_1_12 implements Listener {
    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (e.getItem().hasMetadata("no_pickup")) {
            e.setCancelled(true);
        } else if (!e.getItem().hasMetadata("no_pickup") && e.getItem().getItemStack().hasItemMeta() && e.getItem().getItemStack().getItemMeta().hasDisplayName() && e.getItem().getItemStack().getItemMeta().getDisplayName().startsWith(ChatColor.translateAlternateColorCodes('&', "&5&d祭坛 &3灵柱 - &e"))) {
            e.setCancelled(true);
            e.getItem().remove();
        }

    }

    @EventHandler
    public void onMinecartPickup(InventoryPickupItemEvent e) {
        if (e.getItem().hasMetadata("no_pickup")) {
            e.setCancelled(true);
        } else if (!e.getItem().hasMetadata("no_pickup") && e.getItem().getItemStack().hasItemMeta() && e.getItem().getItemStack().getItemMeta().hasDisplayName() && e.getItem().getItemStack().getItemMeta().getDisplayName().startsWith(ChatColor.translateAlternateColorCodes('&', "&5&d祭坛 &3灵柱 - &e"))) {
            e.setCancelled(true);
            e.getItem().remove();
        }

    }
}


