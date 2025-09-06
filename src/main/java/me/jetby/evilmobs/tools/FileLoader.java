package me.jetby.evilmobs.tools;

import lombok.experimental.UtilityClass;
import me.jetby.evilmobs.EvilMobs;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@UtilityClass
public class FileLoader {

    public FileConfiguration getFileConfiguration(String fileName) {
        File file = new File(EvilMobs.getInstance().getDataFolder().getAbsolutePath(), fileName);
        if (!file.exists()) {
            EvilMobs.getInstance().saveResource(fileName, false);

        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getFileConfiguration(String path, String fileName) {
        File file = new File(path, fileName);

        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    public File getFile(String path, String fileName) {
        File file = new File(path, fileName);

        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }
}
