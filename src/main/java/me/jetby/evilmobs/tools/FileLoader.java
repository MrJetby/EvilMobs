package me.jetby.evilmobs.tools;

import lombok.experimental.UtilityClass;
import me.jetby.evilmobs.EvilMobs;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@UtilityClass
public class FileLoader {

    public FileConfiguration getFileConfiguration(String fileName) {
        File file = new File(EvilMobs.getInstance().getDataFolder().getAbsolutePath(), fileName);
        if (!file.exists()) {
            EvilMobs.getInstance().saveResource(fileName, false);

        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public File getFile(String fileName) {
        File file = new File(EvilMobs.getInstance().getDataFolder().getAbsoluteFile(), fileName);
        if (!file.exists()) {
            EvilMobs.getInstance().saveResource(fileName, false);
        }
        return file;
    }

}
