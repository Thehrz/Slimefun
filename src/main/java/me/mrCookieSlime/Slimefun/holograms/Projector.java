package me.mrCookieSlime.Slimefun.holograms;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.MenuHelper;
import me.mrCookieSlime.CSCoreLibPlugin.general.Math.DoubleHandler;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.ArmorStandFactory;
import me.mrCookieSlime.Slimefun.Setup.Messages;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;


public class Projector {
    public static ArmorStand getArmorStand(Block projector) {
        String nametag = ChatColor.translateAlternateColorCodes('&', BlockStorage.getLocationInfo(projector.getLocation(), "text"));
        double offset = Double.valueOf(BlockStorage.getLocationInfo(projector.getLocation(), "offset"));
        Location l = new Location(projector.getWorld(), projector.getX() + 0.5D, projector.getY() + offset, projector.getZ() + 0.5D);

        for (Entity n : l.getChunk().getEntities()) {
            if (n instanceof ArmorStand &&
                    n.getCustomName() != null && n.getCustomName().equals(nametag) && l.distanceSquared(n.getLocation()) < 0.4D) {
                return (ArmorStand) n;
            }

        }

        ArmorStand hologram = ArmorStandFactory.createHidden(l);
        hologram.setCustomName(nametag);
        return hologram;
    }

    public static void openEditor(Player p, final Block projector) {
        ChestMenu menu = new ChestMenu("全息设置");

        menu.addItem(0, new CustomItem(new MaterialData(Material.NAME_TAG), "&7文本 &e(点击修改)", "", "&r" + ChatColor.translateAlternateColorCodes('&', BlockStorage.getLocationInfo(projector.getLocation(), "text"))));
        menu.addMenuClickHandler(0, (p13, arg1, arg2, arg3) -> {
            p13.closeInventory();
            Messages.local.sendTranslation(p13, "machines.HOLOGRAM_PROJECTOR.enter-text", true);
            MenuHelper.awaitChatInput(p13, (p12, message) -> {
                ArmorStand hologram = Projector.getArmorStand(projector);

                String holoMsg = ChatColor.translateAlternateColorCodes('&', message);
                if (ChatColor.stripColor(holoMsg).length() > 30) {
                    p12.sendMessage("§c你输入的文本超长");
                    return false;
                }
                hologram.setCustomName(holoMsg);
                BlockStorage.addBlockInfo(projector, "text", message);
                Projector.openEditor(p12, projector);
                return false;
            });
            return false;
        });

        menu.addItem(1, new CustomItem(new MaterialData(Material.WATCH), "&7偏移量: &e" + DoubleHandler.fixDouble(Double.valueOf(BlockStorage.getLocationInfo(projector.getLocation(), "offset")) + 1.0D), "", "&r左键点击: &7+0.1", "&r右键点击: &7-0.1"));
        menu.addMenuClickHandler(1, (p1, arg1, arg2, arg3) -> {
            double offset = DoubleHandler.fixDouble(Double.valueOf(BlockStorage.getLocationInfo(projector.getLocation(), "offset")).doubleValue() + (arg3.isRightClicked() ? -0.1F : 0.1F));
            ArmorStand hologram = Projector.getArmorStand(projector);
            Location l = new Location(projector.getWorld(), projector.getX() + 0.5D, projector.getY() + offset, projector.getZ() + 0.5D);

            if (l.distance(projector.getLocation()) > 3.0D) {
                p1.sendMessage("§c你设置的全息新位置离投射装置太远了！");
                return false;
            }
            hologram.teleport(l);
            BlockStorage.addBlockInfo(projector, "offset", String.valueOf(offset));
            Projector.openEditor(p1, projector);
            return false;
        });

        menu.open(p);
    }
}


