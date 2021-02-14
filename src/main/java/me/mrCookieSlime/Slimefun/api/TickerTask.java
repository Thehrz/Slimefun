package me.mrCookieSlime.Slimefun.api;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.general.Chat.TellRawMessage;
import me.mrCookieSlime.CSCoreLibPlugin.general.Clock;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

public class TickerTask implements Runnable {
    public static final Map<Location, Integer> bugged_blocks = new HashMap<>();
    public static Map<Location, Long> block_timings = new HashMap<>();
    public final Map<Location, Location> move = new HashMap<>();
    public final Map<Location, Boolean> delete = new HashMap<>();
    private final Set<BlockTicker> tickers = new HashSet<>();
    private final Map<String, Integer> map_chunk = new HashMap<>();
    private final Map<String, Integer> map_machine = new HashMap<>();
    private final Map<String, Long> map_machinetime = new HashMap<>();
    private final Map<String, Long> map_chunktime = new HashMap<>();
    private final Set<String> skipped_chunks = new HashSet<>();
    public boolean HALTED = false;
    private int skipped = 0;
    private int chunks = 0;
    private int machines = 0;
    private long time = 0L;

    public void run() {
        long timestamp = System.currentTimeMillis();

        this.skipped = 0;
        this.chunks = 0;
        this.machines = 0;
        this.map_chunk.clear();
        this.map_machine.clear();
        this.time = 0L;
        this.map_chunktime.clear();
        this.skipped_chunks.clear();
        this.map_machinetime.clear();
        block_timings.clear();

        final Map<Location, Integer> bugged = new HashMap<>(bugged_blocks);
        bugged_blocks.clear();

        Map<Location, Boolean> remove = new HashMap<>(this.delete);

        for (Map.Entry<Location, Boolean> entry : remove.entrySet()) {
            BlockStorage._integrated_removeBlockInfo(entry.getKey(), entry.getValue());
            this.delete.remove(entry.getKey());
        }

        if (!this.HALTED) {
            for (String c : BlockStorage.getTickingChunks()) {
                long timestamp2 = System.currentTimeMillis();
                this.chunks++;


                for (Location l : BlockStorage.getTickingLocations(c)) {
                    if (l.getWorld().isChunkLoaded(l.getBlockX() >> 4, l.getBlockZ() >> 4)) {
                        final Block b = l.getBlock();
                        final SlimefunItem item = BlockStorage.check(l);
                        if (item != null) {
                            this.machines++;
                            try {
                                item.getBlockTicker().update();
                                if (item.getBlockTicker().isSynchronized()) {
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
                                        try {
                                            long timestamp3 = System.currentTimeMillis();
                                            item.getBlockTicker().tick(b, item, BlockStorage.getLocationInfo(l));

                                            TickerTask.this.map_machinetime.put(item.getID(), Long.valueOf((TickerTask.this.map_machinetime.containsKey(item.getID()) ? TickerTask.this.map_machinetime.get(item.getID()).longValue() : 0L) + System.currentTimeMillis() - timestamp3));
                                            TickerTask.this.map_chunk.put(c, Integer.valueOf((TickerTask.this.map_chunk.containsKey(c) ? TickerTask.this.map_chunk.get(c).intValue() : 0) + 1));
                                            TickerTask.this.map_machine.put(item.getID(), Integer.valueOf((TickerTask.this.map_machine.containsKey(item.getID()) ? TickerTask.this.map_machine.get(item.getID()).intValue() : 0) + 1));
                                            TickerTask.block_timings.put(l, Long.valueOf(System.currentTimeMillis() - timestamp3));
                                        } catch (Exception x) {
                                            int errors = 0;
                                            if (bugged.containsKey(l))
                                                errors = bugged.get(l).intValue();
                                            errors++;

                                            if (errors == 1) {
                                                int try_count = 1;
                                                File file = new File("plugins/Slimefun/error-reports/" + Clock.getFormattedTime() + ".err");
                                                while (file.exists()) {
                                                    try_count++;
                                                    file = new File("plugins/Slimefun/error-reports/" + Clock.getFormattedTime() + "(" + try_count + ").err");
                                                }
                                                try {
                                                    PrintStream stream = new PrintStream(file);
                                                    stream.println();
                                                    stream.println("Server Software: " + Bukkit.getName());
                                                    stream.println("  Build: " + Bukkit.getVersion());
                                                    stream.println("  Minecraft: " + Bukkit.getBukkitVersion());
                                                    stream.println();
                                                    stream.println("Slimefun Environment:");
                                                    stream.println("  CS-CoreLib v" + CSCoreLib.getLib().getDescription().getVersion());
                                                    stream.println("  Slimefun v" + SlimefunStartup.instance.getDescription().getVersion());
                                                    stream.println();

                                                    List<String> plugins = new ArrayList<>();
                                                    List<String> addons = new ArrayList<>();
                                                    for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
                                                        if (Bukkit.getPluginManager().isPluginEnabled(p)) {
                                                            plugins.add("  + " + p.getName() + " " + p.getDescription().getVersion());
                                                            if (p.getDescription().getDepend().contains("Slimefun") || p.getDescription().getSoftDepend().contains("Slimefun")) {
                                                                addons.add("  + " + p.getName() + " " + p.getDescription().getVersion());
                                                            }
                                                        } else {
                                                            plugins.add("  - " + p.getName() + " " + p.getDescription().getVersion());
                                                            if (p.getDescription().getDepend().contains("Slimefun") || p.getDescription().getSoftDepend().contains("Slimefun")) {
                                                                addons.add("  - " + p.getName() + " " + p.getDescription().getVersion());
                                                            }
                                                        }
                                                    }
                                                    stream.println(" Installed Addons (" + addons.size() + ")");
                                                    for (String addon : addons) {
                                                        stream.println(addon);
                                                    }
                                                    stream.println();
                                                    stream.println("Installed Plugins (" + plugins.size() + "):");
                                                    for (String plugin : plugins) {
                                                        stream.println(plugin);
                                                    }
                                                    stream.println();
                                                    stream.println("Ticked Block:");
                                                    stream.println("  World: " + l.getWorld().getName());
                                                    stream.println("  X: " + l.getBlockX());
                                                    stream.println("  Y: " + l.getBlockY());
                                                    stream.println("  Z: " + l.getBlockZ());
                                                    stream.println();
                                                    stream.println("Slimefun Data:");
                                                    stream.println("  ID: " + item.getID());
                                                    stream.println("  Inventory: " + BlockStorage.getStorage(l.getWorld()).hasInventory(l));
                                                    stream.println("  Data: " + BlockStorage.getBlockInfoAsJson(l));
                                                    stream.println();
                                                    stream.println("Stacktrace:");
                                                    stream.println();
                                                    x.printStackTrace(stream);

                                                    stream.close();
                                                } catch (FileNotFoundException e) {
                                                    e.printStackTrace();
                                                }

                                                System.err.println("[Slimefun] Exception caught while ticking a Block:" + x.getClass().getName());
                                                System.err.println("[Slimefun] X: " + l.getBlockX() + " Y: " + l.getBlockY() + " Z: " + l.getBlockZ());
                                                System.err.println("[Slimefun] Saved as: ");
                                                System.err.println("[Slimefun] /plugins/Slimefun/error-reports/" + file.getName());
                                                System.err.println("[Slimefun] Please consider sending this File to the developer(s) of Slimefun, sending this Error won't get you any help though.");
                                                System.err.println("[Slimefun] ");

                                                TickerTask.bugged_blocks.put(l, errors);
                                            } else if (errors == 4) {
                                                System.err.println("[Slimefun] X: " + l.getBlockX() + " Y: " + l.getBlockY() + " Z: " + l.getBlockZ() + "(" + item.getID() + ")");
                                                System.err.println("[Slimefun] has thrown 4 Exceptions in the last 4 Ticks, the Block has been terminated.");
                                                System.err.println("[Slimefun] Check your /plugins/Slimefun/error-reports/ folder for details.");


                                            } else {

                                                TickerTask.bugged_blocks.put(l, Integer.valueOf(errors));
                                            }
                                        }
                                    });
                                } else {

                                    long timestamp3 = System.currentTimeMillis();
                                    item.getBlockTicker().tick(b, item, BlockStorage.getLocationInfo(l));

                                    this.map_machinetime.put(item.getID(), (this.map_machinetime.containsKey(item.getID()) ? this.map_machinetime.get(item.getID()) : 0L) + System.currentTimeMillis() - timestamp3);
                                    this.map_chunk.put(c, (this.map_chunk.containsKey(c) ? this.map_chunk.get(c) : 0) + 1);
                                    this.map_machine.put(item.getID(), (this.map_machine.containsKey(item.getID()) ? this.map_machine.get(item.getID()) : 0) + 1);
                                    block_timings.put(l, System.currentTimeMillis() - timestamp3);
                                }
                                this.tickers.add(item.getBlockTicker());
                            } catch (Exception x) {

                                int errors = 0;
                                if (bugged.containsKey(l)) errors = bugged.get(l);
                                errors++;

                                if (errors == 1) {
                                    File file = new File("plugins/Slimefun/error-reports/" + Clock.getFormattedTime() + ".err");
                                    if (file.exists()) {
                                        file = new File("plugins/Slimefun/error-reports/" + Clock.getFormattedTime() + "(2).err");
                                        if (file.exists()) {
                                            file = new File("plugins/Slimefun/error-reports/" + Clock.getFormattedTime() + "(3).err");
                                            if (file.exists()) {
                                                file = new File("plugins/Slimefun/error-reports/" + Clock.getFormattedTime() + "(4).err");
                                                if (file.exists()) {
                                                    file = new File("plugins/Slimefun/error-reports/" + Clock.getFormattedTime() + "(5).err");
                                                    if (file.exists()) {
                                                        file = new File("plugins/Slimefun/error-reports/" + Clock.getFormattedTime() + "(6).err");
                                                        if (file.exists()) {
                                                            file = new File("plugins/Slimefun/error-reports/" + Clock.getFormattedTime() + "(7).err");
                                                            if (file.exists()) {
                                                                file = new File("plugins/Slimefun/error-reports/" + Clock.getFormattedTime() + "(8).err");
                                                                if (file.exists()) {
                                                                    file = new File("plugins/Slimefun/error-reports/" + Clock.getFormattedTime() + "(9).err");
                                                                    if (file.exists()) {
                                                                        file = new File("plugins/Slimefun/error-reports/" + Clock.getFormattedTime() + "(10).err");
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    try {
                                        PrintStream stream = new PrintStream(file);
                                        stream.println();
                                        stream.println("Server Software: " + Bukkit.getName());
                                        stream.println("  Build: " + Bukkit.getVersion());
                                        stream.println("  Minecraft: " + Bukkit.getBukkitVersion());
                                        stream.println();
                                        stream.println("Installed Plugins (" + (Bukkit.getPluginManager().getPlugins()).length + ")");
                                        for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
                                            if (Bukkit.getPluginManager().isPluginEnabled(p)) {
                                                stream.println("  + " + p.getName() + " " + p.getDescription().getVersion());
                                            } else {

                                                stream.println("  - " + p.getName() + " " + p.getDescription().getVersion());
                                            }
                                        }
                                        stream.println();
                                        stream.println("Ticked Block:");
                                        stream.println("  World: " + l.getWorld().getName());
                                        stream.println("  X: " + l.getBlockX());
                                        stream.println("  Y: " + l.getBlockY());
                                        stream.println("  Z: " + l.getBlockZ());
                                        stream.println();
                                        stream.println("Slimefun Data:");
                                        stream.println("  ID: " + item.getID());
                                        stream.println("  Inventory: " + BlockStorage.getStorage(l.getWorld()).hasInventory(l));
                                        stream.println("  Data: " + BlockStorage.getBlockInfoAsJson(l));
                                        stream.println();
                                        stream.println("Stacktrace:");
                                        stream.println();
                                        x.printStackTrace(stream);

                                        stream.close();
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }

                                    System.err.println("[Slimefun] Exception caught while ticking a Block:" + x.getClass().getName());
                                    System.err.println("[Slimefun] X: " + l.getBlockX() + " Y: " + l.getBlockY() + " Z: " + l.getBlockZ());
                                    System.err.println("[Slimefun] Saved as: ");
                                    System.err.println("[Slimefun] /plugins/Slimefun/error-reports/" + file.getName());
                                    System.err.println("[Slimefun] Please consider sending this File to the developer(s) of Slimefun, sending this Error won't get you any help though.");


                                    bugged_blocks.put(l, errors);
                                    continue;
                                }
                                if (errors == 4) {
                                    System.err.println("[Slimefun] X: " + l.getBlockX() + " Y: " + l.getBlockY() + " Z: " + l.getBlockZ() + "(" + item.getID() + ")");
                                    System.err.println("[Slimefun] has thrown 4 Exceptions in the last 4 Ticks, the Block has been terminated.");
                                    System.err.println("[Slimefun] Check your /plugins/Slimefun/error-reports/ folder for details.");


                                    continue;
                                }
                                bugged_blocks.put(l, errors);
                            }
                            continue;
                        }
                        this.skipped++;
                        continue;
                    }
                    this.skipped += BlockStorage.getTickingLocations(c).size();
                    this.skipped_chunks.add(c);
                    this.chunks--;
                }


                this.map_chunktime.put(c, System.currentTimeMillis() - timestamp2);
            }
        }

        for (Map.Entry<Location, Location> entry : this.move.entrySet()) {
            BlockStorage._integrated_moveLocationInfo(entry.getKey(), entry.getValue());
        }
        this.move.clear();

        for (BlockTicker ticker : this.tickers) {
            ticker.unique = true;
        }
        this.tickers.clear();

        this.time = System.currentTimeMillis() - timestamp;
    }

    public void info(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2== &aSlimefun Diagnostic Tool &2=="));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Halted: &e&l" + String.valueOf(this.HALTED).toUpperCase()));
        sender.sendMessage("");
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Impact: &e" + this.time + "ms / 50-750ms"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Ticked Chunks: &e" + this.chunks));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Ticked Machines: &e" + this.machines));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Skipped Machines: &e" + this.skipped));
        sender.sendMessage("");
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Ticking Machines:"));
        if (sender instanceof Player) {
            TellRawMessage tellraw = new TellRawMessage();
            tellraw.addText("   &7&oHover for more Info");
            StringBuilder hover = new StringBuilder();
            int hidden = 0;
            for (String item : this.map_machine.keySet()) {
                if (this.map_machinetime.get(item) > 0L) {
                    hover.append("\n&c" + item + " - " + this.map_machine.get(item) + "x &7(" + this.map_machinetime.get(item) + "ms)");
                    continue;
                }
                hidden++;
            }
            hover.append("\n\n&c+ &4" + hidden + " Hidden");
            tellraw.addHoverEvent(TellRawMessage.HoverAction.SHOW_TEXT, hover.toString());
            try {
                tellraw.send((Player) sender);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            int hidden = 0;
            for (String item : this.map_machine.keySet()) {
                if (this.map_machinetime.get(item) > 0L) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "  &e" + item + " - " + this.map_machine.get(item) + "x &7(" + this.map_machinetime.get(item) + "ms)"));
                    continue;
                }
                hidden++;
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c+ &4" + hidden + " Hidden"));
        }
        sender.sendMessage("");
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Ticking Chunks:"));
        if (sender instanceof Player) {
            TellRawMessage tellraw = new TellRawMessage();
            tellraw.addText("   &7&oHover for more Info");
            StringBuilder hover = new StringBuilder();
            int hidden = 0;
            for (String c : this.map_chunktime.keySet()) {
                if (!this.skipped_chunks.contains(c)) {
                    if (this.map_chunktime.get(c) > 0L) {
                        hover.append("\n&c" + c.replace("CraftChunk", "") + " - " + (this.map_chunk.containsKey(c) ? this.map_chunk.get(c) : 0) + "x &7(" + this.map_chunktime.get(c) + "ms)");
                        continue;
                    }
                    hidden++;
                }
            }
            hover.append("\n\n&c+ &4" + hidden + " Hidden");
            tellraw.addHoverEvent(TellRawMessage.HoverAction.SHOW_TEXT, hover.toString());
            try {
                tellraw.send((Player) sender);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            int hidden = 0;
            for (String c : this.map_chunktime.keySet()) {
                if (!this.skipped_chunks.contains(c)) {
                    if (this.map_chunktime.get(c) > 0L) {
                        sender.sendMessage("  &c" + c.replace("CraftChunk", "") + " - " + (this.map_chunk.containsKey(c) ? this.map_chunk.get(c) : 0) + "x &7(" + this.map_chunktime.get(c) + "ms)");
                        continue;
                    }
                    hidden++;
                }
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c+ &4" + hidden + " Hidden"));
        }
    }

    public long getTimings(Block b) {
        return block_timings.containsKey(b.getLocation()) ? block_timings.get(b.getLocation()) : 0L;
    }

    public long getTimings(String item) {
        return this.map_machinetime.containsKey(item) ? this.map_machinetime.get(item) : 0L;
    }

    public long getTimings(Chunk c) {
        return this.map_chunktime.containsKey(c.toString()) ? this.map_chunktime.get(c.toString()) : 0L;
    }
}


