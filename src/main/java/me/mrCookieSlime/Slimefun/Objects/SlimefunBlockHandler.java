package me.mrCookieSlime.Slimefun.Objects;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.UnregisterReason;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface SlimefunBlockHandler {
    void onPlace(Player paramPlayer, Block paramBlock, SlimefunItem paramSlimefunItem);

    boolean onBreak(Player paramPlayer, Block paramBlock, SlimefunItem paramSlimefunItem, UnregisterReason paramUnregisterReason);
}


