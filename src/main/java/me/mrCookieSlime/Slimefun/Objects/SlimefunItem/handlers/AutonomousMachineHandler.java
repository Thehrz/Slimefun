package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.event.block.BlockDispenseEvent;


public abstract class AutonomousMachineHandler
        extends ItemHandler {
    public String toCodename() {
        return "AutonomousMachineHandler";
    }

    public abstract boolean onBlockDispense(BlockDispenseEvent paramBlockDispenseEvent, Block paramBlock1, Dispenser paramDispenser, Block paramBlock2, Block paramBlock3, SlimefunItem paramSlimefunItem);
}


