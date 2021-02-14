package me.mrCookieSlime.Slimefun.Objects.tasks;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.BlockTicker;
import org.bukkit.block.Block;

public class AdvancedRainbowTicker
        extends BlockTicker {
    public final int[] data;
    public int index;

    public AdvancedRainbowTicker(int... data) {
        this.data = data;
        this.index = 0;
    }


    public void tick(Block b, SlimefunItem item, Config cfg) {
        b.setData((byte) this.data[this.index], false);
    }


    public void uniqueTick() {
        this.index = (this.index == this.data.length - 1) ? 0 : (this.index + 1);
    }


    public boolean isSynchronized() {
        return true;
    }
}


