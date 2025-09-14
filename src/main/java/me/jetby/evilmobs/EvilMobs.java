package me.jetby.evilmobs;

import com.jodexindustries.jguiwrapper.common.JGuiInitializer;
import lombok.Getter;
import me.jetby.evilmobs.actions.particles.SendParticle;
import me.jetby.evilmobs.commands.Admin;
import me.jetby.evilmobs.configurations.ArmorSets;
import me.jetby.evilmobs.configurations.Mobs;
import me.jetby.evilmobs.configurations.Particles;
import me.jetby.evilmobs.listeners.OnDamage;
import me.jetby.evilmobs.listeners.OnDeath;
import me.jetby.evilmobs.listeners.OnMove;
import me.jetby.evilmobs.tools.MiniBar;
import me.jetby.evilmobs.tools.MiniTask;
import me.jetby.evilmobs.actions.*;
import me.jetby.evilmobs.actions.bossBar.*;
import me.jetby.evilmobs.actions.abillities.Fireball;
import me.jetby.evilmobs.actions.abillities.Lightning;
import me.jetby.evilmobs.actions.abillities.Teleport;
import me.jetby.evilmobs.actions.task.TaskRun;
import me.jetby.evilmobs.actions.task.TaskStop;
import me.jetby.treex.actions.ActionTypeRegistry;
import me.jetby.treex.tools.LogInitialize;
import me.jetby.treex.tools.log.Logger;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class EvilMobs extends JavaPlugin {


    @Getter
    private ArmorSets armorSets;
    @Getter
    private Mobs mobs;
    @Getter
    private Particles particles;

    public static NamespacedKey NAMESPACED_KEY;

    private static EvilMobs INSTANCE;

    EvilMobsPlaceholderExpansion evilMobsPlaceholderExpansion;

    public static EvilMobs getInstance() {
        return INSTANCE;
    }

    public static final Logger LOGGER = LogInitialize.getLogger("EvilMobs");

    @Override
    public void onEnable() {
        INSTANCE = this;
        NAMESPACED_KEY = new NamespacedKey(this, "data");

        MiniBar.init(this);
        JGuiInitializer.init(this, false);

        armorSets = new ArmorSets();
        armorSets.load();

        mobs = new Mobs(this);
        mobs.load();

        particles = new Particles();
        particles.load();

        setupPlaceholders();

        getServer().getPluginManager().registerEvents(new OnDeath(this), this);
        getServer().getPluginManager().registerEvents(new OnDamage(this), this);
        getServer().getPluginManager().registerEvents(new OnMove(this), this);

        PluginCommand evilmobs = getCommand("evilmobs");
        if (evilmobs != null)
            evilmobs.setExecutor(new Admin(this));

        registerCustomActions();

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
        for (Map<String, MiniTask> map : tasks.values()) {
            for (MiniTask miniTask : map.values()) {
                miniTask.cancel();
            }
        }
        List<UUID> uuids = new ArrayList<>(MiniBar.datas.keySet());
        for (UUID uuid : uuids) {
            MiniBar.deleteBossBar(uuid);
        }
        if (evilMobsPlaceholderExpansion!=null) evilMobsPlaceholderExpansion.unregister();
    }
    @Getter
    private final Map<UUID, Map<String, MiniTask>> tasks = new HashMap<>();

    @Getter
    private final Map<String, MobCreator> mobCreators = new HashMap<>();
}
