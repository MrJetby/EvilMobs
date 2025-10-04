package me.jetby.evilmobs.records;

import org.bukkit.Particle;

import java.util.List;

public record ParticleEffectConfig(
        String id,
        String type,
        Particle particle,
        int repeat,
        int interval,
        double offsetX,
        double offsetY,
        double offsetZ,
        double radius,
        int points,
        int r, int g, int b, double size,
        List<Keyframe> keyframes
) {
    public record Keyframe(
            int tick,
            List<Opcode> opcodes
    ) {
        public record Opcode(
                String key,
                Object value
        ) {
        }
    }
}