package me.mrCookieSlime.Slimefun.Misc.compatibles;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ProtectionUtils {
    public static boolean canAccessItem(Player p, Block b) {
        if (p.isOp()) {
            return true;
        }
        if (SlimefunStartup.slimefunStartup.isPlotSquaredInstalled()) {
            Location plotLoc = new Location(b.getLocation().getWorld().getName(), b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ());
            Plot plot = Plot.getPlot(plotLoc);
            if (plot != null &&
                    !plot.isAdded(p.getUniqueId())) {
                p.sendMessage("§8[§eSlimefun§8] §c抱歉，你不可以在无权使用的地皮上使用机器. 请联系地皮主人给你权限吧！");
                return false;
            }
        }

        if (SlimefunStartup.slimefunStartup.isResidenceInstalled()) {
            ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(b.getLocation());
            if (res != null &&
                    !p.hasPermission("residence.bypass.use") &&
                    !res.getPermissions().playerHas(p.getName(), p.getWorld().getName(), "sf-machines", true) && !res.getPermissions().has("sf-machines", true)) {
                p.sendMessage("§8[§bSlimefun§8] §c你需要这个领地的§esf-machines§c标识§8(flag)§c才能这么做");
                return false;
            }
        }


        return true;
    }

    public static boolean canBuild(Player player, Block block) {
        return canBuild(player, block, true);
    }

    public static boolean canBuild(Player player, Block block, boolean notify) {
        if (player.isOp()) {
            return true;
        }
        if (SlimefunStartup.slimefunStartup.isPlotSquaredInstalled()) {
            Location plotLoc = new Location(block.getLocation().getWorld().getName(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
            Plot plot = Plot.getPlot(plotLoc);
            if (plot != null &&
                    !plot.isAdded(player.getUniqueId())) {
                if (notify) {
                    player.sendMessage("§8[§eSlimefun§8] §c抱歉，你不可以在这里建造或破坏. 请联系地皮主人给你权限吧！");
                }
                return false;
            }
        }

        if (SlimefunStartup.slimefunStartup.isResidenceInstalled()) {
            ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(block.getLocation());
            if (res != null &&
                    !player.hasPermission("residence.bypass.use") &&
                    !res.getPermissions().playerHas(player.getName(), player.getWorld().getName(), "destroy", true)) {
                if (notify) {
                    player.sendMessage("§8[§bSlimefun§8] §c你不可以在这里建造或破坏");
                }
                return false;
            }
        }


        return true;
    }
}


