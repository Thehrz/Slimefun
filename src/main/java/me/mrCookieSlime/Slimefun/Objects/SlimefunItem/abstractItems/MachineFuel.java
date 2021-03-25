package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems;

import org.bukkit.inventory.ItemStack;

public class MachineFuel {
    final int seconds;
    final ItemStack fuel;
    final ItemStack output;

    public MachineFuel(int seconds, ItemStack fuel) {
        this.seconds = seconds * 2;
        this.fuel = fuel;
        this.output = null;
    }

    public MachineFuel(int seconds, ItemStack fuel, ItemStack output) {
        this.seconds = seconds * 2;
        this.fuel = fuel;
        this.output = output;
    }

    public ItemStack getInput() {
        return this.fuel;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    public int getTicks() {
        return this.seconds;
    }

    @Override
    public int hashCode() {
        if (output != null) {
            return fuel.hashCode() + output.hashCode() + seconds;
        }
        return fuel.hashCode() + seconds;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MachineFuel machineFuel = (MachineFuel) obj;
        if (output != null && machineFuel.output != null) {
            return seconds == machineFuel.seconds && fuel.equals(machineFuel.fuel) && output.equals(machineFuel.output);
        }
        return seconds == machineFuel.seconds && fuel.equals(machineFuel.fuel);
    }
}


