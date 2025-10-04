package me.jetby.evilmobs.configurations;

import lombok.Getter;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.locale.Lang;
import me.jetby.evilmobs.tools.FileLoader;
import me.jetby.evilmobs.tools.YamlCommentEditor;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

@Getter
public class Config {

    final EvilMobs plugin;
    final FileConfiguration configuration;
    final File file;

    String lang;

    boolean autoStartEnabled;
    boolean freeze;
    int autoStartMinOnline;
    int zone;
    Object time;

    private String formattedTimeFormat;

    private final FileConfiguration language;

    public Config(EvilMobs plugin) {
        this.plugin = plugin;
        this.configuration = FileLoader.getFileConfiguration("config.yml");
        this.file = FileLoader.getFile("config.yml");

        lang = configuration.getString("lang", "en");
        Lang lang = new Lang(plugin, this.lang);
        plugin.setLang(lang);
        this.language = lang.getConfig();
    }

    public void load() {
        try {
            YamlCommentEditor editor = new YamlCommentEditor(file);

            editor.setComment("lang", language.getStringList("comments.config.lang"));

            autoStartEnabled = configuration.getBoolean("auto-start.enabled", false);

            editor.setComment("auto-start.min-online", language.getStringList("comments.config.auto-start.min-online"));
            autoStartMinOnline = configuration.getInt("auto-start.min-online", 0);

            editor.setComment("auto-start.freeze", language.getStringList("comments.config.auto-start.freeze"));
            freeze = configuration.getBoolean("auto-start.freeze", false);

            editor.setComment("auto-start.time", language.getStringList("comments.config.auto-start.time"));
            time = configuration.get("auto-start.time");

            editor.setComment("auto-start.zone", language.getStringList("comments.config.auto-start.zone"));
            zone = Integer.parseInt(configuration.getString("auto-start.zone", "GMT-3")
                    .replace("GMT", "")
                    .replace("UTC", "")
                    .replace("+", "")
            );


            formattedTimeFormat = configuration.getString("formattedTime.show-format", "%weeks% %days% %hours% %minutes% %seconds%");

            editor.save();
        } catch (Exception e) {

        }

    }

}
