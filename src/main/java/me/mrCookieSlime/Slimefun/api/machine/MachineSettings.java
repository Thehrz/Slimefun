package me.mrCookieSlime.Slimefun.api.machine;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;

import java.util.List;


public class MachineSettings {
    final MachineConfig cfg;
    String prefix = "global";

    public MachineSettings(MachineConfig cfg) {
        this.cfg = cfg;
    }

    public MachineSettings(MachineConfig cfg, AContainer machine) {
        this.cfg = cfg;
        this.prefix = machine.getID();
    }

    public String getString(String path) {
        return this.cfg.getString(this.prefix + "." + path);
    }

    public int getInt(String path) {
        return this.cfg.getInt(this.prefix + "." + path);
    }

    public List<String> getStringList(String path) {
        return this.cfg.getStringList(this.prefix + "." + path);
    }
}


