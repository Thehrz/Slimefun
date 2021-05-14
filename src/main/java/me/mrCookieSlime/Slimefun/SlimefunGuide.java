package me.mrCookieSlime.Slimefun;

import io.izzel.taboolib.module.i18n.version.I18nOrigin;
import io.izzel.taboolib.util.item.ItemBuilder;
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
    private static final int[] slots = new int[]{1, 3, 5, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
    public static Map<UUID, List<URID>> history = new HashMap<>();
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
                menu.addMenuClickHandler(19, (player, slot, itemStack, clickAction) -> {
                    player.getInventory().setItemInMainHand(SlimefunGuide.getItem(BookDesign.CHEAT_SHEET));
                    SlimefunGuide.openSettings(player, player.getInventory().getItemInMainHand());
                    return false;
                });
            } else {
                menu.addItem(19, new CustomItem(new ItemStack(Material.CHEST), "&7界面布局: &e箱子界面"));
                menu.addMenuClickHandler(19, (player, slot, itemStack, clickAction) -> false);
            }

        } else if (SlimefunManager.isItemSimiliar(guide, getItem(BookDesign.CHEAT_SHEET), true)) {
            menu.addItem(19, new CustomItem(new ItemStack(Material.COMMAND), "&7界面布局: &e作弊面板", "", "&7箱子界面", "&a作弊面板", "", "&e 点击 &8⇨ &7修改布局"));
            menu.addMenuClickHandler(19, (player, slot, itemStack, clickAction) -> {
                player.getInventory().setItemInMainHand(SlimefunGuide.getItem(BookDesign.CHEST));
                SlimefunGuide.openSettings(player, player.getInventory().getItemInMainHand());
                return false;
            });
        }

        menu.addItem(0, new CustomItem(new ItemStack(Material.ENCHANTED_BOOK), "&7⇦ 返回"));
        menu.addMenuClickHandler(0, (player, slot, itemStack, clickAction) -> {
            SlimefunGuide.openMainMenu(p, true, 1);
            return false;
        });

        try {
            menu.addItem(2, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg3ZjZlNzFjYzUyMmM3ZjRmMTZkYTI5ODFjMWI5MmUyZDg1Y2EyM2NlNjA4MWVjNTY5OWI1YjBlMmE3In19fQ=="), "&a百宝箱"));
            menu.addMenuClickHandler(2, (player, slot, itemStack, clickAction) -> false);
        } catch (Exception e) {
            System.err.println("SlimefunGuide 头颅异常");
            e.printStackTrace();
        }

        menu.addItem(4, new CustomItem(new ItemStack(Material.BOOK_AND_QUILL), "&a版本信息", "", "&7版本: &a" + SlimefunStartup.instance.getDescription().getVersion(), "&7贡献者: &e" + contributors.size(), "", "&7⇨ 点击查看重置内容"));
        menu.addMenuClickHandler(4, (player, slot, itemStack, clickAction) -> {
            SlimefunGuide.openCredits(player, player.getInventory().getItemInMainHand());
            return false;
        });


        try {
            menu.addItem(6, new CustomItem(new ItemStack(Material.REDSTONE_COMPARATOR), "&e源码", "", "&7代码行数: &6" + IntegerFormat.formatBigNumber(code_bytes), "&7上次更新时间: &a" + IntegerFormat.timeDelta(last_update) + " 之前", "&7Forks: &e" + forks, "&7Stars: &e" + stars, "", "&7&oSlimefun 4 是一个社区项目,", "&7&o源代码可以在GitHub上查看", "&7&o如果你想保持这个项目的活跃,", "&7&o请考虑为这个项目作贡献", "", "&7⇨ 点击前往GitHub"));
            menu.addMenuClickHandler(6, (player, slot, itemStack, clickAction) -> {
                player.closeInventory();
                player.sendMessage("");
                player.sendMessage("§7§ohttps://github.com/Slimefun/Slimefun4");
                player.sendMessage("");
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        menu.addItem(8, new CustomItem(new ItemStack(Material.KNOWLEDGE_BOOK), "&3Slimefun4 WIKI", "", "&a⇨ 点击前往 Slimefun4 中文WIKI"));
        menu.addMenuClickHandler(8, (player, slot, itemStack, clickAction) -> {
            player.closeInventory();
            player.sendMessage("");
            player.sendMessage("§7§ohttps://mineplugin.org/SlimeFun4");
            player.sendMessage("");
            return false;
        });

        menu.open(p);
    }


    public static void openCredits(Player p, final ItemStack guide) {
//        final ChestMenu menu = new ChestMenu("§4贡献者鸣谢");
//
//        menu.setEmptySlotsClickable(false);
//        menu.addMenuOpeningHandler((player) -> player.playSound(p.getLocation(), Sound.BLOCK_NOTE_HARP, 0.7F, 0.7F));
//
//        for (int i = 0; i < 9; i++) {
//            if (i != 4) {
//                menu.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "));
//                menu.addMenuClickHandler(i, (player, slot, itemStack, clickAction) -> false);
//            } else {
//                menu.addItem(4, new CustomItem(new MaterialData(Material.EMERALD), "&7\u21E6 返回设置"));
//                menu.addMenuClickHandler(4, (player, slot, itemStack, clickAction) -> {
//                    openSettings(p, guide);
//                    return false;
//                });
//            }
//        }
//
//        int index = 9;
//
//        double total = 0;
//
//        for (Contributor contributor : contributors) {
//            total += contributor.getCommits();
//        }
//
//        for (final Contributor contributor : contributors) {
//            ItemStack skull = new SkullItem("&a" + contributor.getName(), contributor.getName());
//
//            ItemMeta meta = skull.getItemMeta();
//
//            if (contributor.getCommits() > 0) {
//                double percentage = DoubleHandler.fixDouble((contributor.getCommits() * 100.0) / total, 2);
//
//                meta.setLore(Arrays.asList("", ChatColor.translateAlternateColorCodes('&', "&7Role: &r" + contributor.getJob()), ChatColor.translateAlternateColorCodes('&', "&7Contributions: &r" + contributor.getCommits() + " commits &7(&r" + percentage + "%&7)"), "", ChatColor.translateAlternateColorCodes('&', "&7\u21E8 Click to view my GitHub profile")));
//            } else {
//                meta.setLore(Arrays.asList("", ChatColor.translateAlternateColorCodes('&', "&7Role: &r" + contributor.getJob())));
//            }
//
//            skull.setItemMeta(meta);
//
//            menu.addItem(index, skull);
//            menu.addMenuClickHandler(index, (player, slot, itemStack, clickAction) -> {
//                if (contributor.getCommits() > 0) {
//                    p.closeInventory();
//                    p.sendMessage("");
//                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&o" + contributor.getProfile()));
//                    p.sendMessage("");
//                }
//                return false;
//            });
//
//            index++;
//        }
//
//        for (int i = 0; i < 9; i++) {
//            menu.addItem(36 + i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "));
//            menu.addMenuClickHandler(36 + i, (player, slot, itemStack, clickAction) -> false);
//        }
//
//        menu.open(p);
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
        menu.addMenuClickHandler(1, (player, slot, itemStack, clickAction) -> {
            openSettings(player, player.getInventory().getItemInMainHand());
            return false;
        });

        menu.addItem(7, new CustomItem(new ItemStack(Material.NAME_TAG), "§7搜索...", "", "&7⇨ §b点击搜索物品"));
        menu.addMenuClickHandler(7, (player, slot, itemStack, clickAction) -> {
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
            menu.addMenuClickHandler(index, (player, slot, itemStack, clickAction) -> false);
            index++;
        }

        int finalPages = pages;

        menu.addItem(46, (selected_page > 1) ?
                new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5), "&r⇦ 上一页", "", "&7(" + selected_page + " / " + finalPages + ")") :
                new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), "&r⇦ 上一页", "", "&7(" + selected_page + " / " + finalPages + ")"), (player, slot, itemStack, clickAction) -> {
            int next = selected_page - 1;
            if (next >= 1) {
                SlimefunGuide.openMainMenu(p, survival, next);
            }
            return false;
        });

        menu.addItem(52, (selected_page < pages) ?
                new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5), "&r下一页 ⇨", "", "&7(" + selected_page + " / " + finalPages + ")") :
                new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), "&r下一页 ⇨", "", "&7(" + selected_page + " / " + finalPages + ")"), (player, slot, itemStack, clickAction) -> {
            int next = selected_page + 1;
            if (next <= finalPages) {
                SlimefunGuide.openMainMenu(player, survival, next);
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

        ChestMenu menu = new ChestMenu(category.getItem().getItemMeta().getDisplayName());

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(player -> player.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.7F, 0.7F));

        int index = 9;
        final int pages = category.getItems().size() / 36 + ((category.getItems().size() % 36 >= 1) ? 1 : 0);
        int i;
        for (i = 0; i < 9; i++) {
            if (i == 1 || i == 7) {
                continue;
            }
            menu.addItem(i, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), " "));
            menu.addMenuClickHandler(i, (player, slot, itemStack, clickAction) -> false);
        }

        menu.addItem(1, new CustomItem(new ItemStack(Material.ENCHANTED_BOOK), "&7⇦ 返回"));
        menu.addMenuClickHandler(1, (player, slot, itemStack, clickAction) -> {
            SlimefunGuide.openMainMenu(player, survival, 1);
            return false;
        });

        menu.addItem(7, new CustomItem(new ItemStack(Material.NAME_TAG), "§7搜索...", "", "&7⇨ §b点击搜索物品"));
        menu.addMenuClickHandler(7, (player, slot, itemStack, clickAction) -> {
            player.closeInventory();
            SlimefunItem.searchSlimefunItem(player, survival);
            return false;
        });

        for (i = 45; i < 54; i++) {
            menu.addItem(i, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), " "));
            menu.addMenuClickHandler(i, (player, slot, itemStack, clickAction) -> false);
        }

        menu.addItem(46, (selected_page > 1) ?
                new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5), "&r⇦ 上一页", "", "&7(" + selected_page + " / " + pages + ")") :
                new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), "&r⇦ 上一页", "", "&7(" + selected_page + " / " + pages + ")"), (player, slot, itemStack, clickAction) -> {
            int next = selected_page - 1;
            if (next >= 1) {
                SlimefunGuide.openCategory(player, category, survival, next);
            }
            return false;
        });

        menu.addItem(52, (selected_page < pages) ?
                new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5), "&r下一页 ⇨", "", "&7(" + selected_page + " / " + pages + ")") :
                new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), "&r下一页 ⇨", "", "&7(" + selected_page + " / " + pages + ")"), (player, slot, itemStack, clickAction) -> {
            int next = selected_page + 1;
            if (next <= pages) {
                SlimefunGuide.openCategory(player, category, survival, next);
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
                        menu.addMenuClickHandler(index, (player, slot, itemStack, clickAction) -> {
                            if (!Research.isResearching(player)) {
                                if (research.canUnlock(player)) {
                                    if (research.hasUnlocked(player)) {
                                        SlimefunGuide.openCategory(player, category, true, selected_page);
                                    } else {
                                        if (player.getGameMode() != GameMode.CREATIVE || !Research.creative_research) {
                                            player.setLevel(player.getLevel() - research.getCost());
                                        }
                                        if (player.getGameMode() == GameMode.CREATIVE) {
                                            research.unlock(player, Research.creative_research);
                                            SlimefunGuide.openCategory(player, category, survival, selected_page);
                                        } else {
                                            research.unlock(player, false);
                                            Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> SlimefunGuide.openCategory(player, category, survival, selected_page), 103L);
                                        }
                                    }
                                } else {
                                    Messages.local.sendTranslation(player, "messages.not-enough-xp", true);
                                }
                            }
                            return false;
                        });
                        index++;
                    } else {
                        menu.addItem(index, new CustomItem(Material.BARRIER, StringUtils.formatItemName(sfitem.getItem(), false), 0, new String[]{"", "&r你没有权限", "&r查看这个物品"}));
                        menu.addMenuClickHandler(index, (player, slot, itemStack, clickAction) -> false);
                        index++;
                    }
                } else {
                    menu.addItem(index, sfitem.getItem());
                    menu.addMenuClickHandler(index, (player, slot, itemStack, clickAction) -> {
                        if (survival) {
                            SlimefunGuide.displayItem(player, itemStack, true, 0);
                        } else {
                            player.getInventory().addItem(itemStack);
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
        String menuname = (item.hasItemMeta() && item.getItemMeta().getDisplayName() != null) ? item.getItemMeta().getDisplayName() : new I18nOrigin().getName(item);

        ChestMenu menu = new ChestMenu(menuname);

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(player -> player.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.7F, 0.7F));

        if (sfItem != null) {
            recipe = sfItem.getRecipe();
            recipeType = sfItem.getRecipeType().toItem();
            recipeOutput = (sfItem.getRecipeOutput() != null) ? sfItem.getRecipeOutput() : sfItem.getItem();
        } else {
            List<Recipe> recipes = new ArrayList<>();
            Iterator<Recipe> iterator = Bukkit.recipeIterator();
            while (iterator.hasNext()) {
                Recipe next = iterator.next();
                if (SlimefunManager.isItemSimiliar(new CustomItem(next.getResult(), 1), item, true) && next.getResult().getData().getData() == item.getData().getData()) {
                    recipes.add(next);
                }
            }
            if (recipes.isEmpty()) {
                return;
            }
            Recipe r = recipes.get(page);

            if (recipes.size() > page + 1) {
                menu.addItem(1, new CustomItem(new MaterialData(Material.ENCHANTED_BOOK), "&7下一页 ⇨", "", "&e&l! &r这个物品有多重合成方式"));
                menu.addMenuClickHandler(1, (player, slot, stack, action) -> {
                    SlimefunGuide.displayItem(player, item, false, page + 1);
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
            menu.addMenuClickHandler(0, (player, slot, itemStack, clickAction) -> {
                if (clickAction.isShiftClicked()) {
                    SlimefunGuide.openMainMenu(player, true, 1);
                } else {
                    URID last = SlimefunGuide.getLastEntry(player, true);
                    if (URID.decode(last) instanceof Category) {
                        SlimefunGuide.openCategory(player, (Category) URID.decode(last), true, 1);
                    } else if (URID.decode(last) instanceof SlimefunItem) {
                        SlimefunGuide.displayItem(player, ((SlimefunItem) URID.decode(last)).getItem(), false, 0);
                    } else if (URID.decode(last) instanceof GuideHandler) {
                        ((GuideHandler) URID.decode(last)).run(player, true, false);
                    } else {
                        SlimefunGuide.displayItem(player, (ItemStack) URID.decode(last), false, 0);
                    }
                }
                return false;
            });
        } else {

            menu.addItem(0, new CustomItem(new ItemStack(Material.ENCHANTED_BOOK), "&7⇦ 返回", "", "&r左键点击: &7返回主菜单"));
            menu.addMenuClickHandler(0, (player, slot, itemStack, clickAction) -> {
                SlimefunGuide.openMainMenu(player, true, 1);
                return false;
            });
        }

        menu.addItem(3, Slimefun.hasUnlocked(p, recipe[0], false) ? recipe[0] : new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[0], false), 0, new String[]{"&4&l未解锁", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[0]), false) ? "&r需要在其他地方解锁" : "&r无权限"}));
        menu.addMenuClickHandler(3, (player, slot, itemStack, clickAction) -> {
            SlimefunGuide.displayItem(player, itemStack, true, 0);
            return false;
        });

        menu.addItem(4, Slimefun.hasUnlocked(p, recipe[1], false) ? recipe[1] : new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[1], false), 0, new String[]{"&4&l未解锁", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[1]), false) ? "&r需要在其他地方解锁" : "&r无权限"}));
        menu.addMenuClickHandler(4, (player, slot, itemStack, clickAction) -> {
            SlimefunGuide.displayItem(player, itemStack, true, 0);
            return false;
        });

        menu.addItem(5, Slimefun.hasUnlocked(p, recipe[2], false) ? recipe[2] : new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[2], false), 0, new String[]{"&4&l未解锁", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[2]), false) ? "&r需要在其他地方解锁" : "&r无权限"}));
        menu.addMenuClickHandler(5, (player, slot, itemStack, clickAction) -> {
            SlimefunGuide.displayItem(player, itemStack, true, 0);
            return false;
        });

        if (sfItem != null) {
            if (Slimefun.getItemConfig().contains(sfItem.getID() + ".wiki")) {
                try {
                    menu.addItem(8, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY2OTJmOTljYzZkNzgyNDIzMDQxMTA1NTM1ODk0ODQyOThiMmU0YTAyMzNiNzY3NTNmODg4ZTIwN2VmNSJ9fX0="), "&r查看这个物品的介绍百科 &7(Slimefun Wiki)", "", "&7⇨ 点击打开"));
                    menu.addMenuClickHandler(8, (player, slot, itemStack, clickAction) -> {
                        player.closeInventory();
                        player.sendMessage("");
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&o" + Slimefun.getItemConfig().getString(sfItem.getID() + ".wiki")));
                        player.sendMessage("");
                        return false;
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (Slimefun.getItemConfig().contains(sfItem.getID() + ".youtube")) {
                try {
                    menu.addItem(7, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjQzNTNmZDBmODYzMTQzNTM4NzY1ODYwNzViOWJkZjBjNDg0YWFiMDMzMWI4NzJkZjExYmQ1NjRmY2IwMjllZCJ9fX0="), "&r示例视频 &7(Youtube)", "", "&7⇨ 点击观看"));
                    menu.addMenuClickHandler(7, (player, slot, itemStack, clickAction) -> {
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
        menu.addMenuClickHandler(10, (player, slot, itemStack, clickAction) -> false);

        menu.addItem(12, Slimefun.hasUnlocked(p, recipe[3], false) ? recipe[3] : new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[3], false), 0, new String[]{"&4&l未解锁", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[3]), false) ? "&r需要在其他地方解锁" : "&r无权限"}));
        menu.addMenuClickHandler(12, (player, slot, itemStack, clickAction) -> {
            SlimefunGuide.displayItem(player, itemStack, true, 0);
            return false;
        });

        menu.addItem(13, Slimefun.hasUnlocked(p, recipe[4], false) ? recipe[4] : new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[4], false), 0, new String[]{"&4&l未解锁", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[4]), false) ? "&r需要在其他地方解锁" : "&r无权限"}));
        menu.addMenuClickHandler(13, (player, slot, itemStack, clickAction) -> {
            SlimefunGuide.displayItem(player, itemStack, true, 0);
            return false;
        });

        menu.addItem(14, Slimefun.hasUnlocked(p, recipe[5], false) ? recipe[5] : new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[5], false), 0, new String[]{"&4&l未解锁", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[5]), false) ? "&r需要在其他地方解锁" : "&r无权限"}));
        menu.addMenuClickHandler(14, (player, slot, itemStack, clickAction) -> {
            SlimefunGuide.displayItem(player, itemStack, true, 0);
            return false;
        });

        menu.addItem(16, recipeOutput);
        menu.addMenuClickHandler(16, (player, slot, itemStack, clickAction) -> false);


        if (p.isOp() && sfItem != null) {
            menu.addItem(18, new CustomItem(Material.BOOK, ChatColor.AQUA + "物品ID: " + ChatColor.YELLOW + sfItem.getID(), 0));
        }

        menu.addItem(21, Slimefun.hasUnlocked(p, recipe[6], false) ? recipe[6] : new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[6], false), 0, new String[]{"&4&l未解锁", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[6]), false) ? "&r需要在其他地方解锁" : "&r无权限"}));
        menu.addMenuClickHandler(21, (player, slot, itemStack, clickAction) -> {
            SlimefunGuide.displayItem(player, itemStack, true, 0);
            return false;
        });

        menu.addItem(22, Slimefun.hasUnlocked(p, recipe[7], false) ? recipe[7] : new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[7], false), 0, new String[]{"&4&l未解锁", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[7]), false) ? "&r需要在其他地方解锁" : "&r无权限"}));
        menu.addMenuClickHandler(22, (player, slot, itemStack, clickAction) -> {
            SlimefunGuide.displayItem(player, itemStack, true, 0);
            return false;
        });

        menu.addItem(23, Slimefun.hasUnlocked(p, recipe[8], false) ? recipe[8] : new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[8], false), 0, new String[]{"&4&l未解锁", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[8]), false) ? "&r需要在其他地方解锁" : "&r无权限"}));
        menu.addMenuClickHandler(23, (player, slot, itemStack, clickAction) -> {
            SlimefunGuide.displayItem(player, itemStack, true, 0);
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
                    menu.addMenuClickHandler(slot + addition, (player, s, itemStack, clickAction) -> {
                        SlimefunGuide.displayItem(player, itemStack, true, 0);
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
                    menu.addMenuClickHandler(slot, (player, s, itemStack, clickAction) -> false);
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
                    menu.addMenuClickHandler(slot, (player, s, itemStack, clickAction) -> false);
                    slot++;
                }
            }
        }

        menu.open(p);
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

    public static void openSearchMenu(Player player, ArrayList<SlimefunItem> searchList, String searchString, boolean survival, int selected_page, long time) {
        int[] BORDER = {0, 1, 2, 3, 4, 5, 6, 7, 8, 45, 47, 48, 49, 50, 51, 52, 53};
        ChestMenu searchMenu = new ChestMenu("搜索: §3" + searchString + "§8(耗时: " + time + "ms)");

        for (int slot : BORDER) {
            searchMenu.addItem(slot, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), " "));
            searchMenu.addMenuClickHandler(slot, (p, i, itemStack, clickAction) -> false);
        }

        searchMenu.addItem(1, new CustomItem(new ItemStack(Material.ENCHANTED_BOOK), "&7⇦ 返回"));
        searchMenu.addMenuClickHandler(1, (p, slot, itemStack, clickAction) -> {
            openMainMenu(p, survival, 1);
            return false;
        });

        searchMenu.addItem(7, new CustomItem(new ItemStack(Material.NAME_TAG), "§7搜索...", "", "&7⇨ §b点击搜索物品"));
        searchMenu.addMenuClickHandler(7, (p, slot, itemStack, clickAction) -> {
            player.closeInventory();
            SlimefunItem.searchSlimefunItem(player, survival);
            return false;
        });

        int pages = searchList.size() / 36 + ((searchList.size() % 36 >= 1) ? 1 : 0);

        int start = 36 * (selected_page - 1);
        int end = 35 * selected_page;

        searchMenu.addItem(46, (selected_page > 1) ?
                new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5), "&r⇦ 上一页", "", "&7(" + selected_page + " / " + pages + ")") :
                new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), "&r⇦ 上一页", "", "&7(" + selected_page + " / " + pages + ")"), (p, slot, itemStack, clickAction) -> {
            int next = selected_page - 1;
            if (next >= 1) {
                openSearchMenu(p, searchList, searchString, survival, next, time);
            }
            return false;
        });

        searchMenu.addItem(52, (selected_page < pages) ?
                new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5), "&r下一页 ⇨", "", "&7(" + selected_page + " / " + pages + ")") :
                new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), "&r下一页 ⇨", "", "&7(" + selected_page + " / " + pages + ")"), (p, slot, itemStack, clickAction) -> {
            int next = selected_page + 1;
            if (next <= pages) {
                openSearchMenu(p, searchList, searchString, survival, next, time);
            }
            return false;
        });

        if (searchList.isEmpty()) {
            searchMenu.addItem(9, new ItemBuilder(Material.BARRIER).name("§c没有找到包含此关键词的物品").build(), (p, slot, itemStack, clickAction) -> false);
        } else {
            for (int i = 9; start <= end && start < searchList.size(); i++, start++) {
                SlimefunItem slimefunItem = searchList.get(start);
                searchMenu.addItem(i, new CustomItem(slimefunItem.getItem(), slimefunItem.getItem().getItemMeta().getDisplayName(), "", "⇨ " + slimefunItem.getCategory().getItem().getItemMeta().getDisplayName()));
                searchMenu.addMenuClickHandler(i, (p, slot, itemStack, clickAction) -> {
                    displayItem(p, slimefunItem.getItem(), true, 0);
                    return false;
                });
            }
        }
        searchMenu.open(player);
    }
}


