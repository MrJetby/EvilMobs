package me.jetby.evilmobs.records;

import org.bukkit.Particle;
import org.bukkit.Sound;

public record DropParticle(
        Sound sound,
        float volume,
        float pitch,
        Particle particle,
        int amount,
        double offsetX,
        double offsetY,
        double offsetZ,
        double minY,
        double maxY,
        double minSpeed,
        double maxSpeed,
        int pickupDelay

) {
}
