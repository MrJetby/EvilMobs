package me.jetby.evilmobs.actions.bossBar;

import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.tools.MiniBar;
import me.jetby.treex.actions.Action;
import me.jetby.treex.actions.ActionContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RemoveBossBar implements Action {

    @Override
    public void execute(@NotNull ActionContext ctx) {
        Mob mob = ctx.get("mob", Mob.class);
        String context = ctx.get("message", String.class);

        if (mob == null || context == null) return;

        Collection<Player> players = (Collection<Player>) Bukkit.getOnlinePlayers();
        MiniBar.remove(context, players);

    }
}
