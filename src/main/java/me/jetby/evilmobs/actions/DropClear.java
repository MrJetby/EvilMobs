package me.jetby.evilmobs.actions;

import me.jetby.treex.actions.Action;
import me.jetby.treex.actions.ActionContext;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class DropClear implements Action {

    @Override
    public void execute(@NotNull ActionContext ctx) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getType() == EntityType.DROPPED_ITEM && entity.hasMetadata("evilmobs_originalItem")) {
                    entity.remove();
                }
            }
        }
    }
}
