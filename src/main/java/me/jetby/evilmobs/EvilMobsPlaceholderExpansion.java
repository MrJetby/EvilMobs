package me.jetby.evilmobs;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EvilMobsPlaceholderExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "evilmobs";
    }

    @Override
    public @NotNull String getAuthor() {
        return "";
    }

    @Override
    public @NotNull String getVersion() {
        return "";
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

        return null;
    }

}
