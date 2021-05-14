package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.machines;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.InvUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineHelper;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.energy.ChargableBlock;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class ElectricDustWasher extends AContainer {
    public static boolean legacy_dust_washer = false;
    private static final int[] BORDER_1 = new int[]{0, 1, 2, 3, 9, 18, 27, 36, 37, 38, 39};
    private static final int[] BORDER_2 = new int[]{10, 11, 12, 19, 28, 29, 30, 4, 5, 6, 7, 8, 13, 22, 31, 40, 41, 42, 43, 44, 17, 26, 35};

    public ElectricDustWasher(Category category, ItemStack item, String name, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, name, recipeType, recipe);
    }

    @Override
    public String getInventoryTitle() {
        return "&b电力洗粉机";
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.GOLD_SPADE);
    }

    @Override
    public void registerDefaultRecipes() {
    }

    @Override
    public abstract int getSpeed();

    @Override
    protected void tick(Block b) {
        if (isProcessing(b)) {
            int timeleft = progress.get(b);
            if (timeleft > 0 && getSpeed() < 10) {
                ItemStack item = getProgressBar().clone();
                item.setDurability(MachineHelper.getDurability(item, timeleft, processing.get(b).getTicks()));
                ItemMeta im = item.getItemMeta();
                im.setDisplayName(" ");
                List<String> lore = new ArrayList<>();
                lore.add(MachineHelper.getProgress(timeleft, processing.get(b).getTicks()));
                lore.add("");
                lore.add(MachineHelper.getTimeLeft(timeleft / 2));
                im.setLore(lore);
                item.setItemMeta(im);

                BlockStorage.getInventory(b).replaceExistingItem(22, item);

                if (ChargableBlock.isChargable(b)) {
                    if (ChargableBlock.getCharge(b) < getEnergyConsumption()) {
                        return;
                    }
                    ChargableBlock.addCharge(b, -getEnergyConsumption());
                    progress.put(b, timeleft - 1);
                } else {
                    progress.put(b, timeleft - 1);
                }
            } else if (ChargableBlock.isChargable(b)) {
                if (ChargableBlock.getCharge(b) < getEnergyConsumption()) {
                    return;
                }
                ChargableBlock.addCharge(b, -getEnergyConsumption());

                BlockStorage.getInventory(b).replaceExistingItem(22, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), " "));
                pushItems(b, processing.get(b).getOutput());

                progress.remove(b);
                processing.remove(b);
            }
        } else {

            for (int slot : getInputSlots()) {
                if (SlimefunManager.isItemSimiliar(BlockStorage.getInventory(b).getItemInSlot(slot), SlimefunItems.SIFTED_ORE, true)) {
                    if (!legacy_dust_washer) {
                        boolean emptySlot = false;
                        for (int outputSlot : getOutputSlots()) {
                            if (BlockStorage.getInventory(b).getItemInSlot(outputSlot) == null) {
                                emptySlot = true;
                                break;
                            }
                        }
                        if (!emptySlot) {
                            return;
                        }
                    }
                    ItemStack adding = SlimefunItems.IRON_DUST;
                    if (SlimefunStartup.chance(100, 25)) {
                        adding = SlimefunItems.GOLD_DUST;
                    } else if (SlimefunStartup.chance(100, 25)) {
                        adding = SlimefunItems.ALUMINUM_DUST;
                    } else if (SlimefunStartup.chance(100, 25)) {
                        adding = SlimefunItems.COPPER_DUST;
                    } else if (SlimefunStartup.chance(100, 25)) {
                        adding = SlimefunItems.ZINC_DUST;
                    } else if (SlimefunStartup.chance(100, 25)) {
                        adding = SlimefunItems.TIN_DUST;
                    } else if (SlimefunStartup.chance(100, 25)) {
                        adding = SlimefunItems.MAGNESIUM_DUST;
                    } else if (SlimefunStartup.chance(100, 25)) {
                        adding = SlimefunItems.LEAD_DUST;
                    } else if (SlimefunStartup.chance(100, 25)) {
                        adding = SlimefunItems.SILVER_DUST;
                    }

                    MachineRecipe r = new MachineRecipe(4 / getSpeed(), new ItemStack[0], new ItemStack[]{adding});
                    if (legacy_dust_washer && !fits(b, r.getOutput())) {
                        return;
                    }
                    BlockStorage.getInventory(b).replaceExistingItem(slot, InvUtils.decreaseItem(BlockStorage.getInventory(b).getItemInSlot(slot), 1));
                    processing.put(b, r);
                    progress.put(b, r.getTicks());
                    break;
                }
                if (SlimefunManager.isItemSimiliar(BlockStorage.getInventory(b).getItemInSlot(slot), SlimefunItems.PULVERIZED_ORE, true)) {
                    MachineRecipe r = new MachineRecipe(4 / getSpeed(), new ItemStack[0], new ItemStack[]{SlimefunItems.PURE_ORE_CLUSTER});
                    if (!fits(b, r.getOutput())) {
                        return;
                    }
                    BlockStorage.getInventory(b).replaceExistingItem(slot, InvUtils.decreaseItem(BlockStorage.getInventory(b).getItemInSlot(slot), 1));
                    processing.put(b, r);
                    progress.put(b, r.getTicks());
                    break;
                }
            }
        }
    }


    @Override
    public String getMachineIdentifier() {
        return "ELECTRIC_DUST_WASHER";
    }

    @Override
    protected void constructMenu(BlockMenuPreset preset) {
        for (int i : BORDER_1) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0), " "), (player, slot, itemStack, clickAction) -> false);
        }

        for (int i : BORDER_2) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), " "), (player, slot, itemStack, clickAction) -> false);
        }

        preset.addItem(22, new CustomItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), " "), (player, slot, itemStack, clickAction) -> false);


        for (int i : getOutputSlots()) {
            preset.addMenuClickHandler(i, new ChestMenu.AdvancedMenuClickHandler() {
                @Override
                public boolean onClick(Player p, int slot, ItemStack cursor, ClickAction action) {
                    return false;
                }

                @Override
                public boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor, ClickAction action) {
                    return (cursor == null || cursor.getType() == null || cursor.getType() == Material.AIR);
                }
            });
        }
    }

    @Override
    public int[] getInputSlots() {
        return new int[]{20, 21};
    }

    @Override
    public int[] getOutputSlots() {
        return new int[]{14, 15, 16, 23, 24, 25, 32, 33, 34};
    }
}