package me.jetby.evilmobs.actions;

import me.jetby.evilmobs.records.Mob;
import me.jetby.treex.actions.Action;
import me.jetby.treex.actions.ActionContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class Drop implements Action {

    @Override
    public void execute(@NotNull ActionContext ctx) {
        String context = ctx.get("message", String.class);
        Entity entity = ctx.get("entity", Entity.class);
        Mob mob = ctx.get("mob", Mob.class);
        if (context == null || entity == null || mob == null) return;

        Location location = entity.getLocation().clone();
        location.add(mob.dropParticle().offsetX(), mob.dropParticle().offsetY(), mob.dropParticle().offsetZ());
        DropManager.dropItem(Integer.parseInt(context), mob, location);
    }
}
