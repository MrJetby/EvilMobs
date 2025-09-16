package me.jetby.evilmobs;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.jetby.evilmobs.api.event.MobSpawnEvent;
import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.records.Phases;
import me.jetby.treex.actions.ActionContext;
import me.jetby.treex.actions.ActionExecutor;
import me.jetby.treex.actions.ActionRegistry;
import me.jetby.treex.text.Papi;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    int taskId = -1;


    @Getter
    private LivingEntity livingEntity;
    private final List<LivingEntity> minions = new ArrayList<>();

    public void spawn() {
        spawn(mainMob, mainMob.spawnlocation());
    }
    public void spawn(Location location) {
        spawn(mainMob, location);
    }
    private LivingEntity spawn(Mob mob, Location location) {
        LivingEntity boss = (LivingEntity) location.getWorld().spawnEntity(mob.spawnlocation(), mob.entityType());
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

        boss.getPersistentDataContainer().set(NAMESPACED_KEY, PersistentDataType.STRING, mob.id());

        Bukkit.getPluginManager().callEvent(new MobSpawnEvent(mob.id(), boss));

        ActionContext ctx = new ActionContext(null);
        ctx.put("mob", mob);
        ctx.put("entity", boss);
        ActionExecutor.execute(ctx, ActionRegistry.transform(mob.onSpawnActions()));


        phasesCopy.addAll(mob.phases());
        phasesCopy.forEach(phase -> phases.putAll(phase.actions()));

        taskId = Bukkit.getScheduler().runTaskTimer(EvilMobs.getInstance(), () -> {

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
        }, 0L, 5L).getTaskId();


        livingEntity = boss;
        return boss;
    }
    public void spawnMinion(String id, Location location) {
        LivingEntity entity = spawn(EvilMobs.getInstance().getMobs().getMobs().get(id), location);
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
            case "HEALTH_PERCENTAGE": {
                double healthPercent = (entity.getHealth() / entity.getMaxHealth()) * 100;
                for (String phaseId : new ArrayList<>(phases.keySet())) {
                    double trigger = Double.parseDouble(phaseId);
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
