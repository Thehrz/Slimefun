package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.InvUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Misc.compatibles.ProtectionUtils;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunBlockHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.UnregisterReason;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.energy.ChargableBlock;
import me.mrCookieSlime.Slimefun.api.energy.EnergyTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.*;


public abstract class AGenerator
        extends SlimefunItem {
    public static final Map<Location, MachineFuel> processing = new HashMap<>();
    public static final Map<Location, Integer> progress = new HashMap<>();
    private static final int[] border = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 13, 31, 36, 37, 38, 39, 40, 41, 42, 43, 44};
    private static final int[] border_in = new int[]{9, 10, 11, 12, 18, 21, 27, 28, 29, 30};
    private static final int[] border_out = new int[]{14, 15, 16, 17, 23, 26, 32, 33, 34, 35};
    private final Set<MachineFuel> recipes = new HashSet<>();

    public AGenerator(Category category, ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, id, recipeType, recipe);

        new BlockMenuPreset(id, getInventoryTitle()) {
            public void init() {
                AGenerator.this.constructMenu(this);
            }


            public void newInstance(BlockMenu menu, Block b) {
            }


            public boolean canOpen(Block b, Player p) {
                boolean perm = (p.hasPermission("slimefun.inventory.bypass") || CSCoreLib.getLib().getProtectionManager().canAccessChest(p.getUniqueId(), b, true));
                return (perm && ProtectionUtils.canAccessItem(p, b));
            }


            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                if (flow.equals(ItemTransportFlow.INSERT)) return AGenerator.this.getInputSlots();
                return AGenerator.this.getOutputSlots();
            }
        };

        registerBlockHandler(id, new SlimefunBlockHandler() {
            public void onPlace(Player p, Block b, SlimefunItem item) {
            }


            public boolean onBreak(Player p, Block b, SlimefunItem item, UnregisterReason reason) {
                BlockMenu inv = BlockStorage.getInventory(b);
                if (inv != null) {
                    for (int slot : AGenerator.this.getInputSlots()) {
                        if (inv.getItemInSlot(slot) != null) {
                            b.getWorld().dropItemNaturally(b.getLocation(), inv.getItemInSlot(slot));
                            inv.replaceExistingItem(slot, null);
                        }
                    }
                    for (int slot : AGenerator.this.getOutputSlots()) {
                        if (inv.getItemInSlot(slot) != null) {
                            b.getWorld().dropItemNaturally(b.getLocation(), inv.getItemInSlot(slot));
                            inv.replaceExistingItem(slot, null);
                        }
                    }
                }
                AGenerator.progress.remove(b.getLocation());
                AGenerator.processing.remove(b.getLocation());
                return true;
            }
        });

        registerDefaultRecipes();
    }

    public AGenerator(Category category, ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe, ItemStack recipeOutput) {
        super(category, item, id, recipeType, recipe, recipeOutput);

        new BlockMenuPreset(id, getInventoryTitle()) {
            public void init() {
                AGenerator.this.constructMenu(this);
            }


            public void newInstance(BlockMenu menu, Block b) {
            }


            public boolean canOpen(Block b, Player p) {
                boolean perm = (p.hasPermission("slimefun.inventory.bypass") || CSCoreLib.getLib().getProtectionManager().canAccessChest(p.getUniqueId(), b, true));
                return (perm && ProtectionUtils.canAccessItem(p, b));
            }


            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                if (flow.equals(ItemTransportFlow.INSERT)) return AGenerator.this.getInputSlots();
                return AGenerator.this.getOutputSlots();
            }
        };

        registerBlockHandler(id, new SlimefunBlockHandler() {
            public void onPlace(Player p, Block b, SlimefunItem item) {
            }


            public boolean onBreak(Player p, Block b, SlimefunItem item, UnregisterReason reason) {
                BlockMenu inv = BlockStorage.getInventory(b);
                if (inv != null) {
                    for (int slot : AGenerator.this.getInputSlots()) {
                        if (inv.getItemInSlot(slot) != null) {
                            b.getWorld().dropItemNaturally(b.getLocation(), inv.getItemInSlot(slot));
                            inv.replaceExistingItem(slot, null);
                        }
                    }
                    for (int slot : AGenerator.this.getOutputSlots()) {
                        if (inv.getItemInSlot(slot) != null) {
                            b.getWorld().dropItemNaturally(b.getLocation(), inv.getItemInSlot(slot));
                            inv.replaceExistingItem(slot, null);
                        }
                    }
                }
                AGenerator.progress.remove(b.getLocation());
                AGenerator.processing.remove(b.getLocation());
                return true;
            }
        });

        registerDefaultRecipes();
    }


    private void constructMenu(BlockMenuPreset preset) {
        for (int i : border) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        }

        for (int i : border_in) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 9), " "), (arg0, arg1, arg2, arg3) -> false);
        }

        for (int i : border_out) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 1), " "), (arg0, arg1, arg2, arg3) -> false);
        }


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

        preset.addItem(22, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 15), " "), (arg0, arg1, arg2, arg3) -> false);
    }


    public int[] getInputSlots() {
        return new int[]{19, 20};
    }

    public int[] getOutputSlots() {
        return new int[]{24, 25};
    }

    public MachineFuel getProcessing(Location l) {
        return processing.get(l);
    }

    public boolean isProcessing(Location l) {
        return progress.containsKey(l);
    }

    public void registerFuel(MachineFuel fuel) {
        this.recipes.add(fuel);
    }


    @Override
    public void register(boolean slimefun) {
        addItemHandler(new EnergyTicker() {

            public double generateEnergy(Location l, SlimefunItem sf, Config data) {
                if (AGenerator.this.isProcessing(l)) {
                    int timeleft = AGenerator.progress.get(l);
                    if (timeleft > 0) {
                        ItemStack item = AGenerator.this.getProgressBar().clone();
                        item.setDurability(MachineHelper.getDurability(item, timeleft, AGenerator.processing.get(l).getTicks()));
                        ItemMeta im = item.getItemMeta();
                        im.setDisplayName(" ");
                        List<String> lore = new ArrayList<>();
                        lore.add(MachineHelper.getProgress(timeleft, AGenerator.processing.get(l).getTicks()));
                        lore.add("");
                        lore.add(MachineHelper.getTimeLeft(timeleft / 2));
                        im.setLore(lore);
                        item.setItemMeta(im);

                        BlockStorage.getInventory(l).replaceExistingItem(22, item);

                        if (ChargableBlock.isChargable(l)) {
                            if (ChargableBlock.getMaxCharge(l) - ChargableBlock.getCharge(l) >= AGenerator.this.getEnergyProduction()) {
                                ChargableBlock.addCharge(l, AGenerator.this.getEnergyProduction());
                                AGenerator.progress.put(l, timeleft - 1);
                                return ChargableBlock.getCharge(l);
                            }
                            return 0.0D;
                        }

                        AGenerator.progress.put(l, timeleft - 1);
                        return AGenerator.this.getEnergyProduction();
                    }


                    ItemStack fuel = AGenerator.processing.get(l).getInput();
                    if (SlimefunManager.isItemSimiliar(fuel, new ItemStack(Material.LAVA_BUCKET), true)) {
                        AGenerator.this.pushItems(l, new ItemStack[]{new ItemStack(Material.BUCKET)});
                    } else if (SlimefunManager.isItemSimiliar(fuel, SlimefunItems.BUCKET_OF_FUEL, true)) {
                        AGenerator.this.pushItems(l, new ItemStack[]{new ItemStack(Material.BUCKET)});
                    } else if (SlimefunManager.isItemSimiliar(fuel, SlimefunItems.BUCKET_OF_OIL, true)) {
                        AGenerator.this.pushItems(l, new ItemStack[]{new ItemStack(Material.BUCKET)});
                    }
                    BlockStorage.getInventory(l).replaceExistingItem(22, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 15), " "));

                    AGenerator.progress.remove(l);
                    AGenerator.processing.remove(l);
                    return 0.0D;
                }


                MachineFuel r = null;
                Map<Integer, Integer> found = new HashMap<>();

                label41:
                for (MachineFuel recipe : AGenerator.this.recipes) {
                    for (int slot : AGenerator.this.getInputSlots()) {
                        if (SlimefunManager.isItemSimiliar(BlockStorage.getInventory(l).getItemInSlot(slot), recipe.getInput(), true)) {
                            found.put(slot, recipe.getInput().getAmount());
                            r = recipe;

                            break label41;
                        }
                    }
                }
                if (r != null) {
                    for (Map.Entry<Integer, Integer> entry : found.entrySet()) {
                        BlockStorage.getInventory(l).replaceExistingItem(entry.getKey(), InvUtils.decreaseItem(BlockStorage.getInventory(l).getItemInSlot(entry.getKey()), entry.getValue()));
                    }
                    AGenerator.processing.put(l, r);
                    AGenerator.progress.put(l, r.getTicks());
                }
                return 0.0D;
            }


            public boolean explode(Location l) {
                return false;
            }
        });

        super.register(slimefun);
    }

    public Set<MachineFuel> getFuelTypes() {
        return this.recipes;
    }

    private Inventory inject(Location l) {
        int size = BlockStorage.getInventory(l).toInventory().getSize();
        Inventory inv = Bukkit.createInventory(null, size);
        for (int i = 0; i < size; i++) {
            inv.setItem(i, new CustomItem(Material.COMMAND, " &4ALL YOUR PLACEHOLDERS ARE BELONG TO US", 0));
        }
        for (int slot : getOutputSlots()) {
            inv.setItem(slot, BlockStorage.getInventory(l).getItemInSlot(slot));
        }
        return inv;
    }

    protected void pushItems(Location l, ItemStack[] items) {
        Inventory inv = inject(l);
        inv.addItem(items);

        for (int slot : getOutputSlots())
            BlockStorage.getInventory(l).replaceExistingItem(slot, inv.getItem(slot));
    }

    public abstract String getInventoryTitle();

    public abstract ItemStack getProgressBar();

    public abstract void registerDefaultRecipes();

    public abstract int getEnergyProduction();
}


