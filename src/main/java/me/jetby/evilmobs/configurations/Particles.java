package me.jetby.evilmobs.configurations;

import lombok.Getter;
import me.jetby.evilmobs.records.ParticleEffectConfig;
import me.jetby.evilmobs.tools.FileLoader;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

import static me.jetby.evilmobs.EvilMobs.LOGGER;

public class Particles {

    @Getter
    private final Map<String, ParticleEffectConfig> effects = new HashMap<>();

    final FileConfiguration configuration = FileLoader.getFileConfiguration("particles.yml");

    public void load() {
        try {
            for (String id : configuration.getKeys(false)) {
                ConfigurationSection section = configuration.getConfigurationSection(id);
                if (section == null) {
                    LOGGER.warn("Section " + id + " is null");
                    continue;
                }

                String type = section.getString("type", "circle");
                Particle particle;
                try {
                    particle = Particle.valueOf(section.getString("particle", "FLAME").toUpperCase());
                } catch (IllegalArgumentException e) {
                    LOGGER.warn("Invalid particle type for " + id + ": " + section.getString("particle"));
                    continue;
                }
                int repeat = section.getInt("repeat", 1);
                int interval = section.getInt("interval", 2);
                int lifetime = section.getInt("lifetime", 0);

                ConfigurationSection offsetSec = section.getConfigurationSection("offset");
                double offsetX = getDouble(offsetSec, "x", 0);
                double offsetY = getDouble(offsetSec, "y", 0);
                double offsetZ = getDouble(offsetSec, "z", 0);

                double radius = getDouble(section, "radius", 1.0);
                double height = getDouble(section, "height", 3.0);
                int points = section.getInt("points", 20);
                int coils = section.getInt("coils", 5);
                double size = getDouble(section, "size", 1.0);

                ConfigurationSection startSec = section.getConfigurationSection("start");
                double startX = getDouble(startSec, "x", 0);
                double startY = getDouble(startSec, "y", 0);
                double startZ = getDouble(startSec, "z", 0);
                ConfigurationSection endSec = section.getConfigurationSection("end");
                double endX = getDouble(endSec, "x", 0);
                double endY = getDouble(endSec, "y", 0);
                double endZ = getDouble(endSec, "z", 0);

                int twists = section.getInt("twists", 3);
                double thickness = getDouble(section, "thickness", 0.1);
                String orientation = section.getString("orientation", "horizontal");

                List<double[]> customPoints = new ArrayList<>();
                List<?> pointsList = section.getList("points");
                if (pointsList != null) {
                    for (Object pt : pointsList) {
                        if (pt instanceof List<?> coords && coords.size() == 3) {
                            try {
                                customPoints.add(new double[]{
                                        getDoubleFromObject(coords.get(0), 0),
                                        getDoubleFromObject(coords.get(1), 0),
                                        getDoubleFromObject(coords.get(2), 0)
                                });
                            } catch (Exception e) {
                                LOGGER.warn("Invalid point format in " + id);
                            }
                        }
                    }
                }

                ConfigurationSection colorSec = section.getConfigurationSection("color");

                List<Keyframe> keyframes = parseKeyframes(section.getConfigurationSection("animation"));

                Map<String, LayerConfig> layers = parseLayers(section);

                SequenceConfig sequence = parseSequence(section, id);

                ParticleEffectConfig effect = new ParticleEffectConfig(
                        id, type, particle, repeat, interval, lifetime,
                        offsetX, offsetY, offsetZ,
                        radius, height, points, coils, size,
                        startX, startY, startZ, endX, endY, endZ,
                        twists, thickness, orientation,
                        customPoints,
                        colorSec, keyframes, layers, sequence
                );
                effects.put(id, effect);
                LOGGER.success(id + " загружен");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.warn("Failed to load particle effects: " + e.getMessage());
        }
    }

    private double getDouble(ConfigurationSection sec, String key, double def) {
        if (sec == null) return def;
        Object value = sec.get(key, def);
        return getDoubleFromObject(value, def);
    }

    private double getDoubleFromObject(Object value, double def) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return def;
    }

    private List<Keyframe> parseKeyframes(ConfigurationSection animSec) {
        if (animSec == null) return new ArrayList<>();
        List<?> kfList = animSec.getList("keyframes");
        if (kfList == null) return new ArrayList<>();
        List<Keyframe> keyframes = new ArrayList<>();
        for (Object kfObj : kfList) {
            if (kfObj instanceof ConfigurationSection kf) {
                int time = kf.getInt("time", 0);
                keyframes.add(new Keyframe(time, kf));
            }
        }
        return keyframes;
    }

    private Map<String, LayerConfig> parseLayers(ConfigurationSection section) {
        ConfigurationSection layersSec = section.getConfigurationSection("layers");
        if (layersSec == null) return new HashMap<>();
        Map<String, LayerConfig> layers = new HashMap<>();
        for (String layerId : layersSec.getKeys(false)) {
            ConfigurationSection layerSec = layersSec.getConfigurationSection(layerId);
            LayerConfig layer = new LayerConfig(layerId, layerSec);
            layers.put(layerId, layer);
        }
        return layers;
    }

    private SequenceConfig parseSequence(ConfigurationSection section, String parentId) {
        ConfigurationSection seqSec = section.getConfigurationSection("sequence");
        if (seqSec == null) return null;
        String mode = seqSec.getString("mode", "sequential");
        List<?> effectsList = seqSec.getList("effects");
        if (effectsList == null) return null;
        List<SeqEffect> seqEffects = new ArrayList<>();
        for (Object effObj : effectsList) {
            if (effObj instanceof String ref) {
                seqEffects.add(new SeqEffect(ref, 20));
            } else if (effObj instanceof ConfigurationSection inline) {
                int duration = inline.getInt("duration", 20);
                seqEffects.add(new SeqEffect(inline, duration));
            }
        }
        return new SequenceConfig(mode, seqEffects);
    }

    public static class Keyframe {
        public int time;
        public ConfigurationSection changes;
        public Keyframe(int time, ConfigurationSection changes) {
            this.time = time;
            this.changes = changes;
        }
    }

    public static class LayerConfig {
        public String name;
        public ConfigurationSection config;
        public LayerConfig(String name, ConfigurationSection config) {
            this.name = name;
            this.config = config;
        }
    }

    public static class SeqEffect {
        public String ref;
        public ConfigurationSection inline;
        public int duration;
        public SeqEffect(String ref, int duration) {
            this.ref = ref;
            this.duration = duration;
        }
        public SeqEffect(ConfigurationSection inline, int duration) {
            this.inline = inline;
            this.duration = duration;
        }
    }

    public static class SequenceConfig {
        public String mode;
        public List<SeqEffect> effects;
        public SequenceConfig(String mode, List<SeqEffect> effects) {
            this.mode = mode;
            this.effects = effects;
        }
    }
}