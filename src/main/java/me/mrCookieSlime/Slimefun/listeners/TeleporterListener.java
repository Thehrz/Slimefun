package me.mrCookieSlime.Slimefun.listeners;

import io.izzel.taboolib.module.inject.TListener;
import me.mrCookieSlime.Slimefun.GPS.Elevator;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.Teleporter;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@TListener
public class TeleporterListener implements Listener {
    final BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onStarve(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.PHYSICAL)) {
            return;
        }
        if (e.getClickedBlock() == null) {
            return;
        }
        SlimefunItem item = BlockStorage.check(e.getClickedBlock());
        if (item == null) {
            return;
        }
        switch (item.getID()) {
            case "GPS_ACTIVATION_DEVICE_SHARED":
                SlimefunItem teleporter = BlockStorage.check(e.getClickedBlock().getRelative(BlockFace.DOWN));

                if (teleporter instanceof Teleporter) {
                    for (BlockFace face : this.faces) {
                        if (!BlockStorage.check(e.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(face), "GPS_TELEPORTER_PYLON")) {
                            return;
                        }
                    }
                    try {
                        ((Teleporter) teleporter).onInteract(e.getPlayer(), e.getClickedBlock().getRelative(BlockFace.DOWN));
                    } catch (Exception x) {
                        x.printStackTrace();
                    }

                }
                break;
            case "GPS_ACTIVATION_DEVICE_PERSONAL":
                if (BlockStorage.getLocationInfo(e.getClickedBlock().getLocation(), "owner").equals(e.getPlayer().getUniqueId().toString())) {
                    teleporter = BlockStorage.check(e.getClickedBlock().getRelative(BlockFace.DOWN));

                    if (teleporter instanceof Teleporter) {
                        for (BlockFace face : this.faces) {
                            if (!BlockStorage.check(e.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(face), "GPS_TELEPORTER_PYLON")) {
                                return;
                            }
                        }
                        try {
                            ((Teleporter) teleporter).onInteract(e.getPlayer(), e.getClickedBlock().getRelative(BlockFace.DOWN));
                        } catch (Exception x) {
                            x.printStackTrace();
                        }
                    }
                } else {
                    e.setCancelled(true);
                }
                break;
            case "ELEVATOR_PLATE":
                Elevator.openDialogue(e.getPlayer(), e.getClickedBlock());
                break;
            default:
                break;
        }
    }
}


