package me.mrCookieSlime.Slimefun.api.network;

import me.mrCookieSlime.CSCoreLibPlugin.general.Particles.MC_1_8.ParticleEffect;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import org.bukkit.Location;

import java.util.*;

public abstract class Network {
    private static final List<Network> NETWORK_LIST = new ArrayList<>();
    protected final Location regulator;
    protected final Set<Location> connectedLocations = new HashSet<>();
    protected final Set<Location> regulatorNodes = new HashSet<>();
    protected final Set<Location> connectorNodes = new HashSet<>();
    protected final Set<Location> terminusNodes = new HashSet<>();
    private final Queue<Location> nodeQueue = new ArrayDeque<>();

    protected Network(Location regulator) {
        this.regulator = regulator;
        this.connectedLocations.add(regulator);
        this.nodeQueue.add(regulator.clone());
    }

    public static <T extends Network> T getNetworkFromLocation(Location l, Class<T> type) {
        for (Network n : NETWORK_LIST) {
            if (type.isInstance(n) && n.connectsTo(l)) {
                return type.cast(n);
            }
        }
        return null;
    }

    public static <T extends Network> List<T> getNetworksFromLocation(Location l, Class<T> type) {
        List<T> ret = new ArrayList<>();
        for (Network n : NETWORK_LIST) {
            if (type.isInstance(n) && n.connectsTo(l)) {
                ret.add(type.cast(n));
            }
        }
        return ret;
    }

    public static void registerNetwork(Network n) {
        NETWORK_LIST.add(n);
    }

    public static void unregisterNetwork(Network n) {
        NETWORK_LIST.remove(n);
    }

    public static void handleAllNetworkLocationUpdate(Location l) {
        for (Network n : getNetworksFromLocation(l, Network.class)) {
            n.handleLocationUpdate(l);
        }
    }

    public abstract int getRange();

    public abstract Component classifyLocation(Location paramLocation);

    public abstract void locationClassificationChange(Location paramLocation, Component paramComponent1, Component paramComponent2);

    protected void addLocationToNetwork(Location l) {
        if (this.connectedLocations.contains(l)) {
            return;
        }
        this.connectedLocations.add(l.clone());
        handleLocationUpdate(l);
    }

    public void handleLocationUpdate(Location l) {
        if (this.regulator.equals(l)) {
            unregisterNetwork(this);
            return;
        }
        this.nodeQueue.add(l.clone());
    }

    public boolean connectsTo(Location l) {
        return this.connectedLocations.contains(l);
    }

    private Component getCurrentClassification(Location l) {
        if (this.regulatorNodes.contains(l)) {
            return Component.REGULATOR;
        }
        if (this.connectorNodes.contains(l)) {
            return Component.CONNECTOR;
        }
        if (this.terminusNodes.contains(l)) {
            return Component.TERMINUS;
        }
        return null;
    }

    private void discoverStep() {
        int steps = 0;
        while (this.nodeQueue.peek() != null) {
            Location l = this.nodeQueue.poll();
            Component currentAssignment = getCurrentClassification(l);
            Component classification = classifyLocation(l);
            if (classification != currentAssignment) {
                if (currentAssignment == Component.REGULATOR || currentAssignment == Component.CONNECTOR) {
                    unregisterNetwork(this);
                    return;
                }
                if (currentAssignment == Component.TERMINUS) {
                    this.terminusNodes.remove(l);
                }
                if (classification == Component.REGULATOR) {
                    this.regulatorNodes.add(l);
                    discoverNeighbors(l);
                } else if (classification == Component.CONNECTOR) {
                    this.connectorNodes.add(l);
                    discoverNeighbors(l);
                } else if (classification == Component.TERMINUS) {
                    this.terminusNodes.add(l);
                }
                locationClassificationChange(l, currentAssignment, classification);
            }
            steps++;


            if (steps == 500) {
                break;
            }
        }
    }

    private void discoverNeighbors(Location l, int xDiff, int yDiff, int zDiff) {
        for (int i = getRange() + 1; i > 0; i--) {
            Location newLocation = l.clone().add((i * xDiff), (i * yDiff), (i * zDiff));
            addLocationToNetwork(newLocation);
        }
    }

    private void discoverNeighbors(Location l) {
        discoverNeighbors(l, 1, 0, 0);
        discoverNeighbors(l, -1, 0, 0);
        discoverNeighbors(l, 0, 1, 0);
        discoverNeighbors(l, 0, -1, 0);
        discoverNeighbors(l, 0, 0, 1);
        discoverNeighbors(l, 0, 0, -1);
    }

    public void display() {
        SlimefunStartup.instance.getServer().getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
            for (Location l : Network.this.connectedLocations) {
                try {
                    ParticleEffect.REDSTONE.display(l.clone().add(0.5D, 0.5D, 0.5D), 0.0F, 0.0F, 0.0F, 0.0F, 1);
                } catch (Exception exception) {
                    System.err.println("发送粒子异常 位于 " + l.getWorld() + " X:" + l.getX() + " Y:" + l.getY() + " Z:" + l.getZ());
                }
            }
        });
    }

    public void tick() {
        discoverStep();
    }

    public enum Component {
        CONNECTOR,
        REGULATOR,
        TERMINUS
    }
}


