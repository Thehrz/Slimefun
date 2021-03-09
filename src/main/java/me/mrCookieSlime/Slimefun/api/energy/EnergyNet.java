package me.mrCookieSlime.Slimefun.api.energy;

import me.mrCookieSlime.CSCoreLibPlugin.general.Math.DoubleHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.TickerTask;
import me.mrCookieSlime.Slimefun.api.network.Network;
import me.mrCookieSlime.Slimefun.holograms.EnergyHologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EnergyNet extends Network {
    private static final int RANGE = 6;
    public static Set<String> machines_input = new HashSet<>();
    public static Set<String> machines_storage = new HashSet<>();
    public static Set<String> machines_output = new HashSet<>();
    public static Map<String, EnergyFlowListener> listeners = new HashMap<>();
    private final Set<Location> input = new HashSet<>();
    private final Set<Location> storage = new HashSet<>();
    private final Set<Location> output = new HashSet<>();

    protected EnergyNet(Location l) {
        super(l);
    }

    public static NetworkComponent getComponent(Block b) {
        return getComponent(b.getLocation());
    }

    public static NetworkComponent getComponent(String id) {
        if (machines_input.contains(id)) {
            return NetworkComponent.SOURCE;
        }
        if (machines_storage.contains(id)) {
            return NetworkComponent.DISTRIBUTOR;
        }
        if (machines_output.contains(id)) {
            return NetworkComponent.CONSUMER;
        }
        return NetworkComponent.NONE;
    }

    public static NetworkComponent getComponent(Location l) {
        if (!BlockStorage.hasBlockInfo(l)) {
            return NetworkComponent.NONE;
        }
        String id = BlockStorage.checkID(l);
        if (machines_input.contains(id)) {
            return NetworkComponent.SOURCE;
        }
        if (machines_storage.contains(id)) {
            return NetworkComponent.DISTRIBUTOR;
        }
        if (machines_output.contains(id)) {
            return NetworkComponent.CONSUMER;
        }
        return NetworkComponent.NONE;
    }

    public static void registerComponent(String id, NetworkComponent component) {
        switch (component) {
            case CONSUMER:
                machines_output.add(id);
                break;
            case DISTRIBUTOR:
                machines_storage.add(id);
                break;
            case SOURCE:
                machines_input.add(id);
                break;
            default:
                break;
        }
    }

    public static EnergyNet getNetworkFromLocation(Location l) {
        return getNetworkFromLocation(l, EnergyNet.class);
    }

    public static EnergyNet getNetworkFromLocationOrCreate(Location l) {
        EnergyNet energyNetwork = getNetworkFromLocation(l);
        if (energyNetwork == null) {
            energyNetwork = new EnergyNet(l);
            registerNetwork(energyNetwork);
        }
        return energyNetwork;
    }

    @Override
    public int getRange() {
        return 6;
    }

    @Override
    public Network.Component classifyLocation(Location l) {
        if (this.regulator.equals(l)) {
            return Component.REGULATOR;
        }
        switch (getComponent(l)) {
            case DISTRIBUTOR:
                return Network.Component.CONNECTOR;
            case CONSUMER:
            case SOURCE:
                return Network.Component.TERMINUS;
            default:
                return null;
        }
    }

    @Override
    public void locationClassificationChange(Location l, Network.Component from, Network.Component to) {
        if (from == Network.Component.TERMINUS) {
            this.input.remove(l);
            this.output.remove(l);
        }
        switch (getComponent(l)) {
            case DISTRIBUTOR:
                if (ChargableBlock.isCapacitor(l)) {
                    this.storage.add(l);
                }
                break;
            case CONSUMER:
                this.output.add(l);
                break;
            case SOURCE:
                this.input.add(l);
                break;
            default:
                break;
        }
    }

    public void tick(Block b) {
        if (!this.regulator.equals(b.getLocation())) {
            EnergyHologram.update(b, "&4多个能量核心相连！");
            return;
        }
        tick();
        double supply = 0.0D;
        double demand = 0.0D;

        if (this.connectorNodes.isEmpty() && this.terminusNodes.isEmpty()) {
            EnergyHologram.update(b, "&4未构成能量网络！");
        } else {

            for (Location source : this.input) {
                long timestamp = System.currentTimeMillis();
                SlimefunItem item = BlockStorage.check(source);
                double energy = item.getEnergyTicker().generateEnergy(source, item, BlockStorage.getLocationInfo(source));

                if (item.getEnergyTicker().explode(source)) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
                        BlockStorage.clearBlockInfo(source);
                        EnergyHologram.remove(source.getBlock());
                        source.getBlock().setType(Material.LAVA);
                        source.getWorld().createExplosion(source, 0.0F, false);

                    });

                    Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
                        Network.handleAllNetworkLocationUpdate(source);
                        item.getEnergyTicker().explode(source);

                    });

                } else {

                    supply += energy;
                }
                TickerTask.block_timings.put(source, System.currentTimeMillis() - timestamp);
            }

            for (Location battery : this.storage) {
                supply += ChargableBlock.getCharge(battery);
            }

            int available = (int) DoubleHandler.fixDouble(supply);

            for (Location destination : this.output) {
                int capacity = ChargableBlock.getMaxCharge(destination);
                int charge = ChargableBlock.getCharge(destination);
                if (charge < capacity) {
                    int rest = capacity - charge;
                    demand += rest;
                    if (available > 0) {
                        if (available > rest) {
                            ChargableBlock.setUnsafeCharge(destination, capacity, false);
                            available -= rest;
                            continue;
                        }
                        ChargableBlock.setUnsafeCharge(destination, charge + available, false);
                        available = 0;
                    }
                }
            }


            for (Location battery : this.storage) {
                if (available > 0) {
                    int capacity = ChargableBlock.getMaxCharge(battery);

                    if (available > capacity) {
                        ChargableBlock.setUnsafeCharge(battery, capacity, true);
                        available -= capacity;
                        continue;
                    }
                    ChargableBlock.setUnsafeCharge(battery, available, true);
                    available = 0;
                    continue;
                }
                ChargableBlock.setUnsafeCharge(battery, 0, true);
            }

            for (Location source : this.input) {
                if (ChargableBlock.isChargable(source)) {
                    if (available > 0) {
                        int capacity = ChargableBlock.getMaxCharge(source);

                        if (available > capacity) {
                            ChargableBlock.setUnsafeCharge(source, capacity, false);
                            available -= capacity;
                            continue;
                        }
                        ChargableBlock.setUnsafeCharge(source, available, false);
                        available = 0;
                        continue;
                    }
                    ChargableBlock.setUnsafeCharge(source, 0, false);
                }
            }

            EnergyHologram.update(b, supply, demand);
        }
    }


    public enum NetworkComponent {
        SOURCE,
        DISTRIBUTOR,
        CONSUMER,
        NONE
    }
}


