package me.jetby.evilmobs.listeners;

import io.papermc.paper.event.entity.EntityMoveEvent;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.api.event.MobMoveEvent;
import me.jetby.evilmobs.configurations.Mobs;
import me.jetby.evilmobs.records.Mob;
import me.jetby.treex.actions.ActionContext;
import me.jetby.treex.actions.ActionExecutor;
import me.jetby.treex.actions.ActionRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static me.jetby.evilmobs.EvilMobs.NAMESPACED_KEY;

public class OnMove implements Listener {

    final Mobs mobs;
    final EvilMobs plugin;

    public OnMove(EvilMobs plugin) {
        this.plugin = plugin;
        this.mobs = plugin.getMobs();
    }

    @EventHandler
    public void onMove(EntityMoveEvent e) {
        Entity entity = e.getEntity();
        if (!entity.getPersistentDataContainer().has(NAMESPACED_KEY, PersistentDataType.STRING)) return;

        String id = entity.getPersistentDataContainer().get(NAMESPACED_KEY, PersistentDataType.STRING);
        if (id==null) return;
        Mob mob = mobs.getMobs().get(id);
        if (mob == null) return;

        if (!mob.listeners().isEmpty()) {
            List<String> actions = mob.listeners().get("ON_MOVE");
            if (actions==null || actions.isEmpty()) return;
            ActionContext ctx = new ActionContext(null);
            ctx.put("mob", mob);
            ctx.put("entity", entity);
            ActionExecutor.execute(ctx, ActionRegistry.transform(actions));
        }

        Bukkit.getPluginManager().callEvent(new MobMoveEvent(id, entity, e.getFrom(), e.getTo(),
                e.hasChangedBlock(), e.hasChangedOrientation(),
                e.hasChangedPosition(), e.hasExplicitlyChangedBlock(), e.hasExplicitlyChangedPosition()));
    }
}
