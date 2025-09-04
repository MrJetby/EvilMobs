package me.jetby.evilmobs.tools.actions.impl.standard;

import me.jetby.evilmobs.tools.actions.Action;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Player implements Action {
    @Override
    public void execute(@Nullable org.bukkit.entity.Player player, @NotNull String context, @Nullable Entity entity) {
        if (player != null) {
            player.chat(context);
        }
    }
}
