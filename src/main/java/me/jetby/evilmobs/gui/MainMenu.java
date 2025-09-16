package me.jetby.evilmobs.gui;

import com.jodexindustries.jguiwrapper.api.item.ItemWrapper;
import com.jodexindustries.jguiwrapper.api.text.SerializerType;
import com.jodexindustries.jguiwrapper.gui.advanced.AdvancedGui;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.locale.Lang;
import me.jetby.evilmobs.records.Mob;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class MainMenu extends AdvancedGui {
    public static final NamespacedKey CHANCE = new NamespacedKey("evilmobs", "chance");
    public MainMenu(EvilMobs plugin) {

        super(Lang.getString("gui.main.title"));


        int slot = 0;
        for (String type : plugin.getMobs().getMobs().keySet()) {
            Mob mob = plugin.getMobs().getMobs().get(type);
            int finalSlot = slot;
            registerItem(type, builder -> {
                builder.slots(finalSlot);
                builder.defaultItem(ItemWrapper.builder(Material.valueOf(mob.entityType().name()+"_SPAWN_EGG"), SerializerType.MINI_MESSAGE)
                        .displayName(Lang.getString("gui.main.display_name").replace("{type}", type))
                        .lore(Lang.getList("gui.main.lore"))
                        .build());

                builder.defaultClickHandler((event, controller) ->  {
                    event.setCancelled(true);

                    if (event.getWhoClicked() instanceof Player player) {
                        new InvEditor(plugin, player, mob).open(player);
                    }
                });
            });
            slot++;
        }
    }
}
