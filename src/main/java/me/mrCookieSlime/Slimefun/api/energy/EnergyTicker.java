package me.mrCookieSlime.Slimefun.api.energy;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.ItemHandler;
import org.bukkit.Location;

public abstract class EnergyTicker extends ItemHandler {
    public abstract double generateEnergy(Location paramLocation, SlimefunItem paramSlimefunItem, Config paramConfig);

    public abstract boolean explode(Location paramLocation);

    @Override
    public String toCodename() {
        return "EnergyTicker";
    }
}


