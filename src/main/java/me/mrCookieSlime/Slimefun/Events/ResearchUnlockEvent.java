package me.mrCookieSlime.Slimefun.Events;

import me.mrCookieSlime.Slimefun.Objects.Research;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ResearchUnlockEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    final Player p;
    final Research r;
    boolean cancelled;

    public ResearchUnlockEvent(Player p, Research res) {
        this.p = p;
        this.r = res;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return this.p;
    }

    public Research getResearch() {
        return this.r;
    }


    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }


    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}


