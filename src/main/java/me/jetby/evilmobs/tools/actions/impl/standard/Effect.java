package me.jetby.evilmobs.tools.actions.impl.standard;

import me.jetby.evilmobs.tools.Logger;
import me.jetby.evilmobs.tools.actions.Action;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Effect implements Action {
    @Override
    public void execute(@Nullable Player player, @NotNull String context, @Nullable Entity entity) {
        if (player == null) return;

        var args = context.split(";");
        PotionEffectType potionEffectType;
        try {
            if (args.length >= 1) {
                potionEffectType = PotionEffectType.getByName(args[0].toUpperCase());
            } else {
                Logger.warn("PotionEffectType is null");
                return;
            }
        } catch (IllegalArgumentException e) {
            Logger.warn("PotionEffectType " + args[0] + " is not available");
            return;
        }

        try {
            int duration = args.length > 1 ? Integer.parseInt(args[1]) : 0;
            int strength = args.length > 2 ? Integer.parseInt(args[2]) : 1;
            player.addPotionEffect(new PotionEffect(potionEffectType, duration * 20, strength));
        } catch (NumberFormatException e) {
            Logger.warn("Strength and duration must be a number");
        }

    }
}