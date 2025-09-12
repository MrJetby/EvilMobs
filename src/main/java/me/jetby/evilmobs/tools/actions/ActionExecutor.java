package me.jetby.evilmobs.tools.actions;

import lombok.experimental.UtilityClass;
import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.tools.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import me.jetby.evilmobs.EvilMobs;

import java.util.List;
import java.util.Map;

@UtilityClass
public class ActionExecutor {

    public void execute(Player player,
                        List<ActionRegistry.ActionEntry> actions,
                        Entity entity,
                        Mob mob) {
        executeSequential(actions, 0, player, entity, mob);
    }

    private void executeSequential(List<ActionRegistry.ActionEntry> actions, int startIndex, Player player, Entity entity, Mob mob) {
        for (int i = startIndex; i < actions.size(); i++) {
            ActionRegistry.ActionEntry entry = actions.get(i);
            String c = TextUtil.setPapi(player, TextUtil.colorize(entry.context()));
            ActionType type = entry.type();

            if (type == ActionType.DELAY) {
                try {
                    int delayTicks = Integer.parseInt(c);
                    int finalI = i;
                    Bukkit.getScheduler().runTaskLater(EvilMobs.getInstance(), () ->
                            executeSequential(actions, finalI + 1, player, entity, mob), delayTicks);

                    return;
                } catch (NumberFormatException e) {
                    continue;
                }
            } else {
                type.getAction().execute(player, c, entity, mob);
            }
        }
    }
}