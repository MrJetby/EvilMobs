package me.jetby.evilmobs;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.jetby.evilmobs.api.event.MobSpawnEvent;
import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.records.Phases;
import me.jetby.evilmobs.records.Rtp;
import me.jetby.evilmobs.records.Task;
import me.jetby.evilmobs.tools.MiniTask;
import me.jetby.evilmobs.tools.Placeholders;
import me.jetby.treex.actions.ActionContext;
import me.jetby.treex.actions.ActionExecutor;
import me.jetby.treex.actions.ActionRegistry;
import me.jetby.treex.bukkit.LocationGenerator;
import me.jetby.treex.text.Papi;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static me.jetby.evilmobs.EvilMobs.LOGGER;
import static me.jetby.evilmobs.EvilMobs.NAMESPACED_KEY;

@RequiredArgsConstructor
public class MobCreator {


    final Mob mainMob;
    int taskId = -1;
    final Map<UUID, Integer> tasks = new HashMap<>();

    @Getter
    private Location spawnedLocation = null;

    @Getter
    private LivingEntity livingEntity;
    private final List<LivingEntity> minions = new ArrayList<>();

    /**
     * <li> Spawning the mob
     **/
    public void spawn() {

        if (mainMob.locationType().equalsIgnoreCase("rtp")) {
            Rtp rtp = mainMob.rtp();
            Location location = LocationGenerator.getRandomLocation(rtp.world(), rtp.min(), rtp.max(), rtp.materials(), rtp.blockListType().equalsIgnoreCase("blacklist"));
            if (location != null) {
                spawn(location);
            } else {
                Maps.mobCreators.remove(mainMob.id());
                LOGGER.error("Could not find a safe location to spawn the mob with ID: " + mainMob.id());
            }
            return;
        }
        spawnedLocation = mainMob.location();
        World world = spawnedLocation.getWorld();
        Chunk chunk = world.getChunkAt(spawnedLocation);

        Bukkit.getScheduler().runTaskTimer(EvilMobs.getInstance(), task -> {
            if (chunk.isLoaded()) {
                spawn(mainMob, spawnedLocation);
                task.cancel();
            }
        }, 0L, 20L);
    }

    /**
     * <li> Spawning the mob at a specific location
     **/
    public void spawn(Location location) {
        World world = location.getWorld();
        Chunk chunk = world.getChunkAt(location);
        spawnedLocation = location;

        Bukkit.getScheduler().runTaskTimer(EvilMobs.getInstance(), task -> {
            if (chunk.isLoaded()) {
                spawn(mainMob, location);
                task.cancel();
            }
        }, 0L, 20L);
    }

    /**
     * <li> Running the tasks of the mob </li>
     * It is used to restore the functionality of the mob after a server restart.
     **/
    public void runTasks(LivingEntity entity) {

        spawnedLocation = entity.getLocation();
        phasesCopy.addAll(mainMob.phases());
        phasesCopy.forEach(phase -> phases.putAll(phase.actions()));

        for (String taskId : mainMob.tasks().keySet()) {
            Task task = mainMob.tasks().get(taskId);
            if (task == null) return;

            MiniTask miniTask = new MiniTask(task.delay(), task.period(), task.amount(), task.actions(), entity, mainMob);

            miniTask.run();

            Map<String, MiniTask> tasks = new HashMap<>();
            if (Maps.tasks.get(entity.getUniqueId()) != null) {
                tasks.putAll(Maps.tasks.get(entity.getUniqueId()));
            }

            tasks.put(taskId, miniTask);
            Maps.tasks.put(entity.getUniqueId(), tasks);
        }


        tasks.put(entity.getUniqueId(), Bukkit.getScheduler().runTaskTimer(EvilMobs.getInstance(), () -> {

            if (entity.isDead()) {
                Bukkit.getScheduler().cancelTask(taskId);
                return;
            }

            for (Phases phase : phasesCopy) {
                try {
                    sendPhasesCommand(phase.type(), entity, mainMob);
                } catch (NumberFormatException e) {
                    LOGGER.warn(e.getMessage());
                }
            }
        }, 0L, 5L).getTaskId());
    }

    private LivingEntity spawn(Mob mob, Location location) {

        LivingEntity boss = (LivingEntity) location.getWorld().spawnEntity(spawnedLocation, mob.entityType());
        boss.setGlowing(mob.glow());
        boss.setMaxHealth(mob.health());
        boss.setHealth(mob.health());
        boss.setAI(mob.ai());
        boss.setCanPickupItems(mob.canPickupItems());
        boss.setRemoveWhenFarAway(false);
        boss.setPersistent(true);


        if (boss instanceof Ageable age) {
            if (mob.isBaby()) {
                age.setBaby();
            } else {
                age.setAdult();
            }
        }

        boss.setCustomNameVisible(mob.nameVisible());
        if (mob.nameVisible()) {
            boss.setCustomName(mob.name());
        }

        if (mob.armorItems() != null && !mob.armorItems().isEmpty()) {
            mob.armorItems().forEach(armorItem -> {

                switch (armorItem.id()) {
                    case "helmet": {
                        boss.getEquipment().setHelmet(armorItem.item());
                        boss.getEquipment().setHelmetDropChance(armorItem.dropChance());
                        break;
                    }
                    case "chestplate": {
                        boss.getEquipment().setChestplate(armorItem.item());
                        boss.getEquipment().setChestplateDropChance(armorItem.dropChance());
                        break;
                    }
                    case "leggings": {
                        boss.getEquipment().setLeggings(armorItem.item());
                        boss.getEquipment().setLeggingsDropChance(armorItem.dropChance());
                        break;
                    }
                    case "boots": {
                        boss.getEquipment().setBoots(armorItem.item());
                        boss.getEquipment().setBootsDropChance(armorItem.dropChance());
                        break;
                    }
                    case "hand": {
                        boss.getEquipment().setItemInMainHand(armorItem.item());
                        boss.getEquipment().setItemInMainHandDropChance(armorItem.dropChance());
                        break;
                    }
                    case "offhand": {
                        boss.getEquipment().setItemInOffHand(armorItem.item());
                        boss.getEquipment().setItemInOffHandDropChance(armorItem.dropChance());
                        break;
                    }
                }
            });
        }

        boss.getPersistentDataContainer().set(NAMESPACED_KEY, PersistentDataType.STRING, mob.id());

        Bukkit.getPluginManager().callEvent(new MobSpawnEvent(mob.id(), boss));

        ActionContext ctx = new ActionContext(null);
        ctx.put("mob", mob);
        ctx.put("entity", boss);
        ActionExecutor.execute(ctx, ActionRegistry.transform(Placeholders.list(mob.onSpawnActions(), mob, boss)));


        phasesCopy.addAll(mob.phases());
        phasesCopy.forEach(phase -> phases.putAll(phase.actions()));

        tasks.put(boss.getUniqueId(), Bukkit.getScheduler().runTaskTimer(EvilMobs.getInstance(), () -> {

            if (boss.isDead()) {
                Bukkit.getScheduler().cancelTask(taskId);
                return;
            }

            for (Phases phase : phasesCopy) {
                try {
                    sendPhasesCommand(phase.type(), boss, mob);
                } catch (NumberFormatException e) {
                    LOGGER.warn(e.getMessage());
                }
            }
        }, 0L, 5L).getTaskId());


        livingEntity = boss;
        return boss;
    }

    public void spawnMinion(String id, Location location) {
        LivingEntity entity = spawn(Maps.mobs.get(id), location);
        minions.add(entity);
    }

    public void killAllMinions() {
        for (LivingEntity e : minions) {
            e.setHealth(0);
        }
        minions.clear();
    }

    final List<Phases> phasesCopy = new ArrayList<>();
    final Map<String, List<String>> phases = new HashMap<>();

    private void sendPhasesCommand(String type, LivingEntity entity, Mob mob) {
        switch (type) {
            case "health": {
                double health = entity.getHealth();
                for (var phaseId : new ArrayList<>(phases.keySet())) {
                    double trigger = Double.parseDouble(phaseId);
                    if (health <= trigger) {
                        ActionContext ctx = new ActionContext(null);
                        ctx.put("entity", entity);

                        ActionExecutor.execute(ctx, ActionRegistry.transform(phases.get(phaseId)));

                        phases.remove(phaseId);
                    }
                }
                break;
            }
            case "health_percentage": {
                int healthPercent = (int) (entity.getHealth() / entity.getMaxHealth()) * 100;
                for (String phaseId : new ArrayList<>(phases.keySet())) {
                    int trigger = Integer.parseInt(phaseId);
                    if (healthPercent <= trigger) {
                        ActionContext ctx = new ActionContext(null);
                        ctx.put("mob", mob);
                        ctx.put("entity", entity);
                        ActionExecutor.execute(ctx, ActionRegistry.transform(phases.get(phaseId)));
                        phases.remove(phaseId);
                    }
                }
                break;
            }
            default: {
                var t = Papi.setPapi(null, type);
                for (var phaseId : new ArrayList<>(phases.keySet())) {
                    if (t.equals(phaseId)) {
                        ActionContext ctx = new ActionContext(null);
                        ctx.put("mob", mob);
                        ctx.put("entity", entity);
                        ActionExecutor.execute(ctx, ActionRegistry.transform(phases.get(phaseId)));
                        phases.remove(phaseId);
                    }
                }
            }
        }
    }


    public void end() {
        Bukkit.getScheduler().cancelTask(taskId);
        killAllMinions();
        Maps.mobCreators.remove(mainMob.id());
    }

}
