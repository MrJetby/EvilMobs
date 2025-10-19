package me.jetby.evilmobs;

import com.jodexindustries.jguiwrapper.common.JGuiInitializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
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
import me.jetby.evilmobs.commands.Admin;
import me.jetby.evilmobs.configurations.Config;
import me.jetby.evilmobs.configurations.Items;
import me.jetby.evilmobs.configurations.Mobs;
import me.jetby.evilmobs.configurations.Particles;
import me.jetby.evilmobs.gui.MainMenu;
import me.jetby.evilmobs.listeners.ItemPickup;
import me.jetby.evilmobs.listeners.OnDamage;
import me.jetby.evilmobs.listeners.OnDeath;
import me.jetby.evilmobs.listeners.OnMove;
import me.jetby.evilmobs.locale.Lang;
import me.jetby.evilmobs.tools.*;
import me.jetby.treex.actions.ActionEntry;
import me.jetby.treex.actions.ActionTypeRegistry;
import me.jetby.treex.tools.LogInitialize;
import me.jetby.treex.tools.log.Logger;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;

@Getter
public final class EvilMobs extends JavaPlugin {

    public Config cfg;
    public FormatTime formatTime;
    public Mobs mobs;
    public Particles particles;
    public MainMenu mainMenu;
    public Items items;
    @Setter
    public Lang lang;
    public AutoStart autoStart;


    @Getter(AccessLevel.NONE)
    private static EvilMobs INSTANCE;
    @Getter(AccessLevel.NONE)
    public EvilMobsPlaceholderExpansion evilMobsPlaceholderExpansion;


    public static NamespacedKey NAMESPACED_KEY;

    public static EvilMobs getInstance() {
        return INSTANCE;
    }

    public static Logger LOGGER;


    @Override
    public void onEnable() {

        try {
            new TreexInitializer(this);
            new Actions().registerCustomActions();
        } catch (IOException ex) {
            getLogger().warning("Failed to initialize Treex: " + ex.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        LOGGER = LogInitialize.getLogger("EvilMobs");

        INSTANCE = this;
        NAMESPACED_KEY = new NamespacedKey(this, "data");

        cfg = new Config(this);
        cfg.load();

        formatTime = new FormatTime(this);

        MiniBar.init(this);
        JGuiInitializer.init(this, false);


        items = new Items(FileLoader.getFile("items.yml"));
        items.load();

        mobs = new Mobs(this);
        mobs.load();

        particles = new Particles();
        particles.load();


        try {
            Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");
            mainMenu = new MainMenu(this, true);
        } catch (Exception e) {
            mainMenu = new MainMenu(this, false);
        }


        new bStats(this, 27388);

        setupPlaceholders();

        if (cfg.isAutoStartEnabled()) autoStart = new AutoStart(this,
                cfg.getAutoStartMinOnline(),
                cfg.isFreeze(),
                cfg.getZone(),
                cfg.getTime()
        );

        getServer().getPluginManager().registerEvents(new OnDeath(this), this);
        getServer().getPluginManager().registerEvents(new OnDamage(), this);
        getServer().getPluginManager().registerEvents(new OnMove(), this);
        getServer().getPluginManager().registerEvents(new ItemPickup(), this);


        PluginCommand evilmobs = getCommand("evilmobs");
        if (evilmobs != null)
            evilmobs.setExecutor(new Admin(this));

        rollbackMobTasks();

        Version version = new Version(this);
        getServer().getPluginManager().registerEvents(version, this);
        for (String str : version.getAlert()) {
            LOGGER.info(str);
        }


    }

    private void rollbackMobTasks() {

        for (World world : Bukkit.getWorlds()) {
            for (LivingEntity e : world.getLivingEntities()) {
                if (!e.getPersistentDataContainer().has(NAMESPACED_KEY, PersistentDataType.STRING)) continue;
                String id = e.getPersistentDataContainer().get(NAMESPACED_KEY, PersistentDataType.STRING);

                if (!Maps.mobs.containsKey(id)) continue;

                MobCreator mobCreator = new MobCreator(Maps.mobs.get(id));
                mobCreator.runTasks(e);
                Maps.mobCreators.put(id, mobCreator);

            }
        }
    }


    private void setupPlaceholders() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            LOGGER.warn("PlaceholderAPI not found. Placeholders are not being working");
        } else {
            evilMobsPlaceholderExpansion = new EvilMobsPlaceholderExpansion(this);
            evilMobsPlaceholderExpansion.register();
        }
    }


    @Override
    public void onDisable() {
        if (autoStart != null) autoStart.stop();

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Item item && item.hasMetadata("evilmobs_originalItem")) {
                    item.remove();
                }
            }
        }
        for (Map<String, MiniTask> map : Maps.tasks.values()) {
            for (MiniTask miniTask : map.values()) {
                miniTask.cancel();
            }
        }
        List<UUID> uuids = new ArrayList<>(MiniBar.datas.keySet());
        for (UUID uuid : uuids) {
            MiniBar.deleteBossBar(uuid);
        }
        if (evilMobsPlaceholderExpansion != null) evilMobsPlaceholderExpansion.unregister();

        if (items != null)
            items.save();
    }

}
