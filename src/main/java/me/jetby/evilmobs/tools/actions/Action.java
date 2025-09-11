package me.jetby.evilmobs.tools.actions;

import me.jetby.evilmobs.records.Mob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Action {
    void execute(@Nullable Player player, @NotNull String context, @Nullable Entity entity, @Nullable Mob mob);
}
