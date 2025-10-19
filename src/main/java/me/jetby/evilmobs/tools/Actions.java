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
import me.jetby.treex.actions.ActionEntry;
import me.jetby.treex.actions.ActionTypeRegistry;

import java.util.HashSet;
import java.util.Set;

public class Actions {
    public void registerCustomActions() {
        Set<ActionEntry> actions = new HashSet<>();
        actions.add(new ActionEntry("evilmobs", "TELEPORT", new Teleport()));
        actions.add(new ActionEntry("evilmobs", "FIREBALL", new Fireball()));
        actions.add(new ActionEntry("evilmobs", "LIGHTNING", new Lightning()));

        actions.add(new ActionEntry("evilmobs", "CREATE_BOSSBAR", new CreateBossBar()));
        actions.add(new ActionEntry("evilmobs", "REMOVE_BOSSBAR", new RemoveBossBar()));
        actions.add(new ActionEntry("evilmobs", "REMOVE_BOSSBAR_NEAR", new RemoveBossBarNear()));
        actions.add(new ActionEntry("evilmobs", "SHOW_BOSSBAR", new ShowBossBar()));
        actions.add(new ActionEntry("evilmobs", "SHOW_BOSSBAR_NEAR", new ShowBossBarNear()));
        actions.add(new ActionEntry("evilmobs", "DELETE_BOSSBAR", new DeleteBossBar()));

        actions.add(new ActionEntry("evilmobs", "TASK_RUN", new TaskRun()));
        actions.add(new ActionEntry("evilmobs", "TASK_STOP", new TaskStop()));

        actions.add(new ActionEntry("evilmobs", "DROP", new Drop()));
        actions.add(new ActionEntry("evilmobs", "DROP_CLEAR", new DropClear()));

        actions.add(new ActionEntry("evilmobs", "SET_AGE", new SetAge()));
        actions.add(new ActionEntry("evilmobs", "SETAGE", new SetAge()));

        actions.add(new ActionEntry("evilmobs", "SET_AI", new SetAI()));
        actions.add(new ActionEntry("evilmobs", "SETAI", new SetAI()));

        actions.add(new ActionEntry("evilmobs", "SET_CAN_PICKUP_ITEMS", new SetCanPickupItems()));
        actions.add(new ActionEntry("evilmobs", "SETCANPICKUPITEMS", new SetCanPickupItems()));

        actions.add(new ActionEntry("evilmobs", "SET_GLOW", new SetGlow()));
        actions.add(new ActionEntry("evilmobs", "SETGLOW", new SetGlow()));

        actions.add(new ActionEntry("evilmobs", "SET_NAME", new SetName()));
        actions.add(new ActionEntry("evilmobs", "SETNAME", new SetName()));

        actions.add(new ActionEntry("evilmobs", "SEND_PARTICLE", new SendParticle()));

        actions.add(new ActionEntry("evilmobs", "EFFECT_NEAR", new EffectNear()));
        actions.add(new ActionEntry("evilmobs", "EFFECTNEAR", new EffectNear()));

        actions.add(new ActionEntry("evilmobs", "SET_TARGET", new SetTarget()));
        actions.add(new ActionEntry("evilmobs", "SETTARGET", new SetTarget()));

        actions.add(new ActionEntry("evilmobs", "SET_HEALTH", new SetHealth()));
        actions.add(new ActionEntry("evilmobs", "ADD_HEALTH", new AddHealth()));
        actions.add(new ActionEntry("evilmobs", "TAKE_HEALTH", new TakeHealth()));
        actions.add(new ActionEntry("evilmobs", "SET_MAX_HEALTH", new SetMaxHealth()));

        actions.add(new ActionEntry("evilmobs", "SPAWN_AS_MINION", new SpawnAsMinion()));
        actions.add(new ActionEntry("evilmobs", "SPAWNASMINION", new SpawnAsMinion()));
        actions.add(new ActionEntry("evilmobs", "KILL_ALL_MINIONS", new KillAllMinions()));
        actions.add(new ActionEntry("evilmobs", "KILLALLMINIONS", new KillAllMinions()));
        actions.add(new ActionEntry("evilmobs", "KILL_ALL_MINION", new KillAllMinions()));
        actions.add(new ActionEntry("evilmobs", "KILLALLMINION", new KillAllMinions()));
        ActionTypeRegistry.register(actions);

    }
}
