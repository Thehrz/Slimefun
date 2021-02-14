package me.mrCookieSlime.Slimefun.api.inventory;

import org.bukkit.inventory.ItemStack;

public interface ItemManipulationEvent {
    ItemStack onEvent(int paramInt, ItemStack paramItemStack1, ItemStack paramItemStack2);
}


