package me.mrCookieSlime.Slimefun.api.inventory;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;

public class BlockMenu extends ChestMenu {
    final BlockMenuPreset preset;
    public int changes;
    Location l;
    private ItemManipulationEvent event;

    public BlockMenu(final BlockMenuPreset preset, final Location l) {
        super(preset.getTitle());
        this.changes = 0;
        this.preset = preset;
        this.l = l;
        this.changes = 1;
        preset.clone(this);
        this.getContents();
    }

    public BlockMenu(final BlockMenuPreset preset, final Location l, final Config cfg) {
        super(preset.getTitle());
        this.changes = 0;
        this.preset = preset;
        this.l = l;
        for (int i = 0; i < 54; ++i) {
            if (cfg.contains(String.valueOf(i))) {
                this.addItem(i, cfg.getItem(String.valueOf(i)));
            }
        }
        preset.clone(this);
        if (preset.getSize() > -1 && !preset.getPresetSlots().contains(preset.getSize() - 1) && cfg.contains(String.valueOf(preset.getSize() - 1))) {
            this.addItem(preset.getSize() - 1, cfg.getItem(String.valueOf(preset.getSize() - 1)));
        }
        this.getContents();
    }

    private static String serializeLocation(final Location l) {
        return l.getWorld().getName() + ";" + l.getBlockX() + ";" + l.getBlockY() + ";" + l.getBlockZ();
    }

    public void registerEvent(final ItemManipulationEvent event) {
        this.event = event;
    }

    public void save(final Location l) {
        if (this.changes == 0) {
            return;
        }
        this.getContents();
        final File file = new File("data-storage/Slimefun/stored-inventories/" + serializeLocation(l) + ".sfi");
        final Config cfg = new Config(file);
        cfg.setValue("preset", this.preset.getID());
        for (final int slot : this.preset.getInventorySlots()) {
            cfg.setValue(String.valueOf(slot), this.getItemInSlot(slot));
        }
        cfg.save();
        this.changes = 0;
    }

    @Deprecated
    public void move(final Block b) {
        this.move(b.getLocation());
    }

    public void move(final Location l) {
        this.delete(this.l);
        this.l = l;
        this.preset.newInstance(this, l);
        this.save(l);
    }

    public Block getBlock() {
        return this.l.getBlock();
    }

    public Location getLocation() {
        return this.l;
    }

    public void delete(final Location l) {
        new File("data-storage/Slimefun/stored-inventories/" + serializeLocation(l) + ".sfi").delete();
    }

    public BlockMenuPreset getPreset() {
        return this.preset;
    }

    public boolean canOpen(final Block b, final Player p) {
        return this.preset.canOpen(b, p);
    }

    public void replaceExistingItem(final int slot, final ItemStack item) {
        this.replaceExistingItem(slot, item, true);
    }

    public void replaceExistingItem(final int slot, ItemStack item, final boolean event) {
        final ItemStack previous = this.getItemInSlot(slot);
        if (event && this.event != null) {
            item = this.event.onEvent(slot, previous, item);
        }
        super.replaceExistingItem(slot, item);
        ++this.changes;
    }

    public ChestMenu addMenuOpeningHandler(final ChestMenu.MenuOpeningHandler handler) {
        if (handler instanceof SaveHandler) {
            return super.addMenuOpeningHandler(new SaveHandler(this, ((SaveHandler) handler).handler));
        }
        return super.addMenuOpeningHandler(new SaveHandler(this, handler));
    }

    public void close() {
        for (final HumanEntity human : new ArrayList<>(this.toInventory().getViewers())) {
            human.closeInventory();
        }
    }

    public static class SaveHandler implements ChestMenu.MenuOpeningHandler {
        final BlockMenu menu;
        final ChestMenu.MenuOpeningHandler handler;

        public SaveHandler(final BlockMenu menu, final ChestMenu.MenuOpeningHandler handler) {
            this.handler = handler;
            this.menu = menu;
        }

        public void onOpen(final Player p) {
            this.handler.onOpen(p);
            final BlockMenu menu = this.menu;
            ++menu.changes;
        }
    }
}
