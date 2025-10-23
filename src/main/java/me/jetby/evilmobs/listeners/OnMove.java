package me.jetby.evilmobs.listeners;

import io.papermc.paper.event.entity.EntityMoveEvent;
import me.jetby.evilmobs.Maps;
import me.jetby.evilmobs.MobCreator;
import me.jetby.evilmobs.api.event.MobMoveEvent;
import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.tools.Placeholders;
import me.jetby.treex.actions.ActionContext;
import me.jetby.treex.actions.ActionExecutor;
import me.jetby.treex.actions.ActionRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static me.jetby.evilmobs.EvilMobs.NAMESPACED_KEY;

public class OnMove implements Listener {

    @EventHandler
    public void onMove(EntityMoveEvent e) {
        if (Maps.mobCreators.isEmpty()) {
            return;
        }
        LivingEntity entity = e.getEntity();

        String id = entity.getPersistentDataContainer().get(NAMESPACED_KEY, PersistentDataType.STRING);
        Mob mob = Maps.mobs.get(id);
        if (mob == null) return;

        Bukkit.getPluginManager().callEvent(new MobMoveEvent(id, entity, e.getFrom(), e.getTo(),
                e.hasChangedBlock(), e.hasChangedOrientation(),
                e.hasChangedPosition(), e.hasExplicitlyChangedBlock(), e.hasExplicitlyChangedPosition()));

        if (mob.movingRadius() != -1) {
            MobCreator mc = Maps.mobCreators.get(id);
            if (mc != null) {
                if (mc.getSpawnedLocation().distanceSquared(e.getTo()) > mob.movingRadius() * mob.movingRadius()) {
                    e.setCancelled(true);
                    if (mob.teleportOnRadius()) {
                        entity.teleport(mc.getSpawnedLocation());
                    }
                    return;
                }
            }
        }

        if (!mob.listeners().isEmpty()) {
            List<String> actions = mob.listeners().get("ON_MOVE");
            if (actions == null || actions.isEmpty()) return;
            ActionContext ctx = new ActionContext(null);
            ctx.put("mob", mob);
            ctx.put("entity", entity);
            ActionExecutor.execute(ctx, ActionRegistry.transform(Placeholders.set(actions, mob, entity)));
        }
    }
}
