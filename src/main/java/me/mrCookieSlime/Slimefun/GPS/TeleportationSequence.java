package me.mrCookieSlime.Slimefun.GPS;

import me.mrCookieSlime.CSCoreLibPlugin.general.Particles.MC_1_8.ParticleEffect;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.TitleBuilder;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class TeleportationSequence {
    public static final Set<UUID> players = new HashSet<>();

    public static void start(UUID uuid, int complexity, Location source, Location destination, boolean resistance) {
        players.add(uuid);

        updateProgress(uuid, getSpeed(complexity, source, destination), 1, source, destination, resistance);
    }

    public static int getSpeed(int complexity, Location source, Location destination) {
        int speed = complexity / 200;
        if (speed > 50) speed = 50;
        speed -= distance(source, destination) / 200;

        return Math.max(speed, 1);
    }

    private static int distance(Location source, Location destination) {
        if (source.getWorld().getName().equals(destination.getWorld().getName())) {
            int distance = (int) source.distance(destination);
            return Math.min(distance, 8000);
        }
        return 8000;
    }

    private static boolean isValid(Player p, Location source) {
        if (p == null) return false;
        return !(p.getLocation().distance(source) > 1.4D);
    }

    private static void cancel(UUID uuid, Player p) {
        players.remove(uuid);
        if (p != null) {
            try {
                TitleBuilder title = (TitleBuilder) (new TitleBuilder(20, 60, 20)).addText(ChatColor.translateAlternateColorCodes('&', "&4传送取消"));
                TitleBuilder subtitle = (TitleBuilder) (new TitleBuilder(20, 60, 20)).addText(ChatColor.translateAlternateColorCodes('&', "&40%"));

                title.send(TitleBuilder.TitleType.TITLE, p);
                subtitle.send(TitleBuilder.TitleType.SUBTITLE, p);
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
    }

    private static void updateProgress(final UUID uuid, final int speed, final int progress, final Location source, final Location destination, final boolean resistance) {
        Player p = Bukkit.getPlayer(uuid);
        if (isValid(p, source)) {
            try {
                if (progress > 99) {
                    TitleBuilder title = (TitleBuilder) (new TitleBuilder(20, 60, 20)).addText(ChatColor.translateAlternateColorCodes('&', "&3成功传送!"));
                    TitleBuilder subtitle = (TitleBuilder) (new TitleBuilder(20, 60, 20)).addText(ChatColor.translateAlternateColorCodes('&', "&b100%"));

                    title.send(TitleBuilder.TitleType.TITLE, p);
                    subtitle.send(TitleBuilder.TitleType.SUBTITLE, p);

                    p.teleport(destination);

                    if (resistance) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 20));
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l你获得了30s的无敌状态!"));
                    }

                    ParticleEffect.PORTAL.display(new Location(destination.getWorld(), destination.getX(), destination.getY() + 1.0D, destination.getZ()), 0.2F, 0.8F, 0.2F, 1.0F, progress * 2);
                    destination.getWorld().playSound(destination, Sound.ENTITY_BLAZE_DEATH, 2.0F, 1.4F);
                    players.remove(uuid);
                } else {

                    TitleBuilder title = (TitleBuilder) (new TitleBuilder(0, 60, 0)).addText(ChatColor.translateAlternateColorCodes('&', "&3传送中..."));
                    TitleBuilder subtitle = (TitleBuilder) (new TitleBuilder(0, 60, 0)).addText(ChatColor.translateAlternateColorCodes('&', "&b" + progress + "%"));

                    title.send(TitleBuilder.TitleType.TITLE, p);
                    subtitle.send(TitleBuilder.TitleType.SUBTITLE, p);

                    ParticleEffect.PORTAL.display(source, 0.2F, 0.8F, 0.2F, 1.0F, progress * 2);
                    source.getWorld().playSound(source, Sound.UI_BUTTON_CLICK, 1.7F, 0.6F);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> TeleportationSequence.updateProgress(uuid, speed, progress + speed, source, destination, resistance), 10L);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            cancel(uuid, p);
        }
    }
}


