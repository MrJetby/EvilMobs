package me.jetby.evilmobs.actions.bossBar;

import me.jetby.evilmobs.tools.MiniBar;
import me.jetby.treex.actions.Action;
import me.jetby.treex.actions.ActionContext;
import org.jetbrains.annotations.NotNull;

public class DeleteBossBar implements Action {
    @Override
    public void execute(@NotNull ActionContext ctx) {
        String context = ctx.get("message", String.class);
        MiniBar.deleteBossBar(context);
    }
}
