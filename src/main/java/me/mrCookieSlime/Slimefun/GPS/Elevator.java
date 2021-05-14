package me.mrCookieSlime.Slimefun.GPS;

import io.izzel.taboolib.module.tellraw.TellrawJson;
import io.izzel.taboolib.util.item.Books;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.MenuHelper;
import me.mrCookieSlime.Slimefun.Setup.Messages;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Elevator {
    public static List<UUID> ignored = new ArrayList<>();

    public static void openEditor(Player p, final Block b) {
        ChestMenu menu = new ChestMenu("电梯设置");

        menu.addItem(4, new CustomItem(new MaterialData(Material.NAME_TAG), "§7楼层名 §e(点击修改)", "", "§r" + ChatColor.translateAlternateColorCodes('&', BlockStorage.getLocationInfo(b.getLocation(), "floor"))));
        menu.addMenuClickHandler(4, (p12, arg1, arg2, arg3) -> {
            p12.closeInventory();
            p12.sendMessage("");
            p12.sendMessage("§4§l>> §e请在聊天栏内输入你想要设置的楼层名称!");
            p12.sendMessage("§4§l>> §e(支持颜色代码!");
            p12.sendMessage("");

            MenuHelper.awaitChatInput(p12, (p1, message) -> {
                BlockStorage.addBlockInfo(b, "floor", message.replaceAll("&", "§"));

                p1.sendMessage("");
                p1.sendMessage("§4§l>> §e成功设置楼层名称:");
                p1.sendMessage("§4§l>> §r" + ChatColor.translateAlternateColorCodes('&', message));
                p1.sendMessage("");

                Elevator.openEditor(p1, b);

                return false;
            });
            return false;
        });

        menu.open(p);
    }

    public static void openDialogue(Player p, Block b) {
        if (ignored.contains(p.getUniqueId())) {
            ignored.remove(p.getUniqueId());
            return;
        }
        TellrawJson tellraw = TellrawJson.create();
        tellraw.append("§3- 请选择目的地 -\n\n");
        int index = 1;
        for (int y = b.getWorld().getMaxHeight(); y > 0; y--) {
            Block block = b.getWorld().getBlockAt(b.getX(), y, b.getZ());
            if (BlockStorage.check(block, "ELEVATOR_PLATE")) {
                String floor = ChatColor.translateAlternateColorCodes('&', BlockStorage.getLocationInfo(block.getLocation(), "floor"));
                if (block.getY() == b.getY()) {
                    tellraw.append("§7> " + index + ". §r" + floor + "\n");
                    tellraw.hoverText("\n§e这是你目前所在的楼层:\n§r" + floor + "\n");
                } else {
                    tellraw.append("§7" + index + ". §r" + floor + "\n");
                    tellraw.hoverText("\n§e点击传送至此楼层\n§r" + floor + "\n");
                    tellraw.clickCommand("/sf elevator " + block.getX() + " " + block.getY() + " " + block.getZ() + " ");
                }

                index++;
            }
        }
        if (index > 2) {
            Books.create().write(tellraw).open(p);
        } else {
            Messages.local.sendTranslation(p, "machines.ELEVATOR.no-destinations", true);
        }

    }
}


