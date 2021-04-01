package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class MachineHelper {
    public static String getTimeLeft(int l) {
        String timeleft = "";
        int minutes = (int) (l / 60L);
        if (minutes > 0) {
            timeleft = timeleft + minutes + "m ";
        }
        l -= minutes * 60;
        int seconds = l;
        timeleft = timeleft + seconds + "s";
        return ChatColor.translateAlternateColorCodes('&', "&7剩余 " + timeleft + " 秒");
    }

    public static String getProgress(int time, int total) {
        StringBuilder progress = new StringBuilder();
        float percentage = Math.round((total - time) * 100.0F / total * 100.0F / 100.0F);

        if (percentage < 16.0F) {
            progress.append("&4");
        } else if (percentage < 32.0F) {
            progress.append("&c");
        } else if (percentage < 48.0F) {
            progress.append("&6");
        } else if (percentage < 64.0F) {
            progress.append("&e");
        } else if (percentage < 80.0F) {
            progress.append("&2");
        } else {
            progress = progress.append("&a");
        }

        int rest = 20;
        int i;
        for (i = (int) percentage; i >= 5; i -= 5) {
            progress.append(":");
            rest--;
        }

        progress.append("&7");

        for (i = 0; i < rest; i++) {
            progress.append(":");
        }

        progress.append(" - ").append(percentage).append("%");
        return ChatColor.translateAlternateColorCodes('&', progress.toString());
    }

    public static String getCoolant(int time, int total) {
        int passed = (total - time) % 25;
        StringBuilder progress = new StringBuilder();
        float percentage = Math.round((25 - passed) * 100.0F / 25.0F * 100.0F / 100.0F);

        if (percentage < 33.0F) {
            progress.append("&9");
        } else if (percentage < 66.0F) {
            progress.append("&1");
        } else {
            progress = progress.append("&b");
        }

        int rest = 20;
        int i;
        for (i = (int) percentage; i >= 5; i -= 5) {
            progress.append(":");
            rest--;
        }

        progress.append("&7");

        for (i = 0; i < rest; i++) {
            progress.append(":");
        }

        progress.append(" - ").append(percentage).append("%");
        return ChatColor.translateAlternateColorCodes('&', progress.toString());
    }

    public static float getPercentage(int time, int total) {
        int passed = (total - time) % 25;
        return Math.round((25 - passed) * 100.0F / 25.0F * 100.0F / 100.0F);
    }

    public static short getDurability(ItemStack item, int timeleft, int max) {
        return (short) (item.getType().getMaxDurability() / max * timeleft);
    }
}


