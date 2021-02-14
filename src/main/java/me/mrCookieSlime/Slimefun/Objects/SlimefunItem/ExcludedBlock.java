package me.mrCookieSlime.Slimefun.Objects.SlimefunItem;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.Interfaces.NotPlaceable;
import org.bukkit.inventory.ItemStack;

public class ExcludedBlock
        extends SlimefunItem
        implements NotPlaceable {
    public ExcludedBlock(Category category, ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, id, recipeType, recipe);
    }
}


