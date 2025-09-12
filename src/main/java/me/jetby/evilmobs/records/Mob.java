package me.jetby.evilmobs.records;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Map;

public record Mob(
        String id,
        Location spawnlocation,
        boolean nameVisible,
        String name,
        List<ArmorItem> armorItems,
        EntityType entityType,
        int health,
        boolean ai,
        boolean glow,
        boolean canPickupItems,
        boolean visualFire,
        boolean isBaby,
        List<Phases> phases,
        Map<String, Task> tasks,
        Map<String, Bar> bossBars,
        List<String> onSpawnActions,
        List<String> onHitActions,
        List<String> onDeathActions


) {
}
