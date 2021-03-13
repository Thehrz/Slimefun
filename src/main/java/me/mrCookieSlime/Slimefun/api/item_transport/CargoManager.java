package me.mrCookieSlime.Slimefun.api.item_transport;

import io.papermc.lib.PaperLib;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CargoManager {
    private static final int[] SLOTS = new int[]{19, 20, 21, 28, 29, 30, 37, 38, 39};

    public static ItemSlot withdraw(Block node, BlockStorage storage, Block target, int index) {
        if (storage.hasInventory(target.getLocation())) {
            BlockMenu menu = BlockStorage.getInventory(target.getLocation());
            for (int slot : menu.getPreset().getSlotsAccessedByItemTransport(menu, ItemTransportFlow.WITHDRAW, null)) {
                ItemStack is = menu.getItemInSlot(slot);
                if (matchesFilter(node, is, index)) {
                    menu.replaceExistingItem(slot, null);
                    return new ItemSlot(is.clone(), slot);
                }
            }
            return null;
        }
        BlockState blockState = PaperLib.getBlockState(target, false).getState();
        if (blockState instanceof InventoryHolder) {
            Inventory inv = ((InventoryHolder) blockState).getInventory();
            for (int slot = 0; slot < (inv.getContents()).length; slot++) {
                ItemStack is = inv.getContents()[slot];
                if (matchesFilter(node, is, index)) {
                    inv.setItem(slot, ChestManipulator.trigger(target, slot, is, null));
                    return new ItemSlot(is.clone(), slot);
                }
            }
        }
        return null;
    }

    public static ItemStack insert(Block node, BlockStorage storage, Block target, ItemStack stack, int index) {
        if (!matchesFilter(node, stack, index)) {
            return stack;
        }
        if (storage.hasInventory(target.getLocation())) {
            BlockMenu menu = BlockStorage.getInventory(target.getLocation());
            for (int slot : menu.getPreset().getSlotsAccessedByItemTransport(menu, ItemTransportFlow.INSERT, stack)) {
                ItemStack is = (menu.getItemInSlot(slot) == null) ? null : menu.getItemInSlot(slot).clone();
                if (is == null) {
                    menu.replaceExistingItem(slot, stack.clone());
                    return null;
                }
                if (SlimefunManager.isItemSimiliar(new CustomItem(is, 1), new CustomItem(stack, 1), true, SlimefunManager.DataType.ALWAYS) && is.getAmount() < is.getType().getMaxStackSize()) {
                    int amount = is.getAmount() + stack.getAmount();

                    if (amount > is.getType().getMaxStackSize()) {
                        is.setAmount(is.getType().getMaxStackSize());
                        stack.setAmount(amount - is.getType().getMaxStackSize());
                    } else {

                        is.setAmount(amount);
                        stack = null;
                    }

                    menu.replaceExistingItem(slot, is);
                    return stack;
                }

            }
            return stack;
        }
        BlockState blockState = PaperLib.getBlockState(target, false).getState();
        if (blockState instanceof InventoryHolder) {
            Inventory inv = ((InventoryHolder) blockState).getInventory();
            for (int slot = 0; slot < (inv.getContents()).length; slot++) {
                ItemStack is = inv.getContents()[slot];
                if (slot > 1 && blockState instanceof Furnace) {
                    return stack;
                }
                if (is == null) {
                    inv.setItem(slot, stack.clone());
                    return null;
                }
                if (SlimefunManager.isItemSimiliar(new CustomItem(is, 1), new CustomItem(stack, 1), true, SlimefunManager.DataType.ALWAYS) && is.getAmount() < is.getType().getMaxStackSize()) {
                    ItemStack prev = is.clone();
                    int amount = is.getAmount() + stack.getAmount();

                    if (amount > is.getType().getMaxStackSize()) {
                        is.setAmount(is.getType().getMaxStackSize());
                        stack.setAmount(amount - is.getType().getMaxStackSize());
                    } else {

                        is.setAmount(amount);
                        stack = null;
                    }

                    inv.setItem(slot, ChestManipulator.trigger(target, slot, prev, is));
                    return stack;
                }
            }
        }

        return stack;
    }

    public static boolean matchesFilter(Block block, ItemStack item, int index) {
        if (item == null) {
            return false;
        }
        String id = BlockStorage.checkID(block);
        if ("CARGO_NODE_OUTPUT".equals(id)) {
            return true;
        }

        Config blockInfo = BlockStorage.getLocationInfo(block.getLocation());


        BlockMenu menu = BlockStorage.getInventory(block.getLocation());
        boolean lore = "true".equals(blockInfo.getString("filter-lore"));
        boolean data = "true".equals(blockInfo.getString("filter-durability"));

        if ("whitelist".equals(blockInfo.getString("filter-type"))) {
            List<ItemStack> items = new ArrayList<>();
            for (int slot : SLOTS) {
                ItemStack template = menu.getItemInSlot(slot);
                if (template != null) {
                    items.add(new CustomItem(template, 1));
                }

            }
            if (items.isEmpty()) {
                return false;
            }

            if (index >= 0) {
                index++;
                if (index > items.size() - 1) {
                    index = 0;
                }

                BlockStorage.addBlockInfo(block, "index", String.valueOf(index));

                return SlimefunManager.isItemSimiliar(item, items.get(index), lore, data ? SlimefunManager.DataType.ALWAYS : SlimefunManager.DataType.NEVER);
            }

            for (ItemStack stack : items) {
                if (SlimefunManager.isItemSimiliar(item, stack, lore, data ? SlimefunManager.DataType.ALWAYS : SlimefunManager.DataType.NEVER)) {
                    return true;
                }
            }
            return false;
        }

        for (int slot : SLOTS) {
            if (menu.getItemInSlot(slot) != null && SlimefunManager.isItemSimiliar(item, new CustomItem(menu.getItemInSlot(slot), 1), lore, data ? SlimefunManager.DataType.ALWAYS : SlimefunManager.DataType.NEVER)) {
                return false;
            }
        }
        return true;
    }
}


