package me.mrCookieSlime.Slimefun.api.item_transport;

import org.bukkit.inventory.ItemStack;

public class ItemSlot {
    public final int slot;
    public final ItemStack item;

    public ItemSlot(ItemStack item, int slot) {
        this.slot = slot;
        this.item = item;
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getItem() {
        return this.item;
    }
}


