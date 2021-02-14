package me.mrCookieSlime.Slimefun.Lists;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;
import org.bukkit.inventory.ItemStack;

public class NarItems {
    public static ItemStack UU = null;
    public static ItemStack UU_MACHINE = null;
    public static ItemStack ITEM_COPER = null;
    public static ItemStack ITEM_CREATOR = null;
    public static ItemStack IRIDIUM = null;

    static {
        try {
            UU = new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGYxNGYzMTc5Yjg2ZjY5YjNlZmE3NDcyZGFjYWViMjMzOWY2MjkwZDJkODE3MzYyNzkzMzQ4YWJkOThlMDIxIn19fQ=="), "&d元物质", "", "§7这堆元物质被特殊的力场禁锢而得以保存", "§7据研究元物质是万物的最基本组成粒子", "§7珍贵的元物质有非常广泛的用途");

            UU_MACHINE = new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjY4NGY0YTZlZDE0Mjg2NWRiMDkzOGU0ODc2NzY4NDlhNTRkNjQzNzhlMmU5ZTdmNzEzYjliMWU5ZDA0MSJ9fX0="), "&d元物质分离机", "", "&7能够将特定的物质分离为的元物质", "&7并将这些元物质收集加以力场保存", "§7元物质用途广泛且极为珍贵", "", "§7▷▷ §b耗电: §e400 J/s", "§7▷▷ §b缓存: §e12800 J");

            ITEM_COPER = new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTQ2MTIwNDk5YjMyNDYzOWNkOWExNzc5OWVhMWRmZmYzNzNlNzlhZmUxZjBkOGI1MzI4Y2Y0Nzg5NmM0Nzc1In19fQ=="), "&d物质复构机", "", "&7能够将特定的物质进行复制制造", "&7但需要使用珍贵的元物质", "§7并且极为耗能", "", "§7▷▷ §b耗电: §e1280 J/s", "§7▷▷ §b缓存: §e65535 J");

            ITEM_CREATOR = new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWE3YjNiMTZlNWQwYzRjZmQyMWM0ZWI5MTMzZTk2OWFhZDdjYzczMDNjY2RmMzE3NTEyZTI2YTQ4NzliNTEifX19"), "&d物质制造机", "", "&7通过手动解析物质编码", "&7使用元物质制造出想要的新物质", "§7甚至制作出自然界不存在的元素物质", "", "§7▷▷ §b耗电: §e768 J/s", "§7▷▷ §b缓存: §e65535 J");

            IRIDIUM = new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGNmNWI4ZGViM2Q3YWZlMGY0ZmZhNjI3N2RlZjk3NjYxOWE2N2NjMTYzMTUzMTk4M2U4NTkwODI2ZWRlNjQ2MCJ9fX0="), "&d铱", "", "&7一种稀有的贵重金属", "&7可以用于制作一些高级装备");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


