package me.jetby.evilmobs.api.event;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MobDeathEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final String id;
    private final Entity entity;
    private final Entity killer;

    public MobDeathEvent(String id, Entity entity, Entity killer) {
        this.id = id;
        this.entity = entity;
        this.killer = killer;
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

    public Entity getKiller() {
        return killer;
    }
}
