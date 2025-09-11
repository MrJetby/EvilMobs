package me.jetby.evilmobs.tools.actions;

import lombok.Getter;
import me.jetby.evilmobs.tools.actions.impl.mob.abillities.Fireball;
import me.jetby.evilmobs.tools.actions.impl.mob.abillities.Lightning;
import me.jetby.evilmobs.tools.actions.impl.mob.bossBar.*;
import me.jetby.evilmobs.tools.actions.impl.mob.task.*;
import me.jetby.evilmobs.tools.actions.impl.standard.*;
import org.jetbrains.annotations.Nullable;

@Getter
public enum ActionType {

    LIGHTNING(new Lightning()),
    FIREBALL(new Fireball()),

    CREATE_BOSSBAR(new CreateBossBar()),
    SHOW_BOSSBAR(new ShowBossBar()),
    SHOW_BOSSBAR_NEAR(new ShowBossBarNear()),
    REMOVE_BOSSBAR_NEAR(new RemoveBossBarNear()),
    REMOVE_BOSSBAR(new RemoveBossBar()),
    DELETE_BOSSBAR(new DeleteBossBar()),

    TASK_RUN(new TaskRun()),
    TASK_STOP(new TaskStop()),

    MESSAGE(new Message()),
    MSG(new Message()),
    SOUND(new Sound()),
    EFFECT(new Effect()),
    ACTIONBAR(new ActionBar()),
    TITLE(new Title()),
    CONSOLE(new Console()),
    PLAYER(new Player()),
    BROADCASTMESSAGE(new BroadcastMessage()),
    BROADCAST_MESSAGE(new BroadcastMessage()),
    MESSAGE_ALL(new BroadcastMessage()),
    BC(new BroadcastMessage()),
    BROADCAST(new BroadcastMessage()),
    MSG_ALL(new BroadcastMessage()),
    BROADCASTSOUND(new BroadcastSound()),
    BROADCAST_SOUND(new BroadcastSound()),
    BROADCASTTITLE(new BroadcastTitle()),
    BROADCAST_TITLE(new BroadcastTitle()),
    BROADCASTACTIONBAR(new BroadcastActionBar()),
    BROADCAST_ACTIONBAR(new BroadcastActionBar());

    private final Action action;

    ActionType(Action action) {
        this.action = action;
    }

    @Nullable
    public static ActionType getType(String name) {
        if (name == null) return null;

        try {
            return ActionType.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
