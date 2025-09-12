package me.jetby.evilmobs.tools.actions.impl.mob;

import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.tools.actions.Action;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SetAge implements Action {
    @Override
    public void execute(@Nullable Player player, @NotNull String context, @Nullable Entity entity, @Nullable Mob mob) {
        if (entity==null) return;
        if (mob==null) return;

        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity instanceof Ageable ageable) {
                if (context.equalsIgnoreCase("baby")) {
                    ageable.setBaby();
                } else if (context.equalsIgnoreCase("adult")) {
                    ageable.setAdult();
                }
            }
        }
    }
}
