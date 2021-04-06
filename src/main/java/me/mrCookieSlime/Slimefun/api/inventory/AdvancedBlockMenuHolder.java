package me.mrCookieSlime.Slimefun.api.inventory;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

public class AdvancedBlockMenuHolder {
    Inventory inventory;
    Block block;

    public AdvancedBlockMenuHolder(Location location) {
        block = location.getBlock();
        if (block instanceof Chest) {
            this.inventory = ((Chest) block).getInventory();
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}
