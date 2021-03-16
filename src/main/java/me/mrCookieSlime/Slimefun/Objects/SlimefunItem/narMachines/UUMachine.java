package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.narMachines;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.InvUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Misc.compatibles.ProtectionUtils;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunBlockHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.UnregisterReason;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineHelper;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.container.UURecipe;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.energy.ChargableBlock;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class UUMachine extends SlimefunItem {
    public static final Map<Block, UURecipe> processing = new HashMap<>();
    public static final Map<Block, Integer> progress = new HashMap<>();
    private static final int[] uuBorder = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
    private static final int[] uuInfo = new int[]{10, 11, 12, 13, 14, 15, 16};
    private static final int[] border = new int[]{27, 30, 31, 32, 35, 36, 39, 41, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
    private static final int[] inputSign = new int[]{28, 29};
    private static final int[] outputSign = new int[]{33, 34};
    private static final ItemStack uuItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 10);
    protected final List<UURecipe> recipes = new ArrayList<>();

    public UUMachine(Category category, ItemStack item, String name, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, name, recipeType, recipe);

        new BlockMenuPreset(name, getInventoryTitle()) {

            public void init() {
                UUMachine.this.constructMenu(this);
            }


            public void newInstance(BlockMenu menu, Block b) {
            }


            public boolean canOpen(Block b, Player p) {
                boolean perm = (p.hasPermission("slimefun.inventory.bypass") || CSCoreLib.getLib().getProtectionManager().canAccessChest(p.getUniqueId(), b, true));
                return (perm && ProtectionUtils.canAccessItem(p, b));
            }


            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                if (flow.equals(ItemTransportFlow.INSERT)) {
                    return UUMachine.this.getInputSlots();
                }
                return UUMachine.this.getOutputSlots();
            }
        };
        registerBlockHandler(name, new SlimefunBlockHandler() {
            public void onPlace(Player p, Block b, SlimefunItem item) {
            }


            public boolean onBreak(Player p, Block b, SlimefunItem item, UnregisterReason reason) {
                BlockMenu inv = BlockStorage.getInventory(b);
                if (inv != null) {

                    for (int slot : UUMachine.this.getInputSlots()) {
                        if (inv.getItemInSlot(slot) != null) {
                            b.getWorld().dropItemNaturally(b.getLocation(), inv.getItemInSlot(slot));
                        }
                    }
                    for (int slot : UUMachine.this.getOutputSlots()) {
                        if (inv.getItemInSlot(slot) != null) {
                            b.getWorld().dropItemNaturally(b.getLocation(), inv.getItemInSlot(slot));
                        }
                    }
                }
                UUMachine.progress.remove(b);
                UUMachine.processing.remove(b);
                return true;
            }
        });
        registerDefaultRecipes();
    }

    public UUMachine(Category category, ItemStack item, String name, RecipeType recipeType, ItemStack[] recipe, ItemStack recipeOutput) {
        super(category, item, name, recipeType, recipe, recipeOutput);

        new BlockMenuPreset(name, getInventoryTitle()) {

            public void init() {
                UUMachine.this.constructMenu(this);
            }


            public void newInstance(BlockMenu menu, Block b) {
            }


            public boolean canOpen(Block b, Player p) {
                boolean perm = (p.hasPermission("slimefun.inventory.bypass") || CSCoreLib.getLib().getProtectionManager().canAccessChest(p.getUniqueId(), b, true));
                return (perm && ProtectionUtils.canAccessItem(p, b));
            }


            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                if (flow.equals(ItemTransportFlow.INSERT)) {
                    return UUMachine.this.getInputSlots();
                }
                return UUMachine.this.getOutputSlots();
            }
        };
        registerBlockHandler(name, new SlimefunBlockHandler() {
            public void onPlace(Player p, Block b, SlimefunItem item) {
            }


            public boolean onBreak(Player p, Block b, SlimefunItem item, UnregisterReason reason) {
                for (int slot : UUMachine.this.getInputSlots()) {
                    if (BlockStorage.getInventory(b).getItemInSlot(slot) != null) {
                        b.getWorld().dropItemNaturally(b.getLocation(), BlockStorage.getInventory(b).getItemInSlot(slot));
                    }
                }
                for (int slot : UUMachine.this.getOutputSlots()) {
                    if (BlockStorage.getInventory(b).getItemInSlot(slot) != null) {
                        b.getWorld().dropItemNaturally(b.getLocation(), BlockStorage.getInventory(b).getItemInSlot(slot));
                    }
                }
                UUMachine.processing.remove(b);
                UUMachine.progress.remove(b);
                return true;
            }
        });
        registerDefaultRecipes();
    }

    private void constructMenu(BlockMenuPreset preset) {
        for (int i : uuBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), " "), (player, i14, itemStack, clickAction) -> false);
        }
        for (int i : border) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), " "), (player, i13, itemStack, clickAction) -> false);
        }
        for (int i : outputSign) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11), "§b输出槽"), (player, i12, itemStack, clickAction) -> false);
        }
        for (int i : inputSign) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4), "§e输入槽"), (player, i1, itemStack, clickAction) -> false);
        }

        preset.addItem(40, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), " "), (player, i, itemStack, clickAction) -> false);

        for (int i : getOutputSlots()) {
            preset.addItem(i, null, new ChestMenu.AdvancedMenuClickHandler() {
                public boolean onClick(Player player, int i, ItemStack item, ClickAction action) {
                    return false;
                }


                public boolean onClick(InventoryClickEvent event, Player player, int slot, ItemStack item, ClickAction action) {
                    return (item == null || item.getType() == null || item.getType() == Material.AIR);
                }
            });
        }

        for (int i : uuInfo) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), " "), new ChestMenu.AdvancedMenuClickHandler() {
                public boolean onClick(InventoryClickEvent inventoryClickEvent, Player player, int i, ItemStack itemStack, ClickAction clickAction) {
                    return false;
                }


                public boolean onClick(Player player, int i, ItemStack itemStack, ClickAction clickAction) {
                    return false;
                }
            });
        }
    }

    public int[] getInputSlots() {
        return new int[]{37, 38};
    }

    public int[] getOutputSlots() {
        return new int[]{42, 43};
    }


    public UURecipe getProcessing(Block b) {
        return processing.get(b);
    }


    public boolean isProcessing(Block b) {
        return (getProcessing(b) != null);
    }


    public void registerRecipe(UURecipe recipe) {
        recipe.setTicks(recipe.getTicks());
        this.recipes.add(recipe);
    }

    public void registerRecipe(int seconds, ItemStack[] input, int uuAmount) {
        registerRecipe(new UURecipe(seconds, input, uuAmount));
    }

    public List<UURecipe> getUURecipes() {
        return this.recipes;
    }


    private Inventory inject(Block b) {
        int size = BlockStorage.getInventory(b).toInventory().getSize();
        Inventory inv = Bukkit.createInventory(null, size);
        for (int i = 0; i < size; i++) {
            inv.setItem(i, new CustomItem(Material.COMMAND, " &4ALL YOUR PLACEHOLDERS ARE BELONG TO US", 0));
        }
        for (int slot : getOutputSlots()) {
            inv.setItem(slot, BlockStorage.getInventory(b).getItemInSlot(slot));
        }
        return inv;
    }


    protected boolean fits(Block b, ItemStack[] items) {
        return inject(b).addItem(items).isEmpty();
    }


    protected void pushMainItems(Block b, ItemStack[] items, int uu) {
        if (BlockStorage.getBlockInfo(b, "uuAmount") == null) {
            BlockStorage.addBlockInfo(b, "uuAmount", "0", false);
        } else {
            BlockStorage.addBlockInfo(b, "uuAmount", String.valueOf(uu + Integer.valueOf(BlockStorage.getBlockInfo(b, "uuAmount"))), false);
        }
        if (Integer.valueOf(BlockStorage.getBlockInfo(b, "uuAmount")) >= getUUFull()) {
            BlockStorage.addBlockInfo(b, "uuAmount", "0", false);
            Inventory inv = inject(b);
            inv.addItem(items);
            for (int slot : getOutputSlots()) {
                BlockStorage.getInventory(b).replaceExistingItem(slot, inv.getItem(slot));
            }
        }
        int amount = Integer.valueOf(BlockStorage.getBlockInfo(b, "uuAmount"));
        for (int i : uuInfo) {
            if ((i - uuInfo[0]) * getUUFull() / 7 <= amount) {
                BlockStorage.getInventory(b).replaceExistingItem(i, new CustomItem(uuItem, "§7元物质量: §d" + amount + "§7/§c100000"));
            } else {
                BlockStorage.getInventory(b).replaceExistingItem(i, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), " "));
            }
        }
    }


    public void register(boolean slimefun) {
        addItemHandler(new BlockTicker() {

            public void tick(Block b, SlimefunItem sf, Config data) {
                UUMachine.this.tick(b);
            }


            public void uniqueTick() {
            }


            public boolean isSynchronized() {
                return false;
            }
        });
        super.register(slimefun);
    }


    protected void tick(Block b) {
        if (isProcessing(b)) {
            int timeleft = progress.get(b);
            if (timeleft > 0) {
                ItemStack item = getProgressBar().clone();
                item.setDurability(MachineHelper.getDurability(item, timeleft, processing.get(b).getTicks()));
                ItemMeta im = item.getItemMeta();
                im.setDisplayName(" ");
                List<String> lore = new ArrayList<>();
                lore.add(MachineHelper.getProgress(timeleft, processing.get(b).getTicks()));
                lore.add("");
                lore.add(MachineHelper.getTimeLeft(timeleft / 2));
                im.setLore(lore);
                item.setItemMeta(im);

                BlockStorage.getInventory(b).replaceExistingItem(40, item);
                if (ChargableBlock.isChargable(b)) {

                    if (ChargableBlock.getCharge(b) < getEnergyConsumption()) {
                        return;
                    }
                    ChargableBlock.addCharge(b, -getEnergyConsumption());
                    progress.put(b, timeleft - 1);
                } else {

                    progress.put(b, timeleft - 1);
                }
            } else {

                BlockStorage.getInventory(b).replaceExistingItem(40, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), " "));
                pushMainItems(b, getOutput(), processing.get(b).getUUAmount());
                progress.remove(b);
                processing.remove(b);
            }

        } else {

            UURecipe r = null;
            Map<Integer, Integer> found = new HashMap<>();
            for (UURecipe recipe : this.recipes) {

                for (ItemStack input : recipe.getInput()) {
                    for (int slot : getInputSlots()) {
                        if (SlimefunManager.isItemSimiliar(BlockStorage.getInventory(b).getItemInSlot(slot), input, true)) {
                            if (input != null) {
                                found.put(slot, input.getAmount());
                            }
                        }
                    }
                }

                if (found.size() == (recipe.getInput()).length) {

                    r = recipe;
                    break;
                }
                found.clear();
            }
            if (r != null) {

                if (!fits(b, getOutput())) {
                    return;
                }
                for (Map.Entry<Integer, Integer> entry : found.entrySet()) {
                    BlockStorage.getInventory(b).replaceExistingItem(entry.getKey(), InvUtils.decreaseItem(BlockStorage.getInventory(b).getItemInSlot(entry.getKey()), entry.getValue()));
                }
                processing.put(b, r);
                progress.put(b, r.getTicks());
            }
        }
    }

    public abstract int getUUFull();

    public abstract ItemStack[] getOutput();

    public abstract String getInventoryTitle();

    public abstract ItemStack getProgressBar();

    public abstract void registerDefaultRecipes();

    public abstract int getEnergyConsumption();

    public abstract int getLevel();

    public abstract String getMachineIdentifier();
}


