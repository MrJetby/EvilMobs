package me.jetby.evilmobs.actions.task;

import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.records.Task;
import me.jetby.evilmobs.tools.MiniTask;
import me.jetby.treex.actions.Action;
import me.jetby.treex.actions.ActionContext;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class TaskRun implements Action {


    @Override
    public void execute(@NotNull ActionContext ctx) {
        Entity entity = ctx.get("entity", Entity.class);
        Mob mob = ctx.get("mob", Mob.class);
        String context = ctx.get("message", String.class);

        if (entity == null || mob==null || context == null) return;

        Task task = mob.tasks().get(context);
        if (task == null) return;

        MiniTask miniTask = new MiniTask(task.delay(), task.period(), task.amount(), task.actions(), entity, mob);

        miniTask.run();

        Map<String, MiniTask> tasks = new HashMap<>();
        if (EvilMobs.getInstance().getTasks().get(entity.getUniqueId())!=null) {
            tasks.putAll(EvilMobs.getInstance().getTasks().get(entity.getUniqueId()));
        }

        tasks.put(context, miniTask);
        EvilMobs.getInstance().getTasks().put(entity.getUniqueId(), tasks);


    }
}
