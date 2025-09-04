package me.jetby.evilmobs.tools;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class Logger {

    public void warn(String message) {
        Bukkit.getConsoleSender().sendMessage("§e[EvilMobs] §e" + message);
    }

    public void info(String message) {
        Bukkit.getConsoleSender().sendMessage("§a[EvilMobs] §f" + message);
    }

    public void success(String message) {
        Bukkit.getConsoleSender().sendMessage("§a[EvilMobs] §a" + message);
    }

    public void error(String message) {
        Bukkit.getConsoleSender().sendMessage("§c[EvilMobs] §c" + message);
    }

    public void msg(String message) {
        Bukkit.getConsoleSender().sendMessage("§6[EvilMobs] §f" + message);
    }
}
