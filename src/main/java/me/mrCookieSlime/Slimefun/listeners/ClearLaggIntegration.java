package me.mrCookieSlime.Slimefun.listeners;

import me.minebuilders.clearlag.events.EntityRemoveEvent;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Iterator;


public class ClearLaggIntegration
        implements Listener {
    public ClearLaggIntegration(SlimefunStartup plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityRemove(EntityRemoveEvent e) {
        Iterator<Entity> iterator = e.getEntityList().iterator();
        while (iterator.hasNext()) {
            Entity n = iterator.next();
            if (n instanceof org.bukkit.entity.Item &&
                    n.hasMetadata("no_pickup")) iterator.remove();
        }
    }
}


