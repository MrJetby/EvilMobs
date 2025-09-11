package me.jetby.evilmobs.listeners;

import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.api.event.MobDamageEvent;
import me.jetby.evilmobs.configurations.Mobs;
import me.jetby.evilmobs.records.Mob;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;

import static me.jetby.evilmobs.EvilMobs.NAMESPACED_KEY;

public class OnDamage implements Listener {

    private final Mobs mobs;

    public OnDamage(EvilMobs plugin) {
        this.mobs = plugin.getMobs();
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        Entity damager = e.getDamager();

        if (!entity.getPersistentDataContainer().has(NAMESPACED_KEY, PersistentDataType.STRING)) return;

        String id = entity.getPersistentDataContainer().get(NAMESPACED_KEY, PersistentDataType.STRING);

        Mob mob = mobs.getMobs().get(id);
        if (mob == null) return;

        double damage = e.getDamage();
        EntityDamageEvent.DamageCause damageCause = e.getCause();

        Bukkit.getPluginManager().callEvent(new MobDamageEvent(id, entity, damager, damage, damageCause));

    }


}
