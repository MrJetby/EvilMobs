package me.jetby.evilmobs.tools;

import lombok.experimental.UtilityClass;
import me.jetby.evilmobs.records.Mob;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Placeholders {

    public @NotNull List<String> set(List<String> list, Mob mob, LivingEntity boss) {
        List<String> actions = new ArrayList<>();
        for (String action : list) {
            action = Placeholders.set(action, mob, boss);
            actions.add(action);

        }
        return actions;
    }

    public @NotNull String set(String str, Mob mob, LivingEntity boss) {
        int healthPercent = (int) ((boss.getHealth() / boss.getMaxHealth()) * 100);

        return str
                .replace("{x}", String.valueOf(boss.getLocation().getX()))
                .replace("{y}", String.valueOf(boss.getLocation().getY()))
                .replace("{z}", String.valueOf(boss.getLocation().getZ()))
                .replace("{world}", boss.getLocation().getWorld().getName())
                .replace("{prefix}", mob.name())
                .replace("{name}", boss.getName())
                .replace("{health}", String.valueOf((int) boss.getHealth()))
                .replace("{health_percentage}", String.valueOf(healthPercent))
                .replace("{max_health}", String.valueOf(boss.getMaxHealth()))
                .replace("{uuid}", String.valueOf(boss.getUniqueId()))
                .replace("{mob_id}", mob.id());
    }

    public boolean containsAtLeastOne(String str) {
        return str.contains("{x}") ||
                str.contains("{y}") ||
                str.contains("{z}") ||
                str.contains("{world}") ||
                str.contains("{prefix}") ||
                str.contains("{name}") ||
                str.contains("{health}") ||
                str.contains("{health_percentage}") ||
                str.contains("{max_health}") ||
                str.contains("{uuid}") ||
                str.contains("{mob_id}");
    }

}
