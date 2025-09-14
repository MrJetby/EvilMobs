package me.jetby.evilmobs.actions.particles;

import me.jetby.evilmobs.tools.ParticleEffectManager;
import me.jetby.treex.actions.Action;
import me.jetby.treex.actions.ActionContext;
import me.jetby.treex.bukkit.LocationHandler;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SendParticle implements Action {
    @Override
    public void execute(@NotNull ActionContext ctx) {
        String context = ctx.get("message", String.class);
        Entity entity = ctx.get("entity", Entity.class);

        if (context == null) return;

        String[] args = context.split(" ");
        if (args.length==2) {
            Location location = LocationHandler.deserialize(args[1]);
            ParticleEffectManager.playEffect(location, args[0]);

        }

        if (entity==null) return;
        ParticleEffectManager.playEffect(entity.getLocation(), context);

    }

}
