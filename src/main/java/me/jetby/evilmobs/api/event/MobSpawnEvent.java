package me.jetby.evilmobs.api.event;

import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class MobSpawnEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final String id;
    private final LivingEntity entity;

    private boolean cancelled = false;

    public MobSpawnEvent(String id, LivingEntity entity) {
        this.id = id;
        this.entity = entity;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
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
