package me.jetby.evilmobs.tools.actions.impl.mob.task;

import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.tools.MiniTask;
import me.jetby.evilmobs.tools.actions.Action;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TaskStop implements Action {


    @Override
    public void execute(@Nullable Player player, @NotNull String context, @Nullable Entity entity, @Nullable Mob mob) {

        if (mob == null) return;
        if (entity == null) return;

        MiniTask miniTask = EvilMobs.getInstance().getTasks().get(entity.getUniqueId()).get(context);
        if (miniTask==null) return;
        miniTask.cancel();

    }
}
