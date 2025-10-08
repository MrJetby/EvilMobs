package me.jetby.evilmobs;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EvilMobsPlaceholderExpansion extends PlaceholderExpansion {

    private final EvilMobs plugin;

    public EvilMobsPlaceholderExpansion(EvilMobs plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "evilmobs";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.valueOf(plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        String[] args = identifier.split("_");
        if (args[0].equalsIgnoreCase("health")) {
            return String.valueOf((int) Maps.mobCreators.get(args[1]).getLivingEntity().getHealth());
        }
        if (args[0].equalsIgnoreCase("health") && args[2].equalsIgnoreCase("percent")) {
            LivingEntity livingEntity = Maps.mobCreators.get(args[1]).getLivingEntity();
            double healthPercent = livingEntity.getHealth() / livingEntity.getMaxHealth();
            return String.valueOf(healthPercent);
        }

        if (identifier.equalsIgnoreCase("time_to_start")) {
            if (plugin.getAutoStart() == null) return "0";
            return String.valueOf(plugin.getAutoStart().getTimeToStart());
        }
        if (identifier.equalsIgnoreCase("time_to_start_formatted") || identifier.equalsIgnoreCase("time_to_start_string")) {
            if (plugin.getAutoStart() == null) return "0";
            return plugin.getFormatTime().stringFormat(plugin.getAutoStart().getTimeToStart());
        }

        return null;
    }

}
