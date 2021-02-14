package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public abstract class BlockPlaceHandler
        extends ItemHandler {
    public abstract boolean onBlockPlace(BlockPlaceEvent paramBlockPlaceEvent, ItemStack paramItemStack);

    public String toCodename() {
        return "BlockPlaceHandler";
    }
}


