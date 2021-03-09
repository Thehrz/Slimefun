package me.mrCookieSlime.Slimefun.Objects;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;


public class LockedCategory
        extends Category {
    private final List<Category> parents;

    public LockedCategory(ItemStack item, Category... parents) {
        super(item);
        this.parents = Arrays.asList(parents);
    }


    public LockedCategory(ItemStack item, int tier, Category... parents) {
        super(item, tier);
        this.parents = Arrays.asList(parents);
    }


    public List<Category> getParents() {
        return this.parents;
    }


    public void addParent(Category category) {
        if (category == this)
            throw new IllegalArgumentException("Category '" + getItem().getItemMeta().getDisplayName() + "' cannot be a parent of itself.");
        this.parents.add(category);
    }


    public void removeParent(Category category) {
        this.parents.remove(category);
    }


    public boolean hasUnlocked(Player p) {
        for (Category category : this.parents) {
            for (SlimefunItem item : category.getItems()) {
                if (Slimefun.isEnabled(p, item.getItem(), false) && Slimefun.hasPermission(p, item, false) &&
                        item.getResearch() != null &&
                        !item.getResearch().hasUnlocked(p)) {
                    return false;
                }

            }
        }

        return true;
    }
}


