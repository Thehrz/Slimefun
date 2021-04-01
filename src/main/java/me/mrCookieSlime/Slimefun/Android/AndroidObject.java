package me.mrCookieSlime.Slimefun.Android;

import org.bukkit.block.Block;

public class AndroidObject {
    ProgrammableAndroid android;
    Block b;

    public AndroidObject(final ProgrammableAndroid android, final Block b) {
        this.android = android;
        this.b = b;
    }

    public ProgrammableAndroid getAndroid() {
        return this.android;
    }

    public Block getBlock() {
        return this.b;
    }
}
