package me.jetby.evilmobs.tools.actions.impl.mob.abillities;

import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.tools.actions.Action;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class Lightning implements Action {

    Random random = new Random();

    @Override
    public void execute(@Nullable Player player, @NotNull String context, @Nullable Entity entity, @Nullable Mob mob) {
        if (entity == null) return;


        var args = context.split(";");
        var radius = args.length > 0 ? Integer.parseInt(args[0]) : 0;
        boolean visual = args.length > 1 && Boolean.parseBoolean(args[1]);

        if (radius == 0) {
            if (visual) {
                entity.getWorld().strikeLightningEffect(entity.getLocation());
            } else {
                entity.getWorld().strikeLightning(entity.getLocation());}
        } else {

            entity.getWorld().strikeLightningEffect(getRandomLocation(entity.getLocation(), radius));
        }

    }

    public Location getRandomLocation(Location center, double radius) {
        World world = center.getWorld();

        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = random.nextDouble() * radius;

        double x = center.getX() + distance * Math.cos(angle);
        double z = center.getZ() + distance * Math.sin(angle);
        double y = center.getY();

        return new Location(world, x, y, z);
    }
}
