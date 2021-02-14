package me.mrCookieSlime.Slimefun.Objects;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.URID.URID;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Category {
    public static final List<Category> list = new ArrayList<>();


    private final ItemStack item;


    private final List<SlimefunItem> items;


    private final URID urid;


    private final int tier;


    public Category(ItemStack item) {
        this.item = item;
        this.items = new ArrayList<>();
        this.urid = URID.nextURID(this, false);
        this.tier = 3;
    }


    public Category(ItemStack item, int tier) {
        this.item = item;
        this.items = new ArrayList<>();
        this.urid = URID.nextURID(this, false);
        this.tier = tier;
    }

    public static List<Category> list() {
        return list;
    }

    public static Category getByItem(ItemStack item) {
        for (Category c : list) {
            if (c.getItem().isSimilar(item)) return c;
        }
        return null;
    }

    public void register() {
        list.add(this);
        Collections.sort(list, new CategorySorter());

        if (this instanceof SeasonCategory) {
            if (((SeasonCategory) this).isUnlocked()) Slimefun.current_categories.add(this);
        } else {
            Slimefun.current_categories.add(this);
        }
        Collections.sort(Slimefun.current_categories, new CategorySorter());
    }

    public void add(SlimefunItem item) {
        this.items.add(item);
    }

    public ItemStack getItem() {
        return this.item;
    }

    public List<SlimefunItem> getItems() {
        return this.items;
    }

    public URID getURID() {
        return this.urid;
    }


    public int getTier() {
        return this.tier;
    }


    static class CategorySorter
            implements Comparator<Category> {
        public int compare(Category c1, Category c2) {
            return Integer.compare(c1.getTier(), c2.getTier());
        }
    }
}


