package me.mrCookieSlime.Slimefun.Objects.SlimefunItem;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.Interfaces.NotPlaceable;
import org.bukkit.inventory.ItemStack;

public class ExcludedGadget
        extends SlimefunGadget
        implements NotPlaceable {
    public ExcludedGadget(Category category, ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe, ItemStack[] machineRecipes) {
        super(category, item, id, recipeType, recipe, machineRecipes);
    }

    public ExcludedGadget(Category category, ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe, ItemStack[] machineRecipes, String[] keys, Object[] values) {
        super(category, item, id, recipeType, recipe, machineRecipes, keys, values);
    }
}


