package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.machines;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.InvUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.Math.DoubleHandler;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunBlockHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.UnregisterReason;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.energy.ChargableBlock;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class WitherAssembler extends SlimefunItem {
    private static final int[] BORDER = new int[]{0, 2, 3, 4, 5, 6, 8, 12, 14, 21, 23, 30, 32, 39, 40, 41};
    private static final int[] BORDER_1 = new int[]{9, 10, 11, 18, 20, 27, 29, 36, 37, 38};
    private static final int[] BORDER_2 = new int[]{15, 16, 17, 24, 26, 33, 35, 42, 43, 44};
    private static int lifetime = 0;

    public WitherAssembler(Category category, ItemStack item, String name, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, name, recipeType, recipe);

        new BlockMenuPreset(name, getInventoryTitle()) {
            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public void newInstance(final BlockMenu menu, final Block b) {
                try {
                    if (!BlockStorage.hasBlockInfo(b) || BlockStorage.getLocationInfo(b.getLocation(), "enabled") == null || BlockStorage.getLocationInfo(b.getLocation(), "enabled").equals("false")) {
                        menu.replaceExistingItem(22, new CustomItem(new MaterialData(Material.SULPHUR), "&7激活状态: &4✘", "", "&e> 点击激活这个机器"));
                        menu.addMenuClickHandler(22, (p, arg1, arg2, arg3) -> {
                            BlockStorage.addBlockInfo(b, "enabled", "true");
                            newInstance(menu, b);
                            return false;
                        });
                    } else {
                        menu.replaceExistingItem(22, new CustomItem(new MaterialData(Material.REDSTONE), "&7激活状态: &2✔", "", "&e> 点击停止这个机器"));
                        menu.addMenuClickHandler(22, (p, arg1, arg2, arg3) -> {
                            BlockStorage.addBlockInfo(b, "enabled", "false");
                            newInstance(menu, b);
                            return false;
                        });
                    }

                    double offset = (!BlockStorage.hasBlockInfo(b) || BlockStorage.getLocationInfo(b.getLocation(), "offset") == null) ? 3.0D : Double.parseDouble(BlockStorage.getLocationInfo(b.getLocation(), "offset"));

                    menu.replaceExistingItem(31, new CustomItem(new MaterialData(Material.PISTON_BASE), "&7偏移: &3" + offset + " 方块", "", "&r左键点击: &7+0.1", "&r右键点击: &7-0.1"));
                    menu.addMenuClickHandler(31, (p, arg1, arg2, arg3) -> {
                        double offset1 = DoubleHandler.fixDouble(Double.valueOf(BlockStorage.getLocationInfo(b.getLocation(), "offset")) + (arg3.isRightClicked() ? -0.1F : 0.1F));
                        BlockStorage.addBlockInfo(b, "offset", String.valueOf(offset1));
                        newInstance(menu, b);
                        return false;
                    });
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }

            @Override
            public boolean canOpen(Block b, Player p) {
                return (p.hasPermission("slimefun.inventory.bypass") || CSCoreLib.getLib().getProtectionManager().canAccessChest(p.getUniqueId(), b, true));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                if (flow.equals(ItemTransportFlow.INSERT)) {
                    return getInputSlots();
                }
                return new int[0];
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(BlockMenu menu, ItemTransportFlow flow, ItemStack item) {
                if (flow.equals(ItemTransportFlow.INSERT)) {
                    if (SlimefunManager.isItemSimiliar(item, new ItemStack(Material.SOUL_SAND), true)) {
                        getSoulSandSlots();
                    }
                    getWitherSkullSlots();
                }
                return new int[0];
            }
        };

        registerBlockHandler(name, new SlimefunBlockHandler() {
            @Override
            public void onPlace(Player p, Block b, SlimefunItem item) {
                BlockStorage.addBlockInfo(b, "offset", "3.0");
                BlockStorage.addBlockInfo(b, "enabled", "false");
            }

            @Override
            public boolean onBreak(Player p, Block b, SlimefunItem item, UnregisterReason reason) {
                if (reason.equals(UnregisterReason.EXPLODE)) {
                    return false;
                }
                BlockMenu inv = BlockStorage.getInventory(b);
                if (inv != null) {
                    for (int slot : getSoulSandSlots()) {
                        if (inv.getItemInSlot(slot) != null) {
                            b.getWorld().dropItemNaturally(b.getLocation(), inv.getItemInSlot(slot));
                            inv.replaceExistingItem(slot, null);
                        }
                    }
                    for (int slot : getWitherSkullSlots()) {
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


    private void constructMenu(BlockMenuPreset preset) {
        for (int i : BORDER) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (player, slot, itemStack, clickAction) -> false);
        }

        for (int i : BORDER_1) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 15), " "), (player, slot, itemStack, clickAction) -> false);
        }

        for (int i : BORDER_2) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 12), " "), (player, slot, itemStack, clickAction) -> false);
        }

        preset.addItem(1, new CustomItem(new MaterialData(Material.SKULL_ITEM, (byte) 1), "&7凋零头颅槽", "", "&r这个槽位用于放置凋零头颅"), (player, slot, itemStack, clickAction) -> false);

        preset.addItem(7, new CustomItem(new MaterialData(Material.SOUL_SAND), "&7灵魂沙槽", "", "&r这个槽位用于放置灵魂沙"), (player, slot, itemStack, clickAction) -> false);

        preset.addItem(13, new CustomItem(new MaterialData(Material.WATCH), "&7冷却: &b30 秒", "", "&r这个机器需要半分钟的时间来作运转准备", "&r请耐心等待!"), (player, slot, itemStack, clickAction) -> false);
    }

    public String getInventoryTitle() {
        return "&5凋零组装机";
    }

    public int[] getInputSlots() {
        return new int[]{19, 28, 25, 34};
    }

    public int[] getWitherSkullSlots() {
        return new int[]{19, 28};
    }

    public int[] getSoulSandSlots() {
        return new int[]{25, 34};
    }

    @Override
    public void register(boolean slimefun) {
        addItemHandler(new BlockTicker() {
            @Override
            public void tick(final Block b, SlimefunItem sf, Config data) {
                if (BlockStorage.getLocationInfo(b.getLocation(), "enabled").equals("false")) {
                    return;
                }
                if (WitherAssembler.lifetime % 60 == 0) {
                    if (ChargableBlock.getCharge(b) < getEnergyConsumption()) {
                        return;
                    }
                    int soulsand = 0;
                    int skulls = 0;

                    for (int slot : getSoulSandSlots()) {
                        if (SlimefunManager.isItemSimiliar(BlockStorage.getInventory(b).getItemInSlot(slot), new ItemStack(Material.SOUL_SAND), true, SlimefunManager.DataType.ALWAYS)) {
                            soulsand += BlockStorage.getInventory(b).getItemInSlot(slot).getAmount();
                            if (soulsand > 3) {
                                soulsand = 4;
                                break;
                            }
                        }
                    }
                    for (int slot : getWitherSkullSlots()) {
                        if (SlimefunManager.isItemSimiliar(BlockStorage.getInventory(b).getItemInSlot(slot), (new MaterialData(Material.SKULL_ITEM, (byte) 1)).toItemStack(1), true, SlimefunManager.DataType.ALWAYS)) {
                            skulls += BlockStorage.getInventory(b).getItemInSlot(slot).getAmount();
                            if (skulls > 2) {
                                skulls = 3;
                                break;
                            }
                        }
                    }
                    if (soulsand > 3 && skulls > 2) {
                        for (int slot : getSoulSandSlots()) {
                            if (SlimefunManager.isItemSimiliar(BlockStorage.getInventory(b).getItemInSlot(slot), new ItemStack(Material.SOUL_SAND), true, SlimefunManager.DataType.ALWAYS)) {
                                int amount = BlockStorage.getInventory(b).getItemInSlot(slot).getAmount();
                                if (amount >= soulsand) {
                                    BlockStorage.getInventory(b).replaceExistingItem(slot, InvUtils.decreaseItem(BlockStorage.getInventory(b).getItemInSlot(slot), soulsand));
                                    break;
                                }
                                soulsand -= amount;
                                BlockStorage.getInventory(b).replaceExistingItem(slot, null);
                            }
                        }


                        for (int slot : getWitherSkullSlots()) {
                            if (SlimefunManager.isItemSimiliar(BlockStorage.getInventory(b).getItemInSlot(slot), (new MaterialData(Material.SKULL_ITEM, (byte) 1)).toItemStack(1), true, SlimefunManager.DataType.ALWAYS)) {
                                int amount = BlockStorage.getInventory(b).getItemInSlot(slot).getAmount();
                                if (amount >= skulls) {
                                    BlockStorage.getInventory(b).replaceExistingItem(slot, InvUtils.decreaseItem(BlockStorage.getInventory(b).getItemInSlot(slot), skulls));

                                    break;
                                }
                                skulls -= amount;
                                BlockStorage.getInventory(b).replaceExistingItem(slot, null);
                            }
                        }

                        ChargableBlock.addCharge(b, -getEnergyConsumption());

                        final double offset = Double.parseDouble(BlockStorage.getLocationInfo(b.getLocation(), "offset"));

                        Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> b.getWorld().spawnEntity(new Location(b.getWorld(), b.getX() + 0.5D, b.getY() + offset, b.getZ() + 0.5D), EntityType.WITHER));
                    }
                }
            }

            @Override
            public void uniqueTick() {
                WitherAssembler.lifetime++;
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });

        super.register(slimefun);
    }

    public int getEnergyConsumption() {
        return 4096;
    }
}


