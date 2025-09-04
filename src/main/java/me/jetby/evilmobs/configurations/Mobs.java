package me.jetby.evilmobs.configurations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.jetby.evilmobs.Main;
import me.jetby.evilmobs.records.ArmorItem;
import me.jetby.evilmobs.records.BossBars;
import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.records.Phases;
import me.jetby.evilmobs.tools.LocationHandler;
import me.jetby.evilmobs.tools.Logger;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mobs {

    private final Main plugin;


    @Getter
    private final Map<String, Mob> mobs = new HashMap<>();
    private final File file;

    public Mobs(Main plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "mobs");

    }
    public void load() {

        File[] files = file.listFiles();
        if (!file.exists()) {
            if (file.mkdirs()) {
                File defaultFile = new File(file, "evil.yml");
                if (!defaultFile.exists()) {
                    plugin.saveResource("mobs/evil.yml", false);
                }
                FileConfiguration config = YamlConfiguration.loadConfiguration(defaultFile);
                loadMob(config);
                Logger.info("Файл mobs/"+config.getString("id")+".yml создан");
                return;
            }
        }

        if (files==null) return;

        for (File file : files) {
            if (!file.getName().endsWith(".yml")) continue;
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            loadMob(config);
            Logger.info("Файл mobs/"+config.getString("id")+".yml загружен");
        }
    }

    private void loadMob(FileConfiguration configuration) {
        try {

            String id = configuration.getString("id");

            String spawnLocation = configuration.getString("spawn-location");

            Location location = LocationHandler.deserialize(spawnLocation);

            String name = configuration.getString("name");
            boolean nameVisible = name != null && !name.isEmpty( );

            String armorName = configuration.getString("armor");
            List<ArmorItem> armorItem = plugin.getArmorSets().getArmorItems().get(armorName);

            String type = configuration.getString("type");
            EntityType entityType = EntityType.valueOf(type);

            int health = configuration.getInt("health");

            boolean ai = configuration.getBoolean("AI", false);
            boolean glow = configuration.getBoolean("glow", false);
            boolean canPickupItems = configuration.getBoolean("can-pickup-items", false);
            boolean visualFire = configuration.getBoolean("visual-fire", false);
            boolean isBaby = configuration.getBoolean("isBaby", false);

            Phases phases = new Phases();

            BossBars bossBars = new BossBars();

            List<String> onSpawnActions = configuration.getStringList("actions.onSpawn");
            List<String> onDeathActions = configuration.getStringList("actions.onDeath");

            Mob mob  = new Mob(id, location, nameVisible, name, armorItem, entityType, health, ai, glow, canPickupItems, visualFire, isBaby, phases, bossBars, onSpawnActions, onDeathActions);
            mobs.put(id, mob);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
