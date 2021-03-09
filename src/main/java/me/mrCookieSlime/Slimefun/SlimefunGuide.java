package me.mrCookieSlime.Slimefun;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.Math.DoubleHandler;
import me.mrCookieSlime.CSCoreLibPlugin.general.String.StringUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;
import me.mrCookieSlime.Slimefun.GitHub.Contributor;
import me.mrCookieSlime.Slimefun.GitHub.IntegerFormat;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Misc.BookDesign;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.LockedCategory;
import me.mrCookieSlime.Slimefun.Objects.Research;
import me.mrCookieSlime.Slimefun.Objects.SeasonCategory;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunGadget;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunMachine;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AGenerator;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AReactor;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;
import me.mrCookieSlime.Slimefun.Setup.Messages;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.URID.URID;
import me.mrCookieSlime.Slimefun.api.GuideHandler;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.*;

public class SlimefunGuide {
    private static final int category_size = 36;
    private static final int[] slots = new int[]{1, 3, 5, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
    public static Map<UUID, List<URID>> history = new HashMap<>();
    public static int month = 0;
    public static List<Contributor> contributors = new ArrayList<>();
    public static int issues = 0;
    public static int forks = 0;
    public static int stars = 0;
    public static int code_bytes = 0;
    public static Date last_update = new Date();
    static boolean all_recipes = true;

    public static ItemStack getItem(BookDesign design) {
        switch (design) {
            case CHEAT_SHEET:
                return new CustomItem(new ItemStack(Material.ENCHANTED_BOOK), "&cSlimefun之书 &4(作弊面板)", "", "&4&l仅供管理员使用", "", "&e右键点击 &8⇨ &7浏览物品", "&eShift + 右键点击 &8⇨ &7打开界面设置菜单");

            case CHEST:
                return new CustomItem(new ItemStack(Material.ENCHANTED_BOOK), "&eSlimefun之书 &7(箱子界面)", "", "&e右键点击 &8⇨ &7浏览物品", "&eShift + 右键点击 &8⇨ &7打开界面设置菜单");
        }
        return null;
    }

    public static ItemStack getItem() {
        return getItem(BookDesign.CHEST);
    }

    public static ItemStack getDeprecatedItem(boolean book) {
        return new CustomItem(new ItemStack(Material.ENCHANTED_BOOK), "&eSlimefun之书 &8(右键点击打开)", book ? "" : "&2", "&7这是Slimefun的使用向导书", "&7书本虽已泛黄, 知识却历久弥新", "&7你可以在书中解锁物品", "&7查看机器搭建方法以及物品制作、合成方法");
    }

    public static void openSettings(Player p, final ItemStack guide) {
        ChestMenu menu = new ChestMenu("设置 / 信息");

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 0.7F, 0.7F));

        for (int i : slots) {
            menu.addItem(i, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), " "));
            menu.addMenuClickHandler(i, (player, slot, itemStack, clickAction) -> false);
        }


        if (SlimefunManager.isItemSimiliar(guide, getItem(BookDesign.CHEST), true)) {
            if (p.hasPermission("slimefun.cheat.items")) {
                menu.addItem(19, new CustomItem(new ItemStack(Material.CHEST), "&7界面布局: &e箱子界面", "", "&a箱子界面", "&7作弊面板", "", "&e 点击 &8⇨ &7修改布局"));
                menu.addMenuClickHandler(19, (p17, arg1, arg2, arg3) -> {
                    p17.getInventory().setItemInMainHand(SlimefunGuide.getItem(BookDesign.CHEAT_SHEET));
                    SlimefunGuide.openSettings(p17, p17.getInventory().getItemInMainHand());
                    return false;
                });
            } else {
                menu.addItem(19, new CustomItem(new ItemStack(Material.CHEST), "&7界面布局: &e箱子界面"));
                menu.addMenuClickHandler(19, (player, arg1, arg2, arg3) -> false);
            }

        } else if (SlimefunManager.isItemSimiliar(guide, getItem(BookDesign.CHEAT_SHEET), true)) {
            menu.addItem(19, new CustomItem(new ItemStack(Material.COMMAND), "&7界面布局: &e作弊面板", "", "&7箱子界面", "&a作弊面板", "", "&e 点击 &8⇨ &7修改布局"));
            menu.addMenuClickHandler(19, (p13, arg1, arg2, arg3) -> {
                p13.getInventory().setItemInMainHand(SlimefunGuide.getItem(BookDesign.CHEST));
                SlimefunGuide.openSettings(p13, p13.getInventory().getItemInMainHand());
                return false;
            });
        }

        menu.addItem(0, new CustomItem(new ItemStack(Material.ENCHANTED_BOOK), "&7⇦ 返回"));
        menu.addMenuClickHandler(0, (arg0, arg1, arg2, arg3) -> {
            SlimefunGuide.openMainMenu(p, true, 1);
            return false;
        });

        try {
            menu.addItem(2, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg3ZjZlNzFjYzUyMmM3ZjRmMTZkYTI5ODFjMWI5MmUyZDg1Y2EyM2NlNjA4MWVjNTY5OWI1YjBlMmE3In19fQ=="), "&a百宝箱"));
            menu.addMenuClickHandler(2, (arg0, arg1, arg2, arg3) -> {
                return false;
            });
        } catch (Exception e) {
            System.err.println("SlimefunGuide 头颅异常");
            e.printStackTrace();
        }

        menu.addItem(4, new CustomItem(new ItemStack(Material.BOOK_AND_QUILL), "&a版本信息", "", "&7版本: &a" + SlimefunStartup.instance.getDescription().getVersion(), "&7贡献者: &e" + contributors.size(), "", "&7⇨ 点击查看重置内容"));
        menu.addMenuClickHandler(4, (pl, slot, item, action) -> {
            SlimefunGuide.openCredits(pl, pl.getInventory().getItemInMainHand());
            return false;
        });


        try {
            menu.addItem(6, new CustomItem(new ItemStack(Material.REDSTONE_COMPARATOR), "&e源码", "", "&7代码行数: &6" + IntegerFormat.formatBigNumber(code_bytes), "&7上次更新时间: &a" + IntegerFormat.timeDelta(last_update) + " 之前", "&7Forks: &e" + forks, "&7Stars: &e" + stars, "", "&7&oSlimefun 4 是一个社区项目,", "&7&o源代码可以在GitHub上查看", "&7&o如果你想保持这个项目的活跃,", "&7&o请考虑为这个项目作贡献", "", "&7⇨ 点击前往GitHub"));
            menu.addMenuClickHandler(6, (p12, arg1, arg2, arg3) -> {
                p12.closeInventory();
                p12.sendMessage("");
                p12.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&ohttps://github.com/TheBusyBiscuit/Slimefun4"));
                p12.sendMessage("");
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        menu.addItem(8, new CustomItem(new ItemStack(Material.KNOWLEDGE_BOOK), "&3Slimefun4 WIKI", "", "&a⇨ 点击前往 Slimefun4 中文WIKI"));
        menu.addMenuClickHandler(8, (p1, arg1, arg2, arg3) -> {
            p1.closeInventory();
            p1.sendMessage("");
            p1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&ohttps://mineplugin.org/SlimeFun4"));
            p1.sendMessage("");
            return false;
        });

        menu.open(p);
    }


    public static void openCredits(Player p, final ItemStack guide) {
        ChestMenu menu = new ChestMenu("重置内容");

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(p12 -> p12.playSound(p12.getLocation(), Sound.BLOCK_NOTE_HARP, 0.7F, 0.7F));

        try {
            menu.addItem(9, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzUwM2NiN2VkODQ1ZTdhNTA3ZjU2OWFmYzY0N2M0N2FjNDgzNzcxNDY1YzlhNjc5YTU0NTk0Yzc2YWZiYSJ9fX0="), "&c修复机器人漏洞", "", "&a修复机器人主人离线后一系列问题", "&a解决于: 2020.10.1"));
            menu.addItem(10, new CustomItem(new ItemStack(Material.BARRIER), "&c修复汉化错误", "", "&a修复少量汉化错误", "&a解决于: 2020.10.1"));
            menu.addItem(11, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM0M2NlNThkYTU0Yzc5OTI0YTJjOTMzMWNmYzQxN2ZlOGNjYmJlYTliZTQ1YTdhYzg1ODYwYTZjNzMwIn19fQ=="), "&c修复核电错误", "", "&a修复核电爆炸会连带能量调度器", "&a解决于: 2020.10.1"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 9; i++) {
            if (i != 4) {
                menu.addItem(i, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), " "));
                menu.addMenuClickHandler(i, (player, slot, itemStack, clickAction) -> false);
            } else {
                menu.addItem(4, new CustomItem(new ItemStack(Material.EMERALD), "&7⇦ 返回设置界面"));
                menu.addMenuClickHandler(4, (p1, arg1, arg2, arg3) -> {
                    SlimefunGuide.openSettings(p1, guide);
                    return false;
                });
            }
        }

        for (int j = 0; j < 9; j++) {
            menu.addItem(36 + j, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), " "));
            menu.addMenuClickHandler(36 + j, (arg0, arg1, arg2, arg3) -> false);
        }

        menu.open(p);
    }

    public static void openCheatMenu(Player p) {
        openMainMenu(p, false, 1);
    }

    public static void openGuide(Player p, boolean book) {
        if (!SlimefunStartup.getWhitelist().getBoolean(p.getWorld().getName() + ".enabled")) {
            return;
        }
        if (!SlimefunStartup.getWhitelist().getBoolean(p.getWorld().getName() + ".enabled-items.SLIMEFUN_GUIDE")) {
            return;
        }
        if (!history.containsKey(p.getUniqueId())) {
            openMainMenu(p, true, 1);
        } else {
            URID last = getLastEntry(p, false);
            if (URID.decode(last) instanceof Category) {
                openCategory(p, (Category) URID.decode(last), true, 1);
            } else if (URID.decode(last) instanceof SlimefunItem) {
                displayItem(p, ((SlimefunItem) URID.decode(last)).getItem(), false, 0);
            } else if (URID.decode(last) instanceof GuideHandler) {
                ((GuideHandler) URID.decode(last)).run(p, true, book);
            } else {
                displayItem(p, (ItemStack) URID.decode(last), false, 0);
            }
        }

    }

    public static void openMainMenu(final Player p, final boolean survival, final int selected_page) {
        clearHistory(p.getUniqueId());

        ChestMenu menu = new ChestMenu("Slimefun4 向导");

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(player -> player.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.7F, 0.7F));

        List<Category> categories = Category.list();
        List<GuideHandler> handlers = Slimefun.guide_handlers2;

        int index = 9;
        int pages = 1;
        int i;
        for (i = 0; i < 9; i++) {
            if (i == 1 || i == 7) {
                continue;
            }
            menu.addItem(i, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), " "));
            menu.addMenuClickHandler(i, (player, slot, itemStack, clickAction) -> false);
        }

        menu.addItem(1, new CustomItem(new ItemStack(Material.REDSTONE_COMPARATOR), "§e设置 / 信息", "", "&7⇨ §b点击前往"));
        menu.addMenuClickHandler(1, (player, arg1, arg2, arg3) -> {

            openSettings(player, player.getInventory().getItemInMainHand());
            return false;
        });

        menu.addItem(7, new CustomItem(new ItemStack(Material.NAME_TAG), "§7搜索...", "", "&7⇨ §b点击搜索物品"));
        menu.addMenuClickHandler(7, (player, arg1, arg2, arg3) -> {
            player.closeInventory();
            SlimefunItem.searchSlimefunItem(player, survival);
            return false;
        });

        for (i = 45; i < 54; i++) {
            menu.addItem(i, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), " "));
            menu.addMenuClickHandler(i, (player, slot, itemStack, clickAction) -> false);
        }

        int target = 36 * (selected_page - 1) - 1;

        while (target < categories.size() + handlers.size() - 1) {
            if (index >= 45) {
                pages++;
                break;
            }
            target++;

            if (target >= categories.size()) {
                if (!survival) {
                    break;
                }
                index = handlers.get(target - categories.size()).next(p, index, menu);
                continue;
            }
            final Category category = categories.get(target);

            boolean locked = true;

            for (SlimefunItem item : category.getItems()) {
                if (Slimefun.isEnabled(p, item, false)) {
                    locked = false;
                    break;
                }
            }
            if (locked) {
                continue;
            }
            if (!(category instanceof LockedCategory)) {
                if (!(category instanceof SeasonCategory)) {
                    menu.addItem(index, category.getItem());
                    menu.addMenuClickHandler(index, (player, slot, itemStack, clickAction) -> {
                        SlimefunGuide.openCategory(player, category, survival, 1);
                        return false;
                    });
                    index++;
                    continue;
                }
                if (((SeasonCategory) category).isUnlocked() || (!survival)) {
                    menu.addItem(index, category.getItem());
                    menu.addMenuClickHandler(index, (player, slot, itemStack, clickAction) -> {
                        SlimefunGuide.openCategory(player, category, survival, 1);
                        return false;
                    });
                    index++;
                }
                continue;
            }
            if (((LockedCategory) category).hasUnlocked(p) || (!survival)) {
                menu.addItem(index, category.getItem());
                menu.addMenuClickHandler(index, (player, slot, item, action) -> {
                    SlimefunGuide.openCategory(player, category, survival, 1);
                    return false;
                });
                index++;
                continue;
            }
            List<String> parents = new ArrayList<>();
            parents.add("");
            parents.add(ChatColor.translateAlternateColorCodes('&', "&r你需要先解锁所有"));
            parents.add(ChatColor.translateAlternateColorCodes('&', "&r来自以下系列的物品:"));
            parents.add("");
            for (Category parent : ((LockedCategory) category).getParents()) {
                parents.add(parent.getItem().getItemMeta().getDisplayName());
            }
            menu.addItem(index, new CustomItem(Material.BARRIER, "&4未解锁 &7- &r" + category.getItem().getItemMeta().getDisplayName(), 0, parents.toArray(new String[0])));
            menu.addMenuClickHandler(index, (arg0, arg1, arg2, arg3) -> false);
            index++;
        }


        final int finalPages = pages;

        menu.addItem(46, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5), "&r⇦ 上一页", "", "&7(" + selected_page + " / " + pages + ")"));
        menu.addMenuClickHandler(46, (arg0, arg1, arg2, arg3) -> {
            int next = selected_page - 1;
            if (next < 1) {
                next = finalPages;
            }
            if (next != selected_page) {
                SlimefunGuide.openMainMenu(p, survival, next);
            }
            return false;
        });

        menu.addItem(52, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5), "&r下一页 ⇨", "", "&7(" + selected_page + " / " + pages + ")"));
        menu.addMenuClickHandler(52, (arg0, arg1, arg2, arg3) -> {
            int next = selected_page + 1;
            if (next > finalPages) {
                next = 1;
            }
            if (next != selected_page) {
                SlimefunGuide.openMainMenu(p, survival, next);
            }
            return false;
        });

        menu.open(p);

    }

    public static String shorten(String string, String string2) {
        if (ChatColor.stripColor(string + string2).length() > 19) {
            return (string + ChatColor.stripColor(string2)).substring(0, 18) + "...";
        }
        return string + ChatColor.stripColor(string2);
    }

    public static void openCategory(final Player p, final Category category, final boolean survival, final int selected_page) {
        if (category == null) {
            return;
        }

        ChestMenu menu = new ChestMenu("Slimefun4 向导");

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(p13 -> p13.playSound(p13.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.7F, 0.7F));

        int index = 9;
        final int pages = category.getItems().size() / 36 + 1;
        int i;
        for (i = 0; i < 9; i++) {
            if (i == 1 || i == 7) {
                continue;
            }
            menu.addItem(i, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), " "));
            menu.addMenuClickHandler(i, (player, slot, itemStack, clickAction) -> false);
        }

        menu.addItem(1, new CustomItem(new ItemStack(Material.ENCHANTED_BOOK), "&7⇦ 返回"));
        menu.addMenuClickHandler(1, (arg0, arg1, arg2, arg3) -> {
            SlimefunGuide.openMainMenu(p, survival, 1);
            return false;
        });

        menu.addItem(7, new CustomItem(new ItemStack(Material.NAME_TAG), "§7搜索...", "", "&7⇨ §b点击搜索物品"));
        menu.addMenuClickHandler(7, (player, arg1, arg2, arg3) -> {
            player.closeInventory();
            SlimefunItem.searchSlimefunItem(player, survival);
            return false;
        });

        for (i = 45; i < 54; i++) {
            menu.addItem(i, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), " "));
            menu.addMenuClickHandler(i, (player, slot, itemStack, clickAction) -> false);
        }

        menu.addItem(46, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5), "&r⇦ 上一页", "", "&7(" + selected_page + " / " + pages + ")"));
        menu.addMenuClickHandler(46, (arg0, arg1, arg2, arg3) -> {
            int next = selected_page - 1;
            if (next < 1) {
                next = pages;
            }
            if (next != selected_page) {
                SlimefunGuide.openCategory(p, category, survival, next);
            }
            return false;
        });

        menu.addItem(52, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5), "&r下一页 ⇨", "", "&7(" + selected_page + " / " + pages + ")"));
        menu.addMenuClickHandler(52, (arg0, arg1, arg2, arg3) -> {
            int next = selected_page + 1;
            if (next > pages) {
                next = 1;
            }
            if (next != selected_page) {
                SlimefunGuide.openCategory(p, category, survival, next);
            }
            return false;
        });

        int category_index = 36 * (selected_page - 1);
        for (int j = 0; j < 36; j++) {
            int target = category_index + j;
            if (target >= category.getItems().size()) {
                break;
            }
            SlimefunItem sfitem = category.getItems().get(target);
            if (Slimefun.isEnabled(p, sfitem, false)) {
                if (survival && !Slimefun.hasUnlocked(p, sfitem.getItem(), false) && sfitem.getResearch() != null) {
                    if (Slimefun.hasPermission(p, sfitem, false)) {
                        final Research research = sfitem.getResearch();
                        menu.addItem(index, new CustomItem(Material.BARRIER, "&r" + StringUtils.formatItemName(sfitem.getItem(), false), 0, new String[]{"&4&l未解锁", "", "&a> 点击解锁", "", "&7消耗: &b" + research.getCost() + " 级"}));
                        menu.addMenuClickHandler(index, (p12, slot, item, action) -> {
                            if (!Research.isResearching(p12)) {
                                if (research.canUnlock(p12)) {
                                    if (research.hasUnlocked(p12)) {
                                        SlimefunGuide.openCategory(p12, category, true, selected_page);
                                    } else {
                                        if (p12.getGameMode() != GameMode.CREATIVE || !Research.creative_research) {
                                            p12.setLevel(p12.getLevel() - research.getCost());
                                        }
                                        if (p12.getGameMode() == GameMode.CREATIVE) {
                                            research.unlock(p12, Research.creative_research);
                                            SlimefunGuide.openCategory(p12, category, survival, selected_page);
                                        } else {
                                            research.unlock(p12, false);
                                            Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> SlimefunGuide.openCategory(p12, category, survival, selected_page), 103L);
                                        }
                                    }
                                } else {
                                    Messages.local.sendTranslation(p12, "messages.not-enough-xp", true);
                                }
                            }
                            return false;
                        });
                        index++;
                    } else {
                        menu.addItem(index, new CustomItem(Material.BARRIER, StringUtils.formatItemName(sfitem.getItem(), false), 0, new String[]{"", "&r你没有权限", "&r查看这个物品"}));
                        menu.addMenuClickHandler(index, (arg0, arg1, arg2, arg3) -> false);
                        index++;
                    }
                } else {
                    menu.addItem(index, sfitem.getItem());
                    menu.addMenuClickHandler(index, (p1, slot, item, action) -> {
                        if (survival) {
                            SlimefunGuide.displayItem(p1, item, true, 0);
                        } else {
                            p1.getInventory().addItem(item);
                        }
                        return false;
                    });
                    index++;
                }
            }
        }
        menu.open(p);
        if (survival) {
            addToHistory(p, category.getURID());
        }
    }

    public static void addToHistory(Player p, URID urid) {
        List<URID> list = new ArrayList<>();
        if (history.containsKey(p.getUniqueId())) {
            list = history.get(p.getUniqueId());
        }
        list.add(urid);
        history.put(p.getUniqueId(), list);
    }

    private static URID getLastEntry(Player p, boolean remove) {
        List<URID> list = new ArrayList<>();
        if (history.containsKey(p.getUniqueId())) {
            list = history.get(p.getUniqueId());
        }
        if (remove && list.size() >= 1) {
            URID urid = list.get(list.size() - 1);
            urid.markDirty();
            list.remove(urid);
        }
        if (list.isEmpty()) {
            history.remove(p.getUniqueId());
        } else {
            history.put(p.getUniqueId(), list);
        }
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }


    public static void displayItem(Player p, final ItemStack item, boolean addToHistory, final int page) {
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        final SlimefunItem sfItem = SlimefunItem.getByItem(item);
        if (sfItem == null &&
                !all_recipes) {
            return;
        }
        ItemStack[] recipe = new ItemStack[9];
        ItemStack recipeType = null;
        ItemStack recipeOutput = item;

        //物品具体合成菜单
        ChestMenu menu = new ChestMenu(item.getItemMeta().getDisplayName());

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(p120 -> p120.playSound(p120.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.7F, 0.7F));

        if (sfItem != null) {
            recipe = sfItem.getRecipe();
            recipeType = sfItem.getRecipeType().toItem();
            recipeOutput = (sfItem.getRecipeOutput() != null) ? sfItem.getRecipeOutput() : sfItem.getItem();
        } else {
            List<Recipe> recipes = new ArrayList<>();
            Iterator<Recipe> iterator = Bukkit.recipeIterator();
            while (iterator.hasNext()) {
                Recipe recipe1 = iterator.next();
                if (SlimefunManager.isItemSimiliar(new CustomItem(recipe1.getResult(), 1), item, true) && recipe1.getResult().getData().getData() == item.getData().getData()) {
                    recipes.add(recipe1);
                }
            }
            if (recipes.isEmpty()) {
                return;
            }
            Recipe r = recipes.get(page);

            if (recipes.size() > page + 1) {
                menu.addItem(1, new CustomItem(new ItemStack(Material.ENCHANTED_BOOK), "&7下一页 ⇨", "", "&e&l! &r这个物品有多重合成方式"));
                menu.addMenuClickHandler(1, (p119, slot, stack, action) -> {
                    SlimefunGuide.displayItem(p119, item, false, page + 1);
                    return false;
                });
            }

            if (r instanceof ShapedRecipe) {
                String[] shape = ((ShapedRecipe) r).getShape();
                for (int i = 0; i < shape.length; i++) {
                    for (int j = 0; j < shape[i].length(); j++) {
                        ItemStack ingredient = ((ShapedRecipe) r).getIngredientMap().get(shape[i].charAt(j));
                        if (ingredient != null) {
                            MaterialData data = ingredient.getData();
                            if (ingredient.getData().getData() < 0) {
                                data.setData((byte) 0);
                            }
                            ingredient = data.toItemStack(ingredient.getAmount());
                        }
                        recipe[i * 3 + j] = ingredient;
                    }
                }
                recipeType = RecipeType.SHAPED_RECIPE.toItem();
                recipeOutput = r.getResult();
            } else if (r instanceof ShapelessRecipe) {
                List<ItemStack> ingredients = ((ShapelessRecipe) r).getIngredientList();
                for (int i = 0; i < ingredients.size(); i++) {
                    ItemStack ingredient = ingredients.get(i);
                    if (ingredient != null) {
                        MaterialData data = ingredient.getData();
                        if (ingredient.getData().getData() < 0) {
                            data.setData((byte) 0);
                        }
                        ingredient = data.toItemStack(ingredient.getAmount());
                    }
                    recipe[i] = ingredient;
                }
                recipeType = RecipeType.SHAPELESS_RECIPE.toItem();
                recipeOutput = r.getResult();
            } else if (r instanceof FurnaceRecipe) {
                ItemStack ingredient = ((FurnaceRecipe) r).getInput();
                if (ingredient != null) {
                    MaterialData data = ingredient.getData();
                    if (ingredient.getData().getData() < 0) {
                        data.setData((byte) 0);
                    }
                    ingredient = data.toItemStack(ingredient.getAmount());
                }
                recipe[4] = ingredient;

                recipeType = RecipeType.FURNACE.toItem();
                recipeOutput = r.getResult();
            }
        }


        if (addToHistory) {
            addToHistory(p, (sfItem != null) ? sfItem.getURID() : URID.nextURID(item, true));
        }

        if (history.containsKey(p.getUniqueId()) && history.get(p.getUniqueId()).size() > 1) {
            menu.addItem(0, new CustomItem(new ItemStack(Material.ENCHANTED_BOOK), "&7⇦ 返回", "", "&r左键点击: &7返回上一页", "&rShift + 左键点击: &7返回主菜单"));
            menu.addMenuClickHandler(0, (p118, slot, item118, action) -> {
                if (action.isShiftClicked()) {
                    SlimefunGuide.openMainMenu(p118, true, 1);
                } else {
                    URID last = SlimefunGuide.getLastEntry(p118, true);
                    if (URID.decode(last) instanceof Category) {
                        SlimefunGuide.openCategory(p118, (Category) URID.decode(last), true, 1);
                    } else if (URID.decode(last) instanceof SlimefunItem) {
                        SlimefunGuide.displayItem(p118, ((SlimefunItem) URID.decode(last)).getItem(), false, 0);
                    } else if (URID.decode(last) instanceof GuideHandler) {
                        ((GuideHandler) URID.decode(last)).run(p118, true, false);
                    } else {
                        SlimefunGuide.displayItem(p118, (ItemStack) URID.decode(last), false, 0);
                    }
                }
                return false;
            });
        } else {

            menu.addItem(0, new CustomItem(new ItemStack(Material.ENCHANTED_BOOK), "&7⇦ 返回", "", "&r左键点击: &7返回主菜单"));
            menu.addMenuClickHandler(0, (p117, slot, item117, action) -> {
                SlimefunGuide.openMainMenu(p117, true, 1);
                return false;
            });
        }

        menu.addItem(3, Slimefun.hasUnlocked(p, recipe[0], false) ? recipe[0] : new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[0], false), 0, new String[]{"&4&l未解锁", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[0]), false) ? "&r需要在其他地方解锁" : "&r无权限"}));
        menu.addMenuClickHandler(3, (p116, slot, item116, action) -> {
            SlimefunGuide.displayItem(p116, item116, true, 0);
            return false;
        });

        menu.addItem(4, Slimefun.hasUnlocked(p, recipe[1], false) ? recipe[1] : new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[1], false), 0, new String[]{"&4&l未解锁", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[1]), false) ? "&r需要在其他地方解锁" : "&r无权限"}));
        menu.addMenuClickHandler(4, (p115, slot, item115, action) -> {
            SlimefunGuide.displayItem(p115, item115, true, 0);
            return false;
        });

        menu.addItem(5, Slimefun.hasUnlocked(p, recipe[2], false) ? recipe[2] : new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[2], false), 0, new String[]{"&4&l未解锁", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[2]), false) ? "&r需要在其他地方解锁" : "&r无权限"}));
        menu.addMenuClickHandler(5, (p114, slot, item114, action) -> {
            SlimefunGuide.displayItem(p114, item114, true, 0);
            return false;
        });

        if (sfItem != null) {
            if (Slimefun.getItemConfig().contains(sfItem.getID() + ".wiki")) {
                try {
                    menu.addItem(8, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY2OTJmOTljYzZkNzgyNDIzMDQxMTA1NTM1ODk0ODQyOThiMmU0YTAyMzNiNzY3NTNmODg4ZTIwN2VmNSJ9fX0="), "&r查看这个物品的介绍百科 &7(Slimefun Wiki)", "", "&7⇨ 点击打开"));
                    menu.addMenuClickHandler(8, (p113, slot, item113, action) -> {
                        p113.closeInventory();
                        p113.sendMessage("");
                        p113.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&o" + Slimefun.getItemConfig().getString(sfItem.getID() + ".wiki")));
                        p113.sendMessage("");
                        return false;
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (Slimefun.getItemConfig().contains(sfItem.getID() + ".youtube")) {
                try {
                    menu.addItem(7, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjQzNTNmZDBmODYzMTQzNTM4NzY1ODYwNzViOWJkZjBjNDg0YWFiMDMzMWI4NzJkZjExYmQ1NjRmY2IwMjllZCJ9fX0="), "&r示例视频 &7(Youtube)", "", "&7⇨ 点击观看"));
                    menu.addMenuClickHandler(7, (player, slot, item112, action) -> {
                        player.closeInventory();
                        player.sendMessage("");
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&o" + Slimefun.getItemConfig().getString(sfItem.getID() + ".youtube")));
                        player.sendMessage("");
                        return false;
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        menu.addItem(10, recipeType);
        menu.addMenuClickHandler(10, (player, slot, item111, action) -> false);

        menu.addItem(12, Slimefun.hasUnlocked(p, recipe[3], false) ? recipe[3] : new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[3], false), 0, new String[]{"&4&l未解锁", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[3]), false) ? "&r需要在其他地方解锁" : "&r无权限"}));
        menu.addMenuClickHandler(12, (player, slot, item110, action) -> {
            SlimefunGuide.displayItem(player, item110, true, 0);
            return false;
        });

        menu.addItem(13, Slimefun.hasUnlocked(p, recipe[4], false) ? recipe[4] : new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[4], false), 0, new String[]{"&4&l未解锁", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[4]), false) ? "&r需要在其他地方解锁" : "&r无权限"}));
        menu.addMenuClickHandler(13, (player, slot, item19, action) -> {
            SlimefunGuide.displayItem(player, item19, true, 0);
            return false;
        });

        menu.addItem(14, Slimefun.hasUnlocked(p, recipe[5], false) ? recipe[5] : new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[5], false), 0, new String[]{"&4&l未解锁", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[5]), false) ? "&r需要在其他地方解锁" : "&r无权限"}));
        menu.addMenuClickHandler(14, (player, slot, item18, action) -> {
            SlimefunGuide.displayItem(player, item18, true, 0);
            return false;
        });

        menu.addItem(16, recipeOutput);
        menu.addMenuClickHandler(16, (p17, slot, item17, action) -> false);


        if (p.isOp() && sfItem != null) {
            menu.addItem(18, new CustomItem(Material.BOOK, ChatColor.AQUA + "物品ID: " + ChatColor.YELLOW + sfItem.getID(), 0));
        }

        menu.addItem(21, Slimefun.hasUnlocked(p, recipe[6], false) ? recipe[6] : new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[6], false), 0, new String[]{"&4&l未解锁", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[6]), false) ? "&r需要在其他地方解锁" : "&r无权限"}));
        menu.addMenuClickHandler(21, (player, slot, item16, action) -> {
            SlimefunGuide.displayItem(player, item16, true, 0);
            return false;
        });

        menu.addItem(22, Slimefun.hasUnlocked(p, recipe[7], false) ? recipe[7] : new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[7], false), 0, new String[]{"&4&l未解锁", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[7]), false) ? "&r需要在其他地方解锁" : "&r无权限"}));
        menu.addMenuClickHandler(22, (player, slot, item15, action) -> {
            SlimefunGuide.displayItem(player, item15, true, 0);
            return false;
        });

        menu.addItem(23, Slimefun.hasUnlocked(p, recipe[8], false) ? recipe[8] : new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[8], false), 0, new String[]{"&4&l未解锁", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[8]), false) ? "&r需要在其他地方解锁" : "&r无权限"}));
        menu.addMenuClickHandler(23, (player, slot, item14, action) -> {
            SlimefunGuide.displayItem(player, item14, true, 0);
            return false;
        });

        if (sfItem != null) {
            if ((sfItem instanceof SlimefunMachine && ((SlimefunMachine) sfItem).getDisplayRecipes().size() > 0) || (sfItem instanceof SlimefunGadget && ((SlimefunGadget) sfItem).getRecipes().size() > 0)) {
                for (int i = 27; i < 36; i++) {
                    menu.addItem(i, new CustomItem(Material.STAINED_GLASS_PANE, (SlimefunItem.getByItem(item) instanceof SlimefunMachine) ? "&7⇩ 合成需要在这个机器中进行 ⇩" : " ", 7));
                    menu.addMenuClickHandler(i, (player, slot, itemStack, clickAction) -> false);
                }

                List<ItemStack> recipes = (SlimefunItem.getByItem(item) instanceof SlimefunMachine) ? ((SlimefunMachine) SlimefunItem.getByItem(item)).getDisplayRecipes() : ((SlimefunGadget) SlimefunItem.getByItem(item)).getDisplayRecipes();
                int recipe_size = recipes.size();
                if (recipe_size > 18) {
                    recipe_size = 18;
                }
                int inputs = -1, outputs = -1;

                for (int j = 0; j < recipe_size; j++) {
                    int slot = 36;
                    if (j % 2 == 1) {
                        slot += 9;
                        outputs++;
                    } else {
                        inputs++;
                    }
                    int addition = (j % 2 == 0) ? inputs : outputs;

                    menu.addItem(slot + addition, recipes.get(j));
                    menu.addMenuClickHandler(slot + addition, (p13, slot13, item13, action) -> {
                        SlimefunGuide.displayItem(p13, item13, true, 0);
                        return false;
                    });
                }

            } else if (sfItem instanceof AGenerator) {
                int slot = 27;
                for (MachineFuel fuel : ((AGenerator) sfItem).getFuelTypes()) {
                    if (slot >= 54) {
                        break;
                    }
                    ItemStack fItem = fuel.getInput().clone();
                    ItemMeta im = fItem.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.translateAlternateColorCodes('&', "&8⇨ &7耗时 " + getTimeLeft(fuel.getTicks() / 2)));
                    lore.add(ChatColor.translateAlternateColorCodes('&', "&8⇨ &e⚡ &7" + (((AGenerator) sfItem).getEnergyProduction() * 2) + " J/s"));
                    lore.add(ChatColor.translateAlternateColorCodes('&', "&8⇨ &e⚡ &7总计能量 " + DoubleHandler.getFancyDouble((fuel.getTicks() * ((AGenerator) sfItem).getEnergyProduction())) + " J"));
                    im.setLore(lore);
                    fItem.setItemMeta(im);
                    menu.addItem(slot, fItem);
                    menu.addMenuClickHandler(slot, (p12, slot12, item12, action) -> false);
                    slot++;
                }

            } else if (sfItem instanceof AReactor) {
                int slot = 27;
                for (MachineFuel fuel : ((AReactor) sfItem).getFuelTypes()) {
                    if (slot >= 54) {
                        break;
                    }
                    ItemStack fItem = fuel.getInput().clone();
                    ItemMeta im = fItem.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.translateAlternateColorCodes('&', "&8⇨ &7耗时 " + getTimeLeft(fuel.getTicks() / 2)));
                    lore.add(ChatColor.translateAlternateColorCodes('&', "&8⇨ &e⚡ &7" + (((AReactor) sfItem).getEnergyProduction() * 2) + " J/s"));
                    lore.add(ChatColor.translateAlternateColorCodes('&', "&8⇨ &e⚡ &7总计能量 " + DoubleHandler.getFancyDouble((fuel.getTicks() * ((AReactor) sfItem).getEnergyProduction())) + " J"));
                    im.setLore(lore);
                    fItem.setItemMeta(im);
                    menu.addItem(slot, fItem);
                    menu.addMenuClickHandler(slot, (p1, slot1, item1, action) -> false);
                    slot++;
                }
            }
        }

        menu.build().open(p);
    }

    public static void clearHistory(UUID uuid) {
        if (!history.containsKey(uuid)) {
            return;
        }
        for (URID urid : history.get(uuid)) {
            urid.markDirty();
        }
        history.remove(uuid);
    }

    private static String getTimeLeft(int l) {
        String timeleft = "";
        int minutes = (int) (l / 60L);
        if (minutes > 0) {
            timeleft = timeleft + minutes + "m ";
        }
        l -= minutes * 60;
        int seconds = l;
        timeleft = timeleft + seconds + "s";
        return "&7" + timeleft;
    }
}


