package me.jetby.evilmobs.tools.actions.impl.standard;

import me.jetby.evilmobs.tools.Logger;
import me.jetby.evilmobs.tools.actions.Action;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BroadcastSound implements Action {
    @Override
    public void execute(@Nullable Player player, @NotNull String context, @Nullable Entity entity) {
        var args = context.split(";");
        Sound sound;

        try {
            if (args.length >= 1) {
                sound = Sound.valueOf(args[0].toUpperCase());
            } else {
                Logger.warn("Sound is null");
                return;
            }
        } catch (IllegalArgumentException e) {
            Logger.warn("Sound " + args[0] + " is not available");
            return;
        }

        float volume = 0;
        float pitch = 0;

        try {
            volume = args.length > 1 ? Float.parseFloat(args[1]) : 1;
            pitch = args.length > 2 ? Float.parseFloat(args[2]) : 1;
        } catch (NumberFormatException e) {
            Logger.warn("Volume and pitch must be a number");
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), sound, volume, pitch);
        }
    }
}
