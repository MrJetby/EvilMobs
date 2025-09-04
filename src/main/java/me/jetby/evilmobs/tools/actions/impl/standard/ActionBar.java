package me.jetby.evilmobs.tools.actions.impl.standard;

import me.jetby.evilmobs.tools.actions.Action;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActionBar implements Action {
    @Override
    public void execute(@Nullable Player player, @NotNull String context, @Nullable Entity entity) {
        if (player != null)
            player.sendActionBar(context);
    }
}