package me.jetby.evilmobs.listeners;

import io.papermc.paper.event.entity.EntityMoveEvent;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.api.event.MobMoveEvent;
import me.jetby.evilmobs.configurations.Mobs;
import me.jetby.evilmobs.records.Mob;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

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
        Mob mob = mobs.getMobs().get(id);
        if (mob == null) return;

        Bukkit.getPluginManager().callEvent(new MobMoveEvent(id, entity, e.getFrom(), e.getTo(),
                e.hasChangedBlock(), e.hasChangedOrientation(),
                e.hasChangedPosition(), e.hasExplicitlyChangedBlock(), e.hasExplicitlyChangedPosition()));
    }
}
