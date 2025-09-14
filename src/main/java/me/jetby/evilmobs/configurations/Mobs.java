package me.jetby.evilmobs.configurations;

import lombok.Getter;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.records.*;
import me.jetby.treex.bukkit.LocationHandler;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.jetby.evilmobs.EvilMobs.LOGGER;

public class Mobs {

    private final EvilMobs plugin;


    @Getter
    private final Map<String, Mob> mobs = new HashMap<>();
    private final File file;

    public Mobs(EvilMobs plugin) {
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
                LOGGER.info("Файл mobs/" + config.getString("id") + ".yml создан");
                return;
            }
        }

        if (files == null) return;

        for (File file : files) {
            if (!file.getName().endsWith(".yml")) continue;
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            loadMob(config);
            LOGGER.info("Файл mobs/" + config.getString("id") + ".yml загружен");
        }
    }

    private void loadMob(FileConfiguration configuration) {
        try {

            String id = configuration.getString("id");

            String spawnLocation = configuration.getString("spawn-location");

            Location location = LocationHandler.deserialize(spawnLocation);

            String name = configuration.getString("name");
            boolean nameVisible = name != null && !name.isEmpty();

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


            Map<String, List<String>> listeners = new HashMap<>();
            ConfigurationSection listenerSection = configuration.getConfigurationSection("listeners");
            if (listenerSection != null) {
                for (String listenerId : listenerSection.getKeys(false)) {
                    List<String> actions = listenerSection.getStringList(listenerId);
                    listeners.put(listenerId, actions);
                }
            }


            List<Phases> phases = new ArrayList<>();
            ConfigurationSection phasesSection = configuration.getConfigurationSection("phases");
            if (phasesSection != null) {
                for (String phaseId : phasesSection.getKeys(false)) {
                    ConfigurationSection phaseSection = phasesSection.getConfigurationSection(phaseId);
                    if (phaseSection == null) continue;
                    String phaseType = phaseSection.getString("type", "HEALTH").toLowerCase();

                    ConfigurationSection actionSection = phasesSection.getConfigurationSection(phaseId + ".actions");
                    if (actionSection == null) continue;
                    Map<String, List<String>> phase = new HashMap<>();
                    for (String actionId : actionSection.getKeys(false)) {
                        phase.put(actionId, actionSection.getStringList(actionId));
                    }
                    phases.add(new Phases(phaseId, phaseType, phase));
                }
            }

            Map<String, Bar> bossBarsMap = new HashMap<>();
            ConfigurationSection bossBars = configuration.getConfigurationSection("bossBars");
            if (bossBars != null) {
                for (String bossBarId : bossBars.getKeys(false)) {
                    ConfigurationSection bossBar = bossBars.getConfigurationSection(bossBarId);
                    if (bossBar == null) continue;
                    String bossBarTitle = bossBar.getString("title", "");
                    int bossBarDuration = bossBar.getInt("duration", -1);

                    BossBar.Color bossBarColor = BossBar.Color.valueOf(bossBar.getString("Color", "BLUE"));
                    BossBar.Overlay bossBarStyle = BossBar.Overlay.valueOf(bossBar.getString("Style", "PROGRESS"));

                    String bossBarProgress = bossBar.getString("Progress", "1.0");

                    bossBarsMap.put(bossBarId, new Bar(bossBarId, bossBarTitle, bossBarColor, bossBarStyle, bossBarProgress, bossBarDuration));
                }
            }

            Map<String, Task> tasks = new HashMap<>();
            ConfigurationSection taskSection = configuration.getConfigurationSection("tasks");
            if (taskSection != null) {
                for (String taskId : taskSection.getKeys(false)) {
                    ConfigurationSection task = taskSection.getConfigurationSection(taskId);
                    if (task == null) continue;

                    int delay = task.getInt("delay", 20);
                    int period = task.getInt("period", 20);
                    int amount = task.getInt("amount", -1);
                    List<String> actions = task.getStringList("actions");

                    tasks.put(taskId, new Task(delay, period, amount, actions));
                }
            }

            List<String> onSpawnActions = configuration.getStringList("actions.onSpawn");
            List<String> onHitActions = configuration.getStringList("actions.onHit");
            List<String> onDeathActions = configuration.getStringList("actions.onDeath");

            Mob mob = new Mob(id, location, nameVisible, name, armorItem, entityType, health, ai, glow, canPickupItems, visualFire, isBaby, phases, listeners, tasks, bossBarsMap, onSpawnActions, onHitActions, onDeathActions);
            mobs.put(id, mob);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
