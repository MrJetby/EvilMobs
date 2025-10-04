package me.jetby.evilmobs.records;

import org.bukkit.Material;
import org.bukkit.World;

import java.util.Set;

public record Rtp(
        World world,
        int min,
        int max,
        Set<Material> materials,
        String blockListType

) {
}
