package me.jetby.evilmobs.configurations;

import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.Maps;
import me.jetby.evilmobs.records.*;
import me.jetby.treex.bukkit.LocationHandler;
import me.jetby.treex.text.Colorize;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
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

    private final File file;

    public Mobs(EvilMobs plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "mobs");

    }

    public void load() {
        Maps.mobs.clear();

        File[] files = file.listFiles();

        String[] defaults = {"example.yml", "example_minion.yml"};

        if (!file.exists()) {
            for (String name : defaults) {
                File target = new File(file, name);

                if (!target.exists()) {
                    plugin.saveResource("mobs/" + name, false);
                    FileConfiguration configuration = YamlConfiguration.loadConfiguration(target);
                    loadMob(configuration);
                    return;
                }
            }
        }


        if (files == null) return;

        for (File file : files) {
            if (!file.getName().endsWith(".yml")) continue;
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            loadMob(config);
        }
        LOGGER.success(Maps.mobs.size()+" mobs loaded");
    }

    private void loadMob(FileConfiguration configuration) {
        try {

            String id = configuration.getString("id");
            int movingRadius = configuration.getInt("moving-radius", -1);

            String spawnLocation = configuration.getString("spawn-location");

            Location location = LocationHandler.deserialize(spawnLocation);

            String name = Colorize.text(configuration.getString("name"));
            boolean nameVisible = !name.isEmpty();

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

            ConfigurationSection dropSettings = configuration.getConfigurationSection("drop-settings");


            Map<String, Mask> masks = new HashMap<>();

            boolean isMask = false;
            String lootAmount = "0";
            boolean customDrops = false;

            boolean flyingDropParticle = false;
            Sound sound = Sound.ENTITY_ITEM_PICKUP;
            float soundVolume = 1;
            float soundPitch = 1;
            Particle particle = Particle.FLAME;
            int paritcleAmount = 1;
            double offsetX = 0;
            double offsetY = 0;
            double offsetZ = 0;
            double minY = 5.0;
            double maxY = 10.0;
            double minSpeed = 0.5;
            double maxSpeed = 1.0;
            int pickupDelay = 0;

            if (dropSettings != null) {
                customDrops = dropSettings.getBoolean("only-custom", false);
                lootAmount = dropSettings.getString("lootAmount", "0");


                ConfigurationSection dropParticleSection = dropSettings.getConfigurationSection("flying-drop-particle");

                if (dropParticleSection!=null) {
                    flyingDropParticle = dropParticleSection.getBoolean("enabled", false);

                    sound = Sound.valueOf(dropParticleSection.getString("sound".toUpperCase(), "ENTITY_ITEM_PICKUP"));
                    soundVolume = (float) dropParticleSection.getDouble("volume", 1);
                    soundPitch = (float) dropParticleSection.getDouble("pitch", 1);
                    paritcleAmount = dropParticleSection.getInt("amount", 1);
                    offsetX = dropParticleSection.getDouble("offset-x", 0);
                    offsetY = dropParticleSection.getDouble("offset-y", 0);
                    offsetZ = dropParticleSection.getDouble("offset-z", 0);
                    minY = dropParticleSection.getDouble("min-y", 5.0);
                    maxY = dropParticleSection.getDouble("max-y", 10.0);
                    minSpeed = dropParticleSection.getDouble("min-speed", 0.5);
                    maxSpeed = dropParticleSection.getDouble("max-speed", 1.0);
                    particle = Particle.valueOf(dropParticleSection.getString("particle", "FLAME"));
                    pickupDelay = dropParticleSection.getInt("pickup-delay", 0);
                }


                ConfigurationSection maskSection = dropSettings.getConfigurationSection("mask");
                if (maskSection != null) {

                    isMask = maskSection.getBoolean("enabled", false);

                    ConfigurationSection maskItems = maskSection.getConfigurationSection("items");
                    if (maskItems != null) {
                        for (String maskId : maskItems.getKeys(false)) {

                            ConfigurationSection mask = maskItems.getConfigurationSection(maskId);
                            if (mask != null) {
                                Material material = Material.valueOf(mask.getString("material", "STONE"));
                                boolean enchanted = mask.getBoolean("enchanted", false);
                                String s = Colorize.text(mask.getString("name", "Default item"));
                                masks.put(maskId, new Mask(material, s, enchanted));
                            }
                        }
                    }
                }

            }
            DropParticle dropParticle = new DropParticle( sound, soundVolume, soundPitch, particle, paritcleAmount, offsetX, offsetY, offsetZ, minY, maxY, minSpeed, maxSpeed, pickupDelay);


            Map<String, List<String>> listeners = new HashMap<>();
            ConfigurationSection listenerSection = configuration.getConfigurationSection("listeners");
            if (listenerSection != null) {
                for (String listenerId : listenerSection.getKeys(false)) {
                    List<String> actions = Colorize.list(listenerSection.getStringList(listenerId));
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

                    String bossBarTitle = Colorize.text(bossBar.getString("title", ""));
                    int bossBarDuration = bossBar.getInt("duration", -1);

                    BarColor bossBarColor = BarColor.valueOf(bossBar.getString("Color", "BLUE"));
                    BarStyle bossBarStyle = BarStyle.valueOf(bossBar.getString("Style", "PROGRESS"));

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

            List<Items.ItemsData> items = plugin.getItems().getData().get(id);


            Mob mob = new Mob(
                    id,
                    movingRadius,
                    location,
                    nameVisible,
                    name,
                    armorItem,
                    entityType,
                    health,
                    ai,
                    glow,
                    canPickupItems,
                    visualFire,
                    isBaby,
                    phases,
                    flyingDropParticle,
                    dropParticle,
                    isMask,
                    masks,
                    listeners,
                    tasks,
                    bossBarsMap,
                    onSpawnActions,
                    onHitActions,
                    onDeathActions,
                    lootAmount,
                    customDrops,
                    items);
            Maps.mobs.put(id, mob);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
