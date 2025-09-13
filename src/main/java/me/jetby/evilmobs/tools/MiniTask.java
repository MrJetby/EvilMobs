package me.jetby.evilmobs.tools;

import lombok.RequiredArgsConstructor;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.records.Mob;
import me.jetby.treex.actions.ActionContext;
import me.jetby.treex.actions.ActionExecutor;
import me.jetby.treex.actions.ActionRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.List;

@RequiredArgsConstructor
public class MiniTask {

    private final int delay;
    private final int period;
    private final int amount;
    private final List<String> actions;
    private final Entity entity;
    private final Mob mob;

    int taskId;
    int currentAmount = 0;

    public void run() {
        taskId = Bukkit.getScheduler().runTaskTimer(EvilMobs.getInstance(), () -> {

            if (amount!=-1) {
                currentAmount++;
                if (currentAmount > amount) {
                    cancel();
                    return;
                }
            }
            ActionContext ctx = new ActionContext(null);
            ctx.put("mob", mob);
            ctx.put("entity", entity);
            ActionExecutor.execute(ctx, ActionRegistry.transform(actions));

        }, delay, period).getTaskId();
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(taskId);
    }
}
