package me.jetby.evilmobs.actions.task;

import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.tools.MiniTask;
import me.jetby.treex.actions.Action;
import me.jetby.treex.actions.ActionContext;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class TaskStop implements Action {


    @Override
    public void execute(@NotNull ActionContext ctx) {
        Entity entity = ctx.get("entity", Entity.class);
        Mob mob = ctx.get("mob", Mob.class);
        String context = ctx.get("message", String.class);

        if (entity == null || mob==null || context == null) return;

        if (EvilMobs.getInstance().getTasks().get(entity.getUniqueId())==null) return;

        MiniTask miniTask = EvilMobs.getInstance().getTasks().get(entity.getUniqueId()).get(context);
        if (miniTask==null) return;

        miniTask.cancel();

    }
}
