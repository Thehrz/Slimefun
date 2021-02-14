package me.mrCookieSlime.Slimefun.listeners;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Maps;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Arrays;


public class TempFixListener
        implements Listener {
    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            Block relative = block.getRelative(event.getDirection(), 1);
            if (BlockStorage.check(relative) != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            Block relative = block.getRelative(event.getDirection(), 1);
            if (BlockStorage.check(relative) != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onBreakSlimefunBlock(BlockBreakEvent event) {
        BlockMenu blockMenu;
        Block block = event.getBlock();
        if (block == null) {
            return;
        }
        if (block.getLocation() == null) {
            return;
        }
        if (BlockStorage.check(block) == null) {
            return;
        }
        if (block.getLocation().getWorld() == null) {
            return;
        }

        try {
            blockMenu = BlockStorage.getInventory(block);
        } catch (NullPointerException e) {
            return;
        }
        if (blockMenu == null) {
            return;
        }
        for (Player player : block.getWorld().getPlayers()) {
            ChestMenu chestMenu = (Maps.getInstance()).menus.get(player.getUniqueId());
            if (chestMenu == null) {
                continue;
            }
            if (Arrays.equals(blockMenu.getContents(), chestMenu.getContents())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§a远古工艺 §7> §c有其他玩家打开界面时无法拆除机器！§8(若无其他玩家打开时仍无法拆除，请向机器内放入任意物品或清空再试)");
                return;
            }
        }
    }

    @EventHandler
    public void onPlaceNoPermSfItem(BlockPlaceEvent event) {
        if (!Slimefun.hasUnlocked(event.getPlayer(), event.getPlayer().getInventory().getItemInOffHand(), true))
            event.setCancelled(true);
    }
}


