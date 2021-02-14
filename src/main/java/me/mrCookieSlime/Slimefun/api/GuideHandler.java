package me.mrCookieSlime.Slimefun.api;

import me.mrCookieSlime.CSCoreLibPlugin.PlayerRunnable;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.SlimefunGuide;
import me.mrCookieSlime.Slimefun.URID.URID;
import org.bukkit.entity.Player;

import java.util.List;


public abstract class GuideHandler {
    final URID urid = URID.nextURID(this, false);


    public URID getURID() {
        return this.urid;
    }


    public abstract void addEntry(List<String> paramList1, List<String> paramList2);


    public abstract PlayerRunnable getRunnable();


    public PlayerRunnable getRunnable(boolean book) {
        return getRunnable();
    }

    public abstract int getTier();

    public abstract boolean trackHistory();

    public abstract int next(Player paramPlayer, int paramInt, ChestMenu paramChestMenu);

    public void run(Player p, boolean survival, boolean book) {
        getRunnable(book).run(p);

        if (survival && trackHistory())
            SlimefunGuide.addToHistory(p, getURID());
    }
}


