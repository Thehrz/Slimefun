package me.mrCookieSlime.Slimefun.api;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.GPS.GPSNetwork;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.Research;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Setup.Messages;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;


public class Slimefun {
    public static final List<GuideHandler> guide_handlers2 = new ArrayList<>();
    private static final GPSNetwork gps = new GPSNetwork();
    public static Map<Integer, List<GuideHandler>> guide_handlers = new HashMap<>();
    public static boolean emeraldenchants = false;

    public static void registerGuideHandler(GuideHandler handler) {
        List<GuideHandler> handlers = new ArrayList<>();
        if (guide_handlers.containsKey(handler.getTier())) {
            handlers = guide_handlers.get(handler.getTier());
        }
        handlers.add(handler);
        guide_handlers.put(handler.getTier(), handlers);
        guide_handlers2.add(handler);
    }


    public static GPSNetwork getGPSNetwork() {
        return gps;
    }


    public static Object getItemValue(String id, String key) {
        return getItemConfig().getValue(id + "." + key);
    }


    public static void setItemVariable(String id, String key, Object value) {
        getItemConfig().setDefaultValue(id + "." + key, value);
    }


    public static Config getItemConfig() {
        return SlimefunStartup.getItemCfg();
    }


    public static void registerResearch(Research research, ItemStack... items) {
        for (ItemStack item : items) {
            research.addItems(SlimefunItem.getByItem(item));
        }
        research.register();
    }


    public static boolean hasUnlocked(Player p, ItemStack item, boolean message) {
        SlimefunItem sfItem = SlimefunItem.getByItem(item);
        SlimefunItem.State state = SlimefunItem.getState(item);
        if (sfItem == null) {
            if (state != SlimefunItem.State.ENABLED) {
                if (message && state != SlimefunItem.State.VANILLA)
                    Messages.local.sendTranslation(p, "messages.disabled-item", true);
                return false;
            }
            return true;
        }
        if (isEnabled(p, item, message) && hasPermission(p, sfItem, message)) {
            if (sfItem.getResearch() == null) {
                return true;
            }
            if (sfItem.getResearch().hasUnlocked(p)) {
                return true;
            }
            if (message && !(sfItem instanceof me.mrCookieSlime.Slimefun.Objects.SlimefunItem.VanillaItem)) {
                Messages.local.sendTranslation(p, "messages.not-researched", true);
            }
            return false;
        }

        return false;
    }


    public static boolean hasUnlocked(Player p, SlimefunItem sfItem, boolean message) {
        if (isEnabled(p, sfItem, message) && hasPermission(p, sfItem, message)) {
            if (sfItem.getResearch() == null) return true;
            if (sfItem.getResearch().hasUnlocked(p)) return true;

            if (message && !(sfItem instanceof me.mrCookieSlime.Slimefun.Objects.SlimefunItem.VanillaItem))
                Messages.local.sendTranslation(p, "messages.not-researched", true);
            return false;
        }

        return false;
    }


    public static boolean hasPermission(Player p, SlimefunItem item, boolean message) {
        if (item == null) return true;
        if (item.getPermission().equalsIgnoreCase("")) return true;
        if (p.hasPermission(item.getPermission())) return true;

        if (message)
            Messages.local.sendTranslation(p, "messages.no-permission", true);
        return false;
    }


    public static boolean isEnabled(Player p, ItemStack item, boolean message) {
        String world = p.getWorld().getName();
        SlimefunItem sfItem = SlimefunItem.getByItem(item);
        if (sfItem == null) return !SlimefunItem.isDisabled(item);
        if (SlimefunStartup.getWhitelist().contains(world + ".enabled")) {
            if (SlimefunStartup.getWhitelist().getBoolean(world + ".enabled")) {
                if (!SlimefunStartup.getWhitelist().contains(world + ".enabled-items." + sfItem.getID()))
                    SlimefunStartup.getWhitelist().setDefaultValue(world + ".enabled-items." + sfItem.getID(), Boolean.TRUE);
                if (SlimefunStartup.getWhitelist().getBoolean(world + ".enabled-items." + sfItem.getID())) return true;

                if (message)
                    Messages.local.sendTranslation(p, "messages.disabled-in-world", true);
                return false;
            }


            if (message)
                Messages.local.sendTranslation(p, "messages.disabled-in-world", true);
            return false;
        }

        return true;
    }


    public static boolean isEnabled(Player p, SlimefunItem sfItem, boolean message) {
        String world = p.getWorld().getName();
        if (SlimefunStartup.getWhitelist().contains(world + ".enabled")) {
            if (SlimefunStartup.getWhitelist().getBoolean(world + ".enabled")) {
                if (!SlimefunStartup.getWhitelist().contains(world + ".enabled-items." + sfItem.getID()))
                    SlimefunStartup.getWhitelist().setDefaultValue(world + ".enabled-items." + sfItem.getID(), Boolean.TRUE);
                if (SlimefunStartup.getWhitelist().getBoolean(world + ".enabled-items." + sfItem.getID())) return true;

                if (message)
                    Messages.local.sendTranslation(p, "messages.disabled-in-world", true);
                return false;
            }


            if (message)
                Messages.local.sendTranslation(p, "messages.disabled-in-world", true);
            return false;
        }

        return true;
    }


    public static List<String> listIDs() {
        List<String> ids = new ArrayList<>();
        for (SlimefunItem item : SlimefunItem.list()) {
            ids.add(item.getID());
        }
        return ids;
    }


    public static List<ItemStack> listCategories() {
        List<ItemStack> items = new ArrayList<>();
        for (Category c : Category.list()) {
            items.add(c.getItem());
        }
        return items;
    }


    @Deprecated
    public static void addDescription(String id, String... description) {
        getItemConfig().setDefaultValue(id + ".description", Arrays.asList(description));
    }


    public static void addHint(String id, String... hint) {
        getItemConfig().setDefaultValue(id + ".hint", Arrays.asList(hint));
    }


    public static void addYoutubeVideo(String id, String link) {
        getItemConfig().setDefaultValue(id + ".youtube", link);
    }


    public static void addWikiPage(String id, String link) {
        getItemConfig().setDefaultValue(id + ".wiki", link);
    }


    public static void addOfficialWikiPage(String id, String page) {
        addWikiPage(id, "https://github.com/TheBusyBiscuit/Slimefun4/wiki/" + page);
    }


    public static boolean isEmeraldEnchantsInstalled() {
        return emeraldenchants;
    }

    public static List<GuideHandler> getGuideHandlers(int tier) {
        return guide_handlers.containsKey(tier) ? guide_handlers.get(tier) : new ArrayList<>();
    }
}


