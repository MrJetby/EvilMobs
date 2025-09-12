package me.jetby.evilmobs.tools.actions.impl.mob.abillities;

import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.tools.actions.Action;
import me.jetby.evilmobs.tools.LocationHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class Teleport implements Action {

    private final Random random = new Random();

    @Override
    public void execute(@Nullable Player player, @NotNull String context, @Nullable Entity entity, @Nullable Mob mob) {
        if (entity == null) return;

        Location targetLocation = parseContext(context, entity, player);
        if (targetLocation != null) {
            entity.teleport(targetLocation);
        }
    }

    private Location parseContext(@NotNull String context, @NotNull Entity entity, @Nullable Player player) {
        // Handle placeholders first
        if ("%closest_player%".equalsIgnoreCase(context)) {
            return getClosestPlayer(entity);
        }
        if (context.startsWith("%rand_player_near_")) {
            try {
                int radius = Integer.parseInt(context.replaceAll("%rand_player_near_(\\d+)%", "$1"));
                return getRandomPlayerNearby(entity, radius);
            } catch (NumberFormatException ignored) {}
        }

        // Fallback: deserialize a location string
        return LocationHandler.deserialize(context);
    }

    private Location getClosestPlayer(Entity entity) {
        Player closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Player p : entity.getWorld().getPlayers()) {
            double dist = p.getLocation().distanceSquared(entity.getLocation());
            if (dist < minDistance) {
                minDistance = dist;
                closest = p;
            }
        }

        return closest != null ? closest.getLocation() : null;
    }

    private Location getRandomPlayerNearby(Entity entity, int radius) {
        List<Player> players = entity.getWorld().getPlayers();
        players.removeIf(p -> p.getLocation().distanceSquared(entity.getLocation()) > radius * radius);

        if (players.isEmpty()) return null;

        Player target = players.get(random.nextInt(players.size()));
        return target.getLocation();
    }
}
