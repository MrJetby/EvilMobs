package me.jetby.evilmobs.actions.entity;

import me.jetby.evilmobs.records.Mob;
import me.jetby.treex.actions.Action;
import me.jetby.treex.actions.ActionContext;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class SetAge implements Action {
    @Override
    public void execute(@NotNull ActionContext ctx) {
        Entity entity = ctx.get("entity", Entity.class);
        Mob mob = ctx.get("mob", Mob.class);
        String context = ctx.get("message", String.class);

        if (entity == null || mob==null || context == null) return;

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
