package me.jetby.evilmobs.tools.actions.impl.standard;

import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.tools.Logger;
import me.jetby.evilmobs.tools.actions.Action;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Delay implements Action {
    @Override
    public void execute(@Nullable Player player, @NotNull String context, @Nullable Entity entity, @Nullable Mob mob) {
        try {
            int delayTicks = Integer.parseInt(context.trim());
            Bukkit.getScheduler().runTaskLater(EvilMobs.getInstance(), () -> {
            }, delayTicks);
        } catch (NumberFormatException e) {
            Logger.error(e.getMessage());
        }
    }
}