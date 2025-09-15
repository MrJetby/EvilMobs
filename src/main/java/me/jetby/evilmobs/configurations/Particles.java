package me.jetby.evilmobs.configurations;

import lombok.Getter;
import me.jetby.evilmobs.records.ParticleEffectConfig;
import me.jetby.evilmobs.tools.FileLoader;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.jetby.evilmobs.EvilMobs.LOGGER;

public class Particles {

    @Getter
    private final Map<String, ParticleEffectConfig> effects = new HashMap<>();
    private final FileConfiguration configuration = FileLoader.getFileConfiguration("particles.yml");

    public void load() {
        try {
            for (String id : configuration.getKeys(false)) {
                ConfigurationSection section = configuration.getConfigurationSection(id);
                if (section == null) {
                    LOGGER.warn("Section " + id + " is null");
                    continue;
                }

                String type = section.getString("type", "circle");
                if (!List.of("circle", "square", "helix", "line").contains(type)) {
                    LOGGER.warn("Invalid type for " + id + ": " + type);
                    continue;
                }

                Particle particle;
                try {
                    particle = Particle.valueOf(section.getString("particle", "FLAME").toUpperCase());
                } catch (IllegalArgumentException e) {
                    LOGGER.warn("Invalid particle type for " + id + ": " + section.getString("particle"));
                    continue;
                }

                int repeat = section.getInt("repeat", 1);
                int interval = section.getInt("interval", 2);
                double radius = section.getDouble("radius", 1.0);
                int points = section.getInt("points", 20);

                ConfigurationSection offsetSec = section.getConfigurationSection("offset");
                double offsetX = getDouble(offsetSec, "x", 0.0);
                double offsetY = getDouble(offsetSec, "y", 0.0);
                double offsetZ = getDouble(offsetSec, "z", 0.0);

                ConfigurationSection colorSec = section.getConfigurationSection("color");
                int r = 255, g = 0, b = 0;
                double size = 1.0;
                if (colorSec != null) {
                   r =  colorSec.getInt("r", 255);
                   g =  colorSec.getInt("g", 0);
                   b =  colorSec.getInt("b", 0);
                   size = colorSec.getDouble("size", 1.0);
                }

                List<ParticleEffectConfig.Keyframe> keyframes = parseKeyframes(section.getList("animation"));

                ParticleEffectConfig effect = new ParticleEffectConfig(
                        id, type, particle, repeat, interval,
                        offsetX, offsetY, offsetZ,
                        radius, points,
                        r, g, b, size, keyframes
                );
                effects.put(id, effect);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.warn("Failed to load particle effects: " + e.getMessage());
        }
    }

    private double getDouble(ConfigurationSection sec, String key, double def) {
        if (sec == null) return def;
        Object value = sec.get(key, def);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return def;
    }

    private List<ParticleEffectConfig.Keyframe> parseKeyframes(List<?> kfList) {
        List<ParticleEffectConfig.Keyframe> keyframes = new ArrayList<>();
        if (kfList == null || kfList.isEmpty()) {
            return keyframes;
        }

        int autoTick = 0;
        for (Object kfObj : kfList) {
            if (!(kfObj instanceof Map<?, ?> kfMap)) {
                LOGGER.warn("Invalid keyframe format: not a map");
                continue;
            }

            Object tickObj = kfMap.get("tick");
            int tick;
            if (tickObj instanceof Number) {
                tick = ((Number) tickObj).intValue();
                autoTick = tick + 1;
            } else {
                tick = autoTick++;
            }

            Object opcodesObj = kfMap.get("opcodes");
            if (!(opcodesObj instanceof List<?> opcodeList)) {
                LOGGER.warn("No or invalid opcodes for tick: " + tick);
                continue;
            }

            List<ParticleEffectConfig.Keyframe.Opcode> opcodes = new ArrayList<>();
            for (Object opcodeObj : opcodeList) {
                if (!(opcodeObj instanceof Map<?, ?> opcodeMap)) {
                    LOGGER.warn("Invalid opcode format for tick " + tick + ": not a map");
                    continue;
                }
                if (opcodeMap.size() != 1) {
                    LOGGER.warn("Opcode should have exactly one key-value pair for tick " + tick);
                    continue;
                }
                Map.Entry<?, ?> entry = opcodeMap.entrySet().iterator().next();
                String key = entry.getKey().toString();
                Object value = entry.getValue();

                switch (key) {
                    case "radius", "offsetX", "offsetY", "offsetZ" -> {
                        if (!(value instanceof Number)) {
                            LOGGER.warn("Invalid value for " + key + " at tick " + tick + ": not a number");
                            continue;
                        }
                    }
                    case "points" -> {
                        if (!(value instanceof Number)) {
                            LOGGER.warn("Invalid value for points at tick " + tick + ": not a number");
                            continue;
                        }
                    }
                    case "color" -> {
                        if (!(value instanceof Map)) {
                            LOGGER.warn("Invalid value for color at tick " + tick + ": not a map");
                            continue;
                        }
                    }
                    case "particle" -> {
                        if (!(value instanceof String)) {
                            LOGGER.warn("Invalid value for particle at tick " + tick + ": not a string");
                            continue;
                        }
                        try {
                            Particle.valueOf(((String) value).toUpperCase());
                        } catch (IllegalArgumentException e) {
                            LOGGER.warn("Invalid particle type " + value + " at tick " + tick);
                            continue;
                        }
                    }
                    default -> {
                        LOGGER.warn("Unsupported opcode key: " + key + " at tick " + tick);
                        continue;
                    }
                }
                opcodes.add(new ParticleEffectConfig.Keyframe.Opcode(key, value));
            }

            keyframes.add(new ParticleEffectConfig.Keyframe(tick, opcodes));
        }
        return keyframes;
    }
}