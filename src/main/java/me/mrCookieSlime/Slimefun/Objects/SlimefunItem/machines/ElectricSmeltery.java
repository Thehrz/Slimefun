package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.machines;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunBlockHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.UnregisterReason;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import me.mrCookieSlime.Slimefun.api.item_transport.RecipeSorter;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.*;


public abstract class ElectricSmeltery
        extends AContainer {
    public static final Map<Block, MachineRecipe> processing = new HashMap<>();
    public static final Map<Block, Integer> progress = new HashMap<>();
    private static final int[] border = new int[]{4, 5, 6, 7, 8, 13, 31, 40, 41, 42, 43, 44};
    private static final int[] border_in = new int[]{0, 1, 2, 3, 9, 12, 18, 21, 27, 30, 36, 37, 38, 39};
    private static final int[] border_out = new int[]{14, 15, 16, 17, 23, 26, 32, 33, 34, 35};
    protected List<MachineRecipe> recipes = new ArrayList<>();

    public ElectricSmeltery(Category category, ItemStack item, String name, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, name, recipeType, recipe);

        new BlockMenuPreset(name, getInventoryTitle()) {
            public void init() {
                ElectricSmeltery.this.constructMenu(this);
            }


            public void newInstance(BlockMenu menu, Block b) {
            }


            public boolean canOpen(Block b, Player p) {
                return (p.hasPermission("slimefun.inventory.bypass") || CSCoreLib.getLib().getProtectionManager().canAccessChest(p.getUniqueId(), b, true));
            }


            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }


            public int[] getSlotsAccessedByItemTransport(BlockMenu menu, ItemTransportFlow flow, ItemStack item) {
                if (flow.equals(ItemTransportFlow.WITHDRAW)) return ElectricSmeltery.this.getOutputSlots();

                List<Integer> slots = new ArrayList<>();

                for (int slot : ElectricSmeltery.this.getInputSlots()) {
                    if (SlimefunManager.isItemSimiliar(menu.getItemInSlot(slot), item, true)) {
                        slots.add(slot);
                    }
                }

                if (slots.isEmpty()) {
                    return ElectricSmeltery.this.getInputSlots();
                }

                Collections.sort(slots, new RecipeSorter(menu));
                return ArrayUtils.toPrimitive(slots.<Integer>toArray(new Integer[slots.size()]));
            }
        };


        registerBlockHandler(name, new SlimefunBlockHandler() {
            public void onPlace(Player p, Block b, SlimefunItem item) {
            }


            public boolean onBreak(Player p, Block b, SlimefunItem item, UnregisterReason reason) {
                BlockMenu inv = BlockStorage.getInventory(b);
                if (inv != null) {
                    for (int slot : ElectricSmeltery.this.getInputSlots()) {
                        if (inv.getItemInSlot(slot) != null) {
                            b.getWorld().dropItemNaturally(b.getLocation(), inv.getItemInSlot(slot));
                            inv.replaceExistingItem(slot, null);
                        }
                    }
                    for (int slot : ElectricSmeltery.this.getOutputSlots()) {
                        if (inv.getItemInSlot(slot) != null) {
                            b.getWorld().dropItemNaturally(b.getLocation(), inv.getItemInSlot(slot));
                            inv.replaceExistingItem(slot, null);
                        }
                    }
                }
                ElectricSmeltery.progress.remove(b.getLocation());
                ElectricSmeltery.processing.remove(b.getLocation());
                return true;
            }
        });

        registerDefaultRecipes();
    }


    protected void constructMenu(BlockMenuPreset preset) {
        for (int i : border) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        }

        for (int i : border_in) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 9), " "), (arg0, arg1, arg2, arg3) -> false);
        }

        for (int i : border_out) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 1), " "), (arg0, arg1, arg2, arg3) -> false);
        }


        preset.addItem(22, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 15), " "), (arg0, arg1, arg2, arg3) -> false);


        for (int i : getOutputSlots()) {
            preset.addMenuClickHandler(i, new ChestMenu.AdvancedMenuClickHandler() {
                public boolean onClick(Player p, int slot, ItemStack cursor, ClickAction action) {
                    return false;
                }


                public boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor, ClickAction action) {
                    return (cursor == null || cursor.getType() == null || cursor.getType() == Material.AIR);
                }
            });
        }
    }


    public String getInventoryTitle() {
        return "&c电力冶炼机";
    }


    public ItemStack getProgressBar() {
        return new ItemStack(Material.FLINT_AND_STEEL);
    }


    public int[] getInputSlots() {
        return new int[]{10, 11, 19, 20, 28, 29};
    }


    public int[] getOutputSlots() {
        return new int[]{24, 25};
    }


    public String getMachineIdentifier() {
        return "ELECTRIC_SMELTERY";
    }
}


