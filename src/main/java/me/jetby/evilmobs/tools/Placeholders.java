package me.jetby.evilmobs.tools;

import lombok.experimental.UtilityClass;
import me.jetby.evilmobs.records.Mob;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Placeholders {

    public @NotNull List<String> list(List<String> list, Mob mob, LivingEntity boss) {
        List<String> actions = new ArrayList<>();
        for (String action : list) {
            action = text(action, mob, boss);
            actions.add(action);

        }
        return actions;
    }

    public @NotNull String text(String str, Mob mob, LivingEntity boss) {
        return str
                .replace("{x}", String.valueOf(boss.getLocation().getX()))
                .replace("{y}", String.valueOf(boss.getLocation().getY()))
                .replace("{z}", String.valueOf(boss.getLocation().getZ()))
                .replace("{world}", boss.getLocation().getWorld().getName())
                .replace("{prefix}", mob.name())
                .replace("{name}", boss.getName())
                .replace("{health}", String.valueOf(boss.getHealth()))
                .replace("{max_health}", String.valueOf(boss.getMaxHealth()))
                .replace("{uuid}", String.valueOf(boss.getUniqueId()))
                .replace("{mob_id}", mob.id());
    }

}
