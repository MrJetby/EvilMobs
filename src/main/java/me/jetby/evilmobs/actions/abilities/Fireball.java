package me.jetby.evilmobs.actions.abilities;

import me.jetby.treex.actions.Action;
import me.jetby.treex.actions.ActionContext;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class Fireball implements Action {

    @Override
    public void execute(@NotNull ActionContext ctx) {
        Entity entity = ctx.get("entity", Entity.class);
        String context = ctx.get("message", String.class);

        if (entity == null || context == null) return;

        Map<String, String> params = parseContext(context);
        double radius = Double.parseDouble(params.getOrDefault("RADIUS", "10.0"));
        double speed = Double.parseDouble(params.getOrDefault("SPEED", "0.1"));
        boolean explode = Boolean.parseBoolean(params.getOrDefault("EXPLODE", "false"));
        String fromDirection = params.getOrDefault("FROM", "MOB");
        String target = params.getOrDefault("TARGET", "%closest_player%");

        Location from = getFireballOrigin(entity.getLocation(), fromDirection);
        Location to = null;

        if (target.equalsIgnoreCase("%closest_player%")) {
            to = entity.getNearbyEntities(radius, radius, radius).stream()
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
                Player chosen = nearby.get((int) (Math.random() * nearby.size()));
                to = chosen.getLocation();
            }
        } else {
            to = entity.getNearbyEntities(radius, radius, radius).stream()
                    .filter(e -> e instanceof Player)
                    .findAny()
                    .map(Entity::getLocation)
                    .orElse(null);
        }



        if (to != null) {
            shootFireball(from, to, explode, entity, speed);
        }
    }

    private Map<String, String> parseContext(String context) {

        return Arrays.stream(context.split("\\s+"))
                .filter(s -> s.contains(":"))
                .map(s -> s.split(":"))
                .collect(Collectors.toMap(
                        arr -> arr[0].toUpperCase(),
                        arr -> arr.length > 1 ? arr[1] : ""
                ));
    }

    private Location getFireballOrigin(Location entityLoc, String fromParam) {
        Location origin = entityLoc.clone();

        String base = fromParam.toUpperCase();
        Vector offset = new Vector(0, 0, 0);

        if (fromParam.contains("[")) {
            try {
                String offsetPart = fromParam.substring(fromParam.indexOf("[") + 1, fromParam.indexOf("]"));
                String[] parts = offsetPart.split(",");
                if (parts.length == 3) {
                    double x = Double.parseDouble(parts[0]);
                    double y = Double.parseDouble(parts[1]);
                    double z = Double.parseDouble(parts[2]);
                    offset = new Vector(x, y, z);
                }
                base = fromParam.substring(0, fromParam.indexOf("[")).toUpperCase();
            } catch (Exception ignored) {
            }
        }

        switch (base) {
            case "TOP":
                origin.add(0, 10, 0);
                break;
            case "MOB":
            default:
                origin.add(0, 1, 0);
                break;
        }

        origin.add(offset);

        return origin;
    }


    private void shootFireball(Location from, Location to, boolean explode, Entity shooter, double speed) {
        World world = from.getWorld();

        Vector direction = to.toVector().subtract(from.toVector()).normalize();

        org.bukkit.entity.Fireball fireball = world.spawn(from, org.bukkit.entity.Fireball.class);
        fireball.setShooter((ProjectileSource) shooter);

        fireball.setDirection(direction);
        fireball.setYield(explode ? 2 : 0);
        fireball.setIsIncendiary(explode);

        fireball.setVelocity(direction.multiply(speed));
    }
}