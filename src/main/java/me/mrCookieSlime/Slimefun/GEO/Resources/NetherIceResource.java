package me.mrCookieSlime.Slimefun.GEO.Resources;

import me.mrCookieSlime.Slimefun.GEO.OreGenResource;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;


public class NetherIceResource
        implements OreGenResource {
    @Override
    public int getDefaultSupply(Biome biome) {
        if (biome == Biome.HELL) {
            return 32;
        }

        return 0;
    }


    @Override
    public String getName() {
        return "下界玄冰";
    }


    @Override
    public ItemStack getIcon() {
        return SlimefunItems.NETHER_ICE.clone();
    }


    @Override
    public String getMeasurementUnit() {
        return "块";
    }
}


