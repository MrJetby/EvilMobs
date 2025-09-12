package me.jetby.evilmobs.listeners;

import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.MobCreator;
import me.jetby.evilmobs.api.event.MobDeathEvent;
import me.jetby.evilmobs.configurations.Mobs;
import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.tools.actions.ActionExecutor;
import me.jetby.evilmobs.tools.actions.ActionRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

import static me.jetby.evilmobs.EvilMobs.NAMESPACED_KEY;

public class OnDeath implements Listener {

    final Mobs mobs;
    final Map<String, MobCreator> mobCreators;

    public OnDeath(EvilMobs plugin) {
        this.mobs = plugin.getMobs();
        this.mobCreators = plugin.getMobCreators();
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        if (!entity.getPersistentDataContainer().has(NAMESPACED_KEY, PersistentDataType.STRING)) return;

        String id = entity.getPersistentDataContainer().get(NAMESPACED_KEY, PersistentDataType.STRING);
        Mob mob = mobs.getMobs().get(id);
        if (mob == null) return;

        Bukkit.getPluginManager().callEvent(new MobDeathEvent(id, entity, entity.getKiller()));

        ActionExecutor.execute(null, ActionRegistry.transform(mob.onDeathActions()), entity, mob);
        mobCreators.remove(id);
    }
}
