package me.jetby.evilmobs.actions;

import me.jetby.treex.actions.Action;
import me.jetby.treex.actions.ActionContext;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;

public class DropClear implements Action {

    @Override
    public void execute(@NotNull ActionContext ctx) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Item item && item.hasMetadata("evilmobs_originalItem")) {
                    item.remove();
                }
            }
        }
    }
}
