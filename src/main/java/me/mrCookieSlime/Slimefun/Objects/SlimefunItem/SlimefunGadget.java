package me.mrCookieSlime.Slimefun.Objects.SlimefunItem;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class SlimefunGadget
        extends SlimefunItem {
    final List<ItemStack[]> recipes;
    final List<ItemStack> display_recipes;

    public SlimefunGadget(Category category, ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe, ItemStack[] machineRecipes) {
        super(category, item, id, recipeType, recipe);
        this.recipes = new ArrayList<>();
        this.display_recipes = new ArrayList<>();
        for (ItemStack i : machineRecipes) {
            this.recipes.add(new ItemStack[]{i});
            this.display_recipes.add(i);
        }
    }

    public SlimefunGadget(Category category, ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe, ItemStack[] machineRecipes, String[] keys, Object[] values) {
        super(category, item, id, recipeType, recipe, keys, values);
        this.recipes = new ArrayList<>();
        this.display_recipes = new ArrayList<>();
        for (ItemStack i : machineRecipes) {
            this.recipes.add(new ItemStack[]{i});
            this.display_recipes.add(i);
        }
    }

    public List<ItemStack[]> getRecipes() {
        return this.recipes;
    }

    public List<ItemStack> getDisplayRecipes() {
        return this.display_recipes;
    }

    public void addRecipe(ItemStack input, ItemStack output) {
        this.recipes.add(new ItemStack[]{input});
        this.recipes.add(new ItemStack[]{output});
        this.display_recipes.add(input);
        this.display_recipes.add(output);
    }
}


