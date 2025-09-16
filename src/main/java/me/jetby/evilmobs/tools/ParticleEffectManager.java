package me.jetby.evilmobs.tools;

import lombok.experimental.UtilityClass;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.configurations.Particles;
import me.jetby.evilmobs.records.ParticleEffectConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static me.jetby.evilmobs.EvilMobs.LOGGER;

@UtilityClass
public class ParticleEffectManager {

    public void playEffect(String effectId, Location center, Particles particles) {
        ParticleEffectConfig config = particles.getEffects().get(effectId);
        if (config == null) {
            LOGGER.warn("Effect " + effectId + " not found");
            return;
        }

        int maxTick = config.keyframes().stream()
                .mapToInt(ParticleEffectConfig.Keyframe::tick)
                .max()
                .orElse(0);

        new BukkitRunnable() {
            int animationTick = 0;
            int subTick = 0;
            int renderCount = 0;

            @Override
            public void run() {
                if (renderCount >= config.repeat()) {
                    cancel();
                    return;
                }

                double currentRadius = config.radius();
                double currentOffsetX = config.offsetX();
                double currentOffsetY = config.offsetY();
                double currentOffsetZ = config.offsetZ();
                int currentPoints = config.points();
                Particle currentParticle = config.particle();

                List<ParticleEffectConfig.Keyframe> currentKeyframes = config.keyframes().stream()
                        .filter(kf -> kf.tick() == animationTick)
                        .toList();

                if (!currentKeyframes.isEmpty() && subTick < currentKeyframes.size()) {
                    ParticleEffectConfig.Keyframe keyframe = currentKeyframes.get(subTick);
                    for (ParticleEffectConfig.Keyframe.Opcode opcode : keyframe.opcodes()) {
                        String key = opcode.key();
                        Object value = opcode.value();
                        switch (key) {
                            case "radius":
                                currentRadius = ((Number) value).doubleValue();
                                break;
                            case "offsetX":
                                currentOffsetX = ((Number) value).doubleValue();
                                break;
                            case "offsetY":
                                currentOffsetY = ((Number) value).doubleValue();
                                break;
                            case "offsetZ":
                                currentOffsetZ = ((Number) value).doubleValue();
                                break;
                            case "points":
                                currentPoints = ((Number) value).intValue();
                                break;
                            case "particle":
                                try {
                                    currentParticle = Particle.valueOf(((String) value).toUpperCase());
                                } catch (IllegalArgumentException ignored) {
                                }
                                break;
                            default:
                                LOGGER.warn("Unsupported opcode key: " + key + " at tick " + animationTick);
                        }
                    }
                }

                switch (config.type()) {
                    case "circle" -> renderCircle(config, center, currentRadius, currentOffsetX, currentOffsetY, currentOffsetZ, currentPoints, currentParticle);
                    case "square" -> renderSquare(config, center, currentRadius, currentOffsetX, currentOffsetY, currentOffsetZ, currentPoints, currentParticle);
                    case "helix" -> renderHelix(config, center, currentRadius, currentOffsetX, currentOffsetY, currentOffsetZ, currentPoints, currentParticle);
                    case "line" -> renderLine(config, center, currentRadius, currentOffsetX, currentOffsetY, currentOffsetZ, currentPoints, currentParticle);
                    default -> LOGGER.warn("Unknown effect type: " + config.type());
                }

                subTick++;
                if (subTick >= currentKeyframes.size()) {
                    subTick = 0;
                    animationTick++;
                    if (animationTick > maxTick) {
                        animationTick = 0;
                    }
                }

                renderCount++;

                if (renderCount >= config.repeat()) {
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(EvilMobs.getInstance(), 0L, 1L);
    }

    private void renderCircle(ParticleEffectConfig config, Location center, double radius, double offsetX, double offsetY, double offsetZ, int points, Particle particle) {
        Location loc = center.clone().add(offsetX, offsetY, offsetZ);
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            spawnParticle(config, loc.clone().add(x, 0, z));
        }
    }

    private void renderSquare(ParticleEffectConfig config, Location center, double radius, double offsetX, double offsetY, double offsetZ, int points, Particle particle) {
        Location loc = center.clone().add(offsetX, offsetY, offsetZ);
        for (int i = 0; i < points; i++) {
            double t = (double) i / points * 4;
            double x, z;
            if (t < 1) {
                x = radius * (t - 0.5) * 2;
                z = -radius;
            } else if (t < 2) {
                x = radius;
                z = radius * (t - 1.5) * 2;
            } else if (t < 3) {
                x = radius * (2.5 - t) * 2;
                z = radius;
            } else {
                x = -radius;
                z = radius * (3.5 - t) * 2;
            }
            spawnParticle(config, loc.clone().add(x, 0, z));
        }
    }

    private void renderHelix(ParticleEffectConfig config, Location center, double radius, double offsetX, double offsetY, double offsetZ, int points, Particle particle) {
        Location loc = center.clone().add(offsetX, offsetY, offsetZ);
        double height = radius * 2;
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points * 2;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            double y = (double) i / points * height;
            spawnParticle(config, loc.clone().add(x, y, z));
        }
    }

    private void renderLine(ParticleEffectConfig config, Location center, double radius, double offsetX, double offsetY, double offsetZ, int points, Particle particle) {
        Location loc = center.clone().add(offsetX, offsetY, offsetZ);
        for (int i = 0; i < points; i++) {
            double t = (double) i / points;
            double x = radius * (t - 0.5) * 2;
            spawnParticle(config, loc.clone().add(x, 0, 0));
        }
    }

    private void spawnParticle(ParticleEffectConfig pec, Location location) {
        Bukkit.getScheduler().runTaskAsynchronously(EvilMobs.getInstance(), () -> {
            if (pec.particle() == Particle.REDSTONE) {
                float r = pec.r() / 255.0f;
                float g =pec.g() / 255.0f;
                float b = pec.b() / 255.0f;
                float size = (float) pec.size();

                location.getWorld().spawnParticle(
                        pec.particle(),
                        location,
                        1,
                        0, 0, 0,
                        0,
                        new Particle.DustOptions(org.bukkit.Color.fromRGB((int) (r * 255), (int) (g * 255), (int) (b * 255)), size)
                );
            } else {
                location.getWorld().spawnParticle(
                        pec.particle(),
                        location,
                        1,
                        0, 0, 0,
                        0,
                        null
                );
            }
        });
    }
}