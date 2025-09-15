package me.jetby.evilmobs.locale;

import lombok.experimental.UtilityClass;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.configurations.Config;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

import static me.jetby.evilmobs.EvilMobs.LOGGER;


@UtilityClass
public class Lang {

    public String GUI_MAIN_TITLE;
    public String GUI_MAIN_DISPLAY_NAME;
    public List<String> GUI_MAIN_LORE;

    public String GUI_CHANCE_EDITOR_TITLE;
    public String GUI_CHANCE_EDITOR_DISPLAY_NAME;
    public List<String> GUI_CHANCE_EDITOR_LORE;

    public String GUI_INV_EDITOR_TITLE;
    public String GUI_INV_EDITOR_INVENTORY_DISPLAY_NAME;
    public List<String> GUI_INV_EDITOR_INVENTORY_LORE;
    public String GUI_INV_EDITOR_NEW_INVENTORY_DISPLAY_NAME;
    public List<String> GUI_INV_EDITOR_NEW_INVENTORY_LORE;



    public String GUI_ITEM_EDITOR_TITLE;


    public void init(EvilMobs plugin) {
        File langFolder = new File(plugin.getDataFolder(), "lang");

        Config config = plugin.getCfg();

        File[] files = langFolder.listFiles();

        String[] defaults = {"ru.yml", "en.yml"};

        for (String name : defaults) {
            File target = new File(langFolder, name);

            if (!target.exists()) {
                plugin.saveResource("lang/" + name, false);
                FileConfiguration configuration = YamlConfiguration.loadConfiguration(target);
                String foundedLang = configuration.getString("lang");
                if (foundedLang==null) continue;
                if (!foundedLang.equalsIgnoreCase(config.getLang())) continue;
                loadLang(configuration);
                LOGGER.info("Файл lang/" + name + ".yml создан");
            }

        }


        if (files == null) return;

        for (File file : files) {
            if (!file.getName().endsWith(".yml")) continue;
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            String foundedLang = configuration.getString("lang");
            if (foundedLang==null) continue;
            if (!foundedLang.equalsIgnoreCase(config.getLang())) continue;
            loadLang(configuration);
            LOGGER.info("Файл lang/" + configuration.getString("lang") + ".yml загружен");
        }
    }

    private void loadLang(FileConfiguration configuration) {
        ConfigurationSection gui_section = configuration.getConfigurationSection("gui");
        if (gui_section!=null) {
            ConfigurationSection gui_main_section = gui_section.getConfigurationSection("main");
            ConfigurationSection gui_chanceEditor_section = gui_section.getConfigurationSection("chanceEditor");
            ConfigurationSection gui_invEditor_section = gui_section.getConfigurationSection("invEditor");
            ConfigurationSection gui_itemEditor_section = gui_section.getConfigurationSection("itemEditor");
            if (gui_main_section!=null) {
                GUI_MAIN_TITLE = gui_main_section.getString("title");
                GUI_MAIN_DISPLAY_NAME = gui_main_section.getString("display_name");
                GUI_MAIN_LORE = gui_main_section.getStringList("lore");
            }
            if (gui_chanceEditor_section!=null) {
                GUI_CHANCE_EDITOR_TITLE = gui_chanceEditor_section.getString("title");
                GUI_CHANCE_EDITOR_DISPLAY_NAME = gui_chanceEditor_section.getString("display_name");
                GUI_CHANCE_EDITOR_LORE = gui_chanceEditor_section.getStringList("lore");
            }
            if (gui_invEditor_section!=null) {
                GUI_INV_EDITOR_TITLE = gui_invEditor_section.getString("title");
                ConfigurationSection inventorySection = gui_invEditor_section.getConfigurationSection("inventory");
                if (inventorySection!=null) {
                    GUI_INV_EDITOR_INVENTORY_DISPLAY_NAME = inventorySection.getString("display_name");
                    GUI_INV_EDITOR_INVENTORY_LORE = inventorySection.getStringList("lore");
                }
                ConfigurationSection newInventorySection = gui_invEditor_section.getConfigurationSection("new_inventory");
                if (newInventorySection!=null) {
                    GUI_INV_EDITOR_NEW_INVENTORY_DISPLAY_NAME = newInventorySection.getString("display_name");
                    GUI_INV_EDITOR_NEW_INVENTORY_LORE = newInventorySection.getStringList("lore");
                }

            }
            if (gui_itemEditor_section!=null) {
                GUI_ITEM_EDITOR_TITLE = gui_itemEditor_section.getString("title");
            }

        }

    }
}
