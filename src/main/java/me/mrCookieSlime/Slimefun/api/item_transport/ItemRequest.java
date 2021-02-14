package me.mrCookieSlime.Slimefun.api.item_transport;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class ItemRequest {
    final ItemStack item;
    final ItemTransportFlow flow;
    final Location terminal;
    final int slot;

    public ItemRequest(Location terminal, int slot, ItemStack item, ItemTransportFlow flow) {
        this.terminal = terminal;
        this.item = item;
        this.slot = slot;
        this.flow = flow;
    }

    public Location getTerminal() {
        return this.terminal;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public ItemTransportFlow getDirection() {
        return this.flow;
    }

    public int getSlot() {
        return this.slot;
    }
}


