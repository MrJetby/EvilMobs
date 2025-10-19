package me.jetby.evilmobs.gui;

import com.jodexindustries.jguiwrapper.api.item.ItemWrapper;
import com.jodexindustries.jguiwrapper.api.text.SerializerType;
import com.jodexindustries.jguiwrapper.gui.advanced.AdvancedGui;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.Maps;
import me.jetby.evilmobs.records.Mob;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class MainMenu extends AdvancedGui {

    public static final NamespacedKey CHANCE = new NamespacedKey("evilmobs", "chance");

    public MainMenu(EvilMobs plugin, boolean isMiniMessage) {

        super(plugin.getLang().getConfig().getString("gui.main.title", "gui.main.title"));

        SerializerType serializerType;
        String serializerKey;
        if (isMiniMessage) {
            serializerType = SerializerType.MINI_MESSAGE;
            serializerKey = "miniMessage";
        } else {
            serializerType = SerializerType.LEGACY_AMPERSAND;
            serializerKey = "legacy";
        }

        int slot = 0;
        for (String type : Maps.mobs.keySet()) {
            Mob mob = Maps.mobs.get(type);
            int finalSlot = slot;
            registerItem(type, builder -> {
                builder.slots(finalSlot);
                builder.defaultItem(ItemWrapper.builder(Material.valueOf(mob.entityType().name() + "_SPAWN_EGG"), serializerType)
                        .displayName(plugin.getLang().getConfig().getString("gui.main." + serializerKey + ".display_name", "gui.main." + serializerKey + ".display_name").replace("{type}", type))
                        .lore(plugin.getLang().getConfig().getStringList("gui.main." + serializerKey + ".lore"))
                        .build());

                builder.defaultClickHandler((event, controller) -> {
                    event.setCancelled(true);

                    if (event.getWhoClicked() instanceof Player player) {
                        new InvEditor(plugin, player, mob, isMiniMessage).open(player);
                    }
                });
            });
            slot++;
        }
    }
}
