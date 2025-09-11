package me.jetby.evilmobs.tools.actions.impl.mob.bossBar;

import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.tools.MiniBar;
import me.jetby.evilmobs.tools.actions.Action;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RemoveBossBarNear implements Action {
    @Override
    public void execute(@Nullable Player player, @NotNull String context, @Nullable Entity entity, @Nullable Mob mob) {
        if (mob == null) return;
        if (entity == null) return;

        String[] args = context.split(" ");
        String bossbarId = args[1];
        int radius = Integer.parseInt(args[2]);

        MiniBar.remove(bossbarId, entity.getLocation(), radius);


    }
}
