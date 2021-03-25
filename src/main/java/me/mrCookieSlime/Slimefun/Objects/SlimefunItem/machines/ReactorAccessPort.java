package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.machines;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunBlockHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.UnregisterReason;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Iterator;
import java.util.Map;

public class ReactorAccessPort
        extends SlimefunItem {
    private static final int[] border = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 12, 13, 14, 21, 23};
    private static final int[] border_1 = new int[]{9, 10, 11, 18, 20, 27, 29, 36, 38, 45, 46, 47};
    private static final int[] border_2 = new int[]{15, 16, 17, 24, 26, 33, 35, 42, 44, 51, 52, 53};
    private static final int[] border_3 = new int[]{30, 31, 32, 39, 41, 48, 49, 50};

    public ReactorAccessPort(Category category, ItemStack item, String name, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, name, recipeType, recipe);

        new BlockMenuPreset(name, getInventoryTitle()) {
            @Override
            public void init() {
                ReactorAccessPort.this.constructMenu(this);
            }


            @Override
            public void newInstance(BlockMenu menu, Block b) {
            }


            @Override
            public boolean canOpen(Block b, Player p) {
                return (p.hasPermission("slimefun.inventory.bypass") || CSCoreLib.getLib().getProtectionManager().canAccessChest(p.getUniqueId(), b, true));
            }


            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                if (flow.equals(ItemTransportFlow.INSERT)) return ReactorAccessPort.this.getInputSlots();
                return ReactorAccessPort.getOutputSlots();
            }


            @Override
            public int[] getSlotsAccessedByItemTransport(BlockMenu menu, ItemTransportFlow flow, ItemStack item) {
                if (flow.equals(ItemTransportFlow.INSERT)) {
                    if (SlimefunManager.isItemSimiliar(item, SlimefunItems.REACTOR_COOLANT_CELL, true))
                        return ReactorAccessPort.this.getCoolantSlots();
                    return ReactorAccessPort.this.getFuelSlots();
                }
                return ReactorAccessPort.getOutputSlots();
            }
        };

        registerBlockHandler(name, new SlimefunBlockHandler() {
            @Override
            public void onPlace(Player p, Block b, SlimefunItem item) {
            }


            @Override
            public boolean onBreak(Player p, Block b, SlimefunItem item, UnregisterReason reason) {
                BlockMenu inv = BlockStorage.getInventory(b);
                if (inv != null) {
                    for (int slot : ReactorAccessPort.this.getFuelSlots()) {
                        if (inv.getItemInSlot(slot) != null) {
                            b.getWorld().dropItemNaturally(b.getLocation(), inv.getItemInSlot(slot));
                            inv.replaceExistingItem(slot, null);
                        }
                    }
                    for (int slot : ReactorAccessPort.this.getCoolantSlots()) {
                        if (inv.getItemInSlot(slot) != null) {
                            b.getWorld().dropItemNaturally(b.getLocation(), inv.getItemInSlot(slot));
                            inv.replaceExistingItem(slot, null);
                        }
                    }
                    for (int slot : ReactorAccessPort.getOutputSlots()) {
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

    public static int[] getOutputSlots() {
        return new int[]{40};
    }

    private static Inventory inject(Location l) {
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

    public static ItemStack pushItems(Location l, ItemStack item) {
        Inventory inv = inject(l);
        Map<Integer, ItemStack> map = inv.addItem(item);

        for (int slot : getOutputSlots()) {
            BlockStorage.getInventory(l).replaceExistingItem(slot, inv.getItem(slot));
        }

        Iterator<Map.Entry<Integer, ItemStack>> iterator = map.entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<Integer, ItemStack> entry = iterator.next();
            return entry.getValue();
        }

        return null;
    }

    private void constructMenu(BlockMenuPreset preset) {
        for (int i : border) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        }


        for (int i : border_1) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 5), " "), (arg0, arg1, arg2, arg3) -> false);
        }


        for (int i : border_2) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 9), " "), (arg0, arg1, arg2, arg3) -> false);
        }


        for (int i : border_3) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 13), " "), (arg0, arg1, arg2, arg3) -> false);
        }


        preset.addItem(1, new CustomItem(SlimefunItems.URANIUM, "&7燃料槽", "", "&r这个燃料槽可以放置放射性燃料:", "&2铀 &r或 &a镎"), (arg0, arg1, arg2, arg3) -> false);


        preset.addItem(22, new CustomItem(SlimefunItems.PLUTONIUM, "&7副产品槽", "", "&r这个槽位将收集放射性副产品", "&r例如 &a镎 &r或 &7钚"), (arg0, arg1, arg2, arg3) -> false);


        preset.addItem(7, new CustomItem(SlimefunItems.REACTOR_COOLANT_CELL, "&b冷却槽", "", "&r这个槽位用于放置冷却单元", "&4如果你任性地不放冷却单元", "&4那么你的反应堆就会BOOOOOM"), (arg0, arg1, arg2, arg3) -> false);


        preset.addItem(7, new CustomItem(SlimefunItems.REACTOR_COOLANT_CELL, "&b冷却槽", "", "&r这个槽位用于放置冷却单元", "&4如果你任性地不放冷却单元", "&4那么你的反应堆就会BOOOOOM"), (arg0, arg1, arg2, arg3) -> false);
    }

    public String getInventoryTitle() {
        return "&2反应堆交互接口";
    }

    public int[] getInputSlots() {
        return new int[]{19, 28, 37, 25, 34, 43};
    }

    public int[] getFuelSlots() {
        return new int[]{19, 28, 37};
    }

    public int[] getCoolantSlots() {
        return new int[]{25, 34, 43};
    }
}


