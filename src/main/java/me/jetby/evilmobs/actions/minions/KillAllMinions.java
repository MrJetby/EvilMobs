package me.jetby.evilmobs.actions.minions;

import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.Maps;
import me.jetby.evilmobs.MobCreator;
import me.jetby.evilmobs.locale.Lang;
import me.jetby.treex.actions.Action;
import me.jetby.treex.actions.ActionContext;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import static me.jetby.evilmobs.EvilMobs.LOGGER;

public class KillAllMinions implements Action {
    @Override
    public void execute(@NotNull ActionContext ctx) {
        Entity entity = ctx.get("entity", Entity.class);
        String context = ctx.get("message", String.class);

        if (entity == null || context == null) return;

        MobCreator mobCreator = Maps.mobCreators.get(context);
        if (mobCreator == null) {
            LOGGER.warn(Lang.getString("mob-creator-not-found").replace("{context}", context));
            return;
        }

        mobCreator.killAllMinions();
    }
}
