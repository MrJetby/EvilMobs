package me.jetby.evilmobs.tools.actions;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

@UtilityClass
public class ActionExecutor {

    public void execute(Player player, Map<ActionType, List<String>> actions, Entity entity) {
        actions.keySet().forEach(type -> {
            var contexts = actions.get(type);
            for (String context : contexts) {
                var c = context;
                type.getAction().execute(player, c, entity);
            }
        });
    }
}
