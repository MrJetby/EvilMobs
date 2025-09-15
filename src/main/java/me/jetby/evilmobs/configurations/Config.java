package me.jetby.evilmobs.configurations;

import lombok.Getter;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.tools.FileLoader;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class Config {

    final EvilMobs plugin;
    final FileConfiguration configuration;

    String lang;

    public Config(EvilMobs plugin) {
        this.plugin = plugin;
        this.configuration = FileLoader.getFileConfiguration("config.yml");
    }

    public void load() {
        lang = configuration.getString("system-lang", "en");
    }
}
