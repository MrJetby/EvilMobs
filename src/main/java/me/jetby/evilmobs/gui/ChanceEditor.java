package me.jetby.evilmobs.gui;

import com.jodexindustries.jguiwrapper.api.item.ItemWrapper;
import com.jodexindustries.jguiwrapper.api.text.SerializerType;
import com.jodexindustries.jguiwrapper.gui.advanced.AdvancedGui;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.Maps;
import me.jetby.evilmobs.configurations.Items;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static me.jetby.evilmobs.gui.MainMenu.CHANCE;


public class ChanceEditor extends AdvancedGui {
    private final Items items;
    private final String type;
    private final String inv;
    private final Int2ObjectMap<ItemStack> originalItems = new Int2ObjectOpenHashMap<>();

    public ChanceEditor(Player player, String type, String inv, EvilMobs plugin, boolean isMiniMessage) {
        super(plugin.getLang().getConfig().getString("gui.chanceeditor.title", "gui.chanceeditor.title"));
        this.type = type;
        this.inv = inv;
        this.items = plugin.getItems();
        SerializerType serializerType;
        String serializerKey;
        if (isMiniMessage) {
            serializerType = SerializerType.MINI_MESSAGE;
            serializerKey = "miniMessage";
        } else {
            serializerType = SerializerType.LEGACY_AMPERSAND;
            serializerKey = "legacy";
        }

        List<Items.ItemsData> itemMap = items.getData().get(type);
        for (Items.ItemsData itemData : itemMap) {
            if (!itemData.inv().equals(inv)) continue;
            if (itemData.itemStack() == null) continue;

            ItemStack item = itemData.itemStack().clone();
            originalItems.put(itemData.slot(), item);


            final int[] chance = {item.getItemMeta().getPersistentDataContainer().getOrDefault(CHANCE, PersistentDataType.INTEGER, 100)};

            registerItem("slot_" + itemData.slot(), builder -> {
                builder.slots(itemData.slot())
                        .defaultItem(ItemWrapper.builder(item.getType(), serializerType)
                                .amount(item.getAmount())
                                .displayName(plugin.getLang().getConfig().getString("gui.chanceeditor." + serializerKey + ".display_name", "gui.chanceeditor." + serializerKey + ".display_name").replace("{chance}", String.valueOf(chance[0])))
                                .lore(plugin.getLang().getConfig().getStringList("gui.chanceeditor." + serializerKey + ".lore")).build());

                builder.defaultClickHandler((event, controller) -> {
                    event.setCancelled(true);
                    ClickType click = event.getClick();

                    switch (click) {
                        case LEFT -> chance[0] += 1;
                        case RIGHT -> chance[0] -= 1;
                        case SHIFT_LEFT -> chance[0] += 10;
                        case SHIFT_RIGHT -> chance[0] -= 10;
                    }

                    chance[0] = Math.max(0, Math.min(100, chance[0]));

                    ItemMeta meta = item.getItemMeta();
                    meta.getPersistentDataContainer().set(CHANCE, PersistentDataType.INTEGER, chance[0]);
                    item.setItemMeta(meta);

                    controller.updateItems(wrapper ->
                            wrapper.displayName(plugin.getLang().getConfig().getString("gui.chanceeditor." + serializerKey + ".display_name", "gui.chanceeditor." + serializerKey + ".display_name")
                                    .replace("{chance}", String.valueOf(chance[0])))
                    );
                });
            });
        }

        onClose(event -> {
            saveChanges();
            runTask(() -> new InvEditor(plugin, player, Maps.mobs.get(type), isMiniMessage).open(player), 1L);
        });
    }

    private void saveChanges() {
        for (Int2ObjectMap.Entry<ItemStack> entry : originalItems.int2ObjectEntrySet()) {
            int slot = entry.getIntKey();
            ItemStack item = entry.getValue();
            int chance = item.getItemMeta().getPersistentDataContainer().getOrDefault(CHANCE, PersistentDataType.INTEGER, 100);
            items.saveItem(type, inv, item, slot, chance);
        }
    }
}