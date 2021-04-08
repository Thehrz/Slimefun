package me.mrCookieSlime.Slimefun.api.inventory;


import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AdvancedBlockMenu extends CraftInventory {
    Inventory inventory;

    public AdvancedBlockMenu(Inventory inventory) {
        super(((CraftInventory) inventory).getInventory());
        this.inventory = inventory;
    }

    @Override
    public void setItem(int index, ItemStack item) {
        super.setItem(index, item);
    }
}
