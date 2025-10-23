package me.jetby.evilmobs.listeners;

import me.jetby.evilmobs.Maps;
import me.jetby.evilmobs.api.event.MobDamageEvent;
import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.tools.Placeholders;
import me.jetby.treex.actions.ActionContext;
import me.jetby.treex.actions.ActionExecutor;
import me.jetby.treex.actions.ActionRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static me.jetby.evilmobs.EvilMobs.NAMESPACED_KEY;

public class OnDamage implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (Maps.mobCreators.isEmpty()) {
            return;
        }
        Entity entity = e.getEntity();
        Entity damager = e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && e instanceof EntityDamageByEntityEvent edbee ? edbee.getDamager() : null;

        String id = entity.getPersistentDataContainer().get(NAMESPACED_KEY, PersistentDataType.STRING);
        if (id == null) return;

        Mob mob = Maps.mobs.get(id);
        if (mob == null) return;

        double damage = e.getDamage();
        EntityDamageEvent.DamageCause damageCause = e.getCause();

        Bukkit.getPluginManager().callEvent(new MobDamageEvent(id, entity, damager, damage, damageCause));

        if (damageCause == EntityDamageEvent.DamageCause.PROJECTILE) {
            if (!(damager instanceof Player)) {
                e.setCancelled(true);
                return;
            }
        }

        if (damager instanceof Player player) {
            if (!mob.listeners().isEmpty()) {
                List<String> actions = mob.listeners().get("ON_DAMAGE");
                if (actions == null || actions.isEmpty()) return;
                ActionContext ctx = new ActionContext(player);
                ctx.put("mob", mob);
                ctx.put("entity", entity);
                ActionExecutor.execute(ctx, ActionRegistry.transform(Placeholders.set(actions, mob, (LivingEntity) entity)));
            }
        }
    }
}
