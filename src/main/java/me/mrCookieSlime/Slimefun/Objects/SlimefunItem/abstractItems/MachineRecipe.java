package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems;

import org.bukkit.inventory.ItemStack;

public class MachineRecipe {
    final ItemStack[] input;
    final ItemStack[] output;
    int ticks;

    public MachineRecipe(int seconds, ItemStack[] input, ItemStack[] output) {
        this.ticks = seconds * 2;
        this.input = input;
        this.output = output;
    }

    public ItemStack[] getInput() {
        return this.input;
    }

    public ItemStack[] getOutput() {
        return this.output;
    }

    public int getTicks() {
        return this.ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }
}


