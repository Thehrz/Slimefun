package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public abstract class BowShootHandler
        extends ItemHandler {
    public abstract boolean onHit(EntityDamageByEntityEvent paramEntityDamageByEntityEvent, LivingEntity paramLivingEntity);

    @Override
    public String toCodename() {
        return "BowShootHandler";
    }
}


