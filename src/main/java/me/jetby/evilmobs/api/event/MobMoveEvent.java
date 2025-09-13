package me.jetby.evilmobs.api.event;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class MobMoveEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final String id;
    private final Entity entity;
    private final Location from;
    private final Location to;
    private final boolean hasChangedBlock;
    private final boolean hasChangedOrientation;
    private final boolean hasChangedPosition;
    private final boolean hasExplicitlyChangedBlock;
    private final boolean hasExplicitlyChangedPosition;

    private boolean cancelled = false;

    public MobMoveEvent(String id, Entity entity, Location from, Location location, boolean hasChangedBlock, boolean hasChangedOrientation, boolean hasChangedPosition, boolean hasExplicitlyChangedBlock, boolean hasExplicitlyChangedPosition) {
        this.id = id;
        this.entity = entity;
        this.from = from;
        this.to = location;
        this.hasChangedBlock = hasChangedBlock;
        this.hasChangedOrientation = hasChangedOrientation;
        this.hasChangedPosition = hasChangedPosition;
        this.hasExplicitlyChangedBlock = hasExplicitlyChangedBlock;
        this.hasExplicitlyChangedPosition = hasExplicitlyChangedPosition;
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
