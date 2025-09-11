package me.jetby.evilmobs.tools.actions.impl.mob.bossBar;

import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.tools.MiniBar;
import me.jetby.evilmobs.tools.actions.Action;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RemoveBossBar implements Action {
    @Override
    public void execute(@Nullable Player player, @NotNull String context, @Nullable Entity entity, @Nullable Mob mob) {
        if (mob == null) return;

        List<Audience> audiences = new ArrayList<>(Bukkit.getOnlinePlayers());
        MiniBar.remove(context, audiences);

    }
}
