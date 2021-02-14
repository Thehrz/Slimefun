package me.mrCookieSlime.Slimefun.Objects;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Variable;
import me.mrCookieSlime.CSCoreLibPlugin.general.Particles.FireworkShow;
import me.mrCookieSlime.Slimefun.Events.ResearchUnlockEvent;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Setup.Messages;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


public class Research {
    public static boolean enabled;
    public static List<Research> list = new ArrayList<>();


    public static List<UUID> researching = new ArrayList<>();


    public static boolean creative_research = true;


    private final int id;
    private final List<SlimefunItem> items;
    private String name;
    private int cost;


    public Research(int id, String name, int cost) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.items = new ArrayList<>();
    }

    public static List<Research> list() {
        return list;
    }

    public static boolean isResearching(Player p) {
        return researching.contains(p.getUniqueId());
    }

    public static void sendStats(CommandSender sender, Player p) {
        List<Research> researched = new ArrayList<>();
        int levels = 0;
        for (Research r : list()) {
            if (r.hasUnlocked(p)) {
                researched.add(r);
                levels += r.getLevel();
            }
        }
        String progress = String.valueOf(Math.round(researched.size() * 100.0F / list().size() * 100.0F) / 100.0F);
        if (Float.parseFloat(progress) < 16.0F) {
            progress = "&4" + progress + " &r% ";
        } else if (Float.parseFloat(progress) < 32.0F) {
            progress = "&c" + progress + " &r% ";
        } else if (Float.parseFloat(progress) < 48.0F) {
            progress = "&6" + progress + " &r% ";
        } else if (Float.parseFloat(progress) < 64.0F) {
            progress = "&e" + progress + " &r% ";
        } else if (Float.parseFloat(progress) < 80.0F) {
            progress = "&2" + progress + " &r% ";
        } else {
            progress = "&a" + progress + " &r% ";
        }

        sender.sendMessage("");
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Statistics for Player: &b" + p.getName()));
        sender.sendMessage("");
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Title: &b" + getTitle(p, researched)));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Research Progress: " + progress + "&e(" + researched.size() + " / " + list().size() + ")"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Total XP Levels spent: &b" + levels));
    }

    public static String getTitle(Player p, List<Research> researched) {
        int index = Math.round(Float.valueOf(String.valueOf(Math.round(researched.size() * 100.0F / list().size() * 100.0F) / 100.0F)) / 100.0F) * SlimefunStartup.getCfg().getStringList("research-ranks").size();
        if (index > 0) index--;
        return SlimefunStartup.getCfg().getStringList("research-ranks").get(index);
    }

    public static Research getByID(int id) {
        for (Research research : list) {
            if (research.getID() == id) return research;
        }
        return null;
    }

    public static List<Research> getResearches(UUID uuid) {
        List<Research> researched = new ArrayList<>();
        for (Research r : list()) {
            if (r.hasUnlocked(uuid)) researched.add(r);
        }
        return researched;
    }

    public static List<Research> getResearches(String uuid) {
        return getResearches(UUID.fromString(uuid));
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Deprecated
    public int getLevel() {
        return this.cost;
    }

    @Deprecated
    public void setLevel(int level) {
        this.cost = level;
    }

    public int getCost() {
        return this.cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void addItems(SlimefunItem... items) {
        for (SlimefunItem item : items) {
            if (item != null) item.bindToResearch(this);

        }
    }

    public List<SlimefunItem> getEffectedItems() {
        return this.items;
    }

    public boolean hasUnlocked(Player p) {
        return hasUnlocked(p.getUniqueId());
    }

    public boolean hasUnlocked(UUID uuid) {
        if (!enabled) return true;
        if (!SlimefunStartup.getResearchCfg().getBoolean(this.id + ".enabled")) return true;
        return (new Config(new File("data-storage/Slimefun/Players/" + uuid.toString() + ".yml"))).contains("researches." + this.id);
    }

    public boolean canUnlock(Player p) {
        if (!enabled) return true;
        if (!SlimefunStartup.getResearchCfg().getBoolean(this.id + ".enabled")) return true;
        return ((p.getGameMode() == GameMode.CREATIVE && creative_research) || p.getLevel() >= this.cost);
    }

    public void lock(Player p) {
        Config cfg = new Config(new File("data-storage/Slimefun/Players/" + p.getUniqueId() + ".yml"));
        cfg.setValue("researches." + this.id, null);
        cfg.save();
        Messages.local.sendTranslation(p, "commands.research.reset-target", true);
    }

    public void unlock(final Player p, boolean instant) {
        if (!hasUnlocked(p)) {
            ResearchUnlockEvent event = new ResearchUnlockEvent(p, this);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                final int research = this.id;
                if (instant) {
                    Config cfg = new Config(new File("data-storage/Slimefun/Players/" + p.getUniqueId() + ".yml"));
                    cfg.setValue("researches." + research, Boolean.TRUE);
                    cfg.save();
                    Messages.local.sendTranslation(p, "messages.unlocked", true, new Variable("%research%", getName()));
                    if (SlimefunStartup.getCfg().getBoolean("options.research-give-fireworks"))
                        FireworkShow.launchRandom(p, 1);

                } else if (!researching.contains(p.getUniqueId())) {
                    researching.add(p.getUniqueId());
                    Messages.local.sendTranslation(p, "messages.research.start", true, new Variable("%research%", getName()));
                    Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
                        p.playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.7F, 1.0F);
                        Messages.local.sendTranslation(p, "messages.research.progress", true, new Variable("%research%", getName()), new Variable("%progress%", "23%"));
                        Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
                            p.playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.7F, 1.0F);
                            Messages.local.sendTranslation(p, "messages.research.progress", true, new Variable("%research%", getName()), new Variable("%progress%", "44%"));
                            Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
                                p.playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.7F, 1.0F);
                                Messages.local.sendTranslation(p, "messages.research.progress", true, new Variable("%research%", getName()), new Variable("%progress%", "57%"));
                                Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
                                    p.playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.7F, 1.0F);
                                    Messages.local.sendTranslation(p, "messages.research.progress", true, new Variable("%research%", getName()), new Variable("%progress%", "92%"));
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
                                        Config cfg = new Config(new File("data-storage/Slimefun/Players/" + p.getUniqueId() + ".yml"));
                                        cfg.setValue("researches." + research, Boolean.valueOf(true));
                                        cfg.save();
                                        Messages.local.sendTranslation(p, "messages.unlocked", true, new Variable("%research%", getName()));
                                        if (SlimefunStartup.getCfg().getBoolean("options.research-unlock-fireworks"))
                                            FireworkShow.launchRandom(p, 1);
                                        Research.researching.remove(p.getUniqueId());
                                    }, 20L);
                                }, 20L);
                            }, 20L);
                        }, 20L);
                    }, 20L);
                }
            }
        }
    }

    public void register() {
        SlimefunStartup.getResearchCfg().setDefaultValue("enable-researching", Boolean.TRUE);

        if (SlimefunStartup.getResearchCfg().contains(getID() + ".enabled") && !SlimefunStartup.getResearchCfg().getBoolean(getID() + ".enabled")) {
            Iterator<SlimefunItem> iterator = this.items.iterator();
            while (iterator.hasNext()) {
                SlimefunItem item = iterator.next();
                if (item != null) item.bindToResearch(null);
                iterator.remove();
            }

            return;
        }
        SlimefunStartup.getResearchCfg().setDefaultValue(getID() + ".name", getName());
        SlimefunStartup.getResearchCfg().setDefaultValue(getID() + ".cost", getCost());
        SlimefunStartup.getResearchCfg().setDefaultValue(getID() + ".enabled", Boolean.TRUE);

        this.name = SlimefunStartup.getResearchCfg().getString(getID() + ".name");
        this.cost = SlimefunStartup.getResearchCfg().getInt(getID() + ".cost");

        list.add(this);
        if (SlimefunStartup.getCfg().getBoolean("options.print-out-loading"))
            System.out.println("[Slimefun] Loaded Research \"" + getName() + "\"");

    }
}


