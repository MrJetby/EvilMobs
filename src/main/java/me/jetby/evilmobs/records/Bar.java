package me.jetby.evilmobs.records;


import net.kyori.adventure.bossbar.BossBar;

public record Bar(
        String id,
        String title,
        BossBar.Color color,
        BossBar.Overlay style,
        String progress,
        int duration
) {
}