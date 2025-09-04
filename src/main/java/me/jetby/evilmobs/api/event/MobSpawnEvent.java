package me.jetby.evilmobs.api.event;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MobSpawnEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private final String id;
    @Getter private final LivingEntity entity;

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
