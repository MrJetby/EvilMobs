package me.jetby.evilmobs.api.event;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class MobDamageEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

     private final String id;
     private final Entity entity;
     private final Entity damager;
     private final double damage;
     private final EntityDamageEvent.DamageCause cause;

    private boolean cancelled = false;

    public MobDamageEvent(String id, Entity entity, Entity damager, double damage, EntityDamageEvent.DamageCause cause) {
        this.id = id;
        this.entity = entity;
        this.damager = damager;
        this.damage = damage;
        this.cause = cause;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
