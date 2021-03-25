package me.mrCookieSlime.Slimefun.Objects.SlimefunItem;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.MultiBlock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class SlimefunMachine
        extends SlimefunItem {
    final List<ItemStack[]> recipes;
    final List<ItemStack> shownRecipes;
    final Material trigger;

    public SlimefunMachine(Category category, ItemStack item, String id, ItemStack[] recipe, ItemStack[] machineRecipes, Material trigger) {
        super(category, item, id, RecipeType.MULTIBLOCK, recipe);
        this.recipes = new ArrayList<>();
        this.shownRecipes = new ArrayList<>();
        this.shownRecipes.addAll(Arrays.asList(machineRecipes));
        this.trigger = trigger;
    }

    public SlimefunMachine(Category category, ItemStack item, String id, ItemStack[] recipe, ItemStack[] machineRecipes, Material trigger, boolean ghost) {
        super(category, item, id, RecipeType.MULTIBLOCK, recipe, ghost);
        this.recipes = new ArrayList<>();
        this.shownRecipes = new ArrayList<>();
        this.shownRecipes.addAll(Arrays.asList(machineRecipes));
        this.trigger = trigger;
    }

    public SlimefunMachine(Category category, ItemStack item, String id, ItemStack[] recipe, ItemStack[] machineRecipes, Material trigger, String[] keys, Object[] values) {
        super(category, item, id, RecipeType.MULTIBLOCK, recipe, keys, values);
        this.recipes = new ArrayList<>();
        this.shownRecipes = new ArrayList<>();
        this.shownRecipes.addAll(Arrays.asList(machineRecipes));
        this.trigger = trigger;
    }

    public List<ItemStack[]> getRecipes() {
        return this.recipes;
    }

    public List<ItemStack> getDisplayRecipes() {
        return this.shownRecipes;
    }

    public void addRecipe(ItemStack[] input, ItemStack output) {
        this.recipes.add(input);
        this.recipes.add(new ItemStack[]{output});
    }


    @Override
    public void create() {
        toMultiBlock().register();
    }


    @Override
    public void install() {
        for (ItemStack i : getDisplayRecipes()) {
            SlimefunItem item = SlimefunItem.getByItem(i);
            if (item == null) {
                this.recipes.add(new ItemStack[]{i});
                continue;
            }
            if (!SlimefunItem.isDisabled(i)) this.recipes.add(new ItemStack[]{i});
        }
    }

    public MultiBlock toMultiBlock() {
        List<Material> mats = new ArrayList<>();
        for (ItemStack i : getRecipe()) {
            if (i == null) {
                mats.add(null);
            } else if (i.getType() == Material.CAULDRON_ITEM) {
                mats.add(Material.CAULDRON);
            } else if (i.getType() == Material.FLINT_AND_STEEL) {
                mats.add(Material.FIRE);
            } else {
                mats.add(i.getType());
            }

        }
        Material[] build = mats.toArray(new Material[0]);
        return new MultiBlock(build, this.trigger);
    }

    public Iterator<ItemStack[]> recipeIterator() {
        return this.recipes.iterator();
    }
}


