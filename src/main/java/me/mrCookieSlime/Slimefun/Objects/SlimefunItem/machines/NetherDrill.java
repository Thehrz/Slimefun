package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.machines;

import me.mrCookieSlime.Slimefun.GEO.OreGenResource;
import me.mrCookieSlime.Slimefun.GEO.OreGenSystem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.ADrill;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class NetherDrill
        extends ADrill {
    public NetherDrill(Category category, ItemStack item, String name, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, name, recipeType, recipe);
    }


    public OreGenResource getOreGenResource() {
        return OreGenSystem.getResource("下界玄冰");
    }


    public ItemStack[] getOutputItems() {
        return new ItemStack[]{SlimefunItems.NETHER_ICE};
    }


    public int getProcessingTime() {
        return 24;
    }


    public String getInventoryTitle() {
        return "&4下界矿钻";
    }


    public ItemStack getProgressBar() {
        return new ItemStack(Material.DIAMOND_PICKAXE);
    }


    public String getMachineIdentifier() {
        return "NETHER_DRILL";
    }
}


