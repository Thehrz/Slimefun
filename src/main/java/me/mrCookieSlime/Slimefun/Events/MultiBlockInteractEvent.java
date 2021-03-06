package me.mrCookieSlime.Slimefun.Events;

import me.mrCookieSlime.Slimefun.Objects.MultiBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MultiBlockInteractEvent
        extends Event
        implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    final Player p;
    final MultiBlock mb;
    final Block b;
    boolean cancelled;

    public MultiBlockInteractEvent(Player p, MultiBlock mb, Block clicked) {
        this.p = p;
        this.mb = mb;
        this.b = clicked;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return this.p;
    }

    public MultiBlock getMultiBlock() {
        return this.mb;
    }

    public Block getClickedBlock() {
        return this.b;
    }


    public boolean isCancelled() {
        return this.cancelled;
    }


    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}


