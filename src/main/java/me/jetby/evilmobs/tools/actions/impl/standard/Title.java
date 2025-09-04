package me.jetby.evilmobs.tools.actions.impl.standard;

import me.jetby.evilmobs.tools.actions.Action;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class Title implements Action {
    @Override
    public void execute(@Nullable Player player, @NotNull String context, @Nullable Entity entity) {
        if (player == null) return;

        var args = context.split(";");
        var title = args.length > 0 ? args[0] : "";
        var subTitle = args.length > 1 ? args[1] : "";
        int fadeIn = (args.length > 2 ? Integer.parseInt(args[2]) : 10) * 50;
        int stayIn = (args.length > 3 ? Integer.parseInt(args[3]) : 70) * 50;
        int fadeOut = (args.length > 4 ? Integer.parseInt(args[4]) : 20) * 50;

        player.sendTitle(title, subTitle, fadeIn, stayIn, fadeOut);

    }
}
