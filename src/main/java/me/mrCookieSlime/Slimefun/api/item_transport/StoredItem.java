package me.mrCookieSlime.Slimefun.api.item_transport;

import org.bukkit.inventory.ItemStack;

public class StoredItem {
    public final ItemStack item;
    public int amount;

    public StoredItem(ItemStack item, int amount) {
        this.amount = amount;
        this.item = item;
    }

    public int getAmount() {
        return this.amount;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public void add(int amount) {
        this.amount += amount;
    }
}


