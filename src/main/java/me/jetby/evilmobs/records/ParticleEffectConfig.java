package me.jetby.evilmobs.records;

import me.jetby.evilmobs.configurations.Particles;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;

public record ParticleEffectConfig(
        String id,
        String type,
        Particle particle,
        int repeat,
        int interval,
        int lifetime,
        double offsetX,
        double offsetY,
        double offsetZ,
        double radius,
        double height,
        int points,
        int coils,
        double size,
        double startX,
        double startY,
        double startZ,
        double endX,
        double endY,
        double endZ,
        int twists,
        double thickness,
        String orientation,
        List<double[]> customPoints,
        ConfigurationSection color,
        List<Particles.Keyframe> keyframes,
        Map<String, Particles.LayerConfig> layers,
        Particles.SequenceConfig sequence
) {}