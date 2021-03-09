package me.mrCookieSlime.Slimefun.api.energy;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Math.DoubleHandler;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChargableBlock {
    public static final Set<String> rechargeable = new HashSet<>();
    public static Map<String, Integer> max_charges = new HashMap<>();
    public static Set<String> capacitors = new HashSet<>();

    public static void registerChargableBlock(String id, int capacity, boolean recharge) {
        max_charges.put(id, capacity);
        if (recharge) {
            rechargeable.add(id);
        }
    }

    public static void registerCapacitor(String id, int capacity) {
        max_charges.put(id, capacity);
        rechargeable.add(id);
        capacitors.add(id);
    }

    public static boolean isChargable(Block b) {
        return isChargable(b.getLocation());
    }

    public static boolean isChargable(Location l) {
        if (!BlockStorage.hasBlockInfo(l)) {
            return false;
        }
        return max_charges.containsKey(BlockStorage.checkID(l));
    }

    public static boolean isRechargable(Block b) {
        if (!BlockStorage.hasBlockInfo(b)) {
            return false;
        }
        String id = BlockStorage.checkID(b);
        return (max_charges.containsKey(id) && rechargeable.contains(id));
    }

    public static boolean isCapacitor(Block b) {
        return isCapacitor(b.getLocation());
    }

    public static boolean isCapacitor(Location l) {
        if (!BlockStorage.hasBlockInfo(l)) {
            return false;
        }
        return capacitors.contains(BlockStorage.checkID(l));
    }

    public static int getDefaultCapacity(Block b) {
        return getDefaultCapacity(b.getLocation());
    }

    public static int getDefaultCapacity(Location l) {
        String id = BlockStorage.checkID(l);
        return (id == null) ? 0 : max_charges.get(id);
    }

    public static int getCharge(Block b) {
        return getCharge(b.getLocation());
    }

    public static int getCharge(Location l) {
        String charge = BlockStorage.getLocationInfo(l, "energy-charge");
        if (charge != null) {
            return Integer.parseInt(charge);
        }
        BlockStorage.addBlockInfo(l, "energy-charge", "0", false);
        return 0;
    }


    public static void setCharge(Block b, int charge) {
        setCharge(b.getLocation(), charge);
    }

    public static void setCharge(Location l, int charge) {
        if (charge < 0) {
            charge = 0;
        } else {
            int capacity = getMaxCharge(l);
            if (charge > capacity) {
                charge = capacity;
            }
        }

        if (charge != getCharge(l)) {
            BlockStorage.addBlockInfo(l, "energy-charge", String.valueOf(charge), false);
        }
    }

    public static void setUnsafeCharge(Location l, int charge, boolean updateTexture) {
        if (charge != getCharge(l)) {
            BlockStorage.addBlockInfo(l, "energy-charge", String.valueOf(charge), false);
            if (updateTexture) {
                try {
                    updateTexture(l);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void updateTexture(final Location l) throws Exception {
        Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
            try {
                Block b = l.getBlock();
                int charge = ChargableBlock.getCharge(b);
                int capacity = ChargableBlock.getMaxCharge(b);
                if (b.getState() instanceof org.bukkit.block.Skull) {
                    if (charge < (int) (capacity * 0.25D)) {
                        switch (capacity) {
                            case 128:
                                CustomSkull.setSkull(b, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTEwOTI0MTg2NGUxNzljYjFjMmNkNWEzYjE0Y2RiYTliYWJlYjAyNWVjMjMyMGY1NTZjYmUxZGIwMjM5OGMyZCJ9fX0=");
                                break;
                            case 512:
                                CustomSkull.setSkull(b, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3MjBhMjhkNTVjYzNmMjc1ZDVjMTM2ODlhMjQ1NzkxN2E3ODBmNWMyNmFlYzFmYjBlZDg1N2NkNzRlZTUxYyJ9fX0=");
                                break;
                            case 1024:
                                CustomSkull.setSkull(b, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzViZGM2ZjU3MjZmYjg1NzNmNDA1Yjg0ZDdjNzkyYjgxMzRkNDlmNGY4YWM3YjUxNDQ4Y2QxYTRjYjU2MGExZCJ9fX0=");
                                break;
                            case 8192:
                                CustomSkull.setSkull(b, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjU5ZGFlY2EzNDI3NmNlYWViYmY1YmIzM2NiZmNjYWNjOGFmOTI5YzMwZDI4NzM2ZTRiYTQ0ODk2YWU4NzVmZSJ9fX0=");
                                break;
                            case 65536:
                                CustomSkull.setSkull(b, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODU4NjA3OTgzYjcyNDYzM2U3ZGU1ZTY2N2Q2N2IyNDJlMWU4NDI3NGUxMmIzZDk1MzQ3YjAyMzNjYWViMGMwNSJ9fX0=");
                                break;
                        }
                    } else if (charge < (int) (capacity * 0.5D)) {
                        switch (capacity) {
                            case 128:
                                CustomSkull.setSkull(b, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjdiOWRiMzZkY2I4NTg3ZjlkNDU1NzQ0ZWU4NWY0YjMyZmIxZTYwNTk5OTZhOTBkMWJkY2QxZDBjYmFkMWJjOCJ9fX0=");
                                break;
                            case 512:
                                CustomSkull.setSkull(b, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjYzMDMyZDlkNmY4MjFlMGVjNGM3ODFjMmYyZTdiMGU3ODBiMWU0Zjg4MWM5YTIzNGFhNzkzOTVkMTMyMDRiMyJ9fX0=");
                                break;
                            case 1024:
                                CustomSkull.setSkull(b, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzc1MjE3NGQxYjRmN2MyYzE0Y2U1NjM4ZTc1NTlkMDlhNTg5MzY4NTJkYmU0ZjVhZjA5MzMwYjYxZTQ0NDAzOSJ9fX0=");
                                break;
                            case 8192:
                                CustomSkull.setSkull(b, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGY1M2I0YmExOTgxNjQwYTgxNDJjNGZjZmEwNWQ3ODJjMzdiYTlhM2ZjYzhhYmVhZmQ2MTQ2YjIzMWYwNTFkYyJ9fX0=");
                                break;
                            case 65536:
                                CustomSkull.setSkull(b, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGRkYzViY2NhN2UyMDM3YjY2NGZlMDI5ZjM1ZGJmYjNmNzExYmUyYjI5OTZhMzRjZDY5YWVhOTU4MzkzYzY4In19fQ==");
                                break;
                        }
                    } else if (charge < (int) (capacity * 0.75D)) {
                        switch (capacity) {
                            case 128:
                                CustomSkull.setSkull(b, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjZjNzNmNDY4NjMyZDZjYzNmYjkzMGUwMTY2MjUwZjIwYjA2OTIzZWM4ZjI3NTMyM2FmZDUzNmM3N2E3ZDgwNCJ9fX0=");
                                break;
                            case 512:
                                CustomSkull.setSkull(b, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQ3MzQ4OWE3MGI4YjFmMjEyNjRiMjc5ZWVjOWIxNjA4MjY2ZDUxMjc0YWU4NzkzYTIyYTMxOTY3ZDE4ZTUxOSJ9fX0=");
                                break;
                            case 1024:
                                CustomSkull.setSkull(b, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODgzM2IwODM3MDIwZWNmOWUzM2E3NzQ3NDBhMTRmYTI1MDRlMjE0NWU1ZGVhMDc5MTYwYThiOWUyNmJkMzJjMyJ9fX0=");
                                break;
                            case 8192:
                                CustomSkull.setSkull(b, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTJmNjkyYWFlNGM3MGVkMTAzNzcyMWEzNTNlZWY3OTk5ZmJjNDAyYTg3NDRiMzUyMmE3MGY4MjkzMGFjODM0YSJ9fX0=");
                                break;
                            case 65536:
                                CustomSkull.setSkull(b, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGFkZTk4NTdiOTEzMGZjNmYzMmFlMmRkM2E3NGIwMGMxNmNlZjMwMzU5YzIwZDBjNmE3MGZjMTc5MjBiODUwNCJ9fX0=");
                                break;
                        }
                    } else {
                        switch (capacity) {
                            case 128:
                                CustomSkull.setSkull(b, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTIxYWY1NmU2MmI2ZmEzNGFiMGE3ZjRkNDFjYTRhOTE1NGQyZjM1MDI5OTQ1YTI2ZmQ4NTRmOGYwNDM4ZjY5ZSJ9fX0=");
                                break;
                            case 512:
                                CustomSkull.setSkull(b, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjlkOWQ1MGQyODY1MTYwOWJmZmU2ODRkY2NjMGRiZGQ5ZTUzY2E0MzM1MDc1MjMyZDAwZmY4MjA4NDg3MGViZSJ9fX0=");
                                break;
                            case 1024:
                                CustomSkull.setSkull(b, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ2NThmYWI1OWQ4MjE0M2UyODlkNjdhMjkzMGVlMWU5MjQyYjMwZTk3NzhiNDMxMGM1MmQwNmUxMjE5ZmQ4In19fQ==");
                                break;
                            case 8192:
                                CustomSkull.setSkull(b, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTMyYjY1NzBiNTNkNTIyMjcyZTU2NGI3ZWVhMzdlZDIwMmZiY2Q5MDI5OGI3NzY4MjNjNTYwNTA5ZWYxYTI4NSJ9fX0=");
                                break;
                            case 65536:
                                CustomSkull.setSkull(b, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTI1Yjc4OTI4M2NiYWFkZmYwMWQ5YTY1YzI3MDg0ZTg1MTlhMzQ0NGM5Y2UyMGY0ZjQ2ZTBlNjdkZDZiYTQzNSJ9fX0=");
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static String formatEnergy(Block b) {
        return DoubleHandler.getFancyDouble(getCharge(b)) + " J";
    }

    public static int addCharge(Block b, int charge) {
        return addCharge(b.getLocation(), charge);
    }

    public static int addCharge(Location l, int charge) {
        int energy = getCharge(l);
        int space = getMaxCharge(l) - energy;
        int rest = charge;
        if (space > 0 && charge > 0) {
            if (space > charge) {
                setCharge(l, energy + charge);
                rest = 0;
            } else {

                rest = charge - space;
                setCharge(l, getMaxCharge(l));
            }
            if (capacitors.contains(BlockStorage.checkID(l))) {
                try {
                    updateTexture(l);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else if (charge < 0 && energy >= -charge) {
            setCharge(l, energy + charge);
            if (capacitors.contains(BlockStorage.checkID(l))) {
                try {
                    updateTexture(l);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return rest;
    }

    public static int getMaxCharge(Block b) {
        return getMaxCharge(b.getLocation());
    }

    public static int getMaxCharge(Location l) {
        Config cfg = BlockStorage.getLocationInfo(l);
        if (!cfg.contains("id")) {
            BlockStorage.clearBlockInfo(l);
            return 0;
        }
        if (cfg.contains("energy-capacity")) {
            return Integer.parseInt(cfg.getString("energy-capacity"));
        }

        BlockStorage.addBlockInfo(l, "energy-capacity", String.valueOf(getDefaultCapacity(l)), false);
        return getDefaultCapacity(l);
    }
}


