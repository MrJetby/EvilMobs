package me.jetby.evilmobs.actions.entity;

import me.jetby.evilmobs.records.Mob;
import me.jetby.treex.actions.Action;
import me.jetby.treex.actions.ActionContext;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public class SetTarget implements Action {

    @Override
    public void execute(@NotNull ActionContext ctx) {
        Player player = ctx.getPlayer();
        Entity entity = ctx.get("entity", Entity.class);
        Mob mob = ctx.get("mob", Mob.class);
        String context = ctx.get("message", String.class);

        if (entity == null || mob == null || context == null) return;
        if (!(entity instanceof Creature creature)) return;
        if (!(creature instanceof Player)) return;

        Player target = null;

        if (context.equalsIgnoreCase("%damager%")) {
            target = player;
        } else if (context.equalsIgnoreCase("%closest_player%")) {
            target = creature.getWorld().getPlayers().stream()
                    .filter(p -> !p.isDead() && p.getWorld().equals(creature.getWorld()))
                    .min(Comparator.comparingDouble(p -> p.getLocation().distanceSquared(creature.getLocation())))
                    .orElse(null);
        } else if (context.startsWith("%rand_player_near_")) {
            try {
                int radius = Integer.parseInt(
                        context.replace("%rand_player_near_", "").replace("%", "")
                );

                List<Player> nearby = creature.getWorld().getPlayers().stream()
                        .filter(p -> !p.isDead()
                                && p.getLocation().distanceSquared(creature.getLocation()) <= radius * radius)
                        .toList();

                if (!nearby.isEmpty()) {
                    target = nearby.get((int) (Math.random() * nearby.size()));
                }
            } catch (NumberFormatException ignored) {
            }
        }

        if (target != null) {
            creature.setTarget(target);
        }
    }
}
