package me.jetby.evilmobs.records;

import me.jetby.evilmobs.configurations.Items;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Map;

public record Mob(
        String id,
        int movingRadius,
        boolean teleportOnRadius,
        String locationType,
        Location location,
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
        boolean flyingDropParticle,
        DropParticle dropParticle,
        boolean isMask,
        Map<String, Mask> masks,
        Map<String, List<String>> listeners,
        Map<String, Task> tasks,
        Map<String, Bar> bossBars,
        List<String> onSpawnActions,
        List<String> onDeathActions,
        Rtp rtp,
        String lootAmount,
        boolean onlyCustom,
        List<Items.ItemsData> items


) {
}
