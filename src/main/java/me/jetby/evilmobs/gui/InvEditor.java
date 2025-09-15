package me.jetby.evilmobs.gui;

import com.jodexindustries.jguiwrapper.api.item.ItemWrapper;
import com.jodexindustries.jguiwrapper.api.text.SerializerType;
import com.jodexindustries.jguiwrapper.gui.advanced.AdvancedGui;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.configurations.Items;
import me.jetby.evilmobs.locale.Lang;
import me.jetby.evilmobs.records.Mob;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InvEditor extends AdvancedGui {


    public InvEditor(EvilMobs plugin, Player player, Mob mob) {
        super(Lang.GUI_INV_EDITOR_TITLE);

        String id = mob.id();

        Set<String> invs = new HashSet<>();
        List<Items.ItemsData> itemsData = plugin.getItems().getData().get(id);
        if (itemsData != null) {
            for (Items.ItemsData iData : itemsData) {
                invs.add(iData.inv());
            }
        }

        int slot = 0;
        for (String inv : invs) {
            int currentSlot = slot;
            registerItem(inv + "_" + slot, builder -> {
                builder.slots(currentSlot);
                builder.defaultItem(ItemWrapper.builder(Material.CHEST, SerializerType.MINI_MESSAGE)
                        .lore(Lang.GUI_INV_EDITOR_INVENTORY_LORE)
                        .displayName(String.format(Lang.GUI_INV_EDITOR_INVENTORY_DISPLAY_NAME, inv))
                        .build());

                builder.defaultClickHandler((event, controller) -> {
                    event.setCancelled(true);

                    switch (event.getClick()) {
                        case LEFT -> {
                            new ItemEditor(player, inv, id, plugin).open(player);
                        }
                        case RIGHT -> {
                            new ChanceEditor(player, id, inv, plugin).open(player);
                        }
                    }
                });
            });
            slot++;
        }

        registerItem("add_button", builder -> {
            builder.slots(53);
            builder.defaultItem(ItemWrapper.builder(Material.EMERALD, SerializerType.MINI_MESSAGE)
                    .displayName(Lang.GUI_INV_EDITOR_NEW_INVENTORY_DISPLAY_NAME)
                    .lore(Lang.GUI_INV_EDITOR_NEW_INVENTORY_LORE)
                    .build());

            builder.defaultClickHandler((event, controller) -> {
                event.setCancelled(true);
                String newInvName = "inv_" + System.currentTimeMillis();
                ItemStack dirt = new ItemStack(Material.DIRT);
                plugin.getItems().saveItem(id, newInvName, dirt, 0, 100);

                InvEditor newGui = new InvEditor(plugin, player, mob);
                newGui.open(player);
            });
        });

    }
}
