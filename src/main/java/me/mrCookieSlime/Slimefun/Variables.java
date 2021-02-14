package me.mrCookieSlime.Slimefun;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.*;


public class Variables {
    public static final Map<UUID, Entity[]> remove = new HashMap<>();
    public static Map<UUID, Boolean> jump = new HashMap<>();
    public static Map<UUID, Boolean> damage = new HashMap<>();
    public static Map<UUID, Integer> mode = new HashMap<>();

    public static Map<UUID, Integer> enchanting = new HashMap<>();
    public static Map<UUID, ItemStack> backpack = new HashMap<>();
    public static HashSet<Location> altarinuse = new HashSet<>();

    public static Map<UUID, List<ItemStack>> soulbound = new HashMap<>();
    public static List<UUID> blocks = new ArrayList<>();
    public static List<UUID> cancelPlace = new ArrayList<>();
    public static Map<UUID, ItemStack> arrows = new HashMap<>();
}


