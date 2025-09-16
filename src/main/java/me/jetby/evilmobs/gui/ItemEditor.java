package me.jetby.evilmobs.gui;

import com.jodexindustries.jguiwrapper.api.item.ItemWrapper;
import com.jodexindustries.jguiwrapper.gui.advanced.AdvancedGui;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.configurations.Items;
import me.jetby.evilmobs.locale.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static me.jetby.evilmobs.gui.MainMenu.CHANCE;


public class ItemEditor extends AdvancedGui {

    private final Items items;

    public ItemEditor(Player player, String inv, String type, EvilMobs plugin) {
        super(Lang.getString("gui.itemeditor.title"));
        this.items = plugin.getItems();


        setCancelEmptySlots(false);
        onDrag(inventoryDragEvent -> {
            inventoryDragEvent.setCancelled(false);
        });

        List<Items.ItemsData> map = items.getData().get(type);
        for (Items.ItemsData itemData : map) {
            if (!itemData.inv().equals(inv)) continue;
            if (itemData.itemStack() == null) continue;
            registerItem(itemData.slot().toString()+"-"+itemData.inv(), builder -> {
                builder.slots(itemData.slot());
                builder.defaultItem(new ItemWrapper(itemData.itemStack()));
                builder.defaultClickHandler((event, controller) -> {
                    event.setCancelled(false);
                });
            });
        }

        onClose(event -> {
            saveInv(event.getInventory(), type, inv);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                new InvEditor(plugin, player, plugin.getMobs().getMobs().get(type)).open(player);
            }, 1L);
        });
    }

    private void saveInv(Inventory inventory, String type, String inv) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack item = inventory.getItem(slot);
            if (item == null) {
                items.removeItem(type, inv, slot);
                continue;
            }
            int chance = item.getItemMeta().getPersistentDataContainer()
                    .getOrDefault(CHANCE, PersistentDataType.INTEGER, 100);
            items.saveItem(type, inv, item, slot, chance);
        }
    }
}