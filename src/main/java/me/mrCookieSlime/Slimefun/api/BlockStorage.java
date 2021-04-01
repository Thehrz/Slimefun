package me.mrCookieSlime.Slimefun.api;

import com.google.common.base.Objects;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Math.DoubleHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.UniversalBlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.util.*;

public class BlockStorage {
    private static final String path_blocks = "data-storage/Slimefun/stored-blocks/";
    private static final String path_chunks = "data-storage/Slimefun/stored-chunks/";
    public static Map<String, BlockStorage> worlds;
    public static Map<String, Set<Location>> ticking_chunks;
    public static Set<String> loaded_tickers;
    public static Map<String, UniversalBlockMenu> universal_inventories;
    public static int info_delay;
    private static Map<String, String> map_chunks;
    private static int chunk_changes;

    static {
        BlockStorage.worlds = new HashMap<>();
        BlockStorage.ticking_chunks = new HashMap<>();
        BlockStorage.loaded_tickers = new HashSet<>();
        BlockStorage.map_chunks = new HashMap<>();
        BlockStorage.universal_inventories = new HashMap<>();
        BlockStorage.chunk_changes = 0;
    }

    private final Map<Location, Config> storage;
    private final Map<Location, BlockMenu> inventories;
    private final Map<String, Config> cache_blocks;
    private World world;
    private int changes;

    public BlockStorage(final World w) {
        this.storage = new HashMap<>();
        this.inventories = new HashMap<>();
        this.cache_blocks = new HashMap<>();
        this.changes = 0;
        if (BlockStorage.worlds.containsKey(w.getName())) {
            return;
        }
        this.world = w;
        System.out.println("[Slimefun] 正在加载世界中的方块 \"" + w.getName() + "\"");
        System.out.println("[Slimefun] 可能需要花费一些时间...");
        final File f = new File("data-storage/Slimefun/stored-blocks/" + w.getName());
        if (f.exists()) {
            final long total = f.listFiles().length;
            final long start = System.currentTimeMillis();
            long done = 0L;
            long timestamp = System.currentTimeMillis();
            long totalBlocks = 0L;
            try {
                for (final File file : f.listFiles()) {
                    if (file.getName().endsWith(".sfb")) {
                        if (timestamp + BlockStorage.info_delay < System.currentTimeMillis()) {
                            System.out.println("[Slimefun] 加载方块中... " + Math.round(done * 100.0f / total * 100.0f / 100.0f) + "% 完成 (\"" + w.getName() + "\")");
                            timestamp = System.currentTimeMillis();
                        }
                        final FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                        for (final String key : cfg.getKeys(false)) {
                            final Location l = deserializeLocation(key);
                            final String chunk_string = locationToChunkString(l);
                            try {
                                ++totalBlocks;
                                final String json = cfg.getString(key);
                                final Config blockInfo = parseBlockInfo(l, json);
                                if (blockInfo == null) {
                                    continue;
                                }
                                this.storage.put(l, blockInfo);
                                if (!SlimefunItem.isTicking(file.getName().replace(".sfb", ""))) {
                                    continue;
                                }
                                final Set<Location> locations = BlockStorage.ticking_chunks.containsKey(chunk_string) ? BlockStorage.ticking_chunks.get(chunk_string) : new HashSet<>();
                                locations.add(l);
                                BlockStorage.ticking_chunks.put(chunk_string, locations);
                                if (BlockStorage.loaded_tickers.contains(chunk_string)) {
                                    continue;
                                }
                                BlockStorage.loaded_tickers.add(chunk_string);
                            } catch (Exception x) {
                                System.err.println("[Slimefun] 加载失败 " + file.getName() + "(ERR: " + key + ")");
                                x.printStackTrace();
                            }
                        }
                        ++done;
                    }
                }
            } finally {
                final long time = System.currentTimeMillis() - start;
                System.out.println("[Slimefun] 加载方块中... 100% (已完成 - " + time + "ms)");
                System.out.println("[Slimefun] 共计加载 " + totalBlocks + " 个方块，位于世界 \"" + this.world.getName() + "\"");
                if (totalBlocks > 0L) {
                    System.out.println("[Slimefun] Avg: " + DoubleHandler.fixDouble(time / (double) totalBlocks, 3) + "ms/方块");
                }
            }
        } else {
            f.mkdirs();
        }
        final File chunks = new File("data-storage/Slimefun/stored-chunks/chunks.sfc");
        if (chunks.exists()) {
            final FileConfiguration cfg2 = YamlConfiguration.loadConfiguration(chunks);
            for (final String key2 : cfg2.getKeys(false)) {
                try {
                    if (!this.world.getName().equals(key2.split(";")[0])) {
                        continue;
                    }
                    BlockStorage.map_chunks.put(key2, cfg2.getString(key2));
                } catch (Exception x2) {
                    System.err.println("[Slimefun] 加载失败 " + chunks.getName() + " 位于世界 \"" + this.world.getName() + "\" (ERR: " + key2 + ")");
                    x2.printStackTrace();
                }
            }
        }
        BlockStorage.worlds.put(this.world.getName(), this);
        for (final File file2 : new File("data-storage/Slimefun/stored-inventories").listFiles()) {
            if (file2.getName().startsWith(w.getName()) && file2.getName().endsWith(".sfi")) {
                final Location i = deserializeLocation(file2.getName().replace(".sfi", ""));
                final Config cfg3 = new Config(file2);
                try {
                    if (cfg3.getString("preset") != null) {
                        final BlockMenuPreset preset = BlockMenuPreset.getPreset(cfg3.getString("preset"));
                        this.inventories.put(i, new BlockMenu(preset, i, cfg3));
                    } else {
                        final BlockMenuPreset preset = BlockMenuPreset.getPreset(checkID(i));
                        this.inventories.put(i, new BlockMenu(preset, i, cfg3));
                    }
                } catch (Exception ex) {
                }
            }
        }
        for (final File file2 : new File("data-storage/Slimefun/universal-inventories").listFiles()) {
            if (file2.getName().endsWith(".sfi")) {
                final Config cfg4 = new Config(file2);
                final BlockMenuPreset preset2 = BlockMenuPreset.getPreset(cfg4.getString("preset"));
                BlockStorage.universal_inventories.put(preset2.getID(), new UniversalBlockMenu(preset2, cfg4));
            }
        }
    }

    public static BlockStorage getStorage(final World world) {
        return BlockStorage.worlds.get(world.getName());
    }

    public static BlockStorage getForcedStorage(final World world) {
        return isWorldRegistered(world.getName()) ? BlockStorage.worlds.get(world.getName()) : new BlockStorage(world);
    }

    private static String serializeLocation(final Location l) {
        return l.getWorld().getName() + ";" + l.getBlockX() + ";" + l.getBlockY() + ";" + l.getBlockZ();
    }

    private static String serializeChunk(final Chunk chunk) {
        return chunk.getWorld().getName() + ";Chunk;" + chunk.getX() + ";" + chunk.getZ();
    }

    private static String locationToChunkString(final Location l) {
        return l.getWorld().getName() + ";Chunk;" + (l.getBlockX() >> 4) + ";" + (l.getBlockZ() >> 4);
    }

    private static Location deserializeLocation(final String l) {
        try {
            final World w = Bukkit.getWorld(l.split(";")[0]);
            if (w != null) {
                return new Location(w, Integer.parseInt(l.split(";")[1]), Integer.parseInt(l.split(";")[2]), Integer.parseInt(l.split(";")[3]));
            }
        } catch (NumberFormatException ex) {
        }
        return null;
    }

    public static void store(final Block block, final ItemStack item) {
        final SlimefunItem sfitem = SlimefunItem.getByItem(item);
        if (sfitem != null) {
            addBlockInfo(block, "id", sfitem.getID(), true);
        }
    }

    public static void store(final Block block, final String item) {
        addBlockInfo(block, "id", item, true);
    }

    public static ItemStack retrieve(final Block block) {
        if (!hasBlockInfo(block)) {
            return null;
        }
        final SlimefunItem item = SlimefunItem.getByID(getLocationInfo(block.getLocation(), "id"));
        clearBlockInfo(block);
        if (item == null) {
            return null;
        }
        return item.getItem();
    }

    @Deprecated
    public static Config getBlockInfo(final Block block) {
        return getLocationInfo(block.getLocation());
    }

    @Deprecated
    public static Config getBlockInfo(final Location l) {
        return getLocationInfo(l);
    }

    public static Config getLocationInfo(final Location l) {
        final BlockStorage storage = getStorage(l.getWorld());
        final Config cfg = storage.storage.get(l);
        return (cfg == null) ? new BlockInfoConfig() : cfg;
    }

    private static Map<String, String> parseJSON(final String json) {
        final Map<String, String> map = new HashMap<>();
        if (json != null && json.length() > 2) {
            try {
                final JSONParser parser = new JSONParser();
                final JSONObject obj = (JSONObject) parser.parse(json);
                for (final Object entry : obj.keySet()) {
                    final String key = entry.toString();
                    final String value = obj.get(entry).toString();
                    map.put(key, value);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    private static BlockInfoConfig parseBlockInfo(final Location l, final String json) {
        try {
            return new BlockInfoConfig(parseJSON(json));
        } catch (Exception x) {
            System.err.println(x.getClass().getName());
            System.err.println("[Slimefun] Failed to parse BlockInfo for Block @ " + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ());
            System.err.println(json);
            System.err.println("[Slimefun] ");
            System.err.println("[Slimefun] IGNORE THIS ERROR UNLESS IT IS SPAMMING");
            System.err.println("[Slimefun] ");
            x.printStackTrace();
            return null;
        }
    }

    private static String serializeBlockInfo(final Config cfg) {
        final JSONObject json = new JSONObject();
        for (final String key : cfg.getKeys()) {
            json.put(key, cfg.getString(key));
        }
        return json.toJSONString();
    }

    private static String getJSONData(final Chunk chunk) {
        return BlockStorage.map_chunks.get(serializeChunk(chunk));
    }

    @Deprecated
    public static String getBlockInfo(final Block block, final String key) {
        return getLocationInfo(block.getLocation(), key);
    }

    @Deprecated
    public static String getBlockInfo(final Location l, final String key) {
        return getLocationInfo(l, key);
    }

    public static String getLocationInfo(final Location l, final String key) {
        return getBlockInfo(l).getString(key);
    }

    public static void addBlockInfo(final Location l, final String key, final String value) {
        addBlockInfo(l, key, value, false);
    }

    public static void addBlockInfo(final Block block, final String key, final String value) {
        addBlockInfo(block.getLocation(), key, value);
    }

    public static void addBlockInfo(final Block block, final String key, final String value, final boolean updateTicker) {
        addBlockInfo(block.getLocation(), key, value, updateTicker);
    }

    public static void addBlockInfo(final Location l, final String key, final String value, final boolean updateTicker) {
        final Config cfg = hasBlockInfo(l) ? getLocationInfo(l) : new BlockInfoConfig();
        cfg.setValue(key, value);
        setBlockInfo(l, cfg, updateTicker);
    }

    public static boolean hasBlockInfo(final Block block) {
        return hasBlockInfo(block.getLocation());
    }

    public static boolean hasBlockInfo(final Location l) {
        final BlockStorage storage = getStorage(l.getWorld());
        return storage != null && storage.storage.containsKey(l) && getLocationInfo(l, "id") != null;
    }

    public static void setBlockInfo(final Block block, final Config cfg, final boolean updateTicker) {
        setBlockInfo(block.getLocation(), cfg, updateTicker);
    }

    public static void setBlockInfo(final Location l, final Config cfg, final boolean updateTicker) {
        BlockStorage storage = getStorage(l.getWorld());
        if (storage == null) {
            storage = getForcedStorage(l.getWorld());
        }
        storage.storage.put(l, cfg);
        if (BlockMenuPreset.isInventory(cfg.getString("id"))) {
            if (BlockMenuPreset.isUniversalInventory(cfg.getString("id"))) {
                if (!BlockStorage.universal_inventories.containsKey(cfg.getString("id"))) {
                    storage.loadUniversalInventory(BlockMenuPreset.getPreset(cfg.getString("id")));
                }
            } else if (!storage.hasInventory(l)) {
                final File file = new File("data-storage/Slimefun/stored-inventories/" + serializeLocation(l) + ".sfi");
                if (file.exists()) {
                    storage.inventories.put(l, new BlockMenu(BlockMenuPreset.getPreset(cfg.getString("id")), l, new Config(file)));
                } else {
                    storage.loadInventory(l, BlockMenuPreset.getPreset(cfg.getString("id")));
                }
            }
        }
        refreshCache(getStorage(l.getWorld()), l, cfg.getString("id"), serializeBlockInfo(cfg), updateTicker);
    }

    public static void setBlockInfo(final Block b, final String json, final boolean updateTicker) {
        setBlockInfo(b.getLocation(), json, updateTicker);
    }

    public static void setBlockInfo(final Location l, final String json, final boolean updateTicker) {
        final Config blockInfo = (json == null) ? new BlockInfoConfig() : parseBlockInfo(l, json);
        if (blockInfo == null) {
            return;
        }
        setBlockInfo(l, blockInfo, updateTicker);
    }

    public static void clearBlockInfo(final Block block) {
        clearBlockInfo(block.getLocation());
    }

    public static void clearBlockInfo(final Location l) {
        clearBlockInfo(l, true);
    }

    public static void clearBlockInfo(final Block b, final boolean destroy) {
        clearBlockInfo(b.getLocation(), destroy);
    }

    public static void clearBlockInfo(final Location l, final boolean destroy) {
        SlimefunStartup.ticker.delete.put(l, destroy);
    }

    public static void _integrated_removeBlockInfo(final Location l, final boolean destroy) {
        final BlockStorage storage = getStorage(l.getWorld());
        if (hasBlockInfo(l)) {
            refreshCache(storage, l, getLocationInfo(l).getString("id"), null, destroy);
            storage.storage.remove(l);
        }
        if (destroy) {
            if (storage.hasInventory(l)) {
                storage.clearInventory(l);
            }
            if (storage.hasUniversalInventory(l)) {
                storage.getUniversalInventory(l).close();
                storage.getUniversalInventory(l).save();
            }
            final String chunk_string = locationToChunkString(l);
            if (ticking_chunks.containsKey(chunk_string)) {
                Set<Location> locations = BlockStorage.ticking_chunks.get(chunk_string);
                locations.remove(l);
                if (locations.isEmpty()) {
                    ticking_chunks.remove(chunk_string);
                    loaded_tickers.remove(chunk_string);
                } else {
                    ticking_chunks.put(chunk_string, locations);
                }
            }
        }
    }

    @Deprecated
    public static void moveBlockInfo(Block block, Block newBlock) {
        moveBlockInfo(block.getLocation(), newBlock.getLocation());
    }

    public static void moveBlockInfo(Location from, Location to) {
        SlimefunStartup.ticker.move.put(from, to);
    }

    private static void refreshCache(BlockStorage storage, Location l, final String key, final String value, final boolean updateTicker) {
        final Config cfg = storage.cache_blocks.containsKey(key) ? storage.cache_blocks.get(key) : new Config("data-storage/Slimefun/stored-blocks/" + l.getWorld().getName() + "/" + key + ".sfb");
        cfg.setValue(serializeLocation(l), value);
        storage.cache_blocks.put(key, cfg);
        if (updateTicker) {
            final SlimefunItem item = SlimefunItem.getByID(key);
            if (item != null && item.isTicking()) {
                final String chunk_string = locationToChunkString(l);
                if (value != null) {
                    final Set<Location> locations = BlockStorage.ticking_chunks.containsKey(chunk_string) ? BlockStorage.ticking_chunks.get(chunk_string) : new HashSet<>();
                    locations.add(l);
                    BlockStorage.ticking_chunks.put(chunk_string, locations);
                    BlockStorage.loaded_tickers.add(chunk_string);
                }
            }
        }
    }

    public static SlimefunItem check(Block block) {
        return check(block.getLocation());
    }

    public static SlimefunItem check(Location l) {
        if (!hasBlockInfo(l)) {
            return null;
        }
        return SlimefunItem.getByID(getLocationInfo(l, "id"));
    }

    public static String checkID(Block block) {
        return checkID(block.getLocation());
    }

    public static boolean check(Block block, String slimefunItem) {
        return check(block.getLocation(), slimefunItem);
    }

    public static String checkID(Location l) {
        if (!hasBlockInfo(l)) {
            return null;
        }
        return getLocationInfo(l, "id");
    }

    public static boolean check(Location l, String slimefunItem) {
        if (!hasBlockInfo(l)) {
            return false;
        }
        try {
            String id = getLocationInfo(l, "id");
            return id != null && id.equalsIgnoreCase(slimefunItem);
        } catch (NullPointerException x) {
            return false;
        }
    }

    public static boolean isWorldRegistered(String name) {
        return BlockStorage.worlds.containsKey(name);
    }

    public static Set<String> getTickingChunks() {
        return new HashSet<>(BlockStorage.loaded_tickers);
    }

    @Deprecated
    public static Set<Block> getTickingBlocks(Chunk chunk) {
        return getTickingBlocks(chunk.toString());
    }

    @Deprecated
    public static Set<Block> getTickingBlocks(String chunk) {
        Set<Block> ret = new HashSet<>();
        for (Location l : getTickingLocations(chunk)) {
            ret.add(l.getBlock());
        }
        return ret;
    }

    public static Set<Location> getTickingLocations(String chunk) {
        return new HashSet<>(BlockStorage.ticking_chunks.get(chunk));
    }

    public static BlockMenu getInventory(Block b) {
        return getInventory(b.getLocation());
    }

    public static BlockMenu getInventory(Location l) {
        final BlockStorage storage = getStorage(l.getWorld());
        if (storage == null) {
            return null;
        }
        if (!storage.hasInventory(l)) {
            return storage.loadInventory(l, BlockMenuPreset.getPreset(checkID(l)));
        }
        return storage.inventories.get(l);
    }

    public static JSONParser getParser() {
        return new JSONParser();
    }

    public static Config getChunkInfo(Chunk chunk) {
        try {
            final Config cfg = new Config("data-storage/Slimefun/temp.yml");
            if (!BlockStorage.map_chunks.containsKey(serializeChunk(chunk))) {
                return cfg;
            }
            for (Map.Entry<String, String> entry : parseJSON(getJSONData(chunk)).entrySet()) {
                cfg.setValue(entry.getKey(), entry.getValue());
            }
            return cfg;
        } catch (Exception x) {
            System.err.println(x.getClass().getName());
            System.err.println("[Slimefun] Failed to parse ChunkInfo for Chunk @ " + chunk.getX() + ", " + chunk.getZ());
            try {
                System.err.println(getJSONData(chunk));
            } catch (Exception x2) {
                System.err.println("No Metadata found!");
            }
            x.printStackTrace();
            return new Config("data-storage/Slimefun/temp.yml");
        }
    }

    public static boolean hasChunkInfo(Chunk chunk) {
        return BlockStorage.map_chunks.containsKey(serializeChunk(chunk));
    }

    public static void setChunkInfo(Chunk chunk, String key, String value) {
        Config cfg = new Config("data-storage/Slimefun/temp.yml");
        if (hasChunkInfo(chunk)) {
            cfg = getChunkInfo(chunk);
        }
        cfg.setValue(key, value);
        JSONObject json = new JSONObject(new HashMap<String, String>());
        for (final String path : cfg.getKeys()) {
            json.put(path, cfg.getString(path));
        }
        BlockStorage.map_chunks.put(serializeChunk(chunk), json.toJSONString());
        ++BlockStorage.chunk_changes;
    }

    public static String getChunkInfo(Chunk chunk, String key) {
        return getChunkInfo(chunk).getString(key);
    }

    public static boolean hasChunkInfo(Chunk chunk, String key) {
        return getChunkInfo(chunk, key) != null;
    }

    public static void clearChunkInfo(Chunk chunk) {
        BlockStorage.map_chunks.remove(serializeChunk(chunk));
    }

    public static String getBlockInfoAsJson(Block block) {
        return getBlockInfoAsJson(block.getLocation());
    }

    public static String getBlockInfoAsJson(Location l) {
        return serializeBlockInfo(getLocationInfo(l));
    }

    public void computeChanges() {
        changes = cache_blocks.size() + BlockStorage.chunk_changes;
        final Map<Location, BlockMenu> inventories2 = new HashMap<>(this.inventories);
        for (final Map.Entry<Location, BlockMenu> entry : inventories2.entrySet()) {
            changes += entry.getValue().changes;
        }
        final Map<String, UniversalBlockMenu> universal_inventories2 = new HashMap<>(BlockStorage.universal_inventories);
        for (final Map.Entry<String, UniversalBlockMenu> entry2 : universal_inventories2.entrySet()) {
            changes += entry2.getValue().changes;
        }
    }

    public int getChanges() {
        return this.changes;
    }

    public void save(final boolean remove) {
        this.save(true, remove);
    }

    public void save(boolean computeChanges, boolean remove) {
        if (computeChanges) {
            computeChanges();
        }

        if (changes == 0) {
            return;
        }

        System.out.println("[Slimefun] 正在为世界 \"" + this.world.getName() + "\" 保存方块信息 (共保存" + this.changes + " 个改变)");

        Map<String, Config> cache = new HashMap<>(cache_blocks);

        for (Map.Entry<String, Config> entry : cache.entrySet()) {
            cache_blocks.remove(entry.getKey());
            Config cfg = entry.getValue();
            if (cfg.getKeys().isEmpty()) {
                cfg.getFile().delete();
            } else {
                cfg.save();
            }
        }

        Map<Location, BlockMenu> inventories2 = new HashMap<Location, BlockMenu>(inventories);

        for (Map.Entry<Location, BlockMenu> entry : inventories2.entrySet()) {
            entry.getValue().save(entry.getKey());
        }

        Map<String, UniversalBlockMenu> universal_inventories2 = new HashMap<String, UniversalBlockMenu>(universal_inventories);

        for (Map.Entry<String, UniversalBlockMenu> entry : universal_inventories2.entrySet()) {
            entry.getValue().save();
        }

        if (chunk_changes > 0) {
            File chunks = new File(path_chunks + "chunks.sfc");
            Config cfg = new Config("data-storage/Slimefun/temp.yml");

            for (Map.Entry<String, String> entry : map_chunks.entrySet()) {
                cfg.setValue(entry.getKey(), entry.getValue());
            }

            cfg.save(chunks);

            if (remove) {
                worlds.remove(world.getName());
            }
        }

        changes = 0;
        chunk_changes = 0;
    }

    public BlockMenu loadInventory(final Location l, final BlockMenuPreset preset) {
        BlockMenu menu = new BlockMenu(preset, l);
        inventories.put(l, menu);
        return menu;
    }

    public void loadUniversalInventory(final BlockMenuPreset preset) {
        BlockStorage.universal_inventories.put(preset.getID(), new UniversalBlockMenu(preset));
    }

    public void clearInventory(final Location l) {
        BlockMenu menu = getInventory(l);
        for (final HumanEntity human : new ArrayList<>(menu.toInventory().getViewers())) {
            human.closeInventory();
        }
        inventories.get(l).delete(l);
        inventories.remove(l);
    }

    public boolean hasInventory(final Location l) {
        return this.inventories.containsKey(l);
    }

    public boolean hasUniversalInventory(final String id) {
        return BlockStorage.universal_inventories.containsKey(id);
    }

    public UniversalBlockMenu getUniversalInventory(final Block block) {
        return this.getUniversalInventory(block.getLocation());
    }

    public UniversalBlockMenu getUniversalInventory(final Location l) {
        final String id = checkID(l);
        return (id == null) ? null : this.getUniversalInventory(id);
    }

    public UniversalBlockMenu getUniversalInventory(final String id) {
        return BlockStorage.universal_inventories.get(id);
    }

    public boolean hasUniversalInventory(final Block block) {
        return this.hasUniversalInventory(block.getLocation());
    }

    public boolean hasUniversalInventory(final Location l) {
        final String id = checkID(l);
        return id != null && this.hasUniversalInventory(id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(storage, inventories, cache_blocks, world, changes);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BlockStorage that = (BlockStorage) obj;
        return changes == that.changes && Objects.equal(storage, that.storage) && Objects.equal(inventories, that.inventories) && Objects.equal(cache_blocks, that.cache_blocks) && Objects.equal(world, that.world);
    }
}
