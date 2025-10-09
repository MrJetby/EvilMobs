package me.jetby.evilmobs.actions.minions;

import me.jetby.evilmobs.Maps;
import me.jetby.evilmobs.records.Mob;
import me.jetby.treex.actions.Action;
import me.jetby.treex.actions.ActionContext;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;


public class SpawnAsMinion implements Action {

    @Override
    public void execute(@NotNull ActionContext ctx) {
        Entity entity = ctx.get("entity", Entity.class);
        Mob mob = ctx.get("mob", Mob.class);
        String context = ctx.get("message", String.class);

        if (entity == null || mob == null || context == null) return;

        Maps.mobCreators.get(mob.id()).spawnMinion(context, entity.getLocation());
    }
}
