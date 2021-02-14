package me.mrCookieSlime.Slimefun.Objects.SlimefunItem;

import me.mrCookieSlime.Slimefun.AncientAltar.AltarRecipe;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.Research;
import me.mrCookieSlime.Slimefun.Objects.SlimefunBlockHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.ItemHandler;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import me.mrCookieSlime.Slimefun.URID.URID;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import me.mrCookieSlime.Slimefun.api.energy.ChargableBlock;
import me.mrCookieSlime.Slimefun.api.energy.EnergyNet;
import me.mrCookieSlime.Slimefun.api.energy.EnergyTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.*;


public class SlimefunItem {
    public static final Set<String> tickers = new HashSet<>();
    public static List<SlimefunItem> items = new ArrayList<>();
    public static Map<String, URID> map_id = new HashMap<>();
    public static List<ItemStack> radioactive = new ArrayList<>();
    public static int vanilla = 0;
    public static List<SlimefunItem> all = new ArrayList<>();
    public static Map<String, Set<ItemHandler>> handlers = new HashMap<>();
    public static Map<String, SlimefunBlockHandler> blockhandler = new HashMap<>();

    private final String id;
    private final URID urid;
    private final int month = -1;
    private final Set<ItemHandler> itemhandlers = new HashSet<>();
    private String hash;
    private State state;
    private ItemStack item;
    private Category category;
    private ItemStack[] recipe;
    private RecipeType recipeType;
    private ItemStack recipeOutput = null;
    private Research research;
    private boolean enchantable = true, disenchantable = true;
    private boolean hidden = false;
    private boolean replacing = false;
    private boolean addon = false;
    private String permission = "";
    private boolean ticking = false;
    private BlockTicker blockTicker;
    private EnergyTicker energyTicker;
    private String[] keys = null;
    private Object[] values = null;


    public SlimefunItem(Category category, ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe) {
        this.item = item;
        this.category = category;
        this.id = id;
        this.recipeType = recipeType;
        this.recipe = recipe;

        this.urid = URID.nextURID(this, false);
    }

    public SlimefunItem(Category category, ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe, ItemStack recipeOutput) {
        this.item = item;
        this.category = category;
        this.id = id;
        this.recipeType = recipeType;
        this.recipe = recipe;
        this.recipeOutput = recipeOutput;

        this.urid = URID.nextURID(this, false);
    }

    public SlimefunItem(Category category, ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe, ItemStack recipeOutput, String[] keys, Object[] values) {
        this.item = item;
        this.category = category;
        this.id = id;
        this.recipeType = recipeType;
        this.recipe = recipe;
        this.recipeOutput = recipeOutput;
        this.keys = keys;
        this.values = values;

        this.urid = URID.nextURID(this, false);
    }

    public SlimefunItem(Category category, ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe, String[] keys, Object[] values) {
        this.item = item;
        this.category = category;
        this.id = id;
        this.recipeType = recipeType;
        this.recipe = recipe;
        this.keys = keys;
        this.values = values;

        this.urid = URID.nextURID(this, false);
    }

    public SlimefunItem(Category category, ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe, boolean hidden) {
        this.item = item;
        this.category = category;
        this.id = id;
        this.recipeType = recipeType;
        this.recipe = recipe;
        this.hidden = hidden;

        this.urid = URID.nextURID(this, false);
    }

    public static List<SlimefunItem> list() {
        return items;
    }

    @Deprecated
    public static SlimefunItem getByName(String name) {
        return (SlimefunItem) URID.decode(map_id.get(name));
    }

    public static SlimefunItem getByID(String id) {
        return (SlimefunItem) URID.decode(map_id.get(id));
    }

    public static SlimefunItem getByItem(ItemStack item) {
        if (item == null) return null;
        if (SlimefunManager.isItemSimiliar(item, SlimefunItems.BROKEN_SPAWNER, false)) return getByID("BROKEN_SPAWNER");
        if (SlimefunManager.isItemSimiliar(item, SlimefunItems.REPAIRED_SPAWNER, false))
            return getByID("REINFORCED_SPAWNER");
        for (SlimefunItem sfi : items) {
            if (sfi instanceof ChargableItem && SlimefunManager.isItemSimiliar(item, sfi.getItem(), false)) return sfi;
            if (sfi instanceof DamagableChargableItem && SlimefunManager.isItemSimiliar(item, sfi.getItem(), false))
                return sfi;
            if (sfi instanceof ChargedItem && SlimefunManager.isItemSimiliar(item, sfi.getItem(), false)) return sfi;
            if (sfi instanceof SlimefunBackpack && SlimefunManager.isItemSimiliar(item, sfi.getItem(), false))
                return sfi;
            if (SlimefunManager.isItemSimiliar(item, sfi.getItem(), true)) return sfi;
        }
        return null;
    }

    public static State getState(ItemStack item) {
        for (SlimefunItem i : all) {
            if (i.isItem(item)) {
                return i.getState();
            }
        }
        return State.ENABLED;
    }

    public static boolean isDisabled(ItemStack item) {
        for (SlimefunItem i : all) {
            if (i.isItem(item)) {
                return i.isDisabled();
            }
        }
        return false;
    }

    public static Set<ItemHandler> getHandlers(String codeid) {
        if (handlers.containsKey(codeid)) return handlers.get(codeid);
        return new HashSet<>();
    }

    public static void setRadioactive(ItemStack item) {
        radioactive.add(item);
    }

    public static ItemStack getItem(String id) {
        SlimefunItem item = getByID(id);
        return (item != null) ? item.getItem() : null;
    }

    public static void patchExistingItem(String id, ItemStack stack) {
        SlimefunItem item = getByID(id);
        if (item != null) {
            System.out.println("[Slimefun] WARNING - Patching existing Item - " + id);
            System.out.println("[Slimefun] This might take a while");

            ItemStack old = item.getItem();
            item.setItem(stack);
            for (SlimefunItem sfi : list()) {
                ItemStack[] recipe = sfi.getRecipe();
                for (int i = 0; i < 9; i++) {
                    if (SlimefunManager.isItemSimiliar(recipe[i], old, true)) recipe[i] = stack;
                }
                sfi.setRecipe(recipe);
            }
        }
    }

    public static boolean isTicking(String item) {
        return tickers.contains(item);
    }

    public static void registerBlockHandler(String id, SlimefunBlockHandler handler) {
        blockhandler.put(id, handler);
    }

    @Deprecated
    public String getName() {
        return this.id;
    }

    public String getID() {
        return this.id;
    }

    public URID getURID() {
        return this.urid;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public State getState() {
        return this.state;
    }

    public ItemStack getItem() {
        return this.item;
    }

    protected void setItem(ItemStack stack) {
        this.item = stack;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public ItemStack[] getRecipe() {
        return this.recipe;
    }

    public void setRecipe(ItemStack[] recipe) {
        this.recipe = recipe;
    }

    public RecipeType getRecipeType() {
        return this.recipeType;
    }

    public void setRecipeType(RecipeType type) {
        this.recipeType = type;
    }

    @Deprecated
    public ItemStack getCustomOutput() {
        return this.recipeOutput;
    }

    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
    }

    public void setRecipeOutput(ItemStack output) {
        this.recipeOutput = output;
    }

    public Research getResearch() {
        return this.research;
    }

    public int getMonth() {
        return this.month;
    }

    public boolean isEnchantable() {
        return this.enchantable;
    }

    public boolean isDisenchantable() {
        return this.disenchantable;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public boolean isReplacing() {
        return this.replacing;
    }

    public void setReplacing(boolean replacing) {
        this.replacing = replacing;
    }

    public boolean isAddonItem() {
        return this.addon;
    }

    public String getPermission() {
        return this.permission;
    }

    public Set<ItemHandler> getHandlers() {
        return this.itemhandlers;
    }

    public boolean isTicking() {
        return this.ticking;
    }

    @Deprecated
    public BlockTicker getTicker() {
        return this.blockTicker;
    }

    public BlockTicker getBlockTicker() {
        return this.blockTicker;
    }

    public EnergyTicker getEnergyTicker() {
        return this.energyTicker;
    }

    public String[] listKeys() {
        return this.keys;
    }

    public Object[] listValues() {
        return this.values;
    }

    public boolean isDisabled() {
        return (this.state != State.ENABLED);
    }

    public void register() {
        register(false);
    }

    public void register(boolean slimefun) {
        this.addon = !slimefun;
        try {
            if (map_id.containsKey(this.id))
                throw new IllegalArgumentException("ID \"" + this.id + "\" already exists");
            if (this.recipe.length < 9)
                this.recipe = new ItemStack[]{null, null, null, null, null, null, null, null, null};
            all.add(this);

            SlimefunStartup.getItemCfg().setDefaultValue(this.id + ".enabled", Boolean.TRUE);
            SlimefunStartup.getItemCfg().setDefaultValue(this.id + ".can-be-used-in-workbenches", this.replacing);
            SlimefunStartup.getItemCfg().setDefaultValue(this.id + ".hide-in-guide", this.hidden);
            SlimefunStartup.getItemCfg().setDefaultValue(this.id + ".allow-enchanting", this.enchantable);
            SlimefunStartup.getItemCfg().setDefaultValue(this.id + ".allow-disenchanting", this.disenchantable);
            SlimefunStartup.getItemCfg().setDefaultValue(this.id + ".required-permission", this.permission);
            if (this.keys != null && this.values != null) {
                for (int i = 0; i < this.keys.length; i++) {
                    SlimefunStartup.getItemCfg().setDefaultValue(this.id + "." + this.keys[i], this.values[i]);
                }
            }

            for (World world : Bukkit.getWorlds()) {
                SlimefunStartup.getWhitelist().setDefaultValue(world.getName() + ".enabled", Boolean.TRUE);
                SlimefunStartup.getWhitelist().setDefaultValue(world.getName() + ".enabled-items." + this.id, Boolean.TRUE);
            }

            if (this.ticking && !SlimefunStartup.getCfg().getBoolean("URID.enable-tickers")) {
                this.state = State.DISABLED;

                return;
            }
            if (SlimefunStartup.getItemCfg().getBoolean(this.id + ".enabled")) {
                if (!Category.list().contains(this.category)) this.category.register();

                this.state = State.ENABLED;

                this.replacing = SlimefunStartup.getItemCfg().getBoolean(this.id + ".can-be-used-in-workbenches");
                this.hidden = SlimefunStartup.getItemCfg().getBoolean(this.id + ".hide-in-guide");
                this.enchantable = SlimefunStartup.getItemCfg().getBoolean(this.id + ".allow-enchanting");
                this.disenchantable = SlimefunStartup.getItemCfg().getBoolean(this.id + ".allow-disenchanting");
                this.permission = SlimefunStartup.getItemCfg().getString(this.id + ".required-permission");
                items.add(this);
                if (slimefun) vanilla++;
                map_id.put(this.id, this.urid);
                create();
                for (ItemHandler handler : this.itemhandlers) {
                    Set<ItemHandler> handlerset = getHandlers(handler.toCodename());
                    handlerset.add(handler);
                    handlers.put(handler.toCodename(), handlerset);
                }

                if (SlimefunStartup.getCfg().getBoolean("options.print-out-loading"))
                    System.out.println("[Slimefun] Loaded Item \"" + this.id + "\"");
            } else if (this instanceof VanillaItem) {
                this.state = State.VANILLA;
            } else {
                this.state = State.DISABLED;
            }

        } catch (Exception x) {
            System.err.println("[Slimefun] Item Registration failed: " + this.id);
            x.printStackTrace();
        }
    }

    public void bindToResearch(Research r) {
        if (r != null) r.getEffectedItems().add(this);
        this.research = r;
    }

    public boolean isItem(ItemStack item) {
        if (item == null) return false;
        if (this instanceof ChargableItem && SlimefunManager.isItemSimiliar(item, this.item, false)) return true;
        if (this instanceof DamagableChargableItem && SlimefunManager.isItemSimiliar(item, this.item, false))
            return true;
        if (this instanceof ChargedItem && SlimefunManager.isItemSimiliar(item, this.item, false)) return true;
        return SlimefunManager.isItemSimiliar(item, this.item, true);
    }

    public void load() {
        try {
            if (!this.hidden) this.category.add(this);
            ItemStack output = this.item.clone();
            if (this.recipeOutput != null) output = this.recipeOutput.clone();

            if (this.recipeType.toItem().isSimilar(RecipeType.MOB_DROP.toItem())) {
                try {
                    EntityType entity = EntityType.valueOf(ChatColor.stripColor(this.recipe[4].getItemMeta().getDisplayName()).toUpperCase().replace(" ", "_"));
                    List<ItemStack> dropping = new ArrayList<>();
                    if (SlimefunManager.drops.containsKey(entity))
                        dropping = SlimefunManager.drops.get(entity);
                    dropping.add(output);
                    SlimefunManager.drops.put(entity, dropping);
                } catch (Exception exception) {
                }

            } else if (this.recipeType.toItem().isSimilar(RecipeType.ANCIENT_ALTAR.toItem())) {
                new AltarRecipe(Arrays.asList(this.recipe), output);
            } else if (this.recipeType.getMachine() != null && getByID(this.recipeType.getMachine().getID()) instanceof SlimefunMachine) {
                ((SlimefunMachine) getByID(this.recipeType.getMachine().getID())).addRecipe(this.recipe, output);
            }
            install();
        } catch (Exception x) {
            System.err.println("[Slimefun] Item Initialization failed: " + this.id);
        }
    }

    public void install() {
    }

    public void create() {
    }

    public void addItemHandler(ItemHandler... handler) {
        this.itemhandlers.addAll(Arrays.asList(handler));

        for (ItemHandler h : handler) {
            if (h instanceof BlockTicker) {
                this.ticking = true;
                tickers.add(getID());
                this.blockTicker = (BlockTicker) h;
            } else if (h instanceof EnergyTicker) {
                this.energyTicker = (EnergyTicker) h;
                EnergyNet.registerComponent(getID(), EnergyNet.NetworkComponent.SOURCE);
            }
        }
    }

    public void register(boolean vanilla, ItemHandler... handlers) {
        addItemHandler(handlers);
        register(vanilla);
    }

    public void register(ItemHandler... handlers) {
        addItemHandler(handlers);
        register(false);
    }

    public void register(boolean vanilla, SlimefunBlockHandler handler) {
        blockhandler.put(getID(), handler);
        register(vanilla);
    }

    public void register(SlimefunBlockHandler handler) {
        blockhandler.put(getID(), handler);
        register(false);
    }

    public void registerChargeableBlock(int capacity) {
        registerChargeableBlock(false, capacity);
    }

    public void registerChargeableBlock(boolean slimefun, int capacity) {
        register(slimefun);
        ChargableBlock.registerChargableBlock(this.id, capacity, true);
        EnergyNet.registerComponent(this.id, EnergyNet.NetworkComponent.CONSUMER);
    }

    public void registerUnrechargeableBlock(boolean slimefun, int capacity) {
        register(slimefun);
        ChargableBlock.registerChargableBlock(this.id, capacity, false);
    }

    public void registerBlockCapacitor(boolean slimefun, int capacity) {
        register(slimefun);
        ChargableBlock.registerCapacitor(this.id, capacity);
    }

    public void registerEnergyDistributor(boolean slimefun) {
        register(slimefun);
        EnergyNet.registerComponent(this.id, EnergyNet.NetworkComponent.DISTRIBUTOR);
    }

    public void registerDistibutingCapacitor(boolean slimefun, int capacity) {
        register(slimefun);
        EnergyNet.registerComponent(this.id, EnergyNet.NetworkComponent.DISTRIBUTOR);
        ChargableBlock.registerCapacitor(this.id, capacity);
    }

    public void registerChargeableBlock(boolean vanilla, int capacity, ItemHandler... handlers) {
        addItemHandler(handlers);
        registerChargeableBlock(vanilla, capacity);
    }

    public BlockMenu getBlockMenu(Block b) {
        return BlockStorage.getInventory(b);
    }

    public void addWikipage(String page) {
        Slimefun.addWikiPage(getID(), "https://github.com/TheBusyBiscuit/Slimefun4/wiki/" + page);
    }

    public enum State {
        ENABLED,


        DISABLED,


        VANILLA
    }
}


