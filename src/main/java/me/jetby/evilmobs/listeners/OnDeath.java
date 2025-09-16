package me.jetby.evilmobs.listeners;

import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.Maps;
import me.jetby.evilmobs.MobCreator;
import me.jetby.evilmobs.actions.DropManager;
import me.jetby.evilmobs.api.event.MobDeathEvent;
import me.jetby.evilmobs.configurations.Items;
import me.jetby.evilmobs.configurations.Mobs;
import me.jetby.evilmobs.records.Mob;
import me.jetby.treex.actions.ActionContext;
import me.jetby.treex.actions.ActionExecutor;
import me.jetby.treex.actions.ActionRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;

import static me.jetby.evilmobs.EvilMobs.LOGGER;
import static me.jetby.evilmobs.EvilMobs.NAMESPACED_KEY;

public class OnDeath implements Listener {

    final Mobs mobs;
    final Map<String, MobCreator> mobCreators;
    private final EvilMobs plugin;

    public OnDeath(EvilMobs plugin) {
        this.plugin = plugin;
        this.mobs = plugin.getMobs();
        this.mobCreators = Maps.mobCreators;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        if (!entity.getPersistentDataContainer().has(NAMESPACED_KEY, PersistentDataType.STRING)) return;

        String id = entity.getPersistentDataContainer().get(NAMESPACED_KEY, PersistentDataType.STRING);
        if (id==null) return;
        Mob mob = mobs.getMobs().get(id);
        if (mob == null) return;

        Player player = entity.getKiller();

        Bukkit.getPluginManager().callEvent(new MobDeathEvent(id, entity, player));

        ActionContext ctx = new ActionContext(player);
        ctx.put("mob", mob);
        ctx.put("entity", entity);
        ActionExecutor.execute(ctx, ActionRegistry.transform(mob.onDeathActions()));

        if (mob.onlyCustom()) {
            e.getDrops().clear();
            e.setDroppedExp(0);
        }
        if (!mob.lootAmount().equalsIgnoreCase("0")) {
            if (mob.lootAmount().equalsIgnoreCase("-1")) {

                List<Items.ItemsData> items = plugin.getItems().getData().get(id);
                if (items != null) {
                    for (Items.ItemsData item : items) {
                        ItemStack itemStack = item.itemStack();
                        if (itemStack != null) {
                            e.getDrops().add(itemStack);
                        }
                    }
                }
            } else {
                DropManager.dropRandomItems(entity.getLocation(), mob);

            }
        }

        if (mobCreators.get(id)!=null){
            mobCreators.get(id).end();
            LOGGER.success("ended from death");
        }
    }
}
