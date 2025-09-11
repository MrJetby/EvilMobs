package me.jetby.evilmobs;

import lombok.Getter;
import me.jetby.evilmobs.commands.Admin;
import me.jetby.evilmobs.configurations.ArmorSets;
import me.jetby.evilmobs.configurations.Mobs;
import me.jetby.evilmobs.listeners.OnDamage;
import me.jetby.evilmobs.listeners.OnDeath;
import me.jetby.evilmobs.tools.MiniTask;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class EvilMobs extends JavaPlugin {


    @Getter
    private ArmorSets armorSets;
    @Getter
    private Mobs mobs;

    public static NamespacedKey NAMESPACED_KEY;

    private static EvilMobs INSTANCE;

    public static EvilMobs getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        NAMESPACED_KEY = new NamespacedKey(this, "data");

        armorSets = new ArmorSets();
        armorSets.load();

        mobs = new Mobs(this);
        mobs.load();


        getServer().getPluginManager().registerEvents(new OnDeath(this), this);
        getServer().getPluginManager().registerEvents(new OnDamage(this), this);

        PluginCommand evilmobs = getCommand("evilmobs");
        if (evilmobs != null)
            evilmobs.setExecutor(new Admin(this));

    }


    @Getter
    private final Map<UUID, Map<String, MiniTask>> tasks = new HashMap<>();
}
