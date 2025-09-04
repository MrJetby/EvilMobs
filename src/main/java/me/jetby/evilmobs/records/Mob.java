package me.jetby.evilmobs.records;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.List;

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
        Phases phases,
        BossBars bossBars,
        List<String> onSpawnActions,
        List<String> onDeathActions



) {
}
