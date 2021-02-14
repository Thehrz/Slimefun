package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.machines;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.InvUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.Particles.MC_1_8.ParticleEffect;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunBlockHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.UnregisterReason;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.energy.ChargableBlock;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.Map;

public abstract class CropGrowthAccelerator
        extends SlimefunItem {
    public static final Map<Material, Integer> crops = new HashMap<>();
    private static final int[] border = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};

    static {
        crops.put(Material.CROPS, 7);
        crops.put(Material.POTATO, 7);
        crops.put(Material.CARROT, 7);
        crops.put(Material.NETHER_WARTS, 3);
        crops.put(Material.BEETROOT_BLOCK, 3);
        crops.put(Material.COCOA, 8);
    }

    public CropGrowthAccelerator(Category category, ItemStack item, String name, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, name, recipeType, recipe);

        new BlockMenuPreset(name, "&b植物生长加速器") {
            public void init() {
                CropGrowthAccelerator.this.constructMenu(this);
            }


            public void newInstance(BlockMenu menu, Block b) {
            }


            public boolean canOpen(Block b, Player p) {
                return (p.hasPermission("slimefun.inventory.bypass") || CSCoreLib.getLib().getProtectionManager().canAccessChest(p.getUniqueId(), b, true));
            }


            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                if (flow.equals(ItemTransportFlow.INSERT)) return CropGrowthAccelerator.this.getInputSlots();
                return new int[0];
            }
        };

        registerBlockHandler(name, new SlimefunBlockHandler() {
            public void onPlace(Player p, Block b, SlimefunItem item) {
            }


            public boolean onBreak(Player p, Block b, SlimefunItem item, UnregisterReason reason) {
                BlockMenu inv = BlockStorage.getInventory(b);
                if (inv != null) {
                    for (int slot : CropGrowthAccelerator.this.getInputSlots()) {
                        if (inv.getItemInSlot(slot) != null) {
                            b.getWorld().dropItemNaturally(b.getLocation(), inv.getItemInSlot(slot));
                            inv.replaceExistingItem(slot, null);
                        }
                    }
                }
                return true;
            }
        });
    }


    protected void constructMenu(BlockMenuPreset preset) {
        for (int i : border) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 9), " "), (arg0, arg1, arg2, arg3) -> false);
        }
    }


    public int[] getInputSlots() {
        return new int[]{10, 11, 12, 13, 14, 15, 16};
    }


    public void register(boolean slimefun) {
        addItemHandler(new BlockTicker() {
            public void tick(Block b, SlimefunItem sf, Config data) {
                try {
                    CropGrowthAccelerator.this.tick(b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            public void uniqueTick() {
            }


            public boolean isSynchronized() {
                return true;
            }
        });

        super.register(slimefun);
    }


    protected void tick(Block b) throws Exception {
        int work = 0;
        int x;
        label44:
        for (x = -getRadius(); x <= getRadius(); x++) {
            for (int z = -getRadius(); z <= getRadius(); z++) {
                Block block = b.getRelative(x, 0, z);
                if (crops.containsKey(block.getType()) &&
                        block.getData() < crops.get(block.getType())) {
                    for (int slot : getInputSlots()) {
                        if (SlimefunManager.isItemSimiliar(BlockStorage.getInventory(b).getItemInSlot(slot), SlimefunItems.FERTILIZER, false)) {
                            if (work > getSpeed() - 1)
                                break label44;
                            if (ChargableBlock.getCharge(b) < getEnergyConsumption())
                                break label44;
                            ChargableBlock.addCharge(b, -getEnergyConsumption());

                            if (block.getType().equals(Material.COCOA)) {
                                block.setData((byte) (block.getData() + 4));
                            } else {

                                block.setData((byte) (block.getData() + 1));
                            }

                            ParticleEffect.VILLAGER_HAPPY.display(block.getLocation().add(0.5D, 0.5D, 0.5D), 0.1F, 0.1F, 0.1F, 0.0F, 4);
                            work++;

                            break;
                        }
                    }
                }
            }
        }

        if (work > 0)
            for (int slot : getInputSlots()) {
                if (SlimefunManager.isItemSimiliar(BlockStorage.getInventory(b).getItemInSlot(slot), SlimefunItems.FERTILIZER, false)) {
                    BlockStorage.getInventory(b).replaceExistingItem(slot, InvUtils.decreaseItem(BlockStorage.getInventory(b).getItemInSlot(slot), 1));
                    break;
                }
            }
    }

    public abstract int getEnergyConsumption();

    public abstract int getRadius();

    public abstract int getSpeed();
}


