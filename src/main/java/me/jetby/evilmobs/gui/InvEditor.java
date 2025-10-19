package me.jetby.evilmobs.gui;

import com.jodexindustries.jguiwrapper.api.item.ItemWrapper;
import com.jodexindustries.jguiwrapper.api.text.SerializerType;
import com.jodexindustries.jguiwrapper.gui.advanced.AdvancedGui;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.configurations.Items;
import me.jetby.evilmobs.records.Mob;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InvEditor extends AdvancedGui {


    public InvEditor(EvilMobs plugin, Player player, Mob mob, boolean isMiniMessage) {
        super(plugin.getLang().getConfig().getString("gui.inveditor.title", "gui.inveditor.title"));

        SerializerType serializerType;
        String serializerKey;
        if (isMiniMessage) {
            serializerType = SerializerType.MINI_MESSAGE;
            serializerKey = "miniMessage";
        } else {
            serializerType = SerializerType.LEGACY_AMPERSAND;
            serializerKey = "legacy";
        }
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
                builder.defaultItem(ItemWrapper.builder(Material.CHEST, serializerType)
                        .displayName(plugin.getLang().getConfig().getString("gui.inveditor.inventory."+serializerKey+".display_name", "gui.inveditor.inventory."+serializerKey+".display_name").replace("{inv}", inv))
                        .lore(plugin.getLang().getConfig().getStringList("gui.inveditor.inventory."+serializerKey+".lore"))
                        .build());

                builder.defaultClickHandler((event, controller) -> {
                    event.setCancelled(true);

                    switch (event.getClick()) {
                        case LEFT -> new ItemEditor(player, inv, id, plugin, isMiniMessage).open(player);
                        case RIGHT -> new ChanceEditor(player, id, inv, plugin, isMiniMessage).open(player);
                    }
                });
            });
            slot++;
        }

        registerItem("add_button", builder -> {
            builder.slots(53);
            builder.defaultItem(ItemWrapper.builder(Material.EMERALD, serializerType)
                    .displayName(plugin.getLang().getConfig().getString("gui.inveditor.new_inventory."+serializerKey+".display_name", "gui.inveditor.new_inventory."+serializerKey+".display_name"))
                    .lore(plugin.getLang().getConfig().getStringList("gui.inveditor.new_inventory."+serializerKey+".lore"))
                    .build());

            builder.defaultClickHandler((event, controller) -> {
                event.setCancelled(true);
                String newInvName = "inv_" + System.currentTimeMillis();
                ItemStack dirt = new ItemStack(Material.DIRT);
                plugin.getItems().saveItem(id, newInvName, dirt, 0, 100);

                InvEditor newGui = new InvEditor(plugin, player, mob, isMiniMessage);
                newGui.open(player);
            });
        });

    }
}
