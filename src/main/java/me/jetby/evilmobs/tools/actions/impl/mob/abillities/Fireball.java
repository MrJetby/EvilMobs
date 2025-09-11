package me.jetby.evilmobs.tools.actions.impl.mob.abillities;

import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.tools.actions.Action;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Fireball implements Action {


    @Override
    public void execute(@Nullable Player player, @NotNull String context, @Nullable Entity entity, @Nullable Mob mob) {
        if (entity == null) return;

        double radius = Double.parseDouble(context);

        entity.getNearbyEntities(radius, radius, radius).stream()
                .filter(e -> e instanceof Player)
                .findAny().ifPresent(target -> shootFireball(entity.getLocation(), target.getLocation()));

    }

    public void shootFireball(Location from, Location to) {
        World world = from.getWorld();

        Vector direction = to.toVector().subtract(from.toVector()).normalize();

        org.bukkit.entity.Fireball fireball = world.spawn(from, org.bukkit.entity.Fireball.class);

        fireball.setDirection(direction);
        fireball.setYield(0);
        fireball.setIsIncendiary(false);

        fireball.setVelocity(direction.multiply(0.1));
    }
}
