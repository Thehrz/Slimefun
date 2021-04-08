package me.mrCookieSlime.Slimefun.api.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class AdvancedBlockMenuHolder<T extends InventoryHolder> {
    Inventory inventory;
    T block;

    public AdvancedBlockMenuHolder(T block) {
        this.block = block;
        this.inventory = block.getInventory();

    }

    public AdvancedBlockMenu getMenu() {
        return new AdvancedBlockMenu(inventory);
    }

}
