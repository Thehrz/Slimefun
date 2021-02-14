package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.container;

import org.bukkit.inventory.ItemStack;

public abstract class AbstractDynamicRecipe {
    public abstract ItemStack[] getInput();

    public abstract ItemStack[] getOutput();

    public abstract int getTicks();

    public abstract void setTicks(int paramInt);
}


