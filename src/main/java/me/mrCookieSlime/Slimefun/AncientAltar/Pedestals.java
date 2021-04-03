package me.mrCookieSlime.Slimefun.AncientAltar;

import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Pedestals {
    public static List<AltarRecipe> recipes = new ArrayList<>();

    public static List<Block> getPedestals(Block altar) {
        List<Block> list = new ArrayList<>();

        if (BlockStorage.check(altar.getRelative(3, 0, 0), "ANCIENT_PEDESTAL")) {
            list.add(altar.getRelative(3, 0, 0));
        }
        if (BlockStorage.check(altar.getRelative(-3, 0, 0), "ANCIENT_PEDESTAL")) {
            list.add(altar.getRelative(-3, 0, 0));
        }
        if (BlockStorage.check(altar.getRelative(0, 0, 3), "ANCIENT_PEDESTAL")) {
            list.add(altar.getRelative(0, 0, 3));
        }
        if (BlockStorage.check(altar.getRelative(0, 0, -3), "ANCIENT_PEDESTAL")) {
            list.add(altar.getRelative(0, 0, -3));
        }
        if (BlockStorage.check(altar.getRelative(2, 0, 2), "ANCIENT_PEDESTAL")) {
            list.add(altar.getRelative(2, 0, 2));
        }
        if (BlockStorage.check(altar.getRelative(2, 0, -2), "ANCIENT_PEDESTAL")) {
            list.add(altar.getRelative(2, 0, -2));
        }
        if (BlockStorage.check(altar.getRelative(-2, 0, 2), "ANCIENT_PEDESTAL")) {
            list.add(altar.getRelative(-2, 0, 2));
        }
        if (BlockStorage.check(altar.getRelative(-2, 0, -2), "ANCIENT_PEDESTAL")) {
            list.add(altar.getRelative(-2, 0, -2));
        }

        return list;
    }

    public static ItemStack getRecipeOutput(ItemStack catalyst, List<ItemStack> input) {
        if (input.size() != 8) {
            return null;
        }
        if (SlimefunManager.isItemSimiliar(catalyst, SlimefunItems.BROKEN_SPAWNER, false)) {
            if (checkRecipe(SlimefunItems.BROKEN_SPAWNER, input) == null) {
                return null;
            }
            ItemStack spawner = SlimefunItems.REPAIRED_SPAWNER.clone();
            ItemMeta im = spawner.getItemMeta();
            im.setLore(Collections.singletonList(catalyst.getItemMeta().getLore().get(0)));
            spawner.setItemMeta(im);
            return spawner;
        }

        return checkRecipe(catalyst, input);
    }

    private static ItemStack checkRecipe(ItemStack catalyst, List<ItemStack> input) {
        AltarRecipe r;
        for (AltarRecipe recipe : recipes) {
            if (SlimefunManager.isItemSimiliar(catalyst, recipe.getCatalyst(), true)) {
                r = recipe;

                List<ItemStack> copy = new ArrayList<>(input);


                for (ItemStack item : recipe.getInput()) {
                    Iterator<ItemStack> iterator = copy.iterator();
                    boolean match = false;

                    while (iterator.hasNext()) {
                        ItemStack altarItem = iterator.next();
                        if (SlimefunManager.isItemSimiliar(altarItem, item, true)) {
                            match = true;
                            iterator.remove();

                            break;
                        }
                    }
                    if (!match) {
                        r = null;

                        break;
                    }
                }
                if (r != null) {
                    return r.getOutput();
                }
            }
        }
        return null;
    }
}


