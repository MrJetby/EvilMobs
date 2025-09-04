package me.jetby.evilmobs.api.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MobSpawnEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final String id;
    private final LivingEntity entity;

    public MobSpawnEvent(String id, LivingEntity entity) {
        this.id = id;
        this.entity = entity;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
