package me.mrCookieSlime.Slimefun.Events;

import io.izzel.taboolib.module.inject.TListener;
import me.mrCookieSlime.Slimefun.api.inventory.AdvancedBlockMenuHolder;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class BlockMenuItemsMoveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private AdvancedBlockMenuHolder advancedBlockMenuHolder;

    public BlockMenuItemsMoveEvent(AdvancedBlockMenuHolder advancedBlockMenuHolder) {
        this.advancedBlockMenuHolder = advancedBlockMenuHolder;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public AdvancedBlockMenuHolder getAdvancedBlockMenuHolder() {
        return advancedBlockMenuHolder;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

@TListener
class AdvancedBlockMenuItemsMoveEventListener implements Listener {
    @EventHandler
    public void onInventoryMoveItem(InventoryClickEvent e) {
        System.out.println(e.getInventory().getLocation().getBlock() instanceof InventoryHolder);
//        Bukkit.getServer().getPluginManager().callEvent(new BlockMenuItemsMoveEvent(new AdvancedBlockMenuHolder<>((Chest) e.getInventory().getLocation().getBlock())));
    }
}