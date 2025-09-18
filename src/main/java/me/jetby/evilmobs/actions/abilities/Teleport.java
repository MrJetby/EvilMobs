package me.jetby.evilmobs.actions.abilities;

import me.jetby.treex.actions.Action;
import me.jetby.treex.actions.ActionContext;
import me.jetby.treex.bukkit.LocationHandler;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class Teleport implements Action {

    private final Random random = new Random();


    private Location parseContext(@NotNull String context, @NotNull Entity entity, @Nullable Player player) {

        if ("%closest_player%".equalsIgnoreCase(context)) {
            return getClosestPlayer(entity);
        }
        if (context.startsWith("%rand_player_near_")) {
            try {
                int radius = Integer.parseInt(context.replaceAll("%rand_player_near_(\\d+)%", "$1"));
                return getRandomPlayerNearby(entity, radius);
            } catch (NumberFormatException ignored) {
            }
        }

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

    @Override
    public void execute(@NotNull ActionContext ctx) {
        Entity entity = ctx.get("entity", Entity.class);
        Player player = ctx.getPlayer();
        String context = ctx.get("message", String.class);

        if (entity == null || context == null) return;

        Location targetLocation = parseContext(context, entity, player);
        if (targetLocation != null) {
            entity.teleport(targetLocation);
        }
    }
}
