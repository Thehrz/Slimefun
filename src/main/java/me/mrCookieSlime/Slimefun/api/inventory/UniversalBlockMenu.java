package me.mrCookieSlime.Slimefun.api.inventory;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;

public class UniversalBlockMenu extends ChestMenu {
    final BlockMenuPreset preset;
    public int changes;
    ItemManipulationEvent event;

    public UniversalBlockMenu(final BlockMenuPreset preset) {
        super(preset.getTitle());
        this.changes = 0;
        this.preset = preset;
        this.changes = 1;
        preset.clone(this);
        this.save();
    }

    public UniversalBlockMenu(final BlockMenuPreset preset, final Config cfg) {
        super(preset.getTitle());
        this.changes = 0;
        this.preset = preset;
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

    public void registerEvent(final ItemManipulationEvent event) {
        this.event = event;
    }

    public void save() {
        if (this.changes == 0) {
            return;
        }
        this.getContents();
        final File file = new File("data-storage/Slimefun/universal-inventories/" + this.preset.getID() + ".sfi");
        final Config cfg = new Config(file);
        cfg.setValue("preset", this.preset.getID());
        for (final int slot : this.preset.getInventorySlots()) {
            cfg.setValue(String.valueOf(slot), this.getItemInSlot(slot));
        }
        cfg.save();
        this.changes = 0;
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

    public void close() {
        for (final HumanEntity human : new ArrayList<>(this.toInventory().getViewers())) {
            human.closeInventory();
        }
    }
}
