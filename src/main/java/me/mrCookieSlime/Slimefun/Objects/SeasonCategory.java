package me.mrCookieSlime.Slimefun.Objects;

import org.bukkit.inventory.ItemStack;

import java.util.Calendar;


public class SeasonCategory
        extends Category {
    private final int month;


    public SeasonCategory(int month, int tier, ItemStack item) {
        super(item, tier);
        this.month = month - 1;
    }


    public int getMonth() {
        return this.month;
    }


    public boolean isUnlocked() {
        if (this.month == -1) return true;
        Calendar calendar = Calendar.getInstance();
        return (this.month == calendar.get(2));
    }
}


