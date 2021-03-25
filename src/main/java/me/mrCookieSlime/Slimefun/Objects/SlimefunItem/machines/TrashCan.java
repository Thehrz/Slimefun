package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.machines;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class TrashCan
        extends SlimefunItem {
    private static final int[] border = new int[]{0, 1, 2, 3, 5, 4, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};

    public TrashCan(Category category, ItemStack item, String name, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, name, recipeType, recipe);

        new BlockMenuPreset(name, getInventoryTitle()) {
            @Override
            public void init() {
                TrashCan.this.constructMenu(this);
            }


            @Override
            public void newInstance(BlockMenu menu, Block b) {
            }


            @Override
            public boolean canOpen(Block b, Player p) {
                return true;
            }


            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                if (flow.equals(ItemTransportFlow.INSERT)) return TrashCan.this.getInputSlots();
                return new int[0];
            }
        };
    }


    private void constructMenu(BlockMenuPreset preset) {
        for (int i : border) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 14), " "), (arg0, arg1, arg2, arg3) -> false);
        }
    }


    public String getInventoryTitle() {
        return "&4垃圾桶";
    }

    public int[] getInputSlots() {
        return new int[]{10, 11, 12, 13, 14, 15, 16};
    }


    @Override
    public void register(boolean slimefun) {
        addItemHandler(new BlockTicker() {
            @Override
            public void uniqueTick() {
            }


            @Override
            public void tick(Block b, SlimefunItem item, Config data) {
                BlockMenu menu = BlockStorage.getInventory(b);
                for (int slot : TrashCan.this.getInputSlots()) {
                    menu.replaceExistingItem(slot, null);
                }
            }


            @Override
            public boolean isSynchronized() {
                return false;
            }
        });

        super.register(slimefun);
    }
}


