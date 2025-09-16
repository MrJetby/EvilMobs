package me.jetby.evilmobs.commands;

import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.Maps;
import me.jetby.evilmobs.MobCreator;
import me.jetby.evilmobs.tools.ParticleEffectManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.jetby.evilmobs.EvilMobs.NAMESPACED_KEY;

public class Admin implements CommandExecutor, TabCompleter {
    private final EvilMobs plugin;

    private final Map<String, MobCreator> mobCreators;
    public Admin(EvilMobs plugin) {
        this.plugin = plugin;
        this.mobCreators = Maps.mobCreators;

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
                if (sender instanceof Player player) ParticleEffectManager.playEffect(args[1], player.getLocation(), plugin.getParticles());
                break;
            }
            case "kill": {
                if (mobCreators.containsKey(args[1])) {
                    mobCreators.get(args[1]).getLivingEntity().setHealth(0);
                    mobCreators.remove(args[1]);
                }
                break;
            }
            case "killall": {
                int i = 0;
                for (World world : Bukkit.getWorlds()) {
                    for (Entity e : world.getEntities()) {
                        if (!e.getPersistentDataContainer().has(NAMESPACED_KEY, PersistentDataType.STRING)) continue;
                        if (e instanceof LivingEntity l) {
                            i++;
                            l.setHealth(0);
                        }
                    }
                }
                sender.sendMessage(i+" mobs was killed");
                break;
            }
            case "despawn": {
                if (mobCreators.containsKey(args[1])) {
                    mobCreators.get(args[1]).end();
                    mobCreators.get(args[1]).getLivingEntity().remove();
                    mobCreators.remove(args[1]);
                }
                break;
            }
            case "menu":
                if (sender instanceof Player player) plugin.getMainMenu().open(player);

                break;
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


    private final List<String> completions = new ArrayList<>();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        completions.clear();

        if (args.length==1)  {
            completions.add("spawn");
            completions.add("despawn");
            completions.add("kill");
            completions.add("menu");
            completions.add("list");
        }

        if (args.length==2 && args[0].equalsIgnoreCase("spawn")) {
            completions.addAll(plugin.getMobs().getMobs().keySet());
        }
        if (args.length==2 && args[0].equalsIgnoreCase("despawn") || args[0].equalsIgnoreCase("kill")) {
            completions.addAll(mobCreators.keySet());
        }


        return getResult(args);
    }

    private List<String> getResult(String[] args) {
        if (completions.isEmpty()) {
            return completions;
        }
        final List<String> result = new ArrayList<>();
        for (String c : completions) {
            if (StringUtil.startsWithIgnoreCase(c, args[args.length - 1])) {
                result.add(c);
            }
        }
        return result;
    }

}
