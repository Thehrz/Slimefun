package me.mrCookieSlime.Slimefun.Setup;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.Categories;
import me.mrCookieSlime.Slimefun.Lists.NarItems;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.narMachines.CreatorMachine;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.narMachines.UUMachine;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NarItemSetup {
    public static void setupItems() {
        (new UUMachine(Categories.QUANTUM_MACHINES, NarItems.UU_MACHINE, "UU_MACHINE", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{SlimefunItems.PLASTIC_SHEET, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.PLASTIC_SHEET, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.WITHER_PROOF_GLASS, SlimefunItems.CARBONADO_EDGED_CAPACITOR, SlimefunItems.WITHER_PROOF_GLASS}) {


            public int getUUFull() {
                return 100000;
            }


            public ItemStack[] getOutput() {
                return new ItemStack[]{NarItems.UU};
            }


            public String getInventoryTitle() {
                return "§d元物质分离机";
            }


            public ItemStack getProgressBar() {
                return new ItemStack(Material.FLINT_AND_STEEL);
            }


            public void registerDefaultRecipes() {
                registerRecipe(5, new ItemStack[]{SlimefunItems.STONE_CHUNK}, 210);
                registerRecipe(5, new ItemStack[]{new ItemStack(Material.BONE)}, 200);
                registerRecipe(4, new ItemStack[]{SlimefunItems.FERTILIZER2}, 250);
                registerRecipe(4, new ItemStack[]{SlimefunItems.FERTILIZER3}, 250);
                registerRecipe(4, new ItemStack[]{SlimefunItems.FERTILIZER4}, 250);
                registerRecipe(4, new ItemStack[]{SlimefunItems.FERTILIZER5}, 250);
                registerRecipe(4, new ItemStack[]{SlimefunItems.FERTILIZER6}, 250);
                registerRecipe(4, new ItemStack[]{SlimefunItems.FERTILIZER7}, 250);
                registerRecipe(4, new ItemStack[]{SlimefunItems.FERTILIZER8}, 250);
                registerRecipe(3, new ItemStack[]{new ItemStack(Material.DIAMOND)}, 500);
                registerRecipe(3, new ItemStack[]{new ItemStack(Material.EMERALD)}, 500);
            }


            public int getEnergyConsumption() {
                return 400;
            }


            public int getLevel() {
                return 1;
            }


            public String getMachineIdentifier() {
                return "UU_MACHINE";
            }
        }).registerChargeableBlock(true, 12800);

        (new SlimefunItem(Categories.MISC, NarItems.UU, "UU", new RecipeType(NarItems.UU_MACHINE), new ItemStack[]{null, null, null, null, new CustomItem(Material.GHAST_TEAR, "§e通过元物质分离机收集", 0), null, null, null, null

        })).register(true);

        (new SlimefunItem(Categories.RESOURCES, NarItems.IRIDIUM, "IRIDIUM", new RecipeType(NarItems.ITEM_CREATOR), new ItemStack[]{null, null, null, null, new CustomItem(NarItems.ITEM_CREATOR, "&d物质制造机", "", "§e通过物质制造机制作"), null, null, null, null

        })).register(true);

        (new CreatorMachine(Categories.QUANTUM_MACHINES, NarItems.ITEM_CREATOR, "ITEM_CREATOR", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{NarItems.UU, SlimefunItems.CARBONADO, NarItems.UU, SlimefunItems.NETHER_ICE_COOLANT_CELL, SlimefunItems.ANDROID_MEMORY_CORE, SlimefunItems.CARBONADO_EDGED_CAPACITOR, SlimefunItems.WITHER_PROOF_GLASS, SlimefunItems.GOLD_24K_BLOCK, SlimefunItems.WITHER_PROOF_GLASS}) {


            public String getInventoryTitle() {
                return "&d物质制造机";
            }


            public ItemStack getProgressBar() {
                return new ItemStack(Material.FLINT_AND_STEEL);
            }


            public void registerDefaultRecipes() {
                registerRecipe(36, new ItemStack[]{NarItems.UU}, new ItemStack[]{NarItems.IRIDIUM});
                registerRecipe(36, new ItemStack[]{NarItems.UU}, new ItemStack[]{SlimefunItems.NEPTUNIUM});
                registerRecipe(36, new ItemStack[]{NarItems.UU}, new ItemStack[]{SlimefunItems.PLUTONIUM});
            }


            public int getEnergyConsumption() {
                return 768;
            }


            public String getMachineIdentifier() {
                return "ITEM_CREATOR";
            }
        }).registerChargeableBlock(true, 65535);
    }
}


