package me.jetby.evilmobs.actions.bossBar;

import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.tools.MiniBar;
import me.jetby.treex.actions.Action;
import me.jetby.treex.actions.ActionContext;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ShowBossBarNear implements Action {
    @Override
    public void execute(@NotNull ActionContext ctx) {
        Entity entity = ctx.get("entity", Entity.class);
        Mob mob = ctx.get("mob", Mob.class);
        String context = ctx.get("message", String.class);

        if (entity == null || mob == null || context == null) return;

        String[] args = context.split(" ");
        String bossbarId = args[0];
        int radius = Integer.parseInt(args[1]);

        MiniBar.show(bossbarId, entity, radius);


    }
}
