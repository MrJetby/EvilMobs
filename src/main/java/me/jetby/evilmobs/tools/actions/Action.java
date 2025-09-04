package me.jetby.evilmobs.tools.actions;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Action {
    void execute(@Nullable Player player, @NotNull String context, @Nullable Entity entity);
}
