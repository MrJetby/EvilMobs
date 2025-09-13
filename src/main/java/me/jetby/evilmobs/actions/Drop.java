package me.jetby.evilmobs.actions;

import me.jetby.treex.actions.Action;
import me.jetby.treex.actions.ActionContext;
import org.jetbrains.annotations.NotNull;

public class Drop implements Action {
    @Override
    public void execute(@NotNull ActionContext ctx) {
        String context = ctx.get("message", String.class);

        if (context == null) return;

    }
}
