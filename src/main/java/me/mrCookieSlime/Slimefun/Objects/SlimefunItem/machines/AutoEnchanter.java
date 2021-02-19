package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.machines;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.InvUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.EmeraldEnchants.EmeraldEnchants;
import me.mrCookieSlime.EmeraldEnchants.ItemEnchantment;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineHelper;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import me.mrCookieSlime.Slimefun.api.energy.ChargableBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.*;

public class AutoEnchanter extends AContainer {
    public static int max_emerald_enchantments = 2;

    public AutoEnchanter(Category category, ItemStack item, String name, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, name, recipeType, recipe);
    }


    public String getInventoryTitle() {
        return "&5自动附魔机";
    }


    public ItemStack getProgressBar() {
        return new ItemStack(Material.GOLD_CHESTPLATE);
    }


    public void registerDefaultRecipes() {
    }


    public int getEnergyConsumption() {
        return 9;
    }


    protected void tick(Block b) {
        if (isProcessing(b)) {
            int timeleft = progress.get(b);
            if (timeleft > 0) {
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
                    if (ChargableBlock.getCharge(b) < getEnergyConsumption())
                        return;
                    ChargableBlock.addCharge(b, -getEnergyConsumption());
                    progress.put(b, timeleft - 1);
                } else {
                    progress.put(b, timeleft - 1);
                }
            } else {
                BlockStorage.getInventory(b).replaceExistingItem(22, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 15), " "));
                pushItems(b, processing.get(b).getOutput());

                progress.remove(b);
                processing.remove(b);
            }
        } else {

            MachineRecipe r = null;

            for (int slot : getInputSlots()) {
                ItemStack target = BlockStorage.getInventory(b).getItemInSlot((slot == getInputSlots()[0]) ? getInputSlots()[1] : getInputSlots()[0]);

                SlimefunItem sfTarget = SlimefunItem.getByItem(target);
                if (sfTarget != null && !sfTarget.isEnchantable())
                    return;
                ItemStack item = BlockStorage.getInventory(b).getItemInSlot(slot);


                if (item != null && item.getType() == Material.ENCHANTED_BOOK && target != null) {
                    Map<Enchantment, Integer> enchantments = new HashMap<>();
                    Set<ItemEnchantment> enchantments2 = new HashSet<>();
                    int amount = 0;
                    int special_amount = 0;
                    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                    for (Map.Entry<Enchantment, Integer> e : meta.getStoredEnchants().entrySet()) {
                        if (e.getKey().canEnchantItem(target)) {
                            amount++;
                            enchantments.put(e.getKey(), e.getValue());
                        }
                    }
                    if (Slimefun.isEmeraldEnchantsInstalled()) {
                        for (ItemEnchantment enchantment : EmeraldEnchants.getInstance().getRegistry().getEnchantments(item)) {
                            if (EmeraldEnchants.getInstance().getRegistry().isApplicable(target, enchantment.getEnchantment()) && EmeraldEnchants.getInstance().getRegistry().getEnchantmentLevel(target, enchantment.getEnchantment().getName()) < enchantment.getLevel()) {
                                amount++;
                                special_amount++;
                                enchantments2.add(enchantment);
                            }
                        }
                        special_amount += EmeraldEnchants.getInstance().getRegistry().getEnchantments(target).size();
                    }
                    if (amount > 0 && special_amount <= max_emerald_enchantments) {
                        ItemStack newItem = target.clone();
                        newItem.setAmount(1);
                        for (Map.Entry<Enchantment, Integer> e : enchantments.entrySet()) {
                            newItem.addUnsafeEnchantment(e.getKey(), e.getValue());
                        }
                        for (ItemEnchantment e : enchantments2) {
                            EmeraldEnchants.getInstance().getRegistry().applyEnchantment(newItem, e.getEnchantment(), e.getLevel());
                        }
                        r = new MachineRecipe(75 * amount, new ItemStack[]{target, item}, new ItemStack[]{newItem, new ItemStack(Material.BOOK)});
                    }

                    break;
                }
            }
            if (r != null) {
                if (!fits(b, r.getOutput())) {
                    return;
                }
                for (int slot : getInputSlots()) {
                    BlockStorage.getInventory(b).replaceExistingItem(slot, InvUtils.decreaseItem(BlockStorage.getInventory(b).getItemInSlot(slot), 1));
                }
                processing.put(b, r);
                progress.put(b, r.getTicks());
            }
        }
    }


    public int getSpeed() {
        return 1;
    }


    public String getMachineIdentifier() {
        return "AUTO_ENCHANTER";
    }
}


