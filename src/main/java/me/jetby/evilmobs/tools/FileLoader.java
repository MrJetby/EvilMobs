package me.jetby.evilmobs.tools;

import lombok.experimental.UtilityClass;
import me.jetby.evilmobs.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@UtilityClass
public class FileLoader {

    public FileConfiguration getFileConfiguration(String fileName) {
        File file = new File(Main.getInstance().getDataFolder().getAbsolutePath(), fileName);
        if (!file.exists()) {
            Main.getInstance().saveResource(fileName, false);

        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public File getFile(String fileName) {
        File file = new File(Main.getInstance().getDataFolder().getAbsoluteFile(), fileName);
        if (!file.exists()) {
            Main.getInstance().saveResource(fileName, false);
        }
        return file;
    }

}
