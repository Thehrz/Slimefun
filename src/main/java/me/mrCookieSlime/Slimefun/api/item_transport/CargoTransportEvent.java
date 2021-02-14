package me.mrCookieSlime.Slimefun.api.item_transport;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface CargoTransportEvent {
    ItemStack onEvent(Block paramBlock, int paramInt, ItemStack paramItemStack1, ItemStack paramItemStack2);
}


