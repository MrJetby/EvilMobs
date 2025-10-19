package me.jetby.evilmobs.configurations;

import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.Maps;
import me.jetby.evilmobs.locale.Lang;
import me.jetby.evilmobs.records.*;
import me.jetby.evilmobs.tools.YamlCommentEditor;
import me.jetby.treex.bukkit.LocationHandler;
import me.jetby.treex.text.Colorize;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

        file.mkdirs();

        String[] defaults = {"example.yml", "example_minion.yml", "evil.yml"};

        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (!f.getName().endsWith(".yml")) continue;
                FileConfiguration config = YamlConfiguration.loadConfiguration(f);
                loadMob(config);
            }
        }

        for (String name : defaults) {
            File target = new File(file, name);
            if (!target.exists()) {
                plugin.saveResource("mobs/" + name, false);
                if (name.equals("example.yml")) {
                    addCommentsToExample(target);
                }
                FileConfiguration config = YamlConfiguration.loadConfiguration(target);
                loadMob(config);
            } else if (name.equals("example.yml")) {
                if (isDefaultExample(target)) {
                    addCommentsToExample(target);
                }
            }
        }
        LOGGER.success(Maps.mobs.size() + " mobs loaded");
    }

    private void addCommentsToExample(File target) {
        Lang lang = plugin.getLang();
        try {
            YamlCommentEditor editor = new YamlCommentEditor(target);
            editor.setComment("id", lang.getConfig().getStringList("comments.mobs.example.id"));
            editor.setComment("name", lang.getConfig().getStringList("comments.mobs.example.name"));
            editor.setComment("moving-radius", lang.getConfig().getStringList("comments.mobs.example.moving-radius"));
            editor.setComment("spawn-location", lang.getConfig().getStringList("comments.mobs.example.spawn-location"));
            editor.setComment("listeners", lang.getConfig().getStringList("comments.mobs.example.listeners"));
            editor.setComment("bossBars.example_health.color", lang.getConfig().getStringList("comments.mobs.example.bossBars.example_health.color"));
            editor.setComment("bossBars.example_health.style", lang.getConfig().getStringList("comments.mobs.example.bossBars.example_health.style"));
            editor.setComment("bossBars.example_health.duration", lang.getConfig().getStringList("comments.mobs.example.bossBars.example_health.duration"));
            editor.setComment("tasks.example.delay", lang.getConfig().getStringList("comments.mobs.example.tasks.example.delay"));
            editor.setComment("tasks.example.period", lang.getConfig().getStringList("comments.mobs.example.tasks.example.period"));
            editor.setComment("tasks.example.actions", lang.getConfig().getStringList("comments.mobs.example.tasks.example.actions"));
            editor.setComment("tasks.example.amount", lang.getConfig().getStringList("comments.mobs.example.tasks.example.amount"));
            editor.setComment("phases.example_placeholder", lang.getConfig().getStringList("comments.mobs.example.phases.example_placeholder"));
            editor.setComment("drop-settings.lootAmount", lang.getConfig().getStringList("comments.mobs.example.drop-settings.lootAmount"));
            editor.setComment("drop-settings.only-custom", lang.getConfig().getStringList("comments.mobs.example.drop-settings.only-custom"));
            editor.setComment("drop-settings.flying-drop-particle", lang.getConfig().getStringList("comments.mobs.example.drop-settings.flying-drop-particle"));
            editor.setComment("drop-settings.flying-drop-particle.pickup-delay", lang.getConfig().getStringList("comments.mobs.example.drop-settings.pickup-delay"));
            editor.setComment("drop-settings.mask", lang.getConfig().getStringList("comments.mobs.example.mask.mask"));
            editor.setComment("drop-settings.mask.items.m1", lang.getConfig().getStringList("comments.mobs.example.mask.items.m1"));
            editor.save();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isDefaultExample(File target) {
        try {
            java.io.InputStream defaultStream = plugin.getResource("mobs/example.yml");
            if (defaultStream == null) return false;
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            YamlConfiguration currentConfig = YamlConfiguration.loadConfiguration(target);
            return configsEqual(defaultConfig, currentConfig);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean configsEqual(ConfigurationSection def, ConfigurationSection curr) {
        if (def == null && curr == null) return true;
        if (def == null || curr == null) return false;
        Set<String> defKeys = def.getKeys(false);
        Set<String> currKeys = curr.getKeys(false);
        if (!defKeys.equals(currKeys)) return false;
        for (String key : defKeys) {
            Object defVal = def.get(key);
            Object currVal = curr.get(key);
            if (defVal instanceof ConfigurationSection && currVal instanceof ConfigurationSection) {
                if (!configsEqual((ConfigurationSection) defVal, (ConfigurationSection) currVal)) return false;
            } else if (!Objects.equals(defVal, currVal)) {
                return false;
            }
        }
        return true;
    }

    private void loadMob(FileConfiguration configuration) {
        try {

            String id = configuration.getString("id");
            int movingRadius = configuration.getInt("moving-radius", -1);

            World world = Bukkit.getWorld(configuration.getString("rtp.world", "world"));
            int min, max;

            min = configuration.getInt("rtp.min", 0);
            max = configuration.getInt("rtp.max", 0);
            Set<Material> materials = EnumSet.noneOf(Material.class);
            for (String materialName : configuration.getStringList("rtp.materials")) {
                materials.add(Material.getMaterial(materialName));
            }
            String materialBlockListType = configuration.getString("rtp.block-list-type", "WHITELIST").toUpperCase();
            Rtp rtp = new Rtp(world, min, max, materials, materialBlockListType);


            String locationType = configuration.getString("spawn-location", "rtp");
            Location location;
            if (!locationType.equalsIgnoreCase("rtp")) {
                location = LocationHandler.deserialize(locationType);
            } else {
                location = null;
            }

            String name = Colorize.text(configuration.getString("name"));
            boolean nameVisible = !name.isEmpty();

            List<ArmorItem> armorItems = new ArrayList<>();

            ConfigurationSection armorSection = configuration.getConfigurationSection("armor");
            if (armorSection != null) {
                for (String key : armorSection.getKeys(false)) {
                    ConfigurationSection section = armorSection.getConfigurationSection(key);
                    if (section == null) {
                        continue;
                    }
                    ItemStack item = new ItemStack(Material.valueOf(section.getString("item")));
                    for (String str : section.getStringList("enchants")) {
                        String[] parts = str.split(";");
                        Enchantment enchantment = Enchantment.getByName(getEnchant(parts[0]));
                        if (enchantment == null) {
                            LOGGER.warn("Enchantment " + parts[0] + " was not found");
                            continue;
                        }
                        int level = Integer.parseInt(parts[1]);
                        item.addEnchantment(enchantment, level);
                    }

                    armorItems.add(new ArmorItem(key, item, section.getInt("drop-chance", 0)));
                }
            }


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
            double particleMinY = 5.0;
            double particleMaxY = 10.0;
            double minSpeed = 0.5;
            double maxSpeed = 1.0;
            int pickupDelay = 0;

            if (dropSettings != null) {
                customDrops = dropSettings.getBoolean("only-custom", false);
                lootAmount = dropSettings.getString("lootAmount", "0");


                ConfigurationSection dropParticleSection = dropSettings.getConfigurationSection("flying-drop-particle");

                if (dropParticleSection != null) {
                    flyingDropParticle = dropParticleSection.getBoolean("enabled", false);

                    sound = Sound.valueOf(dropParticleSection.getString("sound".toUpperCase(), "ENTITY_ITEM_PICKUP"));
                    soundVolume = (float) dropParticleSection.getDouble("volume", 1);
                    soundPitch = (float) dropParticleSection.getDouble("pitch", 1);
                    paritcleAmount = dropParticleSection.getInt("amount", 1);
                    offsetX = dropParticleSection.getDouble("offset-x", 0);
                    offsetY = dropParticleSection.getDouble("offset-y", 0);
                    offsetZ = dropParticleSection.getDouble("offset-z", 0);
                    particleMinY = dropParticleSection.getDouble("min-y", 5.0);
                    particleMaxY = dropParticleSection.getDouble("max-y", 10.0);
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
            DropParticle dropParticle = new DropParticle(sound, soundVolume, soundPitch, particle, paritcleAmount, offsetX, offsetY, offsetZ, particleMinY, particleMaxY, minSpeed, maxSpeed, pickupDelay);

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
                    BarStyle bossBarStyle = BarStyle.valueOf(bossBar.getString("Style", "SOLID"));

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
                    locationType,
                    location,
                    nameVisible,
                    name,
                    armorItems,
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
                    rtp,
                    lootAmount,
                    customDrops,
                    items);
            Maps.mobs.put(id, mob);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getEnchant(String enchant) {
        return switch (enchant) {
            case "SHARPNESS" -> "DAMAGE_ALL";
            case "PROTECTION" -> "PROTECTION_ENVIRONMENTAL";
            default -> enchant;
        };
    }

}