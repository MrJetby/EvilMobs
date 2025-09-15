package me.jetby.evilmobs.records;


import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

public record Bar(
        String id,
        String title,
        BarColor color,
        BarStyle style,
        String progress,
        int duration
) {
}