package me.mrCookieSlime.Slimefun.Android;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Block.TreeCalculator;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.InvUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.MenuHelper;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;
import me.mrCookieSlime.ExoticGarden.ExoticGarden;
import me.mrCookieSlime.Slimefun.Android.ScriptComparators.ScriptReputationSorter;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Misc.compatibles.ProtectionUtils;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunBlockHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.UnregisterReason;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.Setup.Messages;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import me.mrCookieSlime.Slimefun.holograms.AndroidStatusHologram;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Skull;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;
import java.util.*;

public abstract class ProgrammableAndroid
        extends SlimefunItem {
    private static final int[] border = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 18, 24, 25, 26, 27, 33, 35, 36, 42, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
    private static final int[] border_out = new int[]{10, 11, 12, 13, 14, 19, 23, 28, 32, 37, 38, 39, 40, 41};
    private static final ItemStack[] fish = new ItemStack[]{new MaterialData(Material.RAW_FISH, (byte) 0).toItemStack(1), new MaterialData(Material.RAW_FISH, (byte) 1).toItemStack(1), new MaterialData(Material.RAW_FISH, (byte) 2).toItemStack(1), new MaterialData(Material.RAW_FISH, (byte) 3).toItemStack(1), new ItemStack(Material.STRING), new ItemStack(Material.BONE), new ItemStack(Material.STICK)};
    private static final List<BlockFace> directions = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
    private static final List<Material> blockwhitelist = new ArrayList<Material>();

    static {
        blockwhitelist.add(Material.COBBLESTONE);
    }

    private final Set<MachineFuel> recipes = new HashSet<MachineFuel>();

    public ProgrammableAndroid(Category category, ItemStack item, String name, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, name, recipeType, recipe);
        if (this.getTier() == 1) {
            this.registerFuel(new MachineFuel(80, new MaterialData(Material.COAL, (byte) 0).toItemStack(1)));
            this.registerFuel(new MachineFuel(80, new MaterialData(Material.COAL, (byte) 1).toItemStack(1)));
            this.registerFuel(new MachineFuel(800, new ItemStack(Material.COAL_BLOCK)));
            this.registerFuel(new MachineFuel(45, new ItemStack(Material.BLAZE_ROD)));
            this.registerFuel(new MachineFuel(4, new MaterialData(Material.LOG, (byte) 0).toItemStack(1)));
            this.registerFuel(new MachineFuel(4, new MaterialData(Material.LOG, (byte) 1).toItemStack(1)));
            this.registerFuel(new MachineFuel(4, new MaterialData(Material.LOG, (byte) 2).toItemStack(1)));
            this.registerFuel(new MachineFuel(4, new MaterialData(Material.LOG, (byte) 3).toItemStack(1)));
            this.registerFuel(new MachineFuel(4, new MaterialData(Material.LOG_2, (byte) 0).toItemStack(1)));
            this.registerFuel(new MachineFuel(4, new MaterialData(Material.LOG_2, (byte) 1).toItemStack(1)));
            this.registerFuel(new MachineFuel(1, new MaterialData(Material.WOOD, (byte) 0).toItemStack(1)));
            this.registerFuel(new MachineFuel(1, new MaterialData(Material.WOOD, (byte) 1).toItemStack(1)));
            this.registerFuel(new MachineFuel(1, new MaterialData(Material.WOOD, (byte) 2).toItemStack(1)));
            this.registerFuel(new MachineFuel(1, new MaterialData(Material.WOOD, (byte) 3).toItemStack(1)));
            this.registerFuel(new MachineFuel(1, new MaterialData(Material.WOOD, (byte) 4).toItemStack(1)));
            this.registerFuel(new MachineFuel(1, new MaterialData(Material.WOOD, (byte) 5).toItemStack(1)));
        } else if (this.getTier() == 2) {
            this.registerFuel(new MachineFuel(100, new ItemStack(Material.LAVA_BUCKET)));
            this.registerFuel(new MachineFuel(200, SlimefunItems.BUCKET_OF_OIL));
            this.registerFuel(new MachineFuel(500, SlimefunItems.BUCKET_OF_FUEL));
        } else {
            this.registerFuel(new MachineFuel(2500, SlimefunItems.URANIUM));
            this.registerFuel(new MachineFuel(1200, SlimefunItems.NEPTUNIUM));
            this.registerFuel(new MachineFuel(3000, SlimefunItems.BOOSTED_URANIUM));
        }
        new BlockMenuPreset(name, this.getInventoryTitle()) {

            @Override
            public void init() {
                try {
                    ProgrammableAndroid.this.constructMenu(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public boolean canOpen(Block b, Player p) {
                boolean open;
                boolean bl = open = BlockStorage.getLocationInfo(b.getLocation(), "owner").equals(p.getUniqueId().toString()) || p.hasPermission("slimefun.android.bypass");
                if (!open) {
                    Messages.local.sendTranslation(p, "inventory.no-access", true);
                }
                return open;
            }

            @Override
            public void newInstance(BlockMenu menu, final Block b) {
                try {
                    menu.replaceExistingItem(15, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTAxYzdiNTcyNjE3ODk3NGIzYjNhMDFiNDJhNTkwZTU0MzY2MDI2ZmQ0MzgwOGYyYTc4NzY0ODg0M2E3ZjVhIn19fQ=="), "&a开始/继续"));
                    menu.addMenuClickHandler(15, (p, arg1, arg2, arg3) -> {
                        Messages.local.sendTranslation(p, "robot.started", true);
                        BlockStorage.addBlockInfo(b, "paused", "false");
                        p.closeInventory();
                        return false;
                    });
                    menu.replaceExistingItem(17, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTYxMzlmZDFjNTY1NGU1NmU5ZTRlMmM4YmU3ZWIyYmQ1YjQ5OWQ2MzM2MTY2NjNmZWVlOTliNzQzNTJhZDY0In19fQ=="), "&4暂停"));
                    menu.addMenuClickHandler(17, (p, arg1, arg2, arg3) -> {
                        BlockStorage.addBlockInfo(b, "paused", "true");
                        Messages.local.sendTranslation(p, "robot.stopped", true);
                        return false;
                    });
                    menu.replaceExistingItem(16, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDc4ZjJiN2U1ZTc1NjM5ZWE3ZmI3OTZjMzVkMzY0YzRkZjI4YjQyNDNlNjZiNzYyNzdhYWRjZDYyNjEzMzcifX19"), "&b记忆核心", "", "&8⇨ &7点击打开脚本编辑器"));
                    menu.addMenuClickHandler(16, (p, arg1, arg2, arg3) -> {
                        try {
                            BlockStorage.addBlockInfo(b, "paused", "true");
                            Messages.local.sendTranslation(p, "robot.stopped", true);
                            ProgrammableAndroid.this.openScriptEditor(p, b);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }
        };
        ProgrammableAndroid.registerBlockHandler(name, new SlimefunBlockHandler() {

            @Override
            public void onPlace(Player p, Block b, SlimefunItem item) {
                BlockStorage.addBlockInfo(b, "owner", p.getUniqueId().toString());
                BlockStorage.addBlockInfo(b, "script", "START-TURN_LEFT-REPEAT");
                BlockStorage.addBlockInfo(b, "index", "0");
                BlockStorage.addBlockInfo(b, "fuel", "0");
                BlockStorage.addBlockInfo(b, "rotation", "NORTH");
                BlockStorage.addBlockInfo(b, "paused", "true");
                b.setData((byte) 1);
                Skull skull = (Skull) b.getState();
                skull.setRotation(BlockFace.NORTH);
                skull.update(true, false);
            }

            @Override
            public boolean onBreak(Player p, Block b, SlimefunItem item, UnregisterReason reason) {
                boolean allow;
                boolean bl = allow = reason.equals(UnregisterReason.PLAYER_BREAK) && (BlockStorage.getLocationInfo(b.getLocation(), "owner").equals(p.getUniqueId().toString()) || p.hasPermission("slimefun.android.bypass"));
                if (allow) {
                    BlockMenu inv = BlockStorage.getInventory(b);
                    if (inv != null) {
                        if (inv.getItemInSlot(43) != null) {
                            b.getWorld().dropItemNaturally(b.getLocation(), inv.getItemInSlot(43));
                            inv.replaceExistingItem(43, null);
                        }
                        for (int slot : ProgrammableAndroid.this.getOutputSlots()) {
                            if (inv.getItemInSlot(slot) == null) continue;
                            b.getWorld().dropItemNaturally(b.getLocation(), inv.getItemInSlot(slot));
                            inv.replaceExistingItem(slot, null);
                        }
                    }
                    AndroidStatusHologram.remove(b);
                }
                return allow;
            }
        });
    }

    public String getInventoryTitle() {
        return "可编程机器人";
    }

    public int[] getOutputSlots() {
        return new int[]{20, 21, 22, 29, 30, 31};
    }

    public abstract AndroidType getAndroidType();

    public abstract float getFuelEfficiency();

    public abstract int getTier();

    protected void tick(Block b) {
        try {
            if (!(b.getState() instanceof Skull)) {
                return;
            }
        } catch (NullPointerException x) {
            return;
        }
        if (BlockStorage.getLocationInfo(b.getLocation(), "paused").equals("false")) {
            float fuel = Float.parseFloat(BlockStorage.getLocationInfo(b.getLocation(), "fuel"));
            if (fuel == 0.0f) {
                ItemStack item = BlockStorage.getInventory(b).getItemInSlot(43);
                if (item != null) {
                    for (MachineFuel recipe : this.recipes) {
                        if (!SlimefunManager.isItemSimiliar(item, recipe.getInput(), true)) continue;
                        BlockStorage.getInventory(b).replaceExistingItem(43, InvUtils.decreaseItem(item, 1));
                        if (this.getTier() == 2) {
                            this.pushItems(b, new ItemStack(Material.BUCKET));
                        }
                        BlockStorage.addBlockInfo(b, "fuel", String.valueOf((int) ((float) recipe.getTicks() * this.getFuelEfficiency())));
                        break;
                    }
                }
            } else {
                UUID owner = UUID.fromString(BlockStorage.getLocationInfo(b.getLocation(), "owner"));
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(owner);
                if (offlinePlayer.isOnline()) {
                    String[] script = BlockStorage.getLocationInfo(b.getLocation(), "script").split("-");
                    int index = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "index")) + 1;
                    if (index >= script.length) {
                        index = 0;
                    }
                    boolean refresh = true;
                    BlockStorage.addBlockInfo(b, "fuel", String.valueOf(fuel - 1.0f));
                    ScriptPart part = ScriptPart.valueOf(script[index]);
                    if (this.getAndroidType().isType(part.getRequiredType())) {
                        block4:
                        switch (part) {
                            case REPEAT: {
                                BlockStorage.addBlockInfo(b, "index", String.valueOf(0));
                                break;
                            }
                            case TURN_LEFT: {
                                int rotIndex = directions.indexOf(BlockFace.valueOf(BlockStorage.getLocationInfo(b.getLocation(), "rotation"))) - 1;
                                if (rotIndex < 0) {
                                    rotIndex = directions.size() - 1;
                                }
                                BlockFace dir = directions.get(rotIndex);
                                Skull skull = (Skull) b.getState();
                                skull.setRotation(dir);
                                skull.update(true, false);
                                BlockStorage.addBlockInfo(b, "rotation", dir.toString());
                                break;
                            }
                            case TURN_RIGHT: {
                                int rotIndex = directions.indexOf(BlockFace.valueOf(BlockStorage.getLocationInfo(b.getLocation(), "rotation"))) + 1;
                                if (rotIndex == directions.size()) {
                                    rotIndex = 0;
                                }
                                BlockFace dir = directions.get(rotIndex);
                                Skull skull = (Skull) b.getState();
                                skull.setRotation(dir);
                                skull.update(true, false);
                                BlockStorage.addBlockInfo(b, "rotation", dir.toString());
                                break;
                            }
                            case DIG_FORWARD: {
                                Block block = b.getRelative(BlockFace.valueOf(BlockStorage.getLocationInfo(b.getLocation(), "rotation")));
                                this.mine(b, block);
                                break;
                            }
                            case DIG_UP: {
                                Block block = b.getRelative(BlockFace.UP);
                                this.mine(b, block);
                                break;
                            }
                            case DIG_DOWN: {
                                Block block = b.getRelative(BlockFace.DOWN);
                                this.mine(b, block);
                                break;
                            }
                            case CATCH_FISH: {
                                Block block = b.getRelative(BlockFace.DOWN);
                                if (!block.getType().equals(Material.STATIONARY_WATER)) break;
                                block.getWorld().playSound(block.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 1.0f, 1.0f);
                                if (CSCoreLib.randomizer().nextInt(100) >= 10 * this.getTier()) break;
                                ItemStack drop = fish[CSCoreLib.randomizer().nextInt(fish.length)];
                                if (!this.fits(b, drop)) break;
                                this.pushItems(b, drop);
                                break;
                            }
                            case INTERFACE_ITEMS: {
                                BlockFace face = BlockFace.valueOf(BlockStorage.getLocationInfo(b.getLocation(), "rotation"));
                                Block block = b.getRelative(face);
                                if (!BlockStorage.check(block, "ANDROID_INTERFACE_ITEMS") || !(block.getState() instanceof Dispenser))
                                    break;
                                Dispenser d = (Dispenser) block.getState();
                                for (int slot : this.getOutputSlots()) {
                                    ItemStack stack = BlockStorage.getInventory(b).getItemInSlot(slot);
                                    if (stack == null) continue;
                                    HashMap items = d.getInventory().addItem(stack);
                                    if (items.isEmpty()) {
                                        BlockStorage.getInventory(b).replaceExistingItem(slot, null);
                                        continue;
                                    }
                                    Iterator iterator = items.entrySet().iterator();
                                    if (!iterator.hasNext()) continue;
                                    Map.Entry entry = (Map.Entry) iterator.next();
                                    BlockStorage.getInventory(b).replaceExistingItem(slot, (ItemStack) entry.getValue());
                                }
                                break;
                            }
                            case INTERFACE_FUEL: {
                                BlockFace face = BlockFace.valueOf(BlockStorage.getLocationInfo(b.getLocation(), "rotation"));
                                Block block = b.getRelative(face);
                                if (!BlockStorage.check(block, "ANDROID_INTERFACE_FUEL") || !(block.getState() instanceof Dispenser))
                                    break;
                                Dispenser d = (Dispenser) block.getState();
                                for (int slot = 0; slot < 9; ++slot) {
                                    ItemStack item = d.getInventory().getItem(slot);
                                    if (item == null) continue;
                                    if (BlockStorage.getInventory(b).getItemInSlot(43) == null) {
                                        BlockStorage.getInventory(b).replaceExistingItem(43, item);
                                        d.getInventory().setItem(slot, null);
                                        break block4;
                                    }
                                    if (!SlimefunManager.isItemSimiliar(item, BlockStorage.getInventory(b).getItemInSlot(43), true))
                                        continue;
                                    int rest = item.getType().getMaxStackSize() - BlockStorage.getInventory(b).getItemInSlot(43).getAmount();
                                    if (rest <= 0) break block4;
                                    int amt = item.getAmount() > rest ? rest : item.getAmount();
                                    BlockStorage.getInventory(b).replaceExistingItem(43, new CustomItem(item, BlockStorage.getInventory(b).getItemInSlot(43).getAmount() + amt));
                                    d.getInventory().setItem(slot, InvUtils.decreaseItem(item, amt));
                                    break block4;
                                }
                                break;
                            }
                            case FARM_FORWARD: {
                                BlockFace face = BlockFace.valueOf(BlockStorage.getLocationInfo(b.getLocation(), "rotation"));
                                Block block = b.getRelative(face);
                                this.farm(b, block);
                                break;
                            }
                            case FARM_DOWN: {
                                Block block = b.getRelative(BlockFace.DOWN);
                                this.farm(b, block);
                                break;
                            }
                            case FARM_EXOTIC_FORWARD: {
                                BlockFace face = BlockFace.valueOf(BlockStorage.getLocationInfo(b.getLocation(), "rotation"));
                                Block block = b.getRelative(face);
                                this.exoticFarm(b, block);
                                break;
                            }
                            case FARM_EXOTIC_DOWN: {
                                Block block = b.getRelative(BlockFace.DOWN);
                                this.exoticFarm(b, block);
                                break;
                            }
                            case CHOP_TREE: {
                                ItemStack[] items;
                                BlockFace face = BlockFace.valueOf(BlockStorage.getLocationInfo(b.getLocation(), "rotation"));
                                Block block = b.getRelative(face);
                                if (!block.getType().equals(Material.LOG) && !block.getType().equals(Material.LOG_2))
                                    break;
                                ArrayList<Location> list = new ArrayList<Location>();
                                list.add(block.getLocation());
                                TreeCalculator.getTree(block.getLocation(), block.getLocation(), list);
                                if (list.isEmpty()) break;
                                refresh = false;
                                Block log = list.get(list.size() - 1).getBlock();
                                Collection drops = log.getDrops();
                                log.getWorld().playEffect(log.getLocation(), Effect.STEP_SOUND, (Object) log.getType());
                                if (drops.isEmpty() || !CSCoreLib.getLib().getProtectionManager().canBuild(UUID.fromString(BlockStorage.getLocationInfo(b.getLocation(), "owner")), log) || !this.fits(b, items = (ItemStack[]) drops.toArray(new ItemStack[drops.size()])))
                                    break;
                                this.pushItems(b, items);
                                log.getWorld().playEffect(log.getLocation(), Effect.STEP_SOUND, (Object) log.getType());
                                if (log.getY() == block.getY()) {
                                    byte data = log.getData();
                                    if (log.getType() == Material.LOG_2) {
                                        data = (byte) (data + 4);
                                    }
                                    log.setType(Material.SAPLING);
                                    log.setData(data);
                                    break;
                                }
                                log.setType(Material.AIR);
                                break;
                            }
                            case ATTACK_MOBS_ANIMALS: {
                                double damage = this.getTier() < 2 ? 20.0 : 4.0 * (double) this.getTier();
                                for (Entity n : AndroidStatusHologram.getNearbyEntities(b, 4.0 + (double) this.getTier())) {
                                    switch (BlockFace.valueOf(BlockStorage.getLocationInfo(b.getLocation(), "rotation"))) {
                                        case NORTH: {
                                            if (!(n instanceof LivingEntity) || n instanceof ArmorStand || n instanceof Player || !(n.getLocation().getZ() < (double) b.getZ()))
                                                break;
                                            if (n.hasMetadata("android_killer")) {
                                                n.removeMetadata("android_killer", SlimefunStartup.instance);
                                            }
                                            n.setMetadata("android_killer", new FixedMetadataValue(SlimefunStartup.instance, new AndroidObject(this, b)));
                                            ((LivingEntity) n).damage(damage);
                                            break block4;
                                        }
                                        case EAST: {
                                            if (!(n instanceof LivingEntity) || n instanceof ArmorStand || n instanceof Player || !(n.getLocation().getX() > (double) b.getX()))
                                                break;
                                            if (n.hasMetadata("android_killer")) {
                                                n.removeMetadata("android_killer", SlimefunStartup.instance);
                                            }
                                            n.setMetadata("android_killer", new FixedMetadataValue(SlimefunStartup.instance, new AndroidObject(this, b)));
                                            ((LivingEntity) n).damage(damage);
                                            break block4;
                                        }
                                        case SOUTH: {
                                            if (!(n instanceof LivingEntity) || n instanceof ArmorStand || n instanceof Player || !(n.getLocation().getZ() > (double) b.getZ()))
                                                break;
                                            if (n.hasMetadata("android_killer")) {
                                                n.removeMetadata("android_killer", SlimefunStartup.instance);
                                            }
                                            n.setMetadata("android_killer", new FixedMetadataValue(SlimefunStartup.instance, new AndroidObject(this, b)));
                                            ((LivingEntity) n).damage(damage);
                                            break block4;
                                        }
                                        case WEST: {
                                            if (!(n instanceof LivingEntity) || n instanceof ArmorStand || n instanceof Player || !(n.getLocation().getX() < (double) b.getX()))
                                                break;
                                            if (n.hasMetadata("android_killer")) {
                                                n.removeMetadata("android_killer", SlimefunStartup.instance);
                                            }
                                            n.setMetadata("android_killer", new FixedMetadataValue(SlimefunStartup.instance, new AndroidObject(this, b)));
                                            ((LivingEntity) n).damage(damage);
                                            break block4;
                                        }
                                    }
                                }
                                break;
                            }
                            case ATTACK_MOBS: {
                                double damage = this.getTier() < 2 ? 20.0 : 4.0 * (double) this.getTier();
                                for (Entity n : AndroidStatusHologram.getNearbyEntities(b, 4.0 + (double) this.getTier())) {
                                    if (n instanceof Animals) continue;
                                    switch (BlockFace.valueOf(BlockStorage.getLocationInfo(b.getLocation(), "rotation"))) {
                                        case NORTH: {
                                            if (!(n instanceof LivingEntity) || n instanceof ArmorStand || n instanceof Player || !(n.getLocation().getZ() < (double) b.getZ()))
                                                break;
                                            if (n.hasMetadata("android_killer")) {
                                                n.removeMetadata("android_killer", SlimefunStartup.instance);
                                            }
                                            n.setMetadata("android_killer", new FixedMetadataValue(SlimefunStartup.instance, new AndroidObject(this, b)));
                                            ((LivingEntity) n).damage(damage);
                                            break block4;
                                        }
                                        case EAST: {
                                            if (!(n instanceof LivingEntity) || n instanceof ArmorStand || n instanceof Player || !(n.getLocation().getX() > (double) b.getX()))
                                                break;
                                            if (n.hasMetadata("android_killer")) {
                                                n.removeMetadata("android_killer", SlimefunStartup.instance);
                                            }
                                            n.setMetadata("android_killer", new FixedMetadataValue(SlimefunStartup.instance, new AndroidObject(this, b)));
                                            ((LivingEntity) n).damage(damage);
                                            break block4;
                                        }
                                        case SOUTH: {
                                            if (!(n instanceof LivingEntity) || n instanceof ArmorStand || n instanceof Player || !(n.getLocation().getZ() > (double) b.getZ()))
                                                break;
                                            if (n.hasMetadata("android_killer")) {
                                                n.removeMetadata("android_killer", SlimefunStartup.instance);
                                            }
                                            n.setMetadata("android_killer", new FixedMetadataValue(SlimefunStartup.instance, new AndroidObject(this, b)));
                                            ((LivingEntity) n).damage(damage);
                                            break block4;
                                        }
                                        case WEST: {
                                            if (!(n instanceof LivingEntity) || n instanceof ArmorStand || n instanceof Player || !(n.getLocation().getX() < (double) b.getX()))
                                                break;
                                            if (n.hasMetadata("android_killer")) {
                                                n.removeMetadata("android_killer", SlimefunStartup.instance);
                                            }
                                            n.setMetadata("android_killer", new FixedMetadataValue(SlimefunStartup.instance, new AndroidObject(this, b)));
                                            ((LivingEntity) n).damage(damage);
                                            break block4;
                                        }
                                    }
                                }
                                break;
                            }
                            case ATTACK_ANIMALS: {
                                double damage = this.getTier() < 2 ? 20.0 : 4.0 * (double) this.getTier();
                                for (Entity n : AndroidStatusHologram.getNearbyEntities(b, 4.0 + (double) this.getTier())) {
                                    if (n instanceof Monster) continue;
                                    switch (BlockFace.valueOf(BlockStorage.getLocationInfo(b.getLocation(), "rotation"))) {
                                        case NORTH: {
                                            if (!(n instanceof LivingEntity) || n instanceof ArmorStand || n instanceof Player || !(n.getLocation().getZ() < (double) b.getZ()))
                                                break;
                                            if (n.hasMetadata("android_killer")) {
                                                n.removeMetadata("android_killer", SlimefunStartup.instance);
                                            }
                                            n.setMetadata("android_killer", new FixedMetadataValue(SlimefunStartup.instance, new AndroidObject(this, b)));
                                            ((LivingEntity) n).damage(damage);
                                            break block4;
                                        }
                                        case EAST: {
                                            if (!(n instanceof LivingEntity) || n instanceof ArmorStand || n instanceof Player || !(n.getLocation().getX() > (double) b.getX()))
                                                break;
                                            if (n.hasMetadata("android_killer")) {
                                                n.removeMetadata("android_killer", SlimefunStartup.instance);
                                            }
                                            n.setMetadata("android_killer", new FixedMetadataValue(SlimefunStartup.instance, new AndroidObject(this, b)));
                                            ((LivingEntity) n).damage(damage);
                                            break block4;
                                        }
                                        case SOUTH: {
                                            if (!(n instanceof LivingEntity) || n instanceof ArmorStand || n instanceof Player || !(n.getLocation().getZ() > (double) b.getZ()))
                                                break;
                                            if (n.hasMetadata("android_killer")) {
                                                n.removeMetadata("android_killer", SlimefunStartup.instance);
                                            }
                                            n.setMetadata("android_killer", new FixedMetadataValue(SlimefunStartup.instance, new AndroidObject(this, b)));
                                            ((LivingEntity) n).damage(damage);
                                            break block4;
                                        }
                                        case WEST: {
                                            if (!(n instanceof LivingEntity) || n instanceof ArmorStand || n instanceof Player || !(n.getLocation().getX() < (double) b.getX()))
                                                break;
                                            if (n.hasMetadata("android_killer")) {
                                                n.removeMetadata("android_killer", SlimefunStartup.instance);
                                            }
                                            n.setMetadata("android_killer", new FixedMetadataValue(SlimefunStartup.instance, new AndroidObject(this, b)));
                                            ((LivingEntity) n).damage(damage);
                                            break block4;
                                        }
                                    }
                                }
                                break;
                            }
                            case ATTACK_ANIMALS_ADULT: {
                                double damage = this.getTier() < 2 ? 20.0 : 4.0 * (double) this.getTier();
                                for (Entity n : AndroidStatusHologram.getNearbyEntities(b, 4.0 + (double) this.getTier())) {
                                    if (n instanceof Monster || n instanceof Ageable && !((Ageable) n).isAdult())
                                        continue;
                                    switch (BlockFace.valueOf(BlockStorage.getLocationInfo(b.getLocation(), "rotation"))) {
                                        case NORTH: {
                                            if (!(n instanceof LivingEntity) || n instanceof ArmorStand || n instanceof Player || !(n.getLocation().getZ() < (double) b.getZ()))
                                                break;
                                            if (n.hasMetadata("android_killer")) {
                                                n.removeMetadata("android_killer", SlimefunStartup.instance);
                                            }
                                            n.setMetadata("android_killer", new FixedMetadataValue(SlimefunStartup.instance, new AndroidObject(this, b)));
                                            ((LivingEntity) n).damage(damage);
                                            break block4;
                                        }
                                        case EAST: {
                                            if (!(n instanceof LivingEntity) || n instanceof ArmorStand || n instanceof Player || !(n.getLocation().getX() > (double) b.getX()))
                                                break;
                                            if (n.hasMetadata("android_killer")) {
                                                n.removeMetadata("android_killer", SlimefunStartup.instance);
                                            }
                                            n.setMetadata("android_killer", new FixedMetadataValue(SlimefunStartup.instance, new AndroidObject(this, b)));
                                            ((LivingEntity) n).damage(damage);
                                            break block4;
                                        }
                                        case SOUTH: {
                                            if (!(n instanceof LivingEntity) || n instanceof ArmorStand || n instanceof Player || !(n.getLocation().getZ() > (double) b.getZ()))
                                                break;
                                            if (n.hasMetadata("android_killer")) {
                                                n.removeMetadata("android_killer", SlimefunStartup.instance);
                                            }
                                            n.setMetadata("android_killer", new FixedMetadataValue(SlimefunStartup.instance, new AndroidObject(this, b)));
                                            ((LivingEntity) n).damage(damage);
                                            break block4;
                                        }
                                        case WEST: {
                                            if (!(n instanceof LivingEntity) || n instanceof ArmorStand || n instanceof Player || !(n.getLocation().getX() < (double) b.getX()))
                                                break;
                                            if (n.hasMetadata("android_killer")) {
                                                n.removeMetadata("android_killer", SlimefunStartup.instance);
                                            }
                                            n.setMetadata("android_killer", new FixedMetadataValue(SlimefunStartup.instance, new AndroidObject(this, b)));
                                            ((LivingEntity) n).damage(damage);
                                            break block4;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    if (refresh) {
                        BlockStorage.addBlockInfo(b, "index", String.valueOf(index));
                    }
                } else {
                    return;
                }
            }
        }
    }

    private void move(Block b, BlockFace face, Block block) throws Exception {
        if (block.getY() < 0 || block.getY() > block.getWorld().getMaxHeight()) {
            return;
        }
        if (block.getType() == Material.AIR) {
            block.setType(Material.SKULL);
            block.setData((byte) 1);
            Skull skull = (Skull) block.getState();
            skull.setRotation(face);
            skull.update(true, false);
            CustomSkull.setSkull(block, CustomSkull.getTexture(this.getItem()));
            b.setType(Material.AIR);
            BlockStorage.moveBlockInfo(b.getLocation(), block.getLocation());
        }
    }

    private void mine(Block b, Block block) {
        Collection drops = block.getDrops();
        UUID owner = UUID.fromString(BlockStorage.getLocationInfo(b.getLocation(), "owner"));
        if (blockwhitelist.contains(block.getType()) && !drops.isEmpty() && CSCoreLib.getLib().getProtectionManager().canBuild(owner, block) && ProtectionUtils.canBuild(Bukkit.getPlayer(owner), block, false)) {
            SlimefunItem item = BlockStorage.check(block);
            if (item != null) {
                if (item.getID().equals("ANCIENT_PEDESTAL")) {
                    return;
                }
                if (this.fits(b, item.getItem()) && SlimefunItem.blockhandler.containsKey(item.getID()) && SlimefunItem.blockhandler.get(item.getID()).onBreak(null, block, item, UnregisterReason.ANDROID_DIG)) {
                    this.pushItems(b, BlockStorage.retrieve(block));
                    if (SlimefunItem.blockhandler.containsKey(item.getID())) {
                        SlimefunItem.blockhandler.get(item.getID()).onBreak(null, block, item, UnregisterReason.ANDROID_DIG);
                    }
                    block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, (Object) block.getType());
                    block.setType(Material.AIR);
                }
            } else {
                ItemStack[] items = (ItemStack[]) drops.toArray(new ItemStack[drops.size()]);
                if (this.fits(b, items)) {
                    this.pushItems(b, items);
                    block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, (Object) block.getType());
                    block.setType(Material.AIR);
                }
            }
        }
    }

    private void movedig(Block b, BlockFace face, Block block) {
        Collection drops = block.getDrops();
        UUID owner = UUID.fromString(BlockStorage.getLocationInfo(b.getLocation(), "owner"));
        if (blockwhitelist.contains(block.getType()) && !drops.isEmpty() && CSCoreLib.getLib().getProtectionManager().canBuild(owner, block) && ProtectionUtils.canBuild(Bukkit.getPlayer(owner), block, false)) {
            try {
                SlimefunItem item = BlockStorage.check(block);
                if (item != null) {
                    if (item.getID().equals("ANCIENT_PEDESTAL")) {
                        return;
                    }
                    if (this.fits(b, item.getItem()) && SlimefunItem.blockhandler.containsKey(item.getID()) && SlimefunItem.blockhandler.get(item.getID()).onBreak(null, block, item, UnregisterReason.ANDROID_DIG)) {
                        this.pushItems(b, BlockStorage.retrieve(block));
                        block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, (Object) block.getType());
                        block.setType(Material.SKULL);
                        block.setData((byte) 1);
                        Skull skull = (Skull) block.getState();
                        skull.setRotation(face);
                        skull.update(true, false);
                        CustomSkull.setSkull(block, CustomSkull.getTexture(this.getItem()));
                        b.setType(Material.AIR);
                        BlockStorage.moveBlockInfo(b.getLocation(), block.getLocation());
                    }
                } else {
                    ItemStack[] items = (ItemStack[]) drops.toArray(new ItemStack[drops.size()]);
                    if (this.fits(b, items)) {
                        this.pushItems(b, items);
                        block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, (Object) block.getType());
                        block.setType(Material.SKULL);
                        block.setData((byte) 1);
                        Skull skull = (Skull) block.getState();
                        skull.setRotation(face);
                        skull.update(true, false);
                        CustomSkull.setSkull(block, CustomSkull.getTexture(this.getItem()));
                        b.setType(Material.AIR);
                        BlockStorage.moveBlockInfo(b.getLocation(), block.getLocation());
                    }
                }
            } catch (Exception x) {
                x.printStackTrace();
            }
        } else {
            try {
                this.move(b, face, block);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void farm(Block b, Block block) {
        switch (block.getType()) {
            case CROPS: {
                if (block.getData() < 7) break;
                ItemStack drop = new ItemStack(Material.WHEAT, CSCoreLib.randomizer().nextInt(3) + 1);
                if (!this.fits(b, drop)) break;
                this.pushItems(b, drop);
                block.setData((byte) 0);
                block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, (Object) block.getType());
                break;
            }
            case POTATO: {
                if (block.getData() < 7) break;
                ItemStack drop = new ItemStack(Material.POTATO_ITEM, CSCoreLib.randomizer().nextInt(3) + 1);
                if (!this.fits(b, drop)) break;
                this.pushItems(b, drop);
                block.setData((byte) 0);
                block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, (Object) block.getType());
                break;
            }
            case CARROT: {
                if (block.getData() < 7) break;
                ItemStack drop = new ItemStack(Material.CARROT_ITEM, CSCoreLib.randomizer().nextInt(3) + 1);
                if (!this.fits(b, drop)) break;
                this.pushItems(b, drop);
                block.setData((byte) 0);
                block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, (Object) block.getType());
                break;
            }
            case BEETROOT_BLOCK: {
                if (block.getData() < 3) break;
                ItemStack drop = new ItemStack(Material.BEETROOT, CSCoreLib.randomizer().nextInt(3) + 1);
                if (!this.fits(b, drop)) break;
                this.pushItems(b, drop);
                block.setData((byte) 0);
                block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, (Object) block.getType());
                break;
            }
            case COCOA: {
                if (block.getData() < 8) break;
                ItemStack drop = new MaterialData(Material.INK_SACK, (byte) 3).toItemStack(CSCoreLib.randomizer().nextInt(3) + 1);
                if (!this.fits(b, drop)) break;
                this.pushItems(b, drop);
                block.setData((byte) (block.getData() - 8));
                block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, (Object) block.getType());
                break;
            }
            case NETHER_WARTS: {
                if (block.getData() < 3) break;
                ItemStack drop = new ItemStack(Material.NETHER_STALK, CSCoreLib.randomizer().nextInt(3) + 1);
                if (!this.fits(b, drop)) break;
                this.pushItems(b, drop);
                block.setData((byte) 0);
                block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, (Object) block.getType());
                break;
            }
        }
    }

    private void exoticFarm(Block b, Block block) {
        ItemStack drop;
        this.farm(b, block);
        if (SlimefunStartup.slimefunStartup.isExoticGardenInstalled() && (drop = ExoticGarden.harvestPlant(block)) != null) {
            if (this.fits(b, drop)) {
                this.pushItems(b, drop);
                block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, (Object) block.getType());
            }
        }
    }

    private void constructMenu(BlockMenuPreset preset) throws Exception {
        for (int i : border) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), ""), (arg0, arg1, arg2, arg3) -> false);
        }
        for (int i : border_out) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 1), ""), (arg0, arg1, arg2, arg3) -> false);
        }
        for (int i : this.getOutputSlots()) {
            preset.addMenuClickHandler(i, new ChestMenu.AdvancedMenuClickHandler() {

                public boolean onClick(Player p, int slot, ItemStack cursor, ClickAction action) {
                    return false;
                }

                public boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor, ClickAction action) {
                    return cursor == null || cursor.getType() == null || cursor.getType() == Material.AIR;
                }
            });
        }
        if (this.getTier() == 1) {
            preset.addItem(34, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM0M2NlNThkYTU0Yzc5OTI0YTJjOTMzMWNmYzQxN2ZlOGNjYmJlYTliZTQ1YTdhYzg1ODYwYTZjNzMwIn19fQ=="), "&8⇩ &c燃料输入槽 &8⇩", "", "&r需要固体燃料", "&r例如: 煤炭, 木炭等..."), (p, slot, stack, action) -> false);
        } else if (this.getTier() == 2) {
            preset.addItem(34, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM0M2NlNThkYTU0Yzc5OTI0YTJjOTMzMWNmYzQxN2ZlOGNjYmJlYTliZTQ1YTdhYzg1ODYwYTZjNzMwIn19fQ=="), "&8⇩ &c燃料输入槽 &8⇩", "", "&r需要液体燃料\", \"&r例如: 岩浆, 石油等..."), (p, slot, stack, action) -> false);
        } else {
            preset.addItem(34, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM0M2NlNThkYTU0Yzc5OTI0YTJjOTMzMWNmYzQxN2ZlOGNjYmJlYTliZTQ1YTdhYzg1ODYwYTZjNzMwIn19fQ=="), "&8⇩ &c燃料输入槽 &8⇩", "", "&r需要放射性燃料", "&r例如: 铀, 镎, 活性铀"), (p, slot, stack, action) -> false);
        }
    }

    public void openScriptEditor(Player p, final Block b) throws Exception {
        ChestMenu menu = new ChestMenu("&e脚本编辑器");
        menu.addItem(2, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDliZjZkYjRhZWRhOWQ4ODIyYjlmNzM2NTM4ZThjMThiOWE0ODQ0Zjg0ZWI0NTUwNGFkZmJmZWU4N2ViIn19fQ=="), "&2> 编辑脚本", "", "&a编辑当前脚本"), (p13, slot, stack, action) -> {
            try {
                ProgrammableAndroid.this.openScript(p13, b, BlockStorage.getLocationInfo(b.getLocation(), "script"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
        menu.addItem(4, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTcxZDg5NzljMTg3OGEwNTk4N2E3ZmFmMjFiNTZkMWI3NDRmOWQwNjhjNzRjZmZjZGUxZWExZWRhZDU4NTIifX19"), "&4> 创建新脚本", "", "&c删除当前脚本", "&c并创建一个空白脚本"), (p12, slot, stack, action) -> {
            try {
                ProgrammableAndroid.this.openScript(p12, b, "START-TURN_LEFT-REPEAT");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
        menu.addItem(6, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzAxNTg2ZTM5ZjZmZmE2M2I0ZmIzMDFiNjVjYTdkYThhOTJmNzM1M2FhYWI4OWQzODg2NTc5MTI1ZGZiYWY5In19fQ=="), "&6> 下载脚本", "", "&e从云端下载一个脚本", "&e你可以对下载的脚本进行编辑或直接使用"), (p1, slot, stack, action) -> {
            try {
                ProgrammableAndroid.this.openScriptDownloader(p1, b, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
        menu.open(p);
    }

    public void openScript(final Player p, final Block b, final String script) throws Exception {
        ChestMenu menu = new ChestMenu("&e脚本编辑器");
        final String[] commands = script.split("-");
        menu.addItem(0, ScriptPart.START.toItemStack());
        menu.addMenuClickHandler(0, (arg0, arg1, arg2, arg3) -> false);
        for (int i = 1; i < commands.length; ++i) {
            final int index = i;
            if (i == commands.length - 1) {
                int additional;
                int n = additional = commands.length == 54 ? 0 : 1;
                if (additional == 1) {
                    menu.addItem(i, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTcxZDg5NzljMTg3OGEwNTk4N2E3ZmFmMjFiNTZkMWI3NDRmOWQwNjhjNzRjZmZjZGUxZWExZWRhZDU4NTIifX19"), "&7> 添加新命令"));
                    menu.addMenuClickHandler(i, (arg0, arg1, arg2, arg3) -> {
                        try {
                            ProgrammableAndroid.this.openScriptComponentEditor(p, b, script, index);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    });
                }
                menu.addItem(i + additional, ScriptPart.REPEAT.toItemStack());
                menu.addMenuClickHandler(i + additional, (arg0, arg1, arg2, arg3) -> false);
                continue;
            }
            ItemStack stack = ScriptPart.valueOf(commands[i]).toItemStack();
            menu.addItem(i, new CustomItem(stack, stack.getItemMeta().getDisplayName(), "", "&7⇨ &e左键点击 &e进行编辑", "&7⇨ &e右键点击 &7进行删除", "&7⇨ &eShift + 右键点击 &7复制"));
            menu.addMenuClickHandler(i, (arg0, arg1, arg2, action) -> {
                if (action.isRightClicked() && action.isShiftClicked()) {
                    if (commands.length == 54) {
                        return false;
                    }
                    int i1 = 0;
                    StringBuilder builder = new StringBuilder("START-");
                    for (String command : commands) {
                        if (i1 > 0) {
                            if (i1 == index) {
                                builder.append(commands[i1] + "-");
                                builder.append(commands[i1] + "-");
                            } else if (i1 < commands.length - 1) {
                                builder.append(command + "-");
                            }
                        }
                        ++i1;
                    }
                    builder.append("REPEAT");
                    BlockStorage.addBlockInfo(b, "script", builder.toString());
                    try {
                        ProgrammableAndroid.this.openScript(p, b, builder.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (action.isRightClicked()) {
                    int i1 = 0;
                    StringBuilder builder = new StringBuilder("START-");
                    for (String command : commands) {
                        if (i1 != index && i1 > 0 && i1 < commands.length - 1) {
                            builder.append(command + "-");
                        }
                        ++i1;
                    }
                    builder.append("REPEAT");
                    BlockStorage.addBlockInfo(b, "script", builder.toString());
                    try {
                        ProgrammableAndroid.this.openScript(p, b, builder.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        ProgrammableAndroid.this.openScriptComponentEditor(p, b, script, index);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            });
        }
        menu.open(p);
    }

    private void openScriptDownloader(final Player p, final Block b, final int page) throws Exception {
        int target;
        ChestMenu menu = new ChestMenu("Slimefun向导");
        menu.addMenuOpeningHandler(p14 -> p14.playSound(p14.getLocation(), Sound.BLOCK_NOTE_HAT, 0.7f, 0.7f));
        List<Config> scripts = this.getUploadedScripts();
        int index = 0;
        final int pages = scripts.size() / 45 + 1;
        for (int i = 45; i < 54; ++i) {
            menu.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), ""));
            menu.addMenuClickHandler(i, (arg0, arg1, arg2, arg3) -> false);
        }
        menu.addItem(46, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 5), "&r⇦上一页", "", "&7(" + page + " / " + pages + ")"));
        menu.addMenuClickHandler(46, (arg0, arg1, arg2, arg3) -> {
            int next = page - 1;
            if (next < 1) {
                next = pages;
            }
            if (next != page) {
                try {
                    ProgrammableAndroid.this.openScriptDownloader(p, b, next);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        });
        menu.addItem(49, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTA1YTJjYWI4YjY4ZWE1N2UzYWY5OTJhMzZlNDdjOGZmOWFhODdjYzg3NzYyODE5NjZmOGMzY2YzMWEzOCJ9fX0="), "&e上传脚本", "", "&6点击 &7上传你机器人的脚本至云端", "&7分享给他人使用"));
        menu.addMenuClickHandler(49, (p13, arg1, arg2, arg3) -> {
            final String code = BlockStorage.getLocationInfo(b.getLocation(), "script");
            int num = 1;
            for (Config script : ProgrammableAndroid.this.getUploadedScripts()) {
                if (script.getString("author").equals(p13.getUniqueId().toString())) {
                    ++num;
                }
                if (!script.getString("code").equals(code)) continue;
                Messages.local.sendTranslation(p13, "android.scripts.already-uploaded", true);
                return false;
            }
            final int id = num;
            p13.closeInventory();
            Messages.local.sendTranslation(p13, "android.scripts.enter-name", true);
            MenuHelper.awaitChatInput(p13, (p12, message) -> {
                Config script = new Config("plugins/Slimefun/scripts/" + ProgrammableAndroid.this.getAndroidType().toString() + "/" + p12.getName() + " " + id + ".sfs");
                script.setValue("author", p12.getUniqueId().toString());
                script.setValue("author_name", p12.getName());
                script.setValue("name", ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message)));
                script.setValue("code", code);
                script.setValue("downloads", 0);
                script.setValue("android", ProgrammableAndroid.this.getAndroidType().toString());
                script.setValue("rating.positive", new ArrayList());
                script.setValue("rating.negative", new ArrayList());
                script.save();
                try {
                    Messages.local.sendTranslation(p12, "android.uploaded", true);
                    ProgrammableAndroid.this.openScriptDownloader(p12, b, page);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            });
            return false;
        });
        menu.addItem(52, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 5), "&r下一页⇨", "", "&7(" + page + " / " + pages + ")"));
        menu.addMenuClickHandler(52, (arg0, arg1, arg2, arg3) -> {
            int next = page + 1;
            if (next > pages) {
                next = 1;
            }
            if (next != page) {
                try {
                    ProgrammableAndroid.this.openScriptDownloader(p, b, next);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        });
        int category_index = 45 * (page - 1);
        for (int i = 0; i < 45 && (target = category_index + i) < scripts.size(); ++i) {
            String author;
            final Config script = scripts.get(target);
            OfflinePlayer op = Bukkit.getOfflinePlayer(script.getUUID("author"));
            String string = author = op != null && op.getName() != null ? op.getName() : script.getString("author_name");
            if (script.getString("author").equals(p.getUniqueId().toString())) {
                menu.addItem(index, new CustomItem(this.getItem(), "&b" + script.getString("name"), "&7by &r" + author, "", "&7Downloads: &r" + script.getInt("downloads"), "&7Rating: " + this.getScriptRatingPercentage(script), "&a" + this.getScriptRating(script, true) + " ☺ &7- &4☹ " + this.getScriptRating(script, false), "", "&e左键点击 &r下载这个脚本", "&4(这将覆盖你当前的脚本)"));
            } else {
                menu.addItem(index, new CustomItem(this.getItem(), "&b" + script.getString("name"), "&7by &r" + author, "", "&7Downloads: &r" + script.getInt("downloads"), "&7Rating: " + this.getScriptRatingPercentage(script), "&a" + this.getScriptRating(script, true) + " ☺ &7- &4☹ " + this.getScriptRating(script, false), "", "&e左键点击 &r下载这个脚本", "&4(这将覆盖你当前的脚本)", "&eShift + 左键点击 &r给脚本点赞", "&eShift + 右键点击 &r差评这个脚本"));
            }
            menu.addMenuClickHandler(index, (p1, slot, stack, action) -> {
                Config script2 = new Config(script.getFile());
                if (action.isShiftClicked()) {
                    if (script2.getString("author").equals(p1.getUniqueId().toString())) {
                        Messages.local.sendTranslation(p1, "android.scripts.rating.own", true);
                    } else if (action.isRightClicked()) {
                        if (!script2.getStringList("rating.negative").contains(p1.getUniqueId().toString()) && !script2.getStringList("rating.positive").contains(p1.getUniqueId().toString())) {
                            List list = script2.getStringList("rating.negative");
                            list.add(p1.getUniqueId().toString());
                            script2.setValue("rating.negative", list);
                            script2.save();
                            try {
                                ProgrammableAndroid.this.openScriptDownloader(p1, b, page);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Messages.local.sendTranslation(p1, "android.scripts.rating.already", true);
                        }
                    } else if (!script2.getStringList("rating.negative").contains(p1.getUniqueId().toString()) && !script2.getStringList("rating.positive").contains(p1.getUniqueId().toString())) {
                        List list = script2.getStringList("rating.positive");
                        list.add(p1.getUniqueId().toString());
                        script2.setValue("rating.positive", list);
                        script2.save();
                        try {
                            ProgrammableAndroid.this.openScriptDownloader(p1, b, page);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Messages.local.sendTranslation(p1, "android.scripts.rating.already", true);
                    }
                } else if (!action.isRightClicked()) {
                    try {
                        script2.setValue("downloads", script2.getInt("downloads") + 1);
                        script2.save();
                        BlockStorage.addBlockInfo(b, "script", script2.getString("code"));
                        ProgrammableAndroid.this.openScriptEditor(p1, b);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            });
            ++index;
        }
        menu.open(p);
    }

    public float getScriptRating(Config script) {
        return (float) Math.round(((float) this.getScriptRating(script, true) * 100.0f / (float) this.getScriptRating(script, true) + (float) this.getScriptRating(script, false)) * 100.0f) / 100.0f;
    }

    private int getScriptRating(Config script, boolean positive) {
        if (positive) {
            return script.getStringList("rating.positive").size();
        }
        return script.getStringList("rating.negative").size();
    }

    private String getScriptRatingPercentage(Config script) {
        String progress = String.valueOf(this.getScriptRating(script));
        progress = Float.parseFloat(progress) < 16.0f ? "&4" + progress + "&r% " : (Float.parseFloat(progress) < 32.0f ? "&c" + progress + "&r% " : (Float.parseFloat(progress) < 48.0f ? "&6" + progress + "&r% " : (Float.parseFloat(progress) < 64.0f ? "&e" + progress + "&r% " : (Float.parseFloat(progress) < 80.0f ? "&2" + progress + "&r% " : "&a" + progress + "&r% "))));
        return progress;
    }

    protected void openScriptComponentEditor(Player p, final Block b, String script, final int index) throws Exception {
        ChestMenu menu = new ChestMenu("&e脚本编辑器");
        final String[] commands = script.split("-");
        menu.addItem(0, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        menu.addItem(1, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        menu.addItem(2, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        menu.addItem(3, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        menu.addItem(4, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        menu.addItem(5, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        menu.addItem(6, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        menu.addItem(7, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        menu.addItem(8, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        menu.addItem(18, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        menu.addItem(19, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        menu.addItem(20, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        menu.addItem(21, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        menu.addItem(22, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        menu.addItem(23, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        menu.addItem(24, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        menu.addItem(25, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        menu.addItem(26, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "), (arg0, arg1, arg2, arg3) -> false);
        menu.addItem(9, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTYxMzlmZDFjNTY1NGU1NmU5ZTRlMmM4YmU3ZWIyYmQ1YjQ5OWQ2MzM2MTY2NjNmZWVlOTliNzQzNTJhZDY0In19fQ=="), "&r待命"), (p12, arg1, arg2, arg3) -> {
            int i = 0;
            StringBuilder builder = new StringBuilder("START-");
            for (String command : commands) {
                if (i != index && i > 0 && i < commands.length - 1) {
                    builder.append(command + "-");
                }
                ++i;
            }
            builder.append("REPEAT");
            BlockStorage.addBlockInfo(b, "script", builder.toString());
            try {
                ProgrammableAndroid.this.openScript(p12, b, builder.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
        int i = 10;
        for (final ScriptPart part : this.getAccessibleScriptParts()) {
            menu.addItem(i, part.toItemStack(), (p1, arg1, arg2, arg3) -> {
                int i1 = 0;
                StringBuilder builder = new StringBuilder("START-");
                for (String command : commands) {
                    if (i1 > 0) {
                        if (i1 == index) {
                            builder.append(part.toString() + "-");
                        } else if (i1 < commands.length - 1) {
                            builder.append(command + "-");
                        }
                    }
                    ++i1;
                }
                builder.append("REPEAT");
                BlockStorage.addBlockInfo(b, "script", builder.toString());
                try {
                    ProgrammableAndroid.this.openScript(p1, b, builder.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            });
            ++i;
        }
        menu.open(p);
    }

    private Inventory inject(Block b) {
        int size = BlockStorage.getInventory(b).toInventory().getSize();
        Inventory inv = Bukkit.createInventory(null, size);
        for (int i = 0; i < size; ++i) {
            inv.setItem(i, new CustomItem(Material.COMMAND, " &4ALL YOUR PLACEHOLDERS ARE BELONG TO US", 0));
        }
        for (int slot : this.getOutputSlots()) {
            inv.setItem(slot, BlockStorage.getInventory(b).getItemInSlot(slot));
        }
        return inv;
    }

    protected boolean fits(Block b, ItemStack... items) {
        return this.inject(b).addItem(items).isEmpty();
    }

    protected void pushItems(Block b, ItemStack... items) {
        Inventory inv = this.inject(b);
        inv.addItem(items);
        for (int slot : this.getOutputSlots()) {
            BlockStorage.getInventory(b).replaceExistingItem(slot, inv.getItem(slot));
        }
    }

    public void addItems(Block b, ItemStack... items) {
        this.pushItems(b, items);
    }

    @Override
    public void register(boolean slimefun) {
        this.addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem sf, Config data) {
                if (b != null) {
                    ProgrammableAndroid.this.tick(b);
                }
            }

            @Override
            public void uniqueTick() {
            }

            @Override
            public boolean isSynchronized() {
                return true;
            }
        });
        super.register(slimefun);
    }

    public void registerFuel(MachineFuel fuel) {
        this.recipes.add(fuel);
    }

    public List<Config> getUploadedScripts() {
        ArrayList<Config> scripts = new ArrayList<Config>();
        File directory = new File("plugins/Slimefun/scripts/" + this.getAndroidType().toString());
        if (!directory.exists()) {
            directory.mkdirs();
        }
        for (File script : directory.listFiles()) {
            if (!script.getName().endsWith("sfs")) continue;
            scripts.add(new Config(script));
        }
        if (!this.getAndroidType().equals(AndroidType.NONE)) {
            File directory2 = new File("plugins/Slimefun/scripts/NONE");
            if (!directory2.exists()) {
                directory2.mkdirs();
            }
            for (File script : directory2.listFiles()) {
                if (!script.getName().endsWith("sfs")) continue;
                scripts.add(new Config(script));
            }
        }
        Collections.sort(scripts, new ScriptReputationSorter(this));
        return scripts;
    }

    public List<ScriptPart> getAccessibleScriptParts() {
        ArrayList<ScriptPart> list = new ArrayList<ScriptPart>();
        for (ScriptPart part : ScriptPart.values()) {
            if (part.equals(ScriptPart.START) || part.equals(ScriptPart.REPEAT) || !this.getAndroidType().isType(part.getRequiredType()))
                continue;
            list.add(part);
        }
        return list;
    }
}

