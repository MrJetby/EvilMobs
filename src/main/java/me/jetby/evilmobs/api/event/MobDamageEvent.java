package me.jetby.evilmobs.api.event;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MobDamageEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

     private final String id;
     private final Entity entity;
     private final Entity damager;

    public MobDamageEvent(String id, Entity entity, Entity damager) {
        this.id = id;
        this.entity = entity;
        this.damager = damager;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getId() {
        return id;
    }

    public Entity getEntity() {
        return entity;
    }

    public Entity getDamager() {
        return damager;
    }
}
