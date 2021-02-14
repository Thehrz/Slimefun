package me.mrCookieSlime.Slimefun.api;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BlockInfoConfig
        extends Config {
    private final Map<String, String> data;

    public BlockInfoConfig() {
        this(new HashMap<>());
    }

    public BlockInfoConfig(Map<String, String> data) {
        super(null, null);
        this.data = data;
    }

    public Map<String, String> getMap() {
        return this.data;
    }

    protected void store(String path, Object value) {
        if (value != null && !(value instanceof String)) {
            throw new UnsupportedOperationException("Can't set \"" + path + "\" to \"" + value + "\" (type: " + value.getClass().getSimpleName() + ") because BlockInfoConfig only supports Strings");
        }
        checkPath(path);
        if (value == null) {
            this.data.remove(path);
        } else {
            this.data.put(path, (String) value);
        }
    }


    private void checkPath(String path) {
        if (path.contains(".")) {
            throw new UnsupportedOperationException("BlockInfoConfig only supports Map<String,String> (path: " + path + ")");
        }
    }


    public boolean contains(String path) {
        checkPath(path);
        return this.data.containsKey(path);
    }


    public Object getValue(String path) {
        checkPath(path);
        return this.data.get(path);
    }


    public String getString(String path) {
        checkPath(path);
        return this.data.get(path);
    }


    public Set<String> getKeys() {
        return this.data.keySet();
    }

    private UnsupportedOperationException invalidType(String path) {
        return new UnsupportedOperationException("Can't get \"" + path + "\" because BlockInfoConfig only supports String values");
    }


    public int getInt(String path) {
        throw invalidType(path);
    }


    public boolean getBoolean(String path) {
        throw invalidType(path);
    }


    public List<String> getStringList(String path) {
        throw invalidType(path);
    }


    public List<Integer> getIntList(String path) {
        throw invalidType(path);
    }


    public Double getDouble(String path) {
        throw invalidType(path);
    }


    public Set<String> getKeys(String path) {
        throw invalidType(path);
    }


    public File getFile() {
        throw new UnsupportedOperationException();
    }


    public FileConfiguration getConfiguration() {
        throw new UnsupportedOperationException();
    }


    public void save() {
        throw new UnsupportedOperationException();
    }


    public void save(File file) {
        throw new UnsupportedOperationException();
    }


    public void createFile() {
        throw new UnsupportedOperationException();
    }


    public void reload() {
        throw new UnsupportedOperationException();
    }
}


