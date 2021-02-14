package me.mrCookieSlime.Slimefun.Objects;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunMachine;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;


public class MultiBlock {
    public static List<MultiBlock> list = new ArrayList<>();

    final Material[] blocks;
    final Material trigger;

    public MultiBlock(Material[] build, Material trigger) {
        this.blocks = build;
        this.trigger = trigger;
    }

    public static List<MultiBlock> list() {
        return list;
    }

    public Material[] getBuild() {
        return this.blocks;
    }

    public Material getTriggerBlock() {
        return this.trigger;
    }

    public void register() {
        list.add(this);
    }

    public boolean isMultiBlock(SlimefunItem machine) {
        if (machine == null) return false;
        if (!(machine instanceof SlimefunMachine)) return false;
        if (machine instanceof SlimefunMachine) {
            MultiBlock mb = ((SlimefunMachine) machine).toMultiBlock();
            if (this.trigger == mb.getTriggerBlock()) {
                for (int i = 0; i < (mb.getBuild()).length; i++) {
                    if (mb.getBuild()[i] != null)
                        if (mb.getBuild()[i] == Material.LOG) {
                            if (!this.blocks[i].toString().contains("LOG")) return false;
                        } else if (mb.getBuild()[i] != this.blocks[i]) {
                            return false;
                        }

                }
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean isMultiBlock(MultiBlock mb) {
        if (mb == null) return false;
        if (this.trigger == mb.getTriggerBlock()) {
            for (int i = 0; i < (mb.getBuild()).length; i++) {
                if (mb.getBuild()[i] != null)
                    if (mb.getBuild()[i] == Material.LOG) {
                        if (!this.blocks[i].toString().contains("LOG")) return false;
                    } else if (mb.getBuild()[i] != this.blocks[i]) {
                        return false;
                    }

            }
            return true;
        }
        return false;
    }
}


