package me.jetby.evilmobs.tools.actions;

import lombok.experimental.UtilityClass;
import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.tools.TextUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import me.jetby.evilmobs.EvilMobs;

import java.util.List;
import java.util.Map;

@UtilityClass
public class ActionExecutor {

    public void execute(Player player,
                        Map<ActionType, List<String>> actions,
                        Entity entity,
                        Mob mob) {
        List<Map.Entry<ActionType, List<String>>> actionEntries = List.copyOf(actions.entrySet());
        executeActionsSequentially(player, actionEntries, entity, mob, 0);
    }

    private void executeActionsSequentially(Player player,
                                            List<Map.Entry<ActionType, List<String>>> actionEntries,
                                            Entity entity,
                                            Mob mob,
                                            int index) {
        if (index >= actionEntries.size()) {
            return;
        }

        var entry = actionEntries.get(index);
        ActionType type = entry.getKey();
        List<String> contexts = entry.getValue();

        if (type == ActionType.DELAY && !contexts.isEmpty()) {
            try {
                int delay = Integer.parseInt(contexts.get(0).trim());

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        executeActionsSequentially(player, actionEntries, entity, mob, index + 1);
                    }
                }.runTaskLater(EvilMobs.getInstance(), delay);
            } catch (NumberFormatException e) {
                EvilMobs.getInstance().getLogger().warning("Invalid delay duration: " + contexts.get(0));
                executeActionsSequentially(player, actionEntries, entity, mob, index + 1);
            }
        } else {
            for (String context : contexts) {
                var c = TextUtil.setPapi(player, TextUtil.colorize(context));
                type.getAction().execute(player, c, entity, mob);
            }
            executeActionsSequentially(player, actionEntries, entity, mob, index + 1);
        }
    }
}