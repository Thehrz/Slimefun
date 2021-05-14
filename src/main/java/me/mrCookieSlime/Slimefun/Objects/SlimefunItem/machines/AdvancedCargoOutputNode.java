package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.machines;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunBlockHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.UnregisterReason;
import me.mrCookieSlime.Slimefun.Setup.Messages;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.CargoNet;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class AdvancedCargoOutputNode extends SlimefunItem {
    private static final int[] BORDER = new int[]{0, 1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 17, 18, 22, 23, 24, 26, 27, 31, 32, 33, 34, 35, 36, 40, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};

    public AdvancedCargoOutputNode(Category category, ItemStack item, String name, RecipeType recipeType, ItemStack[] recipe, ItemStack recipeOutput) {
        super(category, item, name, recipeType, recipe, recipeOutput);

        new BlockMenuPreset(name, "&c输出节点") {
            @Override
            public void init() {
                constructMenu(this);
            }


            @Override
            public void newInstance(final BlockMenu menu, final Block b) {
                try {
                    if (!BlockStorage.hasBlockInfo(b) || BlockStorage.getLocationInfo(b.getLocation(), "filter-type") == null || "whitelist".equals(BlockStorage.getLocationInfo(b.getLocation(), "filter-type"))) {
                        menu.replaceExistingItem(15, new CustomItem(new MaterialData(Material.WOOL), "&7类型: &r白名单", "", "&e> 点击修改为黑名单"));
                        menu.addMenuClickHandler(15, (p, arg1, arg2, arg3) -> {
                            BlockStorage.addBlockInfo(b, "filter-type", "blacklist");
                            newInstance(menu, b);
                            return false;
                        });
                    } else {

                        menu.replaceExistingItem(15, new CustomItem(new MaterialData(Material.WOOL, (byte) 15), "&7类型: &8黑名单", "", "&e> 点击修改为白名单"));
                        menu.addMenuClickHandler(15, (p, arg1, arg2, arg3) -> {
                            BlockStorage.addBlockInfo(b, "filter-type", "whitelist");
                            newInstance(menu, b);
                            return false;
                        });
                    }

                    if (!BlockStorage.hasBlockInfo(b) || BlockStorage.getLocationInfo(b.getLocation(), "filter-durability") == null || "false".equals(BlockStorage.getLocationInfo(b.getLocation(), "filter-durability"))) {
                        menu.replaceExistingItem(16, new CustomItem(new MaterialData(Material.STONE_SWORD, (byte) 20), "&7需要匹配的 子ID/耐久值: &4✘", "", "&e> 点击修改需要匹配的耐久值"));
                        menu.addMenuClickHandler(16, (p, arg1, arg2, arg3) -> {
                            BlockStorage.addBlockInfo(b, "filter-durability", "true");
                            newInstance(menu, b);
                            return false;
                        });
                    } else {

                        menu.replaceExistingItem(16, new CustomItem(new MaterialData(Material.GOLD_SWORD, (byte) 20), "&7需要匹配的 子ID/耐久值: &2✔", "", "&e> 点击修改需要匹配的耐久值"));
                        menu.addMenuClickHandler(16, (p, arg1, arg2, arg3) -> {
                            BlockStorage.addBlockInfo(b, "filter-durability", "false");
                            newInstance(menu, b);
                            return false;
                        });
                    }

                    if (!BlockStorage.hasBlockInfo(b) || BlockStorage.getLocationInfo(b.getLocation(), "filter-lore") == null || "true".equals(BlockStorage.getLocationInfo(b.getLocation(), "filter-lore"))) {
                        menu.replaceExistingItem(25, new CustomItem(new MaterialData(Material.EMPTY_MAP), "&7需要匹配的 说明(Lore): &2✔", "", "&e> 点击修改需要匹配的Lore"));
                        menu.addMenuClickHandler(25, (p, arg1, arg2, arg3) -> {
                            BlockStorage.addBlockInfo(b, "filter-lore", "false");
                            newInstance(menu, b);
                            return false;
                        });
                    } else {

                        menu.replaceExistingItem(25, new CustomItem(new MaterialData(Material.EMPTY_MAP), "&7需要匹配的 说明(Lore): &4✘", "", "&e> 点击修改需要匹配的Lore"));
                        menu.addMenuClickHandler(25, (p, arg1, arg2, arg3) -> {
                            BlockStorage.addBlockInfo(b, "filter-lore", "true");
                            newInstance(menu, b);
                            return false;
                        });
                    }

                    menu.replaceExistingItem(41, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjI1OTliZDk4NjY1OWI4Y2UyYzQ5ODg1MjVjOTRlMTlkZGQzOWZhZDA4YTM4Mjg0YTE5N2YxYjcwNjc1YWNjIn19fQ=="), "&b频段号", "", "&e> 点击 -1 频段号"));
                    menu.addMenuClickHandler(41, (p, arg1, arg2, arg3) -> {
                        int channel = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "frequency")) - 1;
                        if (channel < 0) {
                            if (CargoNet.EXTRA_CHANNELS) {
                                channel = 16;
                            } else {
                                channel = 15;
                            }
                        }

                        BlockStorage.addBlockInfo(b, "frequency", String.valueOf(channel));
                        newInstance(menu, b);
                        return false;
                    });

                    int channel = (!BlockStorage.hasBlockInfo(b) || BlockStorage.getLocationInfo(b.getLocation(), "frequency") == null) ? 0 : Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "frequency"));

                    if (channel == 16) {
                        menu.replaceExistingItem(42, new CustomItem(SlimefunItems.CHEST_TERMINAL, "&b频段 ID: &3" + (channel + 1)));
                        menu.addMenuClickHandler(42, (p, arg1, arg2, arg3) -> false);
                    } else {

                        menu.replaceExistingItem(42, new CustomItem(new MaterialData(Material.WOOL, (byte) channel), "&b频段 ID: &3" + (channel + 1)));
                        menu.addMenuClickHandler(42, (p, arg1, arg2, arg3) -> false);
                    }

                    menu.replaceExistingItem(43, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzJmOTEwYzQ3ZGEwNDJlNGFhMjhhZjZjYzgxY2Y0OGFjNmNhZjM3ZGFiMzVmODhkYjk5M2FjY2I5ZGZlNTE2In19fQ=="), "&b频段号", "", "&e> 点击 +1 频段号"));
                    menu.addMenuClickHandler(43, (p, arg1, arg2, arg3) -> {
                        int channel1 = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "frequency")) + 1;

                        if (CargoNet.EXTRA_CHANNELS) {
                            if (channel1 > 16) {
                                channel1 = 0;
                            }
                        } else if (channel1 > 15) {
                            channel1 = 0;
                        }

                        BlockStorage.addBlockInfo(b, "frequency", String.valueOf(channel1));
                        newInstance(menu, b);
                        return false;
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public boolean canOpen(Block b, Player p) {
                boolean open = (CSCoreLib.getLib().getProtectionManager().canAccessChest(p.getUniqueId(), b) || p.hasPermission("slimefun.cargo.bypass"));
                if (!open) {
                    Messages.local.sendTranslation(p, "inventory.no-access", true);
                }
                return open;
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }
        };

        registerBlockHandler(name, new SlimefunBlockHandler() {
            @Override
            public void onPlace(Player p, Block b, SlimefunItem item) {
                BlockStorage.addBlockInfo(b, "owner", p.getUniqueId().toString());
                BlockStorage.addBlockInfo(b, "index", "0");
                BlockStorage.addBlockInfo(b, "frequency", "0");
                BlockStorage.addBlockInfo(b, "filter-type", "whitelist");
                BlockStorage.addBlockInfo(b, "filter-lore", "true");
                BlockStorage.addBlockInfo(b, "filter-durability", "false");
            }

            @Override
            public boolean onBreak(Player p, Block b, SlimefunItem item, UnregisterReason reason) {
                BlockMenu inv = BlockStorage.getInventory(b);
                if (inv != null) {
                    for (int slot : getInputSlots()) {
                        if (inv.getItemInSlot(slot) != null) {
                            b.getWorld().dropItemNaturally(b.getLocation(), inv.getItemInSlot(slot));
                            inv.replaceExistingItem(slot, null);
                        }
                    }
                }
                return true;
            }
        });
    }

    protected void constructMenu(BlockMenuPreset preset) {
        for (int i : BORDER) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 9), " "), (player, slot, itemStack, clickAction) -> false);
        }


        preset.addItem(2, new CustomItem(new MaterialData(Material.PAPER), "&3物品", "", "&b将你想要的所有物品放入", "&b黑名单/白名单"), (player, slot, itemStack, clickAction) -> false);
    }

    public int[] getInputSlots() {
        return new int[]{19, 20, 21, 28, 29, 30, 37, 38, 39};
    }
}


