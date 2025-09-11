package me.jetby.evilmobs.tools.actions.impl.standard;

import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.tools.Logger;
import me.jetby.evilmobs.tools.actions.Action;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Sound implements Action {
    @Override
    public void execute(@Nullable Player player, @NotNull String context, @Nullable Entity entity, @Nullable Mob mob) {
        if (player == null) return;

        var args = context.split(";");
        org.bukkit.Sound sound;
        try {
            if (args.length >= 1) {
                sound = org.bukkit.Sound.valueOf(args[0].toUpperCase());
            } else {
                Logger.warn("Sound is null");
                return;
            }
        } catch (IllegalArgumentException e) {
            Logger.warn("Sound " + args[0] + " is not available");
            return;
        }

        try {
            float volume = args.length > 1 ? Float.parseFloat(args[1]) : 1;
            float pitch = args.length > 2 ? Float.parseFloat(args[2]) : 1;
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (NumberFormatException e) {
            Logger.warn("Volume and pitch must be a number");
        }
    }
}