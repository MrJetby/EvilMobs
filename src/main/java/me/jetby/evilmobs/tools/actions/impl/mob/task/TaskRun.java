package me.jetby.evilmobs.tools.actions.impl.mob.task;

import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.records.Task;
import me.jetby.evilmobs.tools.MiniTask;
import me.jetby.evilmobs.tools.actions.Action;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TaskRun implements Action {


    @Override
    public void execute(@Nullable Player player, @NotNull String context, @Nullable Entity entity, @Nullable Mob mob) {

        if (mob == null) return;
        if (entity == null) return;

        Task task = mob.tasks().get(context);
        if (task == null) return;

        MiniTask miniTask = new MiniTask(task.delay(), task.period(), task.actions());

        miniTask.run();

        Map<String, MiniTask> tasks = new HashMap<>(EvilMobs.getInstance().getTasks().get(entity.getUniqueId()));

        tasks.put(context, miniTask);
        EvilMobs.getInstance().getTasks().put(entity.getUniqueId(), tasks);


    }
}
