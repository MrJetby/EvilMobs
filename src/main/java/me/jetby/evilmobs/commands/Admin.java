package me.jetby.evilmobs.commands;

import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.Maps;
import me.jetby.evilmobs.MobCreator;
import me.jetby.evilmobs.tools.ParticleEffectManager;
import me.jetby.treex.text.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

        if (args.length == 0) {
            sender.sendMessage(Colorize.text("&#FB430A&lEvilMobs"));
            sender.sendMessage("/em spawn <id>");
            sender.sendMessage("/em despawn <id>");
            sender.sendMessage("/em kill <id>");
            sender.sendMessage("/em killall");
            sender.sendMessage("/em menu");
            sender.sendMessage("/em list");
            sender.sendMessage("/em reload");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "spawn": {
                if (args.length < 2) {
                    sender.sendMessage("Usage: /em spawn <id>");
                    break;
                }
                if (mobCreators.containsKey(args[1])) {
                    sender.sendMessage("Mob with ID " + args[1] + " already exists.");
                    return true;
                }
                MobCreator mobCreator = new MobCreator(Maps.mobs.get(args[1]));
                mobCreator.spawn();

                mobCreators.put(args[1], mobCreator);
                break;
            }
            case "test": {
                if (sender instanceof Player player)
                    ParticleEffectManager.playEffect(args[1], player.getLocation(), plugin.getParticles());
                break;
            }
            case "tp": {
                if (args.length < 2) {
                    sender.sendMessage("Usage: /em tp <id>");
                    break;
                }

                if (mobCreators.containsKey(args[1])) {
                    if (sender instanceof Player player) {
                        Location location = null;
                        for (World world : Bukkit.getWorlds()) {
                            for (Entity e : world.getEntities()) {
                                if (!e.getPersistentDataContainer().has(NAMESPACED_KEY, PersistentDataType.STRING))
                                    continue;
                                if (!e.getPersistentDataContainer().get(NAMESPACED_KEY, PersistentDataType.STRING).equalsIgnoreCase(args[1]))
                                    continue;

                                if (e instanceof LivingEntity l) {
                                    location = l.getLocation();
                                    break;
                                }
                            }
                        }
                        if (location != null) {
                            player.teleport(location);
                            sender.sendMessage("Teleported to mob");
                        } else {
                            sender.sendMessage("No mobs found");
                        }
                        player.teleport(mobCreators.get(args[1]).getLivingEntity().getLocation());
                        sender.sendMessage("Teleported to mob");
                    }
                }
                break;
            }
            case "kill": {
                if (args.length < 2) {
                    sender.sendMessage("Usage: /em kill <id>");
                    break;
                }
                if (mobCreators.containsKey(args[1])) {
                    mobCreators.get(args[1]).killAllMinions();
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
                sender.sendMessage(i + " mobs was killed");
                break;
            }
            case "despawn": {
                if (args.length < 2) {
                    sender.sendMessage("Usage: /em despawn <id>");
                    break;
                }
                if (mobCreators.containsKey(args[1])) {
                    mobCreators.get(args[1]).end();
                    mobCreators.get(args[1]).getLivingEntity().remove();
                    mobCreators.remove(args[1]);
                    sender.sendMessage("Mob with ID " + args[1] + " despawned.");
                } else {
                    sender.sendMessage("Mob with ID " + args[1] + " not found.");
                }
                break;
            }
            case "menu":
                if (sender instanceof Player player) plugin.getMainMenu().open(player);
                break;
            case "list": {
                sender.sendMessage("Mobs list:");
                sender.sendMessage("");
                int num = 1;
                for (World world : Bukkit.getWorlds()) {
                    for (LivingEntity e : world.getLivingEntities()) {
                        if (!e.getPersistentDataContainer().has(NAMESPACED_KEY, PersistentDataType.STRING)) continue;
                        String id = e.getPersistentDataContainer().get(NAMESPACED_KEY, PersistentDataType.STRING);
                        sender.sendMessage("§7#" + num + " §e" + id + " (" + Maps.mobs.get(id).name() + "§r) [" + e.getHealth() + "]");
                        num++;
                    }
                }
                break;
            }
            case "reload": {
                try {
                    long startTime = System.currentTimeMillis();
                    plugin.getCfg().load();
                    plugin.getItems().load();
                    plugin.getMobs().load();
                    plugin.getParticles().load();

                    sender.sendMessage(Colorize.text(plugin.getLang().getConfig().getString("reload", "reload").replace("{time}", String.valueOf(System.currentTimeMillis() - startTime))));
                } catch (Exception e) {
                    sender.sendMessage(e.getMessage());
                }
                break;
            }
            default:
                sender.sendMessage(Colorize.text("&#FB430A&lEvilMobs"));
                sender.sendMessage("/em spawn <id>");
                sender.sendMessage("/em despawn <id>");
                sender.sendMessage("/em kill <id>");
                sender.sendMessage("/em killall");
                sender.sendMessage("/em menu");
                sender.sendMessage("/em list");
                sender.sendMessage("/em reload");
                break;

        }

        return false;
    }


    private final List<String> completions = new ArrayList<>();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        completions.clear();

        if (args.length == 1) {
            completions.add("tp");
            completions.add("spawn");
            completions.add("despawn");
            completions.add("kill");
            completions.add("killall");
            completions.add("menu");
            completions.add("list");
            completions.add("reload");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("spawn")) {
            completions.addAll(Maps.mobs.keySet());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("despawn")
                || args[0].equalsIgnoreCase("kill")
                || args[0].equalsIgnoreCase("tp")) {
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
