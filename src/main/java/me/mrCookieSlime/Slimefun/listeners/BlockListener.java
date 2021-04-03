package me.mrCookieSlime.Slimefun.listeners;

import io.izzel.taboolib.module.inject.TListener;
import me.mrCookieSlime.CSCoreLibPlugin.general.Block.BlockAdjacents;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Maps;
import me.mrCookieSlime.Slimefun.Events.MultiBlockInteractEvent;
import me.mrCookieSlime.Slimefun.Objects.MultiBlock;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.ItemHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.MultiBlockInteractionHandler;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TListener
public class BlockListener implements Listener {
    @EventHandler
    public void onBlockFall(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock &&
                BlockStorage.hasBlockInfo(event.getBlock())) {
            event.setCancelled(true);
            FallingBlock fb = (FallingBlock) event.getEntity();
            if (fb.getDropItem()) {
                fb.getWorld().dropItemNaturally(fb.getLocation(), new ItemStack(fb.getMaterial(), 1, fb.getBlockData()));
            }
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent e) {
        for (Block b : e.getBlocks()) {
            if (BlockStorage.hasBlockInfo(b)) {
                e.setCancelled(true);
                return;
            }
            if (b.getRelative(e.getDirection()) == null && BlockStorage.hasBlockInfo(b.getRelative(e.getDirection()))) {
                e.setCancelled(true);
                return;
            }
            if (b.getRelative(e.getDirection(), 1) == null && BlockStorage.hasBlockInfo(b.getRelative(e.getDirection(), 1))) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent e) {
        if (e.isSticky()) {
            for (Block b : e.getBlocks()) {
                if (BlockStorage.hasBlockInfo(b)) {
                    e.setCancelled(true);
                    return;
                }
                if (b.getRelative(e.getDirection()) == null && BlockStorage.hasBlockInfo(b.getRelative(e.getDirection()))) {
                    e.setCancelled(true);
                    return;
                }
                if (b.getRelative(e.getDirection(), 1) == null && BlockStorage.hasBlockInfo(b.getRelative(e.getDirection(), 1))) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!e.getHand().equals(EquipmentSlot.HAND)) {
                return;
            }
            Player p = e.getPlayer();
            Block b = e.getClickedBlock();
            List<MultiBlock> multiblocks = new ArrayList<>();
            for (MultiBlock mb : MultiBlock.list()) {
                if (mb.getTriggerBlock() == b.getType()) {
                    Material[] blocks = mb.getBuild();

                    if (mb.getTriggerBlock() == blocks[1]) {

                        if (BlockAdjacents.hasMaterialOnSide(b, blocks[0]) &&
                                BlockAdjacents.hasMaterialOnSide(b, blocks[2]) &&
                                BlockAdjacents.isMaterial(b.getRelative(BlockFace.DOWN), blocks[4]) &&
                                BlockAdjacents.hasMaterialOnSide(b.getRelative(BlockFace.DOWN), blocks[3]) &&
                                BlockAdjacents.hasMaterialOnSide(b.getRelative(BlockFace.DOWN), blocks[5]) &&
                                BlockAdjacents.isMaterial(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN), blocks[7]) &&
                                BlockAdjacents.hasMaterialOnSide(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN), blocks[6]) &&
                                BlockAdjacents.hasMaterialOnSide(b.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN), blocks[8])) {

                            if ((blocks[0] != null && blocks[0] == blocks[2] && !BlockAdjacents.hasMaterialOnBothSides(b.getRelative(0, 0, 0), blocks[0])) || (
                                    blocks[3] != null && blocks[3] == blocks[5] && !BlockAdjacents.hasMaterialOnBothSides(b.getRelative(0, -1, 0), blocks[5])) || (
                                    blocks[6] != null && blocks[6] == blocks[8] && !BlockAdjacents.hasMaterialOnBothSides(b.getRelative(0, -2, 0), blocks[8]))) {
                                continue;
                            }
                            multiblocks.add(mb);
                        }
                        continue;
                    }
                    if (mb.getTriggerBlock() == blocks[4]) {

                        if (BlockAdjacents.hasMaterialOnSide(b, blocks[3]) &&
                                BlockAdjacents.hasMaterialOnSide(b, blocks[5]) &&
                                BlockAdjacents.isMaterial(b.getRelative(BlockFace.DOWN), blocks[7]) &&
                                BlockAdjacents.hasMaterialOnSide(b.getRelative(BlockFace.DOWN), blocks[6]) &&
                                BlockAdjacents.hasMaterialOnSide(b.getRelative(BlockFace.DOWN), blocks[8]) &&
                                BlockAdjacents.isMaterial(b.getRelative(BlockFace.UP), blocks[1]) &&
                                BlockAdjacents.hasMaterialOnSide(b.getRelative(BlockFace.UP), blocks[0]) &&
                                BlockAdjacents.hasMaterialOnSide(b.getRelative(BlockFace.UP), blocks[2])) {

                            if ((blocks[0] != null && blocks[0] == blocks[2] && !BlockAdjacents.hasMaterialOnBothSides(b.getRelative(0, 1, 0), blocks[0])) || (
                                    blocks[3] != null && blocks[3] == blocks[5] && !BlockAdjacents.hasMaterialOnBothSides(b.getRelative(0, 0, 0), blocks[5])) || (
                                    blocks[6] != null && blocks[6] == blocks[8] && !BlockAdjacents.hasMaterialOnBothSides(b.getRelative(0, -1, 0), blocks[8]))) {
                                continue;
                            }
                            multiblocks.add(mb);
                        }
                        continue;
                    }
                    if (mb.getTriggerBlock() == blocks[7] &&

                            BlockAdjacents.hasMaterialOnSide(b, blocks[6]) &&
                            BlockAdjacents.hasMaterialOnSide(b, blocks[8]) &&
                            BlockAdjacents.isMaterial(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP), blocks[1]) &&
                            BlockAdjacents.hasMaterialOnSide(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP), blocks[0]) &&
                            BlockAdjacents.hasMaterialOnSide(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP), blocks[2]) &&
                            BlockAdjacents.isMaterial(b.getRelative(BlockFace.UP), blocks[4]) &&
                            BlockAdjacents.hasMaterialOnSide(b.getRelative(BlockFace.UP), blocks[3]) &&
                            BlockAdjacents.hasMaterialOnSide(b.getRelative(BlockFace.UP), blocks[5])) {

                        if ((blocks[0] != null && blocks[0] == blocks[2] && !BlockAdjacents.hasMaterialOnBothSides(b.getRelative(0, 2, 0), blocks[0])) || (
                                blocks[3] != null && blocks[3] == blocks[5] && !BlockAdjacents.hasMaterialOnBothSides(b.getRelative(0, 1, 0), blocks[5])) || (
                                blocks[6] != null && blocks[6] == blocks[8] && !BlockAdjacents.hasMaterialOnBothSides(b.getRelative(0, 0, 0), blocks[8]))) {
                            continue;
                        }
                        multiblocks.add(mb);
                    }
                }
            }

            if (!multiblocks.isEmpty()) {
                e.setCancelled(true);

                for (ItemHandler handler : SlimefunItem.getHandlers("MultiBlockInteractionHandler")) {
                    if (((MultiBlockInteractionHandler) handler).onInteract(p, multiblocks.get(multiblocks.size() - 1), b)) {
                        break;
                    }
                }
                MultiBlockInteractEvent event = new MultiBlockInteractEvent(p, multiblocks.get(multiblocks.size() - 1), b);
                Bukkit.getPluginManager().callEvent(event);
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
                event.getPlayer().sendMessage("§aSlimefun §7> §c有其他玩家打开界面时无法拆除机器！§8(若无其他玩家打开时仍无法拆除，请向机器内放入任意物品或清空再试)");
                return;
            }
        }
    }

    @EventHandler
    public void onPlaceNoPermSfItem(BlockPlaceEvent event) {
        if (!Slimefun.hasUnlocked(event.getPlayer(), event.getPlayer().getInventory().getItemInOffHand(), true)) {
            event.setCancelled(true);
        }
    }
}


