package me.jetby.evilmobs.listeners;

import me.jetby.evilmobs.Main;
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

import static me.jetby.evilmobs.Main.NAMESPACED_KEY;

public class OnDeath implements Listener {

    private final Main plugin;
    private final Mobs mobs;

    public OnDeath(Main plugin) {
        this.plugin = plugin;
        this.mobs = plugin.getMobs();
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        if (!entity.getPersistentDataContainer().has(NAMESPACED_KEY, PersistentDataType.STRING)) return;

        Mob mob = mobs.getMobs().get(entity.getPersistentDataContainer().get(NAMESPACED_KEY, PersistentDataType.STRING));
        if (mob==null) return;

        Bukkit.getPluginManager().callEvent(new MobDeathEvent(entity, entity.getKiller()));

        ActionExecutor.execute(null, ActionRegistry.transform(mob.onDeathActions()), entity);
    }
}
