package me.mrCookieSlime.Slimefun.Lists;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomArmor;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.LockedCategory;
import me.mrCookieSlime.Slimefun.Objects.SeasonCategory;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;


public class Categories {
    public static final Category WEAPONS = new Category(new CustomItem(Material.GOLD_SWORD, "&7武器", 0, new String[]{"", "&a> 点击打开"}), 1);
    public static final Category FOOD = new Category(new CustomItem(Material.APPLE, "&7食物", 0, new String[]{"", "&a> 点击打开"}), 2);
    public static final Category ARMOR = new Category(new CustomItem(Material.IRON_CHESTPLATE, "&7护甲", 0, new String[]{"", "&a> 点击打开"}), 2);
    public static final Category LUMPS_AND_MAGIC = new Category(new CustomItem(Material.FIREBALL, "&7魔法物品", 0, new String[]{"", "&a> 点击打开"}), 2);
    public static final Category MAGIC = new Category(new CustomItem(Material.BLAZE_POWDER, "&7魔法道具", 0, new String[]{"", "&a> 点击打开"}), 3);
    public static final Category TECH = new Category(new CustomArmor(new CustomItem(Material.LEATHER_CHESTPLATE, "&7科技工具", 0, new String[]{"", "&a> 点击打开"}), Color.SILVER), 3);
    public static final Category TECH_MISC = new Category(new CustomItem(Material.REDSTONE_COMPARATOR, "&7科技组件", 0, new String[]{"", "&a> 点击打开"}), 2);
    public static final Category MAGIC_ARMOR = new Category(new CustomItem(Material.GOLD_CHESTPLATE, "&7魔法防具", 0, new String[]{"", "&a> 点击打开"}), 2);
    public static final Category TALISMANS_1 = new Category(new CustomItem(Material.EMERALD, "&7护身符 - &a等级 I", 0, new String[]{"", "&a> 点击打开"}), 2);
    public static final LockedCategory TALISMANS_2 = new LockedCategory(new CustomItem(Material.EMERALD, "&7护身符 - &a等级 II", 0, new String[]{"", "&a> 点击打开"}), 3, TALISMANS_1);
    public static final Category TOOLS = new Category(new CustomItem(Material.GOLD_PICKAXE, "&7工具", 0, new String[]{"", "&a> 点击打开"}), 1);
    public static final SeasonCategory CHRISTMAS = new SeasonCategory(12, 1, new CustomItem(Material.NETHER_STAR, "&a圣诞&c物品", 0, new String[]{"", ChatColor.translateAlternateColorCodes('&', "&c圣诞气氛浓厚哦?"), "&a> 点击打开"}));
    public static final SeasonCategory VALENTINES_DAY = new SeasonCategory(2, 2, new CustomItem(Material.RED_ROSE, "&d情人节", 0, new String[]{"", ChatColor.translateAlternateColorCodes('&', "&c全世界都充满着恋爱的酸臭味"), "&a> 点击打开"}));
    public static final SeasonCategory EASTER = new SeasonCategory(4, 2, new CustomItem(Material.EGG, "&6复活节", 0, new String[]{"", ChatColor.translateAlternateColorCodes('&', "&a搞点彩蛋, 搞点事情"), "&a> 点击打开"}));
    public static final SeasonCategory BIRTHDAY = new SeasonCategory(10, 1, new CustomItem(Material.FIREWORK, "&a&lmrCookieSlime 的生日 &7(10月26日)", 0, new String[]{"", ChatColor.translateAlternateColorCodes('&', "&a为他祝福吧!"), "&a> 点击打开"}));
    public static Category PORTABLE = null;
    public static Category MACHINES_1 = null;
    public static LockedCategory ELECTRICITY = null;
    public static LockedCategory GPS = null;
    public static Category MISC = null;
    public static Category ANDROID = null;
    public static Category RESOURCES = null;
    public static Category CARGO = null;
    public static Category QUANTUM_MACHINES = null;

    static {
        try {
            MISC = new Category(new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTRkYTk3ZjA4MGUzOTViODQyYzRjYzgyYTg0MDgyM2Q0ZGJkOGNhNjg4YTIwNjg1M2U1NzgzZTRiZmRjMDEyIn19fQ=="), "&7杂项", "", "&a> 点击打开"), 2);
            PORTABLE = new Category(new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDBjYjFlNjdiNTEyYWIyZDRiZjNkN2FjZTBlYWFmNjFjMzJjZDQ2ODFkZGMzOTg3Y2ViMzI2NzA2YTMzZmEifX19"), "&7道具", "", "&a> 点击打开"), 1);
            MACHINES_1 = new Category(new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM0M2NlNThkYTU0Yzc5OTI0YTJjOTMzMWNmYzQxN2ZlOGNjYmJlYTliZTQ1YTdhYzg1ODYwYTZjNzMwIn19fQ=="), "&7基础机器", "", "&a> 点击打开"), 1);
            ELECTRICITY = new LockedCategory(new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTU4NDQzMmFmNmYzODIxNjcxMjAyNThkMWVlZThjODdjNmU3NWQ5ZTQ3OWU3YjBkNGM3YjZhZDQ4Y2ZlZWYifX19"), "&b电力与能源", "", "&a> 点击打开"), 4, MACHINES_1);
            GPS = new LockedCategory(new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBjOWMxYTAyMmY0MGI3M2YxNGI0Y2JhMzdjNzE4YzZhNTMzZjNhMjg2NGI2NTM2ZDVmNDU2OTM0Y2MxZiJ9fX0="), "&bGPS 科技机器", "", "&a> 点击打开"), 4, MACHINES_1);
            ANDROID = new LockedCategory(new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjlkMzMzNTdlODQxODgyM2JmNzgzZGU5MmRlODAyOTFiNGViZDM5MmFlYzg3MDY2OThlMDY4OTZkNDk4ZjYifX19"), "&b可编程式机器人", "", "&a> 点击打开"), 4, MACHINES_1);
            RESOURCES = new Category(new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2U4ZjVhZGIxNGQ2YzlmNmI4MTBkMDI3NTQzZjFhOGMxZjQxN2UyZmVkOTkzYzk3YmNkODljNzRmNWUyZTgifX19"), "&7资源", "", "&a> 点击打开"), 1);
            CARGO = new LockedCategory(new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTUxMGJjODUzNjJhMTMwYTZmZjlkOTFmZjExZDZmYTQ2ZDdkMTkxMmEzNDMxZjc1MTU1OGVmM2M0ZDljMiJ9fX0="), "&c运输管理系统", "", "&a> 点击打开"), 4, MACHINES_1);
            QUANTUM_MACHINES = new Category(new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjY4NGY0YTZlZDE0Mjg2NWRiMDkzOGU0ODc2NzY4NDlhNTRkNjQzNzhlMmU5ZTdmNzEzYjliMWU5ZDA0MSJ9fX0="), "&b量子科技&7-&e机器", "", "&a> 点击打开"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


