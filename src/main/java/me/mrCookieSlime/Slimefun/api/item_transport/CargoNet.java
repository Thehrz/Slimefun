package me.mrCookieSlime.Slimefun.api.item_transport;

import me.mrCookieSlime.CSCoreLibPlugin.general.Particles.MC_1_8.ParticleEffect;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.UniversalBlockMenu;
import me.mrCookieSlime.Slimefun.api.network.Network;
import me.mrCookieSlime.Slimefun.holograms.CargoHologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CargoNet extends Network {
    public static final int[] TERMINAL_SLOTS;
    public static boolean EXTRA_CHANNELS;
    public static List<BlockFace> faces;
    public static Map<Location, Integer> round_robin;
    public static Set<ItemRequest> requests;

    static {
        CargoNet.EXTRA_CHANNELS = false;
        CargoNet.faces = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
        CargoNet.round_robin = new HashMap<>();
        CargoNet.requests = new HashSet<>();
        TERMINAL_SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6, 9, 10, 11, 12, 13, 14, 15, 18, 19, 20, 21, 22, 23, 24, 27, 28, 29, 30, 31, 32, 33, 36, 37, 38, 39, 40, 41, 42};
    }

    final Set<Location> terminals;
    private final Set<Location> inputNodes;
    private final Set<Location> outputNodes;
    private final Set<Location> advancedOutputNodes;
    private final Set<Location> cargoNodes;

    protected CargoNet(final Location l) {
        super(l);
        this.inputNodes = new HashSet<>();
        this.outputNodes = new HashSet<>();
        this.advancedOutputNodes = new HashSet<>();
        this.terminals = new HashSet<>();
        this.cargoNodes = new HashSet<>();
    }

    public static CargoNet getNetworkFromLocation(final Location l) {
        return Network.getNetworkFromLocation(l, CargoNet.class);
    }

    public static CargoNet getNetworkFromLocationOrCreate(final Location l) {
        CargoNet networkFromLocation = getNetworkFromLocation(l);
        if (networkFromLocation == null) {
            networkFromLocation = new CargoNet(l);
            Network.registerNetwork(networkFromLocation);
        }
        return networkFromLocation;
    }

    public static boolean isConnected(final Block b) {
        return getNetworkFromLocation(b.getLocation()) != null;
    }

    private static Block getAttachedBlock(final Block block) {
        switch (block.getData()) {
            case 2:
                return block.getRelative(BlockFace.SOUTH);
            case 3:
                return block.getRelative(BlockFace.NORTH);
            case 4:
                return block.getRelative(BlockFace.EAST);
            case 5:
                return block.getRelative(BlockFace.WEST);
            default:
                return null;
        }
    }

    private static int getFrequency(final Location l) {
        int freq = 0;
        try {
            freq = Integer.parseInt(BlockStorage.getLocationInfo(l).getString("frequency"));
        } catch (Exception ex) {
            Bukkit.getLogger().info("获取货运节点频道异常 位于 " + l.getWorld() + " X:" + l.getX() + " Y:" + l.getY() + " Z:" + l.getZ());
            l.getBlock().setType(Material.AIR);
            BlockStorage.clearBlockInfo(l);
        }
        return freq;
    }

    @Override
    public int getRange() {
        return 5;
    }

    @Override
    public Component classifyLocation(final Location l) {
        final String id = BlockStorage.checkID(l);
        if (id == null) {
            return null;
        }
        switch (id) {
            case "CARGO_MANAGER": {
                return Component.REGULATOR;
            }
            case "CARGO_NODE": {
                return Component.CONNECTOR;
            }
            case "CARGO_NODE_INPUT":
            case "CARGO_NODE_OUTPUT":
            case "CARGO_NODE_OUTPUT_ADVANCED":
            case "CHEST_TERMINAL": {
                return Component.TERMINUS;
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public void locationClassificationChange(final Location l, final Component from, final Component to) {
        if (from == Component.TERMINUS) {
            this.inputNodes.remove(l);
            this.outputNodes.remove(l);
            this.advancedOutputNodes.remove(l);
            this.terminals.remove(l);
            this.cargoNodes.remove(l);
        }
        if (to == Component.TERMINUS) {
            switch (Objects.requireNonNull(BlockStorage.checkID(l))) {
                case "CARGO_NODE_INPUT": {
                    this.cargoNodes.add(l);
                    this.inputNodes.add(l);
                    break;
                }
                case "CARGO_NODE_OUTPUT": {
                    this.cargoNodes.add(l);
                    this.outputNodes.add(l);
                    break;
                }
                case "CARGO_NODE_OUTPUT_ADVANCED": {
                    this.cargoNodes.add(l);
                    this.advancedOutputNodes.add(l);
                    break;
                }
                case "CHEST_TERMINAL": {
                    this.terminals.add(l);
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    public void tick(final Block b) {
        if (!this.regulator.equals(b.getLocation())) {
            CargoHologram.update(b, "&4多个货运节点相连");
            return;
        }
        super.tick();
        if (this.connectorNodes.isEmpty() && this.terminusNodes.isEmpty()) {
            CargoHologram.update(b, "&7无货运节点");
            return;
        }
        CargoHologram.update(b, "&7状态: &a&l在线");
        final Map<Integer, List<Location>> output = new HashMap<>();
        for (final Location outputNode : this.outputNodes) {
            final Integer frequency = getFrequency(outputNode);
            if (!output.containsKey(frequency)) {
                output.put(frequency, new ArrayList<>());
            }
            output.get(frequency).add(outputNode);
        }
        for (final Location outputNode : this.advancedOutputNodes) {
            final Integer frequency = getFrequency(outputNode);
            if (!output.containsKey(frequency)) {
                output.put(frequency, new ArrayList<>());
            }
            output.get(frequency).add(outputNode);
        }
        final CargoNet self = this;
        final BlockStorage storage = BlockStorage.getStorage(b.getWorld());
        SlimefunStartup.instance.getServer().getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
            if (BlockStorage.getLocationInfo(b.getLocation(), "visualizer") == null) {
                self.display();
            }
            for (final Location input : CargoNet.this.inputNodes) {
                final int frequency = getFrequency(input);
                if (frequency >= 0) {
                    if (frequency > 15) {
                        continue;
                    }
                    final Block inputTarget = getAttachedBlock(input.getBlock());
                    ItemStack stack = null;
                    int previousSlot = -1;
                    final boolean roundrobin = "true".equals(BlockStorage.getLocationInfo(input, "round-robin"));
                    if (inputTarget != null) {
                        final ItemSlot slot3 = CargoManager.withdraw(input.getBlock(), storage, inputTarget, Integer.parseInt(BlockStorage.getLocationInfo(input, "index")));
                        if (slot3 != null) {
                            stack = slot3.getItem();
                            previousSlot = slot3.getSlot();
                        }
                    }
                    if (stack != null && output.containsKey(frequency)) {
                        final List<Location> outputlist = new ArrayList<>(output.get(frequency));
                        if (roundrobin) {
                            if (!CargoNet.round_robin.containsKey(input)) {
                                CargoNet.round_robin.put(input, 0);
                            }
                            int cIndex = CargoNet.round_robin.get(input);
                            if (cIndex < outputlist.size()) {
                                for (int j = 0; j < cIndex; ++j) {
                                    final Location temp = outputlist.get(0);
                                    outputlist.remove(temp);
                                    outputlist.add(temp);
                                }
                                ++cIndex;
                            } else {
                                cIndex = 1;
                            }
                            CargoNet.round_robin.put(input, cIndex);
                        }
                        for (final Location out : outputlist) {
                            final Block target = getAttachedBlock(out.getBlock());
                            if (target != null) {
                                stack = CargoManager.insert(out.getBlock(), storage, target, stack, -1);
                                if (stack == null) {
                                    break;
                                }
                            }
                        }
                    }
                    if (stack == null || previousSlot <= -1) {
                        continue;
                    }
                    if (storage.hasUniversalInventory(inputTarget)) {
                        final UniversalBlockMenu menu = storage.getUniversalInventory(inputTarget);
                        menu.replaceExistingItem(previousSlot, stack);
                    } else if (storage.hasInventory(inputTarget.getLocation())) {
                        final BlockMenu menu = BlockStorage.getInventory(inputTarget.getLocation());
                        menu.replaceExistingItem(previousSlot, stack);
                    } else {
                        if (!(inputTarget.getState() instanceof InventoryHolder)) {
                            continue;
                        }
                        final Inventory inv = ((InventoryHolder) inputTarget.getState()).getInventory();
                        inv.setItem(previousSlot, stack);
                    }
                }
            }
        });
    }

    @Override
    public void display() {
        SlimefunStartup.instance.getServer().getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
            for (Location l : this.cargoNodes) {
                try {
                    ParticleEffect.REDSTONE.display(l.clone().add(0.5D, 0.5D, 0.5D), 0.0F, 0.0F, 0.0F, 0.0F, 1);
                } catch (Exception exception) {
                    System.err.println("发送粒子异常 位于 " + l.getWorld() + " X:" + l.getX() + " Y:" + l.getY() + " Z:" + l.getZ());
                }
            }
            for (Location l : super.connectorNodes) {
                try {
                    ParticleEffect.REDSTONE.display(l.clone().add(0.5D, 0.5D, 0.5D), 0.0F, 0.0F, 0.0F, 0.0F, 1);
                } catch (Exception exception) {
                    System.err.println("发送粒子异常 位于 " + l.getWorld() + " X:" + l.getX() + " Y:" + l.getY() + " Z:" + l.getZ());
                }
            }
        });
    }
}
