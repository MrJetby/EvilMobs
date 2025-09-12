package me.jetby.evilmobs.tools.actions;

import lombok.experimental.UtilityClass;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.tools.Logger;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@UtilityClass
public class ActionRegistry {
    private final Pattern ACTION_PATTERN = Pattern.compile("\\[(\\S+)] ?(.*)");

    public List<ActionEntry> transform(List<String> settings) {
        List<ActionEntry> actions = new ArrayList<>();
        for (String s : settings) {
            var matcher = ACTION_PATTERN.matcher(s);
            if (!matcher.matches()) {
                Logger.warn("Illegal action pattern " + s);
                continue;
            }

            var typeName = matcher.group(1).toUpperCase();
            var type = ActionType.getType(typeName);
            if (type == null) {
                Logger.warn("ActionType " + typeName + " is not available!");
                continue;
            }
            var context = matcher.group(2).trim();
            actions.add(new ActionEntry(type, context));
        }
        return actions;
    }
    public record ActionEntry(ActionType type, String context) {}
}
