package me.mrCookieSlime.Slimefun.AncientAltar;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class AltarRecipe {
    final ItemStack catalyst;
    final List<ItemStack> input;
    final ItemStack output;

    public AltarRecipe(List<ItemStack> input, ItemStack output) {
        this.catalyst = input.get(4);
        this.input = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            if (i != 4) {
                this.input.add(input.get(i));
            }
        }
        this.output = output;

        Pedestals.recipes.add(this);
    }

    public ItemStack getCatalyst() {
        return this.catalyst;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    public List<ItemStack> getInput() {
        return this.input;
    }
}


