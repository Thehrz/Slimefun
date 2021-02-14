package me.mrCookieSlime.Slimefun.GEO;

import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

public interface OreGenResource {
    int getDefaultSupply(Biome paramBiome);

    String getName();

    ItemStack getIcon();

    String getMeasurementUnit();
}


