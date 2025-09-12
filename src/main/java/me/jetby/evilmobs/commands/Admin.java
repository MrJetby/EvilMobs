package me.jetby.evilmobs.commands;

import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.MobCreator;
import me.jetby.evilmobs.tools.ParticleEffectManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static me.jetby.evilmobs.EvilMobs.NAMESPACED_KEY;

public class Admin implements CommandExecutor, TabCompleter {
    private final EvilMobs plugin;

    private final Map<String, MobCreator> mobCreators;
    public Admin(EvilMobs plugin) {
        this.plugin = plugin;
        this.mobCreators = plugin.getMobCreators();

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {

        switch (args[0]) {
            case "spawn": {
                if (mobCreators.containsKey(args[1])) {
                    sender.sendMessage("Mob with ID " + args[1] + " already exists.");
                    return true;
                }
                MobCreator mobCreator = new MobCreator(plugin.getMobs().getMobs().get(args[1]));
                mobCreator.spawn();

                mobCreators.put(args[1], mobCreator);
                break;
            }
            case "test": {
                ParticleEffectManager.playEffect((Player) sender, plugin.getParticles().getEffects().get(args[1]));
            }
            case "list": {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity e : world.getEntities()) {
                        if (!e.getPersistentDataContainer().has(NAMESPACED_KEY, PersistentDataType.STRING)) continue;
                        String id = e.getPersistentDataContainer().get(NAMESPACED_KEY, PersistentDataType.STRING);
                        sender.sendMessage(id + " (" + plugin.getMobs().getMobs().get(id).name() + ")");

                    }
                }
                break;
            }
        }

        return false;
    }
}
