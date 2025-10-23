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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.jetby.evilmobs.EvilMobs.LOGGER;
import static me.jetby.evilmobs.EvilMobs.NAMESPACED_KEY;

@RequiredArgsConstructor
public class MobCreator {

    final Mob mainMob;
    final int taskId = -1;

    @Getter
    private Location spawnedLocation;

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
                spawn(mainMob, spawnedLocation, false);
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
                spawn(mainMob, location, false);
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


        for (String taskId : mainMob.tasks().keySet()) {
            Task task = mainMob.tasks().get(taskId);
            if (task == null) return;

            MiniTask miniTask = new MiniTask(task.delay(), task.period(), task.amount(), task.actions(), entity, mainMob);

            miniTask.run();

            Map<String, MiniTask> tasks = new HashMap<>();
            var oldTasks = Maps.tasks.get(entity.getUniqueId());
            if (oldTasks != null) {
                tasks.putAll(oldTasks);
            }

            tasks.put(taskId, miniTask);
            Maps.tasks.put(entity.getUniqueId(), tasks);
        }

        startPhases(entity);
    }
    private void startPhases(LivingEntity entity) {
        if (!mainMob.phases().isEmpty() && phases.isEmpty()) {
            for (Phases p : mainMob.phases()) {
                phases.put(p.type(), new HashMap<>(p.actions()));  // Deep copy to avoid mutating original
            }

            Bukkit.getScheduler().runTaskTimerAsynchronously(EvilMobs.getInstance(), (t) -> {

                if (entity.isDead()) {
                    t.cancel();
                    return;
                }

                for (String phaseType : new ArrayList<>(phases.keySet())) {
                    Map<String, List<String>> phase = phases.get(phaseType);
                    if (phase.isEmpty()) {
                        phases.remove(phaseType);
                        continue;
                    }

                    for (String trigger : new ArrayList<>(phase.keySet())) {
                        try {
                            boolean executed = sendPhasesCommand(phaseType, trigger, phase.get(trigger), entity, mainMob);
                            if (executed) {
                                phase.remove(trigger);
                                if (phase.isEmpty()) {
                                    phases.remove(phaseType);
                                }
                            }
                        } catch (NumberFormatException e) {
                            LOGGER.warn(e.getMessage());
                        }
                    }
                }

            }, 0L, 5L);
        }
    }
    private LivingEntity spawn(Mob mob, Location location, boolean isMinion) {

        LivingEntity boss = (LivingEntity) location.getWorld().spawnEntity(location, mob.entityType());
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
        ActionExecutor.execute(ctx, ActionRegistry.transform(Placeholders.set(mob.onSpawnActions(), mob, boss)));

        if (!isMinion) {
            startPhases(boss);
            livingEntity = boss;
        }

        return boss;
    }

    public void spawnMinion(String id, Location location) {
        LivingEntity entity = spawn(Maps.mobs.get(id), location, true);
        minions.add(entity);
    }

    public void killAllMinions() {
        for (LivingEntity e : minions) {
            e.setHealth(0);
        }
        minions.clear();
    }

    final Map<String, Map<String, List<String>>> phases = new HashMap<>(); // phaseType, phaseTrigger, actions

    private boolean sendPhasesCommand(String type, String trigger, List<String> actions, LivingEntity entity, Mob mob) {
        boolean status = false;
        switch (type) {
            case "health": {
                double health = entity.getHealth();

                double t = Double.parseDouble(trigger);
                if (health <= t) {
                    ActionContext ctx = new ActionContext(null);
                    ctx.put("entity", entity);
                    ctx.put("mob", mob);
                    ActionExecutor.execute(ctx, ActionRegistry.transform(Placeholders.set(actions, mob, entity)));

                }

                status = true;
                break;
            }
            case "health_percentage": {
                int healthPercent = (int) ((entity.getHealth() / entity.getMaxHealth()) * 100);

                int t = Integer.parseInt(trigger);
                if (healthPercent <= t) {
                    ActionContext ctx = new ActionContext(null);
                    ctx.put("mob", mob);
                    ctx.put("entity", entity);
                    ActionExecutor.execute(ctx, ActionRegistry.transform(Placeholders.set(actions, mob, entity)));

                }

                status = true;
                break;
            }
            default: {
                var t = Papi.setPapi(null, type);
                if (t.equals(trigger)) {
                    ActionContext ctx = new ActionContext(null);
                    ctx.put("mob", mob);
                    ctx.put("entity", entity);
                    ActionExecutor.execute(ctx, ActionRegistry.transform(Placeholders.set(actions, mob, entity)));
                }

                status = true;
            }
        }
        return status;
    }

    public void end() {
        Bukkit.getScheduler().cancelTask(taskId);
        killAllMinions();
        Maps.mobCreators.remove(mainMob.id());
    }
}