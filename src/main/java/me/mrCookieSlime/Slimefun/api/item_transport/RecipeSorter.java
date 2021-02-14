package me.mrCookieSlime.Slimefun.api.item_transport;

import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

import java.util.Comparator;

public class RecipeSorter
        implements Comparator<Integer> {
    final BlockMenu menu;

    public RecipeSorter(BlockMenu menu) {
        this.menu = menu;
    }


    public int compare(Integer slot1, Integer slot2) {
        return this.menu.getItemInSlot(slot1).getAmount() - this.menu.getItemInSlot(slot2).getAmount();
    }
}


