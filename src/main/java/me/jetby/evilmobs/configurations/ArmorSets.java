package me.jetby.evilmobs.configurations;

import lombok.Getter;
import me.jetby.evilmobs.records.ArmorItem;
import me.jetby.evilmobs.tools.FileLoader;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.jetby.evilmobs.EvilMobs.LOGGER;


public class ArmorSets {

    final FileConfiguration configuration = FileLoader.getFileConfiguration("armors-sets.yml");

    @Getter
    private final Map<String, List<ArmorItem>> armorItems = new HashMap<>();

    public void load() {

        try {
            List<ArmorItem> items = new ArrayList<>();
            for (String id : configuration.getKeys(false)) {
                items.clear();

                ConfigurationSection itemsSection = configuration.getConfigurationSection(id);
                if (itemsSection == null) {
                    continue;
                }

                for (String key : itemsSection.getKeys(false)) {
                    ConfigurationSection section = itemsSection.getConfigurationSection(key);
                    if (section == null) {
                        continue;
                    }
                    ItemStack item = new ItemStack(Material.valueOf(section.getString("item")));
                    for (String str : section.getStringList("enchants")) {
                        String[] parts = str.split(";");
                        Enchantment enchantment = Enchantment.getByName(parts[0]);
                        if (enchantment == null) {
                            LOGGER.warn("Enchantment " + parts[0] + " was not found");
                            continue;
                        }
                        int level = Integer.parseInt(parts[1]);
                        item.addEnchantment(enchantment, level);
                    }

                    items.add(new ArmorItem(key, item, section.getInt("drop-chance", 0)));
                }
                armorItems.put(id, items);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
