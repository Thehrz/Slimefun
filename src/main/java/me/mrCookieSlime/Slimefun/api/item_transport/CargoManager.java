package me.mrCookieSlime.Slimefun.api.item_transport;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.UniversalBlockMenu;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class CargoManager {
    private static final int[] slots = new int[]{19, 20, 21, 28, 29, 30, 37, 38, 39};

    public static ItemStack withdraw(Block node, BlockStorage storage, Block target, ItemStack template) {
        if (storage.hasUniversalInventory(target)) {
            UniversalBlockMenu menu = storage.getUniversalInventory(target);
            for (int slot : menu.getPreset().getSlotsAccessedByItemTransport(menu, ItemTransportFlow.WITHDRAW, null)) {
                ItemStack is = menu.getItemInSlot(slot);
                if (SlimefunManager.isItemSimiliar(is, template, true, SlimefunManager.DataType.ALWAYS) && matchesFilter(node, is, -1)) {
                    if (is.getAmount() > template.getAmount()) {
                        menu.replaceExistingItem(slot, new CustomItem(is, is.getAmount() - template.getAmount()));
                        return template;
                    }

                    menu.replaceExistingItem(slot, null);
                    return is.clone();
                }

            }

        } else if (storage.hasInventory(target.getLocation())) {
            BlockMenu menu = BlockStorage.getInventory(target.getLocation());
            for (int slot : menu.getPreset().getSlotsAccessedByItemTransport(menu, ItemTransportFlow.WITHDRAW, null)) {
                ItemStack is = menu.getItemInSlot(slot);
                if (SlimefunManager.isItemSimiliar(is, template, true, SlimefunManager.DataType.ALWAYS) && matchesFilter(node, is, -1)) {
                    if (is.getAmount() > template.getAmount()) {
                        menu.replaceExistingItem(slot, new CustomItem(is, is.getAmount() - template.getAmount()));
                        return template;
                    }

                    menu.replaceExistingItem(slot, null);
                    return is.clone();
                }

            }

        } else if (target.getState() instanceof InventoryHolder) {
            Inventory inv = ((InventoryHolder) target.getState()).getInventory();
            for (int slot = 0; slot < (inv.getContents()).length; slot++) {
                ItemStack is = inv.getContents()[slot];
                if (SlimefunManager.isItemSimiliar(is, template, true, SlimefunManager.DataType.ALWAYS) && matchesFilter(node, is, -1)) {
                    if (is.getAmount() > template.getAmount()) {
                        inv.setItem(slot, ChestManipulator.trigger(target, slot, is, new CustomItem(is, is.getAmount() - template.getAmount())));
                        return template;
                    }

                    inv.setItem(slot, ChestManipulator.trigger(target, slot, is, new CustomItem(is, is.getAmount() - template.getAmount())));
                    return is.clone();
                }
            }
        }

        return null;
    }

    public static ItemSlot withdraw(Block node, BlockStorage storage, Block target, int index) {
        if (storage.hasUniversalInventory(target)) {
            UniversalBlockMenu menu = storage.getUniversalInventory(target);
            for (int slot : menu.getPreset().getSlotsAccessedByItemTransport(menu, ItemTransportFlow.WITHDRAW, null)) {
                ItemStack is = menu.getItemInSlot(slot);
                if (matchesFilter(node, is, index)) {
                    menu.replaceExistingItem(slot, null);
                    return new ItemSlot(is.clone(), slot);
                }

            }
        } else if (storage.hasInventory(target.getLocation())) {
            BlockMenu menu = BlockStorage.getInventory(target.getLocation());
            for (int slot : menu.getPreset().getSlotsAccessedByItemTransport(menu, ItemTransportFlow.WITHDRAW, null)) {
                ItemStack is = menu.getItemInSlot(slot);
                if (matchesFilter(node, is, index)) {
                    menu.replaceExistingItem(slot, null);
                    return new ItemSlot(is.clone(), slot);
                }

            }
        } else if (target.getState() instanceof InventoryHolder) {
            Inventory inv = ((InventoryHolder) target.getState()).getInventory();
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
        if (storage.hasUniversalInventory(target)) {
            UniversalBlockMenu menu = storage.getUniversalInventory(target);
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
        } else if (storage.hasInventory(target.getLocation())) {
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
        } else if (target.getState() instanceof InventoryHolder) {
            Inventory inv = ((InventoryHolder) target.getState()).getInventory();

            for (int slot = 0; slot < (inv.getContents()).length; slot++) {
                ItemStack is = inv.getContents()[slot];
                if (is == null) {
                    inv.setItem(slot, ChestManipulator.trigger(target, slot, null, stack.clone()));
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
        if (item == null) return false;

        String id = BlockStorage.checkID(block);
        if (id.equals("CARGO_NODE_OUTPUT")) return true;

        Config blockInfo = BlockStorage.getLocationInfo(block.getLocation());

        BlockMenu menu = BlockStorage.getInventory(block.getLocation());
        boolean lore = blockInfo.getString("filter-lore").equals("true");
        boolean data = blockInfo.getString("filter-durability").equals("true");

        if (blockInfo.getString("filter-type").equals("whitelist")) {
            List<ItemStack> items = new ArrayList<>();
            for (int slot : slots) {
                ItemStack template = menu.getItemInSlot(slot);
                if (template != null) items.add(new CustomItem(template, 1));

            }
            if (items.isEmpty()) {
                return false;
            }

            if (index >= 0) {
                index++;
                if (index > items.size() - 1) index = 0;

                BlockStorage.addBlockInfo(block, "index", String.valueOf(index));

                return SlimefunManager.isItemSimiliar(item, items.get(index), lore, data ? SlimefunManager.DataType.ALWAYS : SlimefunManager.DataType.NEVER);
            }

            for (ItemStack stack : items) {
                if (SlimefunManager.isItemSimiliar(item, stack, lore, data ? SlimefunManager.DataType.ALWAYS : SlimefunManager.DataType.NEVER))
                    return true;
            }
            return false;
        }


        for (int slot : slots) {
            if (menu.getItemInSlot(slot) != null && SlimefunManager.isItemSimiliar(item, new CustomItem(menu.getItemInSlot(slot), 1), lore, data ? SlimefunManager.DataType.ALWAYS : SlimefunManager.DataType.NEVER)) {
                return false;
            }
        }
        return true;
    }
}


