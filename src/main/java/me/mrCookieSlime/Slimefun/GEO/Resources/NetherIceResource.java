package me.mrCookieSlime.Slimefun.GEO.Resources;

import me.mrCookieSlime.Slimefun.GEO.OreGenResource;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;


public class NetherIceResource
        implements OreGenResource {
    public int getDefaultSupply(Biome biome) {
        if (biome == Biome.HELL) {
            return 32;
        }

        return 0;
    }


    public String getName() {
        return "下界玄冰";
    }


    public ItemStack getIcon() {
        return SlimefunItems.NETHER_ICE.clone();
    }


    public String getMeasurementUnit() {
        return "块";
    }
}


