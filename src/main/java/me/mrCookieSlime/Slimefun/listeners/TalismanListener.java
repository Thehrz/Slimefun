package me.mrCookieSlime.Slimefun.listeners;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.Talisman;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TalismanListener implements Listener {
    public TalismanListener(final SlimefunStartup plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamageGet(final EntityDamageEvent e) {
        if (!e.isCancelled()) {
            if (e instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) e).getDamager() instanceof Player && SlimefunStartup.chance(100, 45) && SlimefunManager.isItemSimiliar(((Player) ((EntityDamageByEntityEvent) e).getDamager()).getInventory().getItemInMainHand(), SlimefunItem.getItem("BLADE_OF_VAMPIRES"), true)) {
                ((Player) ((EntityDamageByEntityEvent) e).getDamager()).playSound(((EntityDamageByEntityEvent) e).getDamager().getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.7f, 0.7f);
                ((Player) ((EntityDamageByEntityEvent) e).getDamager()).addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 1));
            }
            if (e.getEntity() instanceof Player && !e.isCancelled()) {
                if (e.getCause() == EntityDamageEvent.DamageCause.LAVA) {
                    Talisman.checkFor(e, SlimefunItem.getByID("LAVA_TALISMAN"));
                }
                if (e.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
                    Talisman.checkFor(e, SlimefunItem.getByID("WATER_TALISMAN"));
                }
                if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    Talisman.checkFor(e, SlimefunItem.getByID("ANGEL_TALISMAN"));
                }
                if (e.getCause() == EntityDamageEvent.DamageCause.FIRE) {
                    Talisman.checkFor(e, SlimefunItem.getByID("FIRE_TALISMAN"));
                }
                if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    Talisman.checkFor(e, SlimefunItem.getByID("WARRIOR_TALISMAN"));
                }
                if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    Talisman.checkFor(e, SlimefunItem.getByID("KNIGHT_TALISMAN"));
                }
                if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE && Talisman.checkFor(e, SlimefunItem.getByID("WHIRLWIND_TALISMAN")) && ((EntityDamageByEntityEvent) e).getDamager() instanceof Projectile) {
                    final Vector direction = ((Player) e.getEntity()).getEyeLocation().getDirection().multiply(2.0);
                    final Projectile projectile = (Projectile) e.getEntity().getWorld().spawnEntity(((LivingEntity) e.getEntity()).getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), ((EntityDamageByEntityEvent) e).getDamager().getType());
                    projectile.setVelocity(direction);
                    ((EntityDamageByEntityEvent) e).getDamager().remove();
                }
            }
        }
    }

    @EventHandler
    public void onItemBreak(final PlayerItemBreakEvent e) {
        if (Talisman.checkFor(e, SlimefunItem.getByID("ANVIL_TALISMAN"))) {
            e.getBrokenItem().setAmount(1);
        }
    }

    @EventHandler
    public void onSprint(final PlayerToggleSprintEvent e) {
        if (e.isSprinting()) {
            Talisman.checkFor(e, SlimefunItem.getByID("TRAVELLER_TALISMAN"));
        }
    }

    @EventHandler
    public void onEnchant(final EnchantItemEvent e) {
        if (Talisman.checkFor(e, SlimefunItem.getByID("MAGICIAN_TALISMAN"))) {
            final List<String> enchantments = new ArrayList<>();
            for (final Enchantment en : Enchantment.values()) {
                for (int i = 1; i <= en.getMaxLevel(); ++i) {
                    if ((boolean) Slimefun.getItemValue("MAGICIAN_TALISMAN", "allow-enchantments." + en.getName() + ".level." + i) && en.canEnchantItem(e.getItem())) {
                        enchantments.add(en.getName() + "-" + i);
                    }
                }
            }
            final String enchant = enchantments.get(SlimefunStartup.randomize(enchantments.size()));
            e.getEnchantsToAdd().put(Enchantment.getByName(enchant.split("-")[0]), Integer.parseInt(enchant.split("-")[1]));
        }
        if (!e.getEnchantsToAdd().containsKey(Enchantment.SILK_TOUCH) && Enchantment.LOOT_BONUS_BLOCKS.canEnchantItem(e.getItem()) && Talisman.checkFor(e, SlimefunItem.getByID("WIZARD_TALISMAN"))) {
            e.getEnchantsToAdd().remove(Enchantment.LOOT_BONUS_BLOCKS);
            final Set<Enchantment> enchantments2 = e.getEnchantsToAdd().keySet();
            for (final Enchantment en2 : enchantments2) {
                if (SlimefunStartup.chance(100, 40)) {
                    e.getEnchantsToAdd().put(en2, SlimefunStartup.randomize(3) + 1);
                }
            }
            e.getItem().addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, SlimefunStartup.randomize(3) + 3);
        }
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent e) {
        List<ItemStack> drops = new ArrayList<>();
        final ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        int fortune = 1;
        if (item != null) {
            if (item.getEnchantments().containsKey(Enchantment.LOOT_BONUS_BLOCKS) && !item.getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
                fortune = SlimefunStartup.randomize(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) + 2) - 1;
                if (fortune <= 0) {
                    fortune = 1;
                }
                fortune = ((e.getBlock().getType() == Material.LAPIS_ORE) ? (4 + SlimefunStartup.randomize(5)) : 1) * (fortune + 1);
            }
            if (!item.getEnchantments().containsKey(Enchantment.SILK_TOUCH) && e.getBlock().getType().toString().endsWith("_ORE") && Talisman.checkFor(e, SlimefunItem.getByID("MINER_TALISMAN"))) {
                if (drops.isEmpty()) {
                    drops = (List<ItemStack>) e.getBlock().getDrops();
                }
                for (final ItemStack drop : new ArrayList<>(drops)) {
                    if (!drop.getType().isBlock()) {
                        drops.add(new CustomItem(drop, fortune * 2));
                    }
                }
            }
        }
    }
}
