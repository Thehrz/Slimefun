package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.container;

import org.bukkit.inventory.ItemStack;

public class UURecipe {
    final ItemStack[] input;
    final int uu;
    int ticks;

    public UURecipe(int seconds, ItemStack[] input, int uuAmount) {
        this.ticks = seconds * 2;
        this.input = input;
        this.uu = uuAmount;
    }

    public ItemStack[] getInput() {
        return this.input;
    }

    public int getUUAmount() {
        return this.uu;
    }

    public int getTicks() {
        return this.ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }
}


