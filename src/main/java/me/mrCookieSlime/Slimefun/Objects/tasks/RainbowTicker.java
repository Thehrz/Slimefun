package me.mrCookieSlime.Slimefun.Objects.tasks;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.BlockTicker;
import org.bukkit.block.Block;

public class RainbowTicker
        extends BlockTicker {
    public final int[] queue;
    public int meta;
    public int index;

    public RainbowTicker() {
        this(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
    }

    public RainbowTicker(int... data) {
        this.queue = data;
        this.meta = data[0];
        this.index = 0;
    }


    public void tick(Block b, SlimefunItem item, Config data) {
        b.setData((byte) this.meta, false);
    }


    public void uniqueTick() {
        this.index = (this.index == this.queue.length - 1) ? 0 : (this.index + 1);
        this.meta = this.queue[this.index];
    }


    public boolean isSynchronized() {
        return true;
    }
}


