package me.jetby.evilmobs.actions.abilities;

import me.jetby.treex.actions.Action;
import me.jetby.treex.actions.ActionContext;
import me.jetby.treex.Treex;
import me.jetby.treex.tools.LogInitialize;
import me.jetby.treex.tools.log.Logger;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class EffectNear implements Action {
    private static final Logger logger = LogInitialize.getLogger(Treex.class);

    @Override
    public void execute(@NotNull ActionContext ctx) {
        String message = ctx.get("message", String.class);
        Entity entity = ctx.get("entity", Entity.class);
        Player source = ctx.getPlayer();

        if (message == null) return;
        if (entity == null && source == null) return;

        var args = message.split(";");
        PotionEffectType potionEffectType;
        try {
            if (args.length >= 1) {
                potionEffectType = PotionEffectType.getByName(args[0].toUpperCase());
            } else {
                logger.warn("PotionEffectType is null");
                return;
            }
        } catch (IllegalArgumentException e) {
            logger.warn("PotionEffectType " + args[0] + " is not available");
            return;
        }

        try {
            int duration = args.length > 1 ? Integer.parseInt(args[1]) : 0;
            int strength = args.length > 2 ? Integer.parseInt(args[2]) : 1;
            int radius   = args.length > 3 ? Integer.parseInt(args[3]) : 10;

            Entity center = (entity != null ? entity : source);

            center.getWorld().getPlayers().stream()
                    .filter(p -> !p.isDead() &&
                            p.getLocation().distanceSquared(center.getLocation()) <= radius * radius)
                    .forEach(p -> p.addPotionEffect(new PotionEffect(potionEffectType, duration * 20, strength)));

        } catch (NumberFormatException e) {
            logger.warn("Duration, strength and radius must be numbers");
        }
    }
}
