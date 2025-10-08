package me.jetby.evilmobs.listeners;

import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

import static me.jetby.evilmobs.gui.MainMenu.CHANCE;

public class ItemPickup implements Listener {

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Player player)) return;

        Item item = event.getItem();
        if (item.hasMetadata("evilmobs_originalItem")) {
            List<MetadataValue> metaValues = item.getMetadata("evilmobs_originalItem");
            if (!metaValues.isEmpty()) {
                ItemStack originalItem = (ItemStack) metaValues.get(0).value();
                event.setCancelled(true);
                item.remove();
                ItemStack cleanItem = originalItem.clone();
                cleanItem.getItemMeta().getPersistentDataContainer().remove(CHANCE);
                player.getInventory().addItem(cleanItem);
                player.updateInventory();
            }
        }
    }
}
