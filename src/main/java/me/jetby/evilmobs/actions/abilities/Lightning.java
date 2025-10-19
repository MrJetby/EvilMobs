package me.jetby.evilmobs.actions.abilities;

import me.jetby.treex.actions.Action;
import me.jetby.treex.actions.ActionContext;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Lightning implements Action {

    private static final Pattern PATTERN = Pattern.compile("\\s+");
    final Random random = new Random();

    @Override
    public void execute(@NotNull ActionContext ctx) {
        Entity entity = ctx.get("entity", Entity.class);
        String context = ctx.get("message", String.class);

        if (entity == null || context == null) return;

        Map<String, String> params = parseContext(context);
        double radius = Double.parseDouble(params.getOrDefault("RADIUS", "0.0"));
        boolean visual = Boolean.parseBoolean(params.getOrDefault("VISUAL", "false"));
        String target = params.getOrDefault("TARGET", "");

        Location strikeLocation;

        if (target.equalsIgnoreCase("%closest_player%")) {
            strikeLocation = entity.getNearbyEntities(radius, radius, radius).stream()
                    .filter(e -> e instanceof Player)
                    .map(Entity::getLocation)
                    .min(Comparator.comparingDouble(loc -> loc.distanceSquared(entity.getLocation())))
                    .orElse(null);
        } else if (target.equalsIgnoreCase("%rand_player%")) {
            java.util.List<Player> nearby = entity.getNearbyEntities(radius, radius, radius).stream()
                    .filter(e -> e instanceof Player)
                    .map(e -> (Player) e)
                    .toList();
            if (!nearby.isEmpty()) {
                Player chosen = nearby.get(random.nextInt(nearby.size()));
                strikeLocation = chosen.getLocation();
            } else {
                strikeLocation = null;
            }
        } else {
            strikeLocation = radius > 0 ? getRandomLocation(entity.getLocation(), radius) : entity.getLocation();
        }

        if (strikeLocation != null) {
            if (visual) {
                entity.getWorld().strikeLightningEffect(strikeLocation);
            } else {
                entity.getWorld().strikeLightning(strikeLocation);
            }
        }
    }

    private Map<String, String> parseContext(String context) {
        return Arrays.stream(PATTERN.split(context))
                .filter(s -> s.contains(":"))
                .map(s -> s.split(":"))
                .collect(Collectors.toMap(
                        arr -> arr[0].toUpperCase(),
                        arr -> arr.length > 1 ? arr[1] : ""
                ));
    }

    private Location getRandomLocation(Location center, double radius) {
        World world = center.getWorld();

        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = random.nextDouble() * radius;

        double x = center.getX() + distance * Math.cos(angle);
        double z = center.getZ() + distance * Math.sin(angle);
        double y = center.getY();

        return new Location(world, x, y, z);
    }
}
