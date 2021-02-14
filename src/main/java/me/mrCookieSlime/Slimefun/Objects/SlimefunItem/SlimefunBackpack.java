package me.mrCookieSlime.Slimefun.Objects.SlimefunItem;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import org.bukkit.inventory.ItemStack;

public class SlimefunBackpack
        extends SlimefunItem {
    public final int size;

    public SlimefunBackpack(int size, Category category, ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, id, recipeType, recipe);

        this.size = size;
    }
}


