package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.machines;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class ElectrifiedCrucible
        extends AContainer {
    public ElectrifiedCrucible(Category category, ItemStack item, String name, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, name, recipeType, recipe);
    }


    public void registerDefaultRecipes() {
        registerRecipe(10, new ItemStack[]{new ItemStack(Material.BUCKET), new ItemStack(Material.COBBLESTONE, 16)}, new ItemStack[]{new ItemStack(Material.LAVA_BUCKET)});
        registerRecipe(8, new ItemStack[]{new ItemStack(Material.BUCKET), new ItemStack(Material.HARD_CLAY, 12)}, new ItemStack[]{new ItemStack(Material.LAVA_BUCKET)});
        registerRecipe(10, new ItemStack[]{new ItemStack(Material.BUCKET), new ItemStack(Material.LEAVES, 16)}, new ItemStack[]{new ItemStack(Material.WATER_BUCKET)});
    }


    public String getMachineIdentifier() {
        return "ELECTRIFIED_CRUCIBLE";
    }


    public ItemStack getProgressBar() {
        return new ItemStack(Material.FLINT_AND_STEEL);
    }


    public String getInventoryTitle() {
        return "&4电力坩埚";
    }
}


