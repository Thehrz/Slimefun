package me.mrCookieSlime.Slimefun.api.machine;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;

import java.util.HashMap;
import java.util.Map;

public class MachineConfig extends Config {
    final MachineSettings global;
    final Map<String, MachineSettings> children;

    public MachineConfig(String id) {
        super("plugins/Slimefun/machines/" + id + ".yml");

        this.global = new MachineSettings(this);
        this.children = new HashMap<>();
    }

    public MachineSettings getGlobalSettings() {
        return this.global;
    }

    public MachineSettings getSettings(AContainer item) {
        if (!this.children.containsKey(item.getID())) {
            this.children.put(item.getID(), new MachineSettings(this, item));
        }

        return this.children.get(item.getID());
    }
}


