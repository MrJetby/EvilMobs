package me.jetby.evilmobs.tools;

import lombok.experimental.UtilityClass;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.configurations.Particles;
import me.jetby.evilmobs.records.ParticleEffectConfig;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@UtilityClass
public class ParticleEffectManager {

    public void playEffect(Player player, ParticleEffectConfig effect) {
        if (effect == null) {
            player.sendMessage("§cEffect not found!");
            return;
        }
        int repeat = effect.repeat();
        int interval = effect.interval();
        int lifetime = effect.lifetime();

        new BukkitRunnable() {
            int tick = 0;
            int runs = 0;

            @Override
            public void run() {
                if (repeat > 0 && runs++ >= repeat) {
                    cancel();
                    return;
                }
                if (lifetime > 0 && tick++ >= lifetime) {
                    cancel();
                    return;
                }

                Location baseLoc = player.getLocation().clone().add(effect.offsetX(), effect.offsetY(), effect.offsetZ());
                interpolateKeyframe(effect, tick, baseLoc);

                if (effect.sequence() != null) {
                    playSequence(player, effect, tick);
                } else if (!effect.layers().isEmpty()) {
                    playLayers(player, effect, baseLoc, tick);
                } else {
                    drawShape(baseLoc, effect, tick, player);
                }
            }
        }.runTaskTimer(EvilMobs.getInstance(), 0L, interval);
    }

    private void interpolateKeyframe(ParticleEffectConfig effect, int tick, Location loc) {
        List<Particles.Keyframe> kfs = effect.keyframes();
        if (kfs.isEmpty()) return;
        Particles.Keyframe prev = kfs.get(0);
        for (int i = 1; i < kfs.size(); i++) {
            Particles.Keyframe curr = kfs.get(i);
            if (tick >= prev.time && tick < curr.time) {
                double progress = (tick - prev.time) / (double) (curr.time - prev.time);
                double prevScale = prev.changes.getDouble("scale", 1.0);
                double currScale = curr.changes.getDouble("scale", 1.0);
                double scale = prevScale + (currScale - prevScale) * progress;
                loc.add(0, (scale - 1.0) * effect.height(), 0);
                ConfigurationSection prevColor = prev.changes.getConfigurationSection("color");
                ConfigurationSection currColor = curr.changes.getConfigurationSection("color");
                if (prevColor != null && currColor != null && effect.particle() == Particle.REDSTONE) {
                    int r = (int) (prevColor.getInt("r", 255) + (currColor.getInt("r", 255) - prevColor.getInt("r", 255)) * progress);
                    int g = (int) (prevColor.getInt("g", 0) + (currColor.getInt("g", 0) - prevColor.getInt("g", 0)) * progress);
                    int b = (int) (prevColor.getInt("b", 0) + (currColor.getInt("b", 0) - prevColor.getInt("b", 0)) * progress);
                    float size = (float) (prevColor.getDouble("size", 1.0) + (currColor.getDouble("size", 1.0) - prevColor.getDouble("size", 1.0)) * progress);
                    effect.color().set("r", r);
                    effect.color().set("g", g);
                    effect.color().set("b", b);
                    effect.color().set("size", size);
                }
                ConfigurationSection prevOffset = prev.changes.getConfigurationSection("offset");
                ConfigurationSection currOffset = curr.changes.getConfigurationSection("offset");
                if (prevOffset != null && currOffset != null) {
                    double ox = prevOffset.getDouble("x", 0) + (currOffset.getDouble("x", 0) - prevOffset.getDouble("x", 0)) * progress;
                    double oy = prevOffset.getDouble("y", 0) + (currOffset.getDouble("y", 0) - prevOffset.getDouble("y", 0)) * progress;
                    double oz = prevOffset.getDouble("z", 0) + (currOffset.getDouble("z", 0) - prevOffset.getDouble("z", 0)) * progress;
                    loc.add(ox, oy, oz);
                }
                break;
            }
            prev = curr;
        }
    }

    private void playLayers(Player player, ParticleEffectConfig effect, Location baseLoc, int tick) {
        for (Map.Entry<String, Particles.LayerConfig> layer : effect.layers().entrySet()) {
            ParticleEffectConfig layerEffect = createTempEffectFromLayer(layer.getValue());
            if (layerEffect != null) {
                interpolateKeyframe(layerEffect, tick, baseLoc);
                drawShape(baseLoc, layerEffect, tick, player);
            }
        }
    }

    private void playSequence(Player player, ParticleEffectConfig effect, int tick) {
        Particles.SequenceConfig seq = effect.sequence();
        if ("sequential".equals(seq.mode)) {
            int totalDuration = 0;
            for (Particles.SeqEffect se : seq.effects) {
                totalDuration += se.duration;
                if (tick >= totalDuration - se.duration && tick < totalDuration) {
                    if (se.ref != null) {
                        ParticleEffectConfig subEffect = EvilMobs.getInstance().getParticles().getEffects().get(se.ref);
                        if (subEffect != null) {
                            playEffect(player, subEffect);
                        } else {
                            player.sendMessage("§cReferenced effect not found: " + se.ref);
                        }
                    } else if (se.inline != null) {
                        ParticleEffectConfig inlineEffect = parseInlineEffect(se.inline);
                        if (inlineEffect != null) {
                            playEffect(player, inlineEffect);
                        }
                    }
                    break;
                }
            }
        } else if ("parallel".equals(seq.mode)) {
            for (Particles.SeqEffect se : seq.effects) {
                if (tick < se.duration) {
                    if (se.ref != null) {
                        ParticleEffectConfig subEffect = EvilMobs.getInstance().getParticles().getEffects().get(se.ref);
                        if (subEffect != null) {
                            playEffect(player, subEffect);
                        } else {
                            player.sendMessage("§cReferenced effect not found: " + se.ref);
                        }
                    } else if (se.inline != null) {
                        ParticleEffectConfig inlineEffect = parseInlineEffect(se.inline);
                        if (inlineEffect != null) {
                            playEffect(player, inlineEffect);
                        }
                    }
                }
            }
        }
    }

    private ParticleEffectConfig createTempEffectFromLayer(Particles.LayerConfig layer) {
        ConfigurationSection layerSec = layer.config;
        if (layerSec == null) return null;
        String type = layerSec.getString("type", "circle");
        Particle particle;
        try {
            particle = Particle.valueOf(layerSec.getString("particle", "FLAME").toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
        double radius = getDouble(layerSec, "radius", 1.0);
        double height = getDouble(layerSec, "height", 3.0);
        int points = layerSec.getInt("points", 20);
        int coils = layerSec.getInt("coils", 5);
        double size = getDouble(layerSec, "size", 1.0);
        double offsetX = getDouble(layerSec.getConfigurationSection("offset"), "x", 0);
        double offsetY = getDouble(layerSec.getConfigurationSection("offset"), "y", 0);
        double offsetZ = getDouble(layerSec.getConfigurationSection("offset"), "z", 0);
        List<Particles.Keyframe> keyframes = parseLayerKeyframes(layerSec);
        ConfigurationSection color = layerSec.getConfigurationSection("color");
        return new ParticleEffectConfig(
                layer.name, type, particle, 1, 1, 0,
                offsetX, offsetY, offsetZ,
                radius, height, points, coils, size,
                0, 0, 0, 0, 0, 0,
                3, 0.1, "horizontal",
                List.of(),
                color, keyframes, Map.of(), null
        );
    }

    private List<Particles.Keyframe> parseLayerKeyframes(ConfigurationSection layerSec) {
        ConfigurationSection animSec = layerSec.getConfigurationSection("animation");
        if (animSec == null) return List.of();
        List<?> kfList = animSec.getList("keyframes");
        if (kfList == null) return List.of();
        List<Particles.Keyframe> keyframes = new ArrayList<>();
        for (Object kfObj : kfList) {
            if (kfObj instanceof ConfigurationSection kf) {
                int time = kf.getInt("time", 0);
                keyframes.add(new Particles.Keyframe(time, kf));
            }
        }
        return keyframes;
    }

    private ParticleEffectConfig parseInlineEffect(ConfigurationSection inline) {
        String type = inline.getString("type", "circle");
        Particle particle;
        try {
            particle = Particle.valueOf(inline.getString("particle", "FLAME").toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
        double radius = getDouble(inline, "radius", 1.0);
        int points = inline.getInt("points", 20);
        ConfigurationSection startSec = inline.getConfigurationSection("start");
        double startX = getDouble(startSec, "x", 0);
        double startY = getDouble(startSec, "y", 0);
        double startZ = getDouble(startSec, "z", 0);
        ConfigurationSection endSec = inline.getConfigurationSection("end");
        double endX = getDouble(endSec, "x", 0);
        double endY = getDouble(endSec, "y", 0);
        double endZ = getDouble(endSec, "z", 0);
        return new ParticleEffectConfig(
                "inline", type, particle, 1, 1, 0,
                0, 0, 0,
                radius, 3.0, points, 5, 1.0,
                startX, startY, startZ, endX, endY, endZ,
                3, 0.1, "horizontal",
                List.of(),
                null, List.of(), Map.of(), null
        );
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

    private void drawShape(Location center, ParticleEffectConfig effect, int tick, Player player) {
        String type = effect.type().toLowerCase();
        switch (type) {
            case "circle" -> drawCircle(center, effect);
            case "spiral" -> drawSpiral(center, effect);
            case "line" -> drawLine(center, effect);
            case "cube" -> drawCube(center, effect);
            case "helix" -> drawHelix(center, effect);
            case "ring" -> drawRing(center, effect);
            case "custom_points" -> drawCustomPoints(center, effect);
            default -> player.sendMessage("§eНеизвестный тип: " + type);
        }
    }

    private void drawCircle(Location center, ParticleEffectConfig effect) {
        for (int i = 0; i < effect.points(); i++) {
            double angle = 2 * Math.PI * i / effect.points();
            double x = Math.cos(angle) * effect.radius();
            double z = Math.sin(angle) * effect.radius();
            Location loc = center.clone().add(x, 0, z);
            spawnParticle(loc, effect);
        }
    }

    private void drawSpiral(Location center, ParticleEffectConfig effect) {
        for (int i = 0; i < effect.points(); i++) {
            double progress = (double) i / effect.points();
            double angle = effect.coils() * 2 * Math.PI * progress;
            double x = Math.cos(angle) * effect.radius();
            double z = Math.sin(angle) * effect.radius();
            double y = progress * effect.height();
            Location loc = center.clone().add(x, y, z);
            spawnParticle(loc, effect);
        }
    }

    private void drawLine(Location center, ParticleEffectConfig effect) {
        Vector start = new Vector(effect.startX(), effect.startY(), effect.startZ());
        Vector end = new Vector(effect.endX(), effect.endY(), effect.endZ());
        Vector step = end.clone().subtract(start).multiply(1.0 / effect.points());

        for (int i = 0; i <= effect.points(); i++) {
            Vector point = start.clone().add(step.clone().multiply(i));
            Location loc = center.clone().add(point);
            spawnParticle(loc, effect);
        }
    }

    private void drawCube(Location center, ParticleEffectConfig effect) {
        double s = effect.size();
        for (double x = -s / 2; x <= s / 2; x += s / effect.points()) {
            for (double y = -s / 2; y <= s / 2; y += s / effect.points()) {
                for (double z = -s / 2; z <= s / 2; z += s / effect.points()) {
                    if (Math.abs(x) >= s / 2 - 0.05 || Math.abs(y) >= s / 2 - 0.05 || Math.abs(z) >= s / 2 - 0.05) {
                        Location loc = center.clone().add(x, y, z);
                        spawnParticle(loc, effect);
                    }
                }
            }
        }
    }

    private void drawHelix(Location center, ParticleEffectConfig effect) {
        for (int i = 0; i < effect.points(); i++) {
            double progress = (double) i / effect.points();
            double angle = effect.twists() * 2 * Math.PI * progress;
            double x = Math.cos(angle) * effect.radius();
            double z = Math.sin(angle) * effect.radius();
            double y = progress * effect.height();
            Location loc = center.clone().add(x, y, z);
            spawnParticle(loc, effect);
        }
    }

    private void drawRing(Location center, ParticleEffectConfig effect) {
        for (int i = 0; i < effect.points(); i++) {
            double angle = 2 * Math.PI * i / effect.points();
            double x = Math.cos(angle) * effect.radius();
            double z = Math.sin(angle) * effect.radius();
            Location loc = center.clone().add("vertical".equals(effect.orientation()) ? 0 : x, "vertical".equals(effect.orientation()) ? x : 0, z);
            spawnParticle(loc, effect);
        }
    }

    private void drawCustomPoints(Location center, ParticleEffectConfig effect) {
        for (double[] pt : effect.customPoints()) {
            Location loc = center.clone().add(pt[0], pt[1], pt[2]);
            spawnParticle(loc, effect);
        }
    }

    private void spawnParticle(Location loc, ParticleEffectConfig effect) {
        if (effect.particle() == Particle.REDSTONE && effect.color() != null) {
            int r = effect.color().getInt("r", 255);
            int g = effect.color().getInt("g", 0);
            int b = effect.color().getInt("b", 0);
            float size = (float) effect.color().getDouble("size", 1.0);
            Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(r, g, b), size);
            loc.getWorld().spawnParticle(effect.particle(), loc, 1, 0, 0, 0, 0, dust);
        } else {
            loc.getWorld().spawnParticle(effect.particle(), loc, 1, 0, 0, 0, 0);
        }
    }
}