package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.machines;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.InvUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItemSerializer;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunBlockHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.UnregisterReason;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.energy.ChargableBlock;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import me.mrCookieSlime.Slimefun.api.item_transport.RecipeSorter;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.*;

public abstract class AutomatedCraftingChamber extends SlimefunItem {
    private static final int[] border;
    private static final int[] border_in;
    private static final int[] border_out;
    public static Map<String, ItemStack> recipes;

    static {
        border = new int[]{0, 1, 3, 4, 5, 7, 8, 13, 14, 15, 16, 17, 50, 51, 52, 53};
        border_in = new int[]{9, 10, 11, 12, 13, 18, 22, 27, 31, 36, 40, 45, 46, 47, 48, 49};
        border_out = new int[]{23, 24, 25, 26, 32, 35, 41, 42, 43, 44};
        AutomatedCraftingChamber.recipes = new HashMap<String, ItemStack>();
    }

    public AutomatedCraftingChamber(final Category category, final ItemStack item, final String name, final RecipeType recipeType, final ItemStack[] recipe) {
        super(category, item, name, recipeType, recipe);
        new BlockMenuPreset(name, "&6自动合成机") {
            @Override
            public void init() {
                AutomatedCraftingChamber.this.constructMenu(this);
            }

            @Override
            public void newInstance(final BlockMenu menu, final Block b) {
                if (!BlockStorage.hasBlockInfo(b) || BlockStorage.getLocationInfo(b.getLocation(), "enabled") == null || BlockStorage.getLocationInfo(b.getLocation(), "enabled").equals("false")) {
                    menu.replaceExistingItem(6, (ItemStack) new CustomItem(new MaterialData(Material.SULPHUR), "&7启动状态: &4✘", new String[]{"", "&e> 点击激活这个机器"}));
                    menu.addMenuClickHandler(6, (ChestMenu.MenuClickHandler) (p, arg1, arg2, arg3) -> {
                        BlockStorage.addBlockInfo(b, "enabled", "true");
                        newInstance(menu, b);
                        return false;
                    });
                } else {
                    menu.replaceExistingItem(6, (ItemStack) new CustomItem(new MaterialData(Material.REDSTONE), "&7启动状态: &2✔", new String[]{"", "&e> 点击停止这个机器"}));
                    menu.addMenuClickHandler(6, (ChestMenu.MenuClickHandler) (p, arg1, arg2, arg3) -> {
                        BlockStorage.addBlockInfo(b, "enabled", "false");
                        newInstance(menu, b);
                        return false;
                    });
                }
            }

            @Override
            public boolean canOpen(final Block b, final Player p) {
                return p.hasPermission("slimefun.inventory.bypass") || CSCoreLib.getLib().getProtectionManager().canAccessChest(p.getUniqueId(), b, true);
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(final ItemTransportFlow flow) {
                return new int[0];
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(final BlockMenu menu, final ItemTransportFlow flow, final ItemStack item) {
                if (flow.equals(ItemTransportFlow.WITHDRAW)) {
                    return AutomatedCraftingChamber.this.getOutputSlots();
                }
                final List<Integer> slots = new ArrayList<Integer>();
                for (final int slot : AutomatedCraftingChamber.this.getInputSlots()) {
                    if (menu.getItemInSlot(slot) != null) {
                        slots.add(slot);
                    }
                }
                Collections.sort(slots, new RecipeSorter(menu));
                return ArrayUtils.toPrimitive((Integer[]) slots.toArray(new Integer[slots.size()]));
            }
        };
        SlimefunItem.registerBlockHandler(name, new SlimefunBlockHandler() {
            @Override
            public void onPlace(final Player p, final Block b, final SlimefunItem item) {
                BlockStorage.addBlockInfo(b, "enabled", "false");
            }

            @Override
            public boolean onBreak(final Player p, final Block b, final SlimefunItem item, final UnregisterReason reason) {
                final BlockMenu inv = BlockStorage.getInventory(b);
                if (inv != null) {
                    for (final int slot : AutomatedCraftingChamber.this.getInputSlots()) {
                        if (inv.getItemInSlot(slot) != null) {
                            b.getWorld().dropItemNaturally(b.getLocation(), inv.getItemInSlot(slot));
                            inv.replaceExistingItem(slot, null);
                        }
                    }
                    for (final int slot : AutomatedCraftingChamber.this.getOutputSlots()) {
                        if (inv.getItemInSlot(slot) != null) {
                            b.getWorld().dropItemNaturally(b.getLocation(), inv.getItemInSlot(slot));
                            inv.replaceExistingItem(slot, null);
                        }
                    }
                }
                return true;
            }
        });
    }

    protected void constructMenu(final BlockMenuPreset preset) {
        for (final int i : AutomatedCraftingChamber.border) {
            preset.addItem(i, (ItemStack) new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " ", new String[0]), (ChestMenu.MenuClickHandler) (arg0, arg1, arg2, arg3) -> false);
        }
        for (final int i : AutomatedCraftingChamber.border_in) {
            preset.addItem(i, (ItemStack) new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 11), " ", new String[0]), (ChestMenu.MenuClickHandler) (arg0, arg1, arg2, arg3) -> false);
        }
        for (final int i : AutomatedCraftingChamber.border_out) {
            preset.addItem(i, (ItemStack) new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 1), " ", new String[0]), (ChestMenu.MenuClickHandler) (arg0, arg1, arg2, arg3) -> false);
        }
        for (final int i : this.getOutputSlots()) {
            preset.addMenuClickHandler(i, (ChestMenu.MenuClickHandler) new ChestMenu.AdvancedMenuClickHandler() {
                public boolean onClick(final Player p, final int slot, final ItemStack cursor, final ClickAction action) {
                    return false;
                }

                public boolean onClick(final InventoryClickEvent e, final Player p, final int slot, final ItemStack cursor, final ClickAction action) {
                    return cursor == null || cursor.getType() == null || cursor.getType() == Material.AIR;
                }
            });
        }
        preset.addItem(2, (ItemStack) new CustomItem(new MaterialData(Material.WORKBENCH), "&e合成蓝本", new String[]{"", "&b放入合成方式示例(按合成方式摆放)", "&4只能是强化合成台所属的合成公式"}), (ChestMenu.MenuClickHandler) (arg0, arg1, arg2, arg3) -> false);
    }

    public abstract int getEnergyConsumption();

    public int[] getInputSlots() {
        return new int[]{19, 20, 21, 28, 29, 30, 37, 38, 39};
    }

    public int[] getOutputSlots() {
        return new int[]{33, 34};
    }

    private Inventory inject(final Block b) {
        final int size = BlockStorage.getInventory(b).toInventory().getSize();
        final Inventory inv = Bukkit.createInventory((InventoryHolder) null, size);
        for (int i = 0; i < size; ++i) {
            inv.setItem(i, (ItemStack) new CustomItem(Material.COMMAND, " &4ALL YOUR PLACEHOLDERS ARE BELONG TO US", 0));
        }
        for (final int slot : this.getOutputSlots()) {
            inv.setItem(slot, BlockStorage.getInventory(b).getItemInSlot(slot));
        }
        return inv;
    }

    protected boolean fits(final Block b, final ItemStack[] items) {
        return this.inject(b).addItem(items).isEmpty();
    }

    protected void pushItems(final Block b, final ItemStack[] items) {
        final Inventory inv = this.inject(b);
        inv.addItem(items);
        for (final int slot : this.getOutputSlots()) {
            BlockStorage.getInventory(b).replaceExistingItem(slot, inv.getItem(slot));
        }
    }

    @Override
    public void register(final boolean slimefun) {
        this.addItemHandler(new BlockTicker() {
            @Override
            public void tick(final Block b, final SlimefunItem sf, final Config data) {
                AutomatedCraftingChamber.this.tick(b);
            }

            @Override
            public void uniqueTick() {
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });
        super.register(slimefun);
    }

    protected void tick(final Block b) {
        if (BlockStorage.getLocationInfo(b.getLocation(), "enabled").equals("false")) {
            return;
        }
        if (ChargableBlock.getCharge(b) < this.getEnergyConsumption()) {
            return;
        }
        final BlockMenu menu = BlockStorage.getInventory(b);
        final StringBuilder builder = new StringBuilder();
        int i = 0;
        for (int j = 0; j < 9; ++j) {
            if (i > 0) {
                builder.append(" </slot> ");
            }
            final ItemStack item = menu.getItemInSlot(this.getInputSlots()[j]);
            if (item != null && item.getAmount() == 1) {
                return;
            }
            builder.append(CustomItemSerializer.serialize(item, new CustomItemSerializer.ItemFlag[]{CustomItemSerializer.ItemFlag.DATA, CustomItemSerializer.ItemFlag.ITEMMETA_DISPLAY_NAME, CustomItemSerializer.ItemFlag.ITEMMETA_LORE, CustomItemSerializer.ItemFlag.MATERIAL}));
            ++i;
        }
        final String input = builder.toString();
        if (AutomatedCraftingChamber.recipes.containsKey(input)) {
            final ItemStack output = AutomatedCraftingChamber.recipes.get(input).clone();
            if (this.fits(b, new ItemStack[]{output})) {
                this.pushItems(b, new ItemStack[]{output});
                ChargableBlock.addCharge(b, -this.getEnergyConsumption());
                for (int k = 0; k < 9; ++k) {
                    if (menu.getItemInSlot(this.getInputSlots()[k]) != null) {
                        menu.replaceExistingItem(this.getInputSlots()[k], InvUtils.decreaseItem(menu.getItemInSlot(this.getInputSlots()[k]), 1));
                    }
                }
            }
        }
    }
}
