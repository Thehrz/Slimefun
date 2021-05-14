package me.mrCookieSlime.Slimefun;

import com.bekvon.bukkit.residence.protection.FlagPermissions;
import io.izzel.taboolib.loader.Plugin;
import io.izzel.taboolib.metrics.BMetrics;
import io.izzel.taboolib.module.dependency.Dependency;
import io.izzel.taboolib.module.dependency.TDependencyInjector;
import io.papermc.lib.PaperLib;
import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.PluginUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Reflection.ReflectionUtils;
import me.mrCookieSlime.Slimefun.AncientAltar.Pedestals;
import me.mrCookieSlime.Slimefun.CSCoreLibSetup.CSCoreLibLoader;
import me.mrCookieSlime.Slimefun.Commands.SlimefunCommand;
import me.mrCookieSlime.Slimefun.Commands.SlimefunTabCompleter;
import me.mrCookieSlime.Slimefun.GEO.OreGenSystem;
import me.mrCookieSlime.Slimefun.GEO.Resources.NetherIceResource;
import me.mrCookieSlime.Slimefun.GEO.Resources.OilResource;
import me.mrCookieSlime.Slimefun.GPS.Elevator;
import me.mrCookieSlime.Slimefun.GitHub.GitHubConnector;
import me.mrCookieSlime.Slimefun.GitHub.GitHubSetup;
import me.mrCookieSlime.Slimefun.Hashing.ItemHash;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Misc.BookDesign;
import me.mrCookieSlime.Slimefun.Objects.MultiBlock;
import me.mrCookieSlime.Slimefun.Objects.Research;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunArmorPiece;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.machines.AutoEnchanter;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.machines.ElectricDustWasher;
import me.mrCookieSlime.Slimefun.Setup.*;
import me.mrCookieSlime.Slimefun.URID.AutoSavingTask;
import me.mrCookieSlime.Slimefun.URID.URID;
import me.mrCookieSlime.Slimefun.WorldEdit.WESlimefunManager;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import me.mrCookieSlime.Slimefun.api.SlimefunBackup;
import me.mrCookieSlime.Slimefun.api.TickerTask;
import me.mrCookieSlime.Slimefun.api.energy.ChargableBlock;
import me.mrCookieSlime.Slimefun.api.energy.EnergyNet;
import me.mrCookieSlime.Slimefun.api.energy.ItemEnergy;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.CargoNet;
import me.mrCookieSlime.Slimefun.api.item_transport.ChestManipulator;
import me.mrCookieSlime.Slimefun.listeners.AncientAltarListener;
import me.mrCookieSlime.Slimefun.listeners.BackpackListener;
import me.mrCookieSlime.Slimefun.listeners.CoolerListener;
import me.mrCookieSlime.Slimefun.listeners.TalismanListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.logging.Level;

@Dependency(maven = "io.papermc:paperlib:1.0.6", mavenRepo = "https://papermc.io/repo/repository/maven-public/")
public class SlimefunStartup extends Plugin {
    public static JavaPlugin instance;
    public static SlimefunStartup slimefunStartup;
    public static TickerTask ticker;
    static PluginUtils utils;
    static Config researches;
    static Config items;
    static Config whitelist;
    static Config config;
    final String[] supported = new String[]{"v1_9_", "v1_10_", "v1_11_", "v1_12_"};
    private boolean exoticGarden = false;
    private boolean plotSquared = false;
    private boolean residence = false;

    public static Config getCfg() {
        return config;
    }

    public static Config getResearchCfg() {
        return researches;
    }

    public static Config getItemCfg() {
        return items;
    }

    public static Config getWhitelist() {
        return whitelist;
    }

    public static int randomize(int max) {
        if (max < 1) {
            return 0;
        }
        return CSCoreLib.randomizer().nextInt(max);
    }

    public static boolean chance(int max, int percentage) {
        if (max < 1) {
            return false;
        }
        return CSCoreLib.randomizer().nextInt(max) <= percentage;
    }

    @Override
    public void onLoad() {
        TDependencyInjector.inject(this.getPlugin(), this.getPlugin().getClass());
    }

    @Override
    public void onEnable() {
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlotSquared")) {
            this.plotSquared = true;
        }
        if (this.getPlugin().getServer().getPluginManager().getPlugin("Residence") != null) {
            FlagPermissions.addFlag("sf-machines");
            this.residence = true;
        }
        if (new CSCoreLibLoader(this.getPlugin()).load()) {
            String currentVersion = ReflectionUtils.getVersion();
            if (currentVersion.startsWith("v")) {
                boolean compatibleVersion = false;
                StringBuilder versions = new StringBuilder();
                int i = 0;
                for (String version : this.supported) {
                    if (currentVersion.startsWith(version)) {
                        compatibleVersion = true;
                    }
                    if (i == 0) {
                        versions.append(version.substring(1).replaceFirst("_", ".").replace("_", ".X"));
                    } else if (i == this.supported.length - 1) {
                        versions.append(" or ").append(version.substring(1).replaceFirst("_", ".").replace("_", ".X"));
                    } else {
                        versions.append(", ").append(version.substring(1).replaceFirst("_", ".").replace("_", ".X"));
                    }
                    ++i;
                }
                if (!compatibleVersion) {
                    System.err.println("### Slimefun加载失败!");
                    System.err.println("###");
                    System.err.println("### 你当前使用的Minecraft版本Slimefun不支持!!!");
                    System.err.println("###");
                    System.err.println("### 你正在使用Minecraft " + ReflectionUtils.getVersion());
                    System.err.println("### 但Slimefun v" + this.getPlugin().getDescription().getVersion() + " 只能运行在");
                    System.err.println("### Minecraft " + versions);
                    System.err.println("###");
                    System.err.println("### 请尝试使用旧版并关闭自动更新");
                    System.err.println("### 或者更新你的服务器.");
                    this.getPlugin().getServer().getPluginManager().disablePlugin(this.getPlugin());
                    return;
                }
            }
            instance = this.getPlugin();
            slimefunStartup = this;
            System.out.println("[Slimefun] 加载文件中...");
            Files.cleanup();
            System.out.println("[Slimefun] 加载配置中...");
            utils = new PluginUtils(this.getPlugin());
            utils.setupConfig();
            researches = new Config(Files.RESEARCHES);
            items = new Config(Files.ITEMS);
            whitelist = new Config(Files.WHITELIST);
            utils.setupUpdater(53485, this.getPlugin().getFile());
            utils.setupMetrics();
            utils.setupLocalization();
            config = utils.getConfig();
            Messages.local = utils.getLocalization();
            Messages.setup();
            if (!new File("data-storage/Slimefun/blocks").exists()) {
                new File("data-storage/Slimefun/blocks").mkdirs();
            }
            if (!new File("data-storage/Slimefun/stored-blocks").exists()) {
                new File("data-storage/Slimefun/stored-blocks").mkdirs();
            }
            if (!new File("data-storage/Slimefun/stored-inventories").exists()) {
                new File("data-storage/Slimefun/stored-inventories").mkdirs();
            }
            if (!new File("data-storage/Slimefun/stored-chunks").exists()) {
                new File("data-storage/Slimefun/stored-chunks").mkdirs();
            }
            if (!new File("data-storage/Slimefun/universal-inventories").exists()) {
                new File("data-storage/Slimefun/universal-inventories").mkdirs();
            }
            if (!new File("data-storage/Slimefun/waypoints").exists()) {
                new File("data-storage/Slimefun/waypoints").mkdirs();
            }
            if (!new File("data-storage/Slimefun/block-backups").exists()) {
                new File("data-storage/Slimefun/block-backups").mkdirs();
            }
            if (!new File("plugins/Slimefun/scripts").exists()) {
                new File("plugins/Slimefun/scripts").mkdirs();
            }
            if (!new File("plugins/Slimefun/generators").exists()) {
                new File("plugins/Slimefun/generators").mkdirs();
            }
            if (!new File("plugins/Slimefun/error-reports").exists()) {
                new File("plugins/Slimefun/error-reports").mkdirs();
            }
            if (!new File("plugins/Slimefun/cache/github").exists()) {
                new File("plugins/Slimefun/cache/github").mkdirs();
            }
            SlimefunManager.plugin = this;
            System.out.println("[Slimefun] 加载物品中...");
            MiscSetup.setupItemSettings();
            try {
                SlimefunSetup.setupItems();
                if (getCfg().getBoolean("options.quantum-technology")) {
                    NarItemSetup.setupItems();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            MiscSetup.loadDescriptions();
            System.out.println("[Slimefun] 加载研究中...");
            Research.enabled = SlimefunStartup.getResearchCfg().getBoolean("enable-researching");
            ResearchSetup.setupResearches();
            MiscSetup.setupMisc();
            BlockStorage.info_delay = config.getInt("URID.info-delay");
            PaperLib.suggestPaper(this.getPlugin());
            if (PaperLib.isPaper()) {
                Bukkit.getServer().getLogger().log(Level.INFO, "[Slimefun] 检测到Paper服务端 性能优化已应用!");
            }
            System.out.println("[Slimefun] 加载世界生成器中...");
            OreGenSystem.registerResource(new OilResource());
            OreGenSystem.registerResource(new NetherIceResource());
            GitHubSetup.setup();
            new BMetrics(getPlugin(), 11136);
            if (config.getBoolean("items.talismans")) {
                new TalismanListener(getPlugin());
            }
            if (config.getBoolean("items.backpacks")) {
                new BackpackListener(getPlugin());
            }
            if (config.getBoolean("items.coolers")) {
                new CoolerListener(getPlugin());
            }
            if (config.getBoolean("options.give-guide-on-first-join")) {
                this.getPlugin().getServer().getPluginManager().registerEvents(new Listener() {
                    @EventHandler
                    public void onJoin(PlayerJoinEvent e) {
                        if (!e.getPlayer().hasPlayedBefore()) {
                            Player player = e.getPlayer();
                            if (!SlimefunStartup.getWhitelist().getBoolean(player.getWorld().getName() + ".enabled")) {
                                return;
                            }
                            if (!SlimefunStartup.getWhitelist().getBoolean(player.getWorld().getName() + ".enabled-items.SLIMEFUN_GUIDE")) {
                                return;
                            }
                            player.getInventory().addItem(SlimefunGuide.getItem(BookDesign.CHEST));
                        }
                    }
                }, this.getPlugin());
            }
            this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.getPlugin(), () -> {
                this.getPlugin().getServer().getPluginManager().registerEvents(new Listener() {
                    @EventHandler
                    public void onWorldLoad(WorldLoadEvent e) {
                        BlockStorage.getForcedStorage(e.getWorld());
                        SlimefunStartup.getWhitelist().setDefaultValue(e.getWorld().getName() + ".enabled", true);
                        SlimefunStartup.getWhitelist().setDefaultValue(e.getWorld().getName() + ".enabled-items.SLIMEFUN_GUIDE", true);
                        SlimefunStartup.getWhitelist().save();
                    }

                    @EventHandler
                    public void onWorldUnload(WorldUnloadEvent e) {
                        BlockStorage storage = BlockStorage.getStorage(e.getWorld());
                        if (storage != null) {
                            storage.save(true);
                        } else {
                            System.err.println("[Slimefun] Could not save Slimefun Blocks for World \"" + e.getWorld().getName() + "\"");
                        }
                    }
                }, this.getPlugin());
                this.getPlugin().getServer().getPluginManager().registerEvents(new Listener() {
                    @EventHandler
                    public void onDisconnect(PlayerQuitEvent e) {
                        SlimefunGuide.history.remove(e.getPlayer().getUniqueId());
                    }
                }, this.getPlugin());
                this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.getPlugin(), () -> {
                    SlimefunGuide.all_recipes = config.getBoolean("options.show-vanilla-recipes-in-guide");
                    MiscSetup.loadItems();
                    for (World world : Bukkit.getWorlds()) {
                        new BlockStorage(world);
                    }
                    if (SlimefunItem.getByID("ANCIENT_ALTAR") != null) {
                        new AncientAltarListener();
                    }
                }, 0L);
                if (this.getPlugin().getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
                    try {
                        Class.forName("com.sk89q.worldedit.extent.Extent");
                        new WESlimefunManager();
                        System.out.println("[Slimefun] Successfully hooked into WorldEdit!");
                    } catch (Exception x) {
                        System.err.println("[Slimefun] Failed to hook into WorldEdit!");
                        System.err.println("[Slimefun] Maybe consider updating WorldEdit or Slimefun?");
                    }
                }
                this.getPlugin().getCommand("slimefun").setExecutor(new SlimefunCommand(this));
                this.getPlugin().getCommand("slimefun").setTabCompleter(new SlimefunTabCompleter());
                if (config.getBoolean("options.enable-armor-effects")) {
                    this.getPlugin().getServer().getScheduler().runTaskTimer(this.getPlugin(), () -> {
                        block0:
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            for (ItemStack armor : p.getInventory().getArmorContents()) {
                                if (armor == null || !Slimefun.hasUnlocked(p, armor, true)) {
                                    continue;
                                }
                                if (SlimefunItem.getByItem(armor) instanceof SlimefunArmorPiece) {
                                    for (PotionEffect effect : ((SlimefunArmorPiece) SlimefunItem.getByItem(armor)).getEffects()) {
                                        p.removePotionEffect(effect.getType());
                                        p.addPotionEffect(effect);
                                    }
                                }
                                if (!SlimefunManager.isItemSimiliar(armor, SlimefunItem.getItem("SOLAR_HELMET"), false) || p.getWorld().getTime() >= 12300L && p.getWorld().getTime() <= 23850L || p.getEyeLocation().getBlock().getLightFromSky() != 15) {
                                    continue;
                                }
                                ItemEnergy.chargeInventory(p, Float.parseFloat(String.valueOf(Slimefun.getItemValue("SOLAR_HELMET", "charge-amount"))));
                            }
                            for (ItemStack radioactive : SlimefunItem.radioactive) {
                                if (!p.getInventory().containsAtLeast(radioactive, 1) && !SlimefunManager.isItemSimiliar(p.getInventory().getItemInOffHand(), radioactive, true)) {
                                    continue;
                                }
                                if (SlimefunManager.isItemSimiliar(SlimefunItems.SCUBA_HELMET, p.getInventory().getHelmet(), true) && SlimefunManager.isItemSimiliar(SlimefunItems.HAZMATSUIT_CHESTPLATE, p.getInventory().getChestplate(), true) && SlimefunManager.isItemSimiliar(SlimefunItems.HAZMATSUIT_LEGGINGS, p.getInventory().getLeggings(), true) && SlimefunManager.isItemSimiliar(SlimefunItems.RUBBER_BOOTS, p.getInventory().getBoots(), true)) {
                                    continue block0;
                                }
                                if (!Slimefun.isEnabled(p, radioactive, false)) {
                                    continue;
                                }
                                p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 400, 3));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 400, 3));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 400, 3));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 400, 3));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 400, 1));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 400, 1));
                                p.setFireTicks(400);
                                continue block0;
                            }
                        }
                    }, 0L, (long) config.getInt("options.armor-update-interval") * 20L);
                }
                ticker = new TickerTask();
                this.getPlugin().getServer().getScheduler().runTaskTimer(this.getPlugin(), new AutoSavingTask(), 1200L, (long) config.getInt("options.auto-save-delay-in-minutes") * 60L * 20L);
                this.getPlugin().getServer().getScheduler().runTaskTimer(this.getPlugin(), ticker, 100L, config.getInt("URID.custom-ticker-delay"));
                this.getPlugin().getServer().getScheduler().runTaskTimer(this.getPlugin(), () -> {
                    for (GitHubConnector connector : GitHubConnector.connectors) {
                        connector.pullFile();
                    }
                }, 80L, 72000L);
                Bukkit.getScheduler().runTaskLater(getPlugin(), () -> exoticGarden = getPlugin().getServer().getPluginManager().isPluginEnabled("ExoticGarden"), 0L);
                Research.creative_research = config.getBoolean("options.allow-free-creative-research");
                AutoEnchanter.max_emerald_enchantments = config.getInt("options.emerald-enchantment-limit");
                SlimefunSetup.legacy_ore_washer = config.getBoolean("options.legacy-ore-washer");
                ElectricDustWasher.legacy_dust_washer = config.getBoolean("options.legacy-dust-washer");
                CSCoreLib.getLib().filterLog("([A-Za-z0-9_]{3,16}) issued server command: /sf elevator (.{0,})");
            });
        }

    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this.getPlugin());
        SlimefunStartup.ticker.HALTED = true;
        ticker.run();
        try {
            for (World world : Bukkit.getWorlds()) {
                BlockStorage storage = BlockStorage.getStorage(world);
                if (storage != null) {
                    storage.save(true);
                    continue;
                }
                System.err.println("[Slimefun] Could not save Slimefun Blocks for World \"" + world.getName() + "\"");
            }
            SlimefunBackup.start();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        config = null;
        researches = null;
        items = null;
        whitelist = null;
        instance = null;
        Messages.local = null;
        Files.CONFIG = null;
        Files.DATABASE = null;
        Files.ITEMS = null;
        Files.RESEARCHES = null;
        Files.WHITELIST = null;
        MultiBlock.list = null;
        Research.list = null;
        Research.researching = null;
        SlimefunItem.all = null;
        SlimefunItem.items = null;
        SlimefunItem.map_id = null;
        SlimefunItem.handlers = null;
        SlimefunItem.radioactive = null;
        Variables.damage = null;
        Variables.jump = null;
        Variables.mode = null;
        SlimefunGuide.history = null;
        Variables.altarinuse = null;
        Variables.enchanting = null;
        Variables.backpack = null;
        Variables.soulbound = null;
        Variables.blocks = null;
        Variables.cancelPlace = null;
        Variables.arrows = null;
        SlimefunCommand.arguments = null;
        SlimefunCommand.descriptions = null;
        SlimefunCommand.tabs = null;
        URID.objects = null;
        URID.ids = null;
        SlimefunItem.blockhandler = null;
        BlockMenuPreset.presets = null;
        BlockStorage.loaded_tickers = null;
        BlockStorage.ticking_chunks = null;
        BlockStorage.worlds = null;
        ChargableBlock.capacitors = null;
        ChargableBlock.max_charges = null;
        AContainer.processing = null;
        AContainer.progress = null;
        Slimefun.guide_handlers = null;
        Pedestals.recipes = null;
        Elevator.ignored = null;
        EnergyNet.listeners = null;
        EnergyNet.machines_input = null;
        EnergyNet.machines_output = null;
        EnergyNet.machines_storage = null;
        CargoNet.faces = null;
        BlockStorage.universal_inventories = null;
        TickerTask.block_timings = null;
        OreGenSystem.map = null;
        SlimefunGuide.contributors = null;
        GitHubConnector.connectors = null;
        ChestManipulator.listeners = null;
        ItemHash.digest = null;
        ItemHash.map = null;
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.closeInventory();
        }
    }

    public boolean isExoticGardenInstalled() {
        return this.exoticGarden;
    }

    public boolean isPlotSquaredInstalled() {
        return this.plotSquared;
    }

    public boolean isResidenceInstalled() {
        return this.residence;
    }
}