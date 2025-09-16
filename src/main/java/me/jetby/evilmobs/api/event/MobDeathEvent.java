package me.jetby.evilmobs.api.event;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class MobDeathEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final String id;
    private final Entity entity;
    private final Player killer;

    private boolean cancelled = false;

    public MobDeathEvent(String id, Entity entity, Player killer) {
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
