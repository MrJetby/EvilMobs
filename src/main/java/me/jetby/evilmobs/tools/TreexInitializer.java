package me.jetby.evilmobs.tools;

import me.jetby.evilmobs.actions.Drop;
import me.jetby.evilmobs.actions.DropClear;
import me.jetby.evilmobs.actions.abilities.EffectNear;
import me.jetby.evilmobs.actions.abilities.Fireball;
import me.jetby.evilmobs.actions.abilities.Lightning;
import me.jetby.evilmobs.actions.abilities.Teleport;
import me.jetby.evilmobs.actions.bossBar.*;
import me.jetby.evilmobs.actions.entity.*;
import me.jetby.evilmobs.actions.minions.KillAllMinions;
import me.jetby.evilmobs.actions.minions.SpawnAsMinion;
import me.jetby.evilmobs.actions.particles.SendParticle;
import me.jetby.evilmobs.actions.task.TaskRun;
import me.jetby.evilmobs.actions.task.TaskStop;
import me.jetby.treex.Treex;
import me.jetby.treex.actions.ActionEntry;
import me.jetby.treex.actions.ActionTypeRegistry;
import me.jetby.treex.events.TreexOnPluginDisable;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class TreexInitializer implements Listener {

    private final JavaPlugin plugin;

    @EventHandler
    public void onDisable(TreexOnPluginDisable e) {
        Bukkit.getPluginManager().disablePlugin(plugin);
    }

    public TreexInitializer(JavaPlugin plugin) throws IOException {
        this.plugin = plugin;

        if (Bukkit.getPluginManager().getPlugin("Treex") == null || !Bukkit.getPluginManager().getPlugin("Treex").isEnabled()) {
            downloadAndLoad(Version.getRaw("https://raw.githubusercontent.com/MrJetby/Treex/refs/heads/master/DOWNLOAD_LINK"));
        }
        Treex.init(plugin);

        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    private void downloadAndLoad(String link) {
        try {
            File file = getFile(link);

            Plugin pl = Bukkit.getPluginManager().loadPlugin(file);
            if (pl != null) {
                pl.onLoad();
                Bukkit.getPluginManager().enablePlugin(pl);
            } else {
                plugin.getLogger().warning("Ошибка загрузки плагина!");
            }

        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static @NotNull File getFile(String link) throws IOException {
        URL url = new URL(link);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(3000);

        File pluginDir = new File("plugins");
        if (!pluginDir.exists()) pluginDir.mkdirs();

        String fileName = new File(url.getPath()).getName();
        if (!fileName.endsWith(".jar")) fileName += ".jar";

        File file = new File(pluginDir, fileName);

        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
        return file;
    }

}
