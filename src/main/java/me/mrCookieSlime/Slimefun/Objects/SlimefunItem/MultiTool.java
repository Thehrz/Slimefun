package me.mrCookieSlime.Slimefun.Objects.SlimefunItem;

import me.mrCookieSlime.Slimefun.Lists.Categories;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class MultiTool
        extends DamagableChargableItem {
    List<Integer> modes;

    public MultiTool(ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe, String[] keys, Object[] values) {
        super(Categories.TECH, item, id, recipeType, recipe, "Multi Tool", keys, values);
    }


    public void create() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            if (Slimefun.getItemValue(getID(), "mode." + i + ".enabled") != null && (Boolean) Slimefun.getItemValue(getID(), "mode." + i + ".enabled"))
                list.add(i);

        }
        this.modes = list;
    }

    public List<Integer> getModes() {
        return this.modes;
    }
}


