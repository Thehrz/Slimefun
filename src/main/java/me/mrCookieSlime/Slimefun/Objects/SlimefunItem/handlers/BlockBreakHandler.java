package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;


public abstract class BlockBreakHandler
        extends ItemHandler {
    public abstract boolean onBlockBreak(BlockBreakEvent paramBlockBreakEvent, ItemStack paramItemStack, int paramInt, List<ItemStack> paramList);

    public String toCodename() {
        return "BlockBreakHandler";
    }
}


