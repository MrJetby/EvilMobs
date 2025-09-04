package me.jetby.evilmobs;

import lombok.Getter;
import me.jetby.evilmobs.commands.Admin;
import me.jetby.evilmobs.configurations.ArmorSets;
import me.jetby.evilmobs.configurations.Mobs;
import me.jetby.evilmobs.listeners.OnDamage;
import me.jetby.evilmobs.listeners.OnDeath;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class Main extends JavaPlugin {


    private ArmorSets armorSets;
    private Mobs mobs;

    public static final NamespacedKey NAMESPACED_KEY = new NamespacedKey("evilmobs", "data");


    private static Main INSTANCE;

    public static Main getInstance() {
        return INSTANCE;
    }
    @Override
    public void onEnable() {
        INSTANCE = this;

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


}
