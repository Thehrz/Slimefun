package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.InvUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Misc.compatibles.ProtectionUtils;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunBlockHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.UnregisterReason;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.machines.ReactorAccessPort;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.energy.ChargableBlock;
import me.mrCookieSlime.Slimefun.api.energy.EnergyTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import me.mrCookieSlime.Slimefun.holograms.ReactorHologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.*;

public abstract class AReactor extends SlimefunItem {
    public static final Map<Location, MachineFuel> processing = new HashMap<>();
    public static final Map<Location, Integer> progress = new HashMap<>();
    private static final BlockFace[] cooling = new BlockFace[]{BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};
    private static final int[] border = new int[]{0, 1, 2, 3, 5, 6, 7, 8, 12, 13, 14, 21, 23};
    private static final int[] border_1 = new int[]{9, 10, 11, 18, 20, 27, 29, 36, 38, 45, 46, 47};
    private static final int[] border_2 = new int[]{15, 16, 17, 24, 26, 33, 35, 42, 44, 51, 52, 53};
    private static final int[] border_3 = new int[]{30, 31, 32, 39, 41, 48, 49, 50};
    private static final int[] border_4 = new int[]{25, 34, 43};
    private final Set<MachineFuel> recipes = new HashSet<>();

    public AReactor(Category category, ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, id, recipeType, recipe);

        new BlockMenuPreset(id, getInventoryTitle()) {
            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public void newInstance(final BlockMenu menu, final Block b) {
                try {
                    if (BlockStorage.getLocationInfo(b.getLocation(), "reactor-mode") == null) {
                        BlockStorage.addBlockInfo(b, "reactor-mode", "generator");
                    }
                    if (!BlockStorage.hasBlockInfo(b) || "generator".equals(BlockStorage.getLocationInfo(b.getLocation(), "reactor-mode"))) {
                        menu.replaceExistingItem(4, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM0M2NlNThkYTU0Yzc5OTI0YTJjOTMzMWNmYzQxN2ZlOGNjYmJlYTliZTQ1YTdhYzg1ODYwYTZjNzMwIn19fQ=="), "&7优先: &e发电", "", "&6你的反应器将专注于发电", "&6如果你的能量网络无需能源", "&6它将不会生产任何东西", "", "&7> 点击修改为优先 &e生产"));
                        menu.addMenuClickHandler(4, (p, arg1, arg2, arg3) -> {
                            BlockStorage.addBlockInfo(b, "reactor-mode", "production");
                            newInstance(menu, b);
                            return false;
                        });
                    } else {

                        menu.replaceExistingItem(4, new CustomItem(SlimefunItems.PLUTONIUM, "&7优先: &e生产", "", "&6你的反应器会优先生产产品", "&6如果你的能量网络不需要能源", "&6它将继续运行", "&6并且不会生产任何能源", "", "&7> 点击修改为优先 &e发电"));
                        menu.addMenuClickHandler(4, (p, arg1, arg2, arg3) -> {
                            BlockStorage.addBlockInfo(b, "reactor-mode", "generator");
                            newInstance(menu, b);
                            return false;
                        });
                    }
                } catch (Exception exception) {
                }
            }


            @Override
            public boolean canOpen(Block b, Player p) {
                boolean perm = (p.hasPermission("slimefun.inventory.bypass") || CSCoreLib.getLib().getProtectionManager().canAccessChest(p.getUniqueId(), b, true));
                return (perm && ProtectionUtils.canAccessItem(p, b));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }
        };

        registerBlockHandler(id, new SlimefunBlockHandler() {
            @Override
            public void onPlace(Player p, Block b, SlimefunItem item) {
            }

            @Override
            public boolean onBreak(Player p, Block b, SlimefunItem item, UnregisterReason reason) {
                BlockMenu inv = BlockStorage.getInventory(b);
                if (inv != null) {
                    for (int slot : getFuelSlots()) {
                        if (inv.getItemInSlot(slot) != null) {
                            b.getWorld().dropItemNaturally(b.getLocation(), inv.getItemInSlot(slot));
                            inv.replaceExistingItem(slot, null);
                        }
                    }
                    for (int slot : getCoolantSlots()) {
                        if (inv.getItemInSlot(slot) != null) {
                            b.getWorld().dropItemNaturally(b.getLocation(), inv.getItemInSlot(slot));
                            inv.replaceExistingItem(slot, null);
                        }
                    }
                    for (int slot : getOutputSlots()) {
                        if (inv.getItemInSlot(slot) != null) {
                            b.getWorld().dropItemNaturally(b.getLocation(), inv.getItemInSlot(slot));
                            inv.replaceExistingItem(slot, null);
                        }
                    }
                }
                progress.remove(b.getLocation());
                processing.remove(b.getLocation());
                ReactorHologram.remove(b.getLocation());
                return true;
            }
        });

        registerDefaultRecipes();
    }


    private void constructMenu(BlockMenuPreset preset) {
        for (int i : border) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (player, slot, itemStack, clickAction) -> false);
        }

        for (int i : border_1) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 5), " "), (player, slot, itemStack, clickAction) -> false);
        }

        for (int i : border_3) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 13), " "), (player, slot, itemStack, clickAction) -> false);
        }

        preset.addItem(22, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 15), " "), (player, slot, itemStack, clickAction) -> false);

        preset.addItem(1, new CustomItem(SlimefunItems.URANIUM, "&7燃料槽", getMessage()), (player, slot, itemStack, clickAction) -> false);

        for (int i : border_2) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 9), " "), (player, slot, itemStack, clickAction) -> false);
        }

        if (needsCooling()) {
            preset.addItem(7, new CustomItem(getCoolant(), "&b冷却槽", "", "&r这个槽位用于放置反应器冷却单元", "&4如果反应器不配置冷却槽", "&4反应器将会因为过热导致爆炸"));
        } else {
            preset.addItem(7, new CustomItem(new MaterialData(Material.BARRIER), "&b冷却槽", "", "&r这个槽位用于放置反应器冷却单元"));
            for (int i : border_4) {
                preset.addItem(i, new CustomItem(new ItemStack(Material.BARRIER), "&c无需冷却单元"), (player, i1, itemStack, clickAction) -> false);
            }
        }
    }

    public boolean needsCooling() {
        return (getCoolant() != null);
    }

    public int[] getInputSlots() {
        return new int[]{19, 28, 37, 25, 34, 43};
    }

    public int[] getFuelSlots() {
        return new int[]{19, 28, 37};
    }

    public int[] getCoolantSlots() {
        return needsCooling() ? new int[]{25, 34, 43} : new int[0];
    }

    public int[] getOutputSlots() {
        return new int[]{40};
    }

    public MachineFuel getProcessing(Location l) {
        return processing.get(l);
    }

    public boolean isProcessing(Location l) {
        return progress.containsKey(l);
    }

    public void registerFuel(MachineFuel fuel) {
        this.recipes.add(fuel);
    }

    @Override
    public void register(boolean slimefun) {
        addItemHandler(new EnergyTicker() {
            Set<Location> explode = new HashSet<>();

            @Override
            public double generateEnergy(final Location l, SlimefunItem sf, Config data) {
                BlockMenu port = getAccessPort(l);

                if (isProcessing(l)) {
                    extraTick(l);
                    int timeleft = progress.get(l);
                    if (timeleft > 0) {
                        int produced = getEnergyProduction();
                        int space = ChargableBlock.getMaxCharge(l) - ChargableBlock.getCharge(l);
                        if (space >= produced) {
                            ChargableBlock.addCharge(l, getEnergyProduction());
                            space -= produced;
                        }
                        if (space >= produced || !"generator".equals(BlockStorage.getBlockInfo(l, "reactor-mode"))) {
                            progress.put(l, timeleft - 1);

                            Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
                                if (!l.getBlock().getRelative(AReactor.cooling[CSCoreLib.randomizer().nextInt(AReactor.cooling.length)]).isLiquid()) {
                                    explode.add(l);
                                }

                            });

                            ItemStack item = getProgressBar().clone();
                            ItemMeta im = item.getItemMeta();
                            im.setDisplayName(" ");
                            List<String> lore = new ArrayList<>();
                            lore.add(MachineHelper.getProgress(timeleft, AReactor.processing.get(l).getTicks()));
                            lore.add(MachineHelper.getCoolant(timeleft, AReactor.processing.get(l).getTicks()));
                            lore.add("");
                            lore.add(MachineHelper.getTimeLeft(timeleft / 2));
                            im.setLore(lore);
                            item.setItemMeta(im);

                            BlockStorage.getInventory(l).replaceExistingItem(22, item);

                            if (needsCooling()) {
                                boolean coolant = ((AReactor.processing.get(l).getTicks() - timeleft) % 25 == 0);

                                if (coolant) {
                                    if (port != null) {
                                        for (int slot : getCoolantSlots()) {
                                            if (SlimefunManager.isItemSimiliar(port.getItemInSlot(slot), getCoolant(), true)) {
                                                port.replaceExistingItem(slot, AReactor.this.pushItems(l, port.getItemInSlot(slot), getCoolantSlots()));
                                            }
                                        }
                                    }

                                    boolean explosion = true;
                                    for (int slot : getCoolantSlots()) {
                                        if (SlimefunManager.isItemSimiliar(BlockStorage.getInventory(l).getItemInSlot(slot), AReactor.this.getCoolant(), true)) {
                                            BlockStorage.getInventory(l).replaceExistingItem(slot, InvUtils.decreaseItem(BlockStorage.getInventory(l).getItemInSlot(slot), 1));
                                            ReactorHologram.update(l, "&b❄ &7100%");
                                            explosion = false;

                                            break;
                                        }
                                    }
                                    if (explosion) {
                                        explode.add(l);
                                        return 0.0D;
                                    }
                                } else {
                                    ReactorHologram.update(l, "&b❄ &7" + MachineHelper.getPercentage(timeleft, AReactor.processing.get(l).getTicks()) + "%");
                                }
                            }

                            return ChargableBlock.getCharge(l);
                        }
                        return 0.0D;
                    }

                    BlockStorage.getInventory(l).replaceExistingItem(22, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 15), " "));
                    if (processing.get(l).getOutput() != null) {
                        pushItems(l, processing.get(l).getOutput());
                    }

                    if (port != null) {
                        for (int slot : getOutputSlots()) {
                            if (BlockStorage.getInventory(l).getItemInSlot(slot) != null) {
                                BlockStorage.getInventory(l).replaceExistingItem(slot, ReactorAccessPort.pushItems(port.getLocation(), BlockStorage.getInventory(l).getItemInSlot(slot)));
                            }
                        }
                    }
                    progress.remove(l);
                    processing.remove(l);
                    return 0.0D;
                }


                MachineFuel r = null;
                Map<Integer, Integer> found = new HashMap<>();

                if (port != null) {
                    for (int slot : getFuelSlots()) {
                        for (MachineFuel recipe : recipes) {
                            if (SlimefunManager.isItemSimiliar(port.getItemInSlot(slot), recipe.getInput(), true) &&
                                    pushItems(l, new CustomItem(port.getItemInSlot(slot), 1), getFuelSlots()) == null) {
                                port.replaceExistingItem(slot, InvUtils.decreaseItem(port.getItemInSlot(slot), 1));


                            }
                        }
                    }
                }

                MachineFuel:
                for (MachineFuel recipe : AReactor.this.recipes) {
                    for (int slot : AReactor.this.getFuelSlots()) {
                        if (SlimefunManager.isItemSimiliar(BlockStorage.getInventory(l).getItemInSlot(slot), recipe.getInput(), true)) {
                            found.put(slot, recipe.getInput().getAmount());
                            r = recipe;

                            break MachineFuel;
                        }
                    }
                }
                if (r != null) {
                    for (Map.Entry<Integer, Integer> entry : found.entrySet()) {
                        BlockStorage.getInventory(l).replaceExistingItem(entry.getKey(), InvUtils.decreaseItem(BlockStorage.getInventory(l).getItemInSlot(entry.getKey()), entry.getValue()));
                    }
                    AReactor.processing.put(l, r);
                    AReactor.progress.put(l, r.getTicks());
                }
                return 0.0D;
            }

            @Override
            public boolean explode(final Location l) {
                boolean explosion = explode.contains(l);
                if (explosion) {
                    BlockStorage.getInventory(l).close();

                    Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> ReactorHologram.remove(l), 0L);

                    explode.remove(l);
                    AReactor.processing.remove(l);
                    AReactor.progress.remove(l);
                }
                return explosion;
            }
        });

        super.register(slimefun);
    }

    private Inventory inject(Location l) {
        int size = BlockStorage.getInventory(l).toInventory().getSize();
        Inventory inv = Bukkit.createInventory(null, size);
        for (int i = 0; i < size; i++) {
            inv.setItem(i, new CustomItem(Material.COMMAND, " &4ALL YOUR PLACEHOLDERS ARE BELONG TO US", 0));
        }
        for (int slot : getOutputSlots()) {
            inv.setItem(slot, BlockStorage.getInventory(l).getItemInSlot(slot));
        }
        return inv;
    }

    private Inventory inject(Location l, int[] slots) {
        int size = BlockStorage.getInventory(l).toInventory().getSize();
        Inventory inv = Bukkit.createInventory(null, size);
        for (int i = 0; i < size; i++) {
            inv.setItem(i, new CustomItem(Material.COMMAND, " &4ALL YOUR PLACEHOLDERS ARE BELONG TO US", 0));
        }
        for (int slot : slots) {
            inv.setItem(slot, BlockStorage.getInventory(l).getItemInSlot(slot));
        }
        return inv;
    }

    public void pushItems(Location l, ItemStack item) {
        Inventory inv = inject(l);
        inv.addItem(item);

        for (int slot : getOutputSlots()) {
            BlockStorage.getInventory(l).replaceExistingItem(slot, inv.getItem(slot));
        }
    }

    public ItemStack pushItems(Location l, ItemStack item, int[] slots) {
        Inventory inv = inject(l, slots);
        Map<Integer, ItemStack> map = inv.addItem(item);

        for (int slot : slots) {
            BlockStorage.getInventory(l).replaceExistingItem(slot, inv.getItem(slot));
        }

        Iterator<Map.Entry<Integer, ItemStack>> iterator = map.entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<Integer, ItemStack> entry = iterator.next();
            return entry.getValue();
        }


        return null;
    }

    public Set<MachineFuel> getFuelTypes() {
        return this.recipes;
    }

    public BlockMenu getAccessPort(Location l) {
        Location portL = new Location(l.getWorld(), l.getX(), l.getY() + 3.0D, l.getZ());
        if (BlockStorage.check(portL, "REACTOR_ACCESS_PORT")) {
            return BlockStorage.getInventory(portL);
        }
        return null;
    }

    public abstract String getInventoryTitle();

    public abstract void registerDefaultRecipes();

    public abstract int getEnergyProduction();

    public abstract void extraTick(Location paramLocation);

    public abstract ItemStack getCoolant();

    public abstract ItemStack getProgressBar();

    public abstract String[] getMessage();
}


