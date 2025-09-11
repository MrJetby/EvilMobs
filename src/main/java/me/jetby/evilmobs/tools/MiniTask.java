package me.jetby.evilmobs.tools;

import lombok.RequiredArgsConstructor;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.tools.actions.ActionExecutor;
import me.jetby.evilmobs.tools.actions.ActionRegistry;
import org.bukkit.Bukkit;

import java.util.List;

@RequiredArgsConstructor
public class MiniTask {

    private final int delay;
    private final int period;
    private final List<String> actions;

    int taskId;

    public void run() {
        taskId = Bukkit.getScheduler().runTaskTimer(EvilMobs.getInstance(), () -> {

            ActionExecutor.execute(null, ActionRegistry.transform(actions), null, null);

        }, delay, period).getTaskId();
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(taskId);
    }
}
