package me.jetby.evilmobs.tools.actions.impl.mob.bossBar;

import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.tools.MiniBar;
import me.jetby.evilmobs.tools.actions.Action;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeleteBossBar implements Action {
    @Override
    public void execute(@Nullable Player player, @NotNull String context, @Nullable Entity entity, @Nullable Mob mob) {
        if (entity == null) return;
        MiniBar.deleteBossBar(context, entity);
    }
}
