package me.jetby.evilmobs;

import com.jodexindustries.jguiwrapper.common.JGuiInitializer;
import lombok.Getter;
import lombok.Setter;
import me.jetby.evilmobs.actions.abilities.EffectNear;
import me.jetby.evilmobs.actions.entity.*;
import me.jetby.evilmobs.actions.minions.KillAllMinions;
import me.jetby.evilmobs.actions.minions.SpawnAsMinion;
import me.jetby.evilmobs.actions.particles.SendParticle;
import me.jetby.evilmobs.commands.Admin;
import me.jetby.evilmobs.configurations.*;
import me.jetby.evilmobs.gui.MainMenu;
import me.jetby.evilmobs.listeners.ItemPickup;
import me.jetby.evilmobs.listeners.OnDamage;
import me.jetby.evilmobs.listeners.OnDeath;
import me.jetby.evilmobs.listeners.OnMove;
import me.jetby.evilmobs.locale.Lang;
import me.jetby.evilmobs.tools.FileLoader;
import me.jetby.evilmobs.tools.MiniBar;
import me.jetby.evilmobs.tools.MiniTask;
import me.jetby.evilmobs.actions.*;
import me.jetby.evilmobs.actions.bossBar.*;
import me.jetby.evilmobs.actions.abilities.Fireball;
import me.jetby.evilmobs.actions.abilities.Lightning;
import me.jetby.evilmobs.actions.abilities.Teleport;
import me.jetby.evilmobs.actions.task.TaskRun;
import me.jetby.evilmobs.actions.task.TaskStop;
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

import java.util.*;

public final class EvilMobs extends JavaPlugin {


    @Getter
    private Config cfg;
    @Getter
    private ArmorSets armorSets;
    @Getter
    private Mobs mobs;
    @Getter
    private Particles particles;

    public static NamespacedKey NAMESPACED_KEY;

    static EvilMobs INSTANCE;

    EvilMobsPlaceholderExpansion evilMobsPlaceholderExpansion;

    public static EvilMobs getInstance() {
        return INSTANCE;
    }

    @Getter
    private MainMenu mainMenu;

    public static final Logger LOGGER = LogInitialize.getLogger("EvilMobs");
    @Getter
    private Items items;

    @Getter @Setter
    private Lang lang;

    @Override
    public void onEnable() {
        INSTANCE = this;
        NAMESPACED_KEY = new NamespacedKey(this, "data");

        cfg = new Config(this);
        cfg.load();

        lang = new Lang(this);
        MiniBar.init(this);
        JGuiInitializer.init(this, false);

        armorSets = new ArmorSets();
        armorSets.load();

        items = new Items(FileLoader.getFile("items.yml"));
        items.load();

        mobs = new Mobs(this);
        mobs.load();

        particles = new Particles();
        particles.load();

        mainMenu = new MainMenu(this);

        setupPlaceholders();

        getServer().getPluginManager().registerEvents(new OnDeath(this), this);
        getServer().getPluginManager().registerEvents(new OnDamage(), this);
        getServer().getPluginManager().registerEvents(new OnMove(), this);
        getServer().getPluginManager().registerEvents(new ItemPickup(), this);

        PluginCommand evilmobs = getCommand("evilmobs");
        if (evilmobs != null)
            evilmobs.setExecutor(new Admin(this));

        registerCustomActions();

        rollbackMobTasks();

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

    private void registerCustomActions() {
        ActionTypeRegistry.register("TELEPORT", new Teleport());
        ActionTypeRegistry.register("FIREBALL", new Fireball());
        ActionTypeRegistry.register("LIGHTNING", new Lightning());

        ActionTypeRegistry.register("CREATE_BOSSBAR", new CreateBossBar());
        ActionTypeRegistry.register("REMOVE_BOSSBAR", new RemoveBossBar());
        ActionTypeRegistry.register("REMOVE_BOSSBAR_NEAR", new RemoveBossBarNear());
        ActionTypeRegistry.register("SHOW_BOSSBAR", new ShowBossBar());
        ActionTypeRegistry.register("SHOW_BOSSBAR_NEAR", new ShowBossBarNear());
        ActionTypeRegistry.register("DELETE_BOSSBAR", new DeleteBossBar());

        ActionTypeRegistry.register("TASK_RUN", new TaskRun());
        ActionTypeRegistry.register("TASK_STOP", new TaskStop());

        ActionTypeRegistry.register("DROP", new Drop());
        ActionTypeRegistry.register("DROP_CLEAR", new DropClear());


        ActionTypeRegistry.register("SET_AGE", new SetAge());
        ActionTypeRegistry.register("SETAGE", new SetAge());

        ActionTypeRegistry.register("SET_AI", new SetAI());
        ActionTypeRegistry.register("SETAI", new SetAI());

        ActionTypeRegistry.register("SET_CAN_PICKUP_ITEMS", new SetCanPickupItems());
        ActionTypeRegistry.register("SETCANPICKUPITEMS", new SetCanPickupItems());

        ActionTypeRegistry.register("SET_GLOW", new SetGlow());
        ActionTypeRegistry.register("SETGLOW", new SetGlow());

        ActionTypeRegistry.register("SET_NAME", new SetName());
        ActionTypeRegistry.register("SETNAME", new SetName());

        ActionTypeRegistry.register("SEND_PARTICLE", new SendParticle());

        ActionTypeRegistry.register("EFFECT_NEAR", new EffectNear());
        ActionTypeRegistry.register("EFFECTNEAR", new EffectNear());

        ActionTypeRegistry.register("SET_TARGET", new SetTarget());
        ActionTypeRegistry.register("SETTARGET", new SetTarget());

        ActionTypeRegistry.register("SPAWN_AS_MINION", new SpawnAsMinion());
        ActionTypeRegistry.register("SPAWNASMINION", new SpawnAsMinion());
        ActionTypeRegistry.register("KILL_ALL_MINIONS", new KillAllMinions());
        ActionTypeRegistry.register("KILLALLMINIONS", new KillAllMinions());
        ActionTypeRegistry.register("KILL_ALL_MINION", new KillAllMinions());
        ActionTypeRegistry.register("KILLALLMINION", new KillAllMinions());


    }

    private void setupPlaceholders() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            LOGGER.warn("PlaceholderAPI not found. Placeholders are not being working");
        } else {
            evilMobsPlaceholderExpansion = new EvilMobsPlaceholderExpansion();
            evilMobsPlaceholderExpansion.register();
        }
    }
    @Override
    public void onDisable() {
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
        if (evilMobsPlaceholderExpansion!=null) evilMobsPlaceholderExpansion.unregister();

        items.save();
    }

}
