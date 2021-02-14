package me.mrCookieSlime.Slimefun.api.item_transport;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class ChestManipulator {
    public static List<CargoTransportEvent> listeners = new ArrayList<>();

    public static void registerListener(CargoTransportEvent listener) {
        listeners.add(listener);
    }

    public static ItemStack trigger(Block b, int slot, ItemStack prev, ItemStack next) {
        for (CargoTransportEvent listener : listeners) {
            next = listener.onEvent(b, slot, prev, next);
        }

        return next;
    }
}


