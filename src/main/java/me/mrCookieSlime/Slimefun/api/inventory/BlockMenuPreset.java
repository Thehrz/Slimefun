package me.mrCookieSlime.Slimefun.api.inventory;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class BlockMenuPreset extends ChestMenu {
    public static Map<String, BlockMenuPreset> presets = new HashMap<>();

    private final String title;
    private final Set<Integer> occupied = new HashSet<>();
    private final String id;
    private final boolean universal;
    private int size = -1;
    private ItemManipulationEvent event;

    public BlockMenuPreset(String id, String title) {
        super(title);
        this.id = id;
        this.title = title;
        init();
        this.universal = false;
        presets.put(id, this);
    }

    public BlockMenuPreset(String id, String title, boolean universal) {
        super(title);
        this.id = id;
        this.title = title;
        init();
        this.universal = universal;
        presets.put(id, this);
    }

    public static BlockMenuPreset getPreset(String id) {
        return presets.get(id);
    }

    public static boolean isInventory(String id) {
        return presets.containsKey(id);
    }

    public static boolean isUniversalInventory(String id) {
        return (presets.containsKey(id) && presets.get(id).isUniversal());
    }

    public void registerEvent(ItemManipulationEvent event) {
        this.event = event;
    }

    public abstract void init();

    public abstract void newInstance(BlockMenu paramBlockMenu, Block paramBlock);

    public int[] getSlotsAccessedByItemTransport(BlockMenu menu, ItemTransportFlow flow, ItemStack item) {
        return getSlotsAccessedByItemTransport(flow);
    }

    public abstract boolean canOpen(Block paramBlock, Player paramPlayer);

    public abstract int[] getSlotsAccessedByItemTransport(ItemTransportFlow paramItemTransportFlow);

    public int[] getSlotsAccessedByItemTransport(UniversalBlockMenu menu, ItemTransportFlow flow, ItemStack item) {
        return getSlotsAccessedByItemTransport(flow);
    }

    @Override
    public ChestMenu addItem(int slot, ItemStack item) {
        this.occupied.add(slot);
        return super.addItem(slot, item);
    }

    public ChestMenu setSize(int size) {
        this.size = size;
        return this;
    }

    public int getSize() {
        return this.size;
    }

    public String getTitle() {
        return this.title;
    }

    public Set<Integer> getPresetSlots() {
        return this.occupied;
    }

    public Set<Integer> getInventorySlots() {
        Set<Integer> empty = new HashSet<>();
        if (this.size > -1) {
            for (int i = 0; i < this.size; i++) {
                if (!this.occupied.contains(i)) {
                    empty.add(i);
                }

            }
        } else {
            for (int i = 0; i < toInventory().getSize(); i++) {
                if (!this.occupied.contains(i)) {
                    empty.add(i);
                }
            }
        }
        return empty;
    }

    public boolean isUniversal() {
        return this.universal;
    }

    public void clone(BlockMenu menu) {
        menu.setPlayerInventoryClickable(true);

        for (int i : this.occupied) {
            menu.addItem(i, getItemInSlot(i));
        }


        if (this.size > -1) {
            menu.addItem(this.size - 1, null);
        }

        newInstance(menu, menu.getLocation());
        for (int slot = 0; slot < 54; slot++) {
            if (getMenuClickHandler(slot) != null) {
                menu.addMenuClickHandler(slot, getMenuClickHandler(slot));
            }

        }
        menu.addMenuOpeningHandler(getMenuOpeningHandler());
        menu.addMenuCloseHandler(getMenuCloseHandler());
        menu.registerEvent(this.event);
    }

    public void clone(UniversalBlockMenu menu) {
        menu.setPlayerInventoryClickable(true);

        for (int i : this.occupied) {
            menu.addItem(i, getItemInSlot(i));
        }


        if (this.size > -1) {
            menu.addItem(this.size - 1, null);
        }
        for (int slot = 0; slot < 54; slot++) {
            if (getMenuClickHandler(slot) != null) {
                menu.addMenuClickHandler(slot, getMenuClickHandler(slot));
            }
        }
        menu.addMenuOpeningHandler(getMenuOpeningHandler());
        menu.addMenuCloseHandler(getMenuCloseHandler());
        menu.registerEvent(this.event);
    }

    public String getID() {
        return this.id;
    }

    public void newInstance(final BlockMenu menu, final Location l) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> newInstance(menu, l.getBlock()));
    }
}


