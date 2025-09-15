package me.jetby.evilmobs.records;

import org.bukkit.Material;

public record Mask(
        Material material,
        String name,
        boolean enchanted
) {
}
