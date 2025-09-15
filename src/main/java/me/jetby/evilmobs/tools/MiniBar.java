package me.jetby.evilmobs.tools;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.records.Bar;
import me.jetby.evilmobs.records.Mob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class MiniBar {

    public final Map<UUID, Data> datas = new ConcurrentHashMap<>();

    @RequiredArgsConstructor
    @Setter
    @Getter
    public class Data {
        BossBar bossBar = null;
        Set<Player> players = new HashSet<>();
        int durationTask;
        int nearTask = 0;
        String id;
        String progress;
        String originalTitle;
        Entity entity;
    }

    public void init(EvilMobs plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, MiniBar::updateAll, 0L, 5L);
    }

    public void createBossBar(@NonNull String id, @NonNull Mob mob, @NonNull Entity entity) {
        Bar bossBarConfig = mob.bossBars().get(id);
        BossBar bar = Bukkit.createBossBar(
                bossBarConfig.title(),
                bossBarConfig.color(),
                bossBarConfig.style()
        );

        UUID entityId = entity.getUniqueId();
        if (datas.containsKey(entityId)) {
            var getData = datas.get(entityId);
            deleteBossBar(entityId);
            if (getData.durationTask != 0) Bukkit.getScheduler().cancelTask(getData.durationTask);
        }

        int taskId = 0;
        if (bossBarConfig.duration() != -1) {
            taskId = Bukkit.getScheduler().runTaskLater(EvilMobs.getInstance(), () -> {
                deleteBossBar(entityId);
            }, bossBarConfig.duration() * 20L).getTaskId();
        }

        var data = new Data();
        data.setDurationTask(taskId);
        data.setBossBar(bar);
        data.setId(id);
        data.setEntity(entity);
        data.setOriginalTitle(bar.getTitle());
        data.setProgress(bossBarConfig.progress());
        datas.put(entityId, data);
    }

    public void show(@NonNull String id, @NonNull Player target) {
        datas.values().stream()
                .filter(data -> data.id.equals(id))
                .forEach(data -> {
                    BossBar bar = data.bossBar;
                    if (bar == null) return;

                    Set<Player> viewers = data.players;
                    bar.addPlayer(target);
                    viewers.add(target);
                });
    }

    public void updateAll() {
        if (datas.isEmpty()) return;
        datas.values().forEach(data -> {
            BossBar bossBar = data.bossBar;
            if (bossBar == null) return;

            if (data.entity instanceof LivingEntity livingEntity) {
                double healthPercent = livingEntity.getHealth() / livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

                if (data.progress.equalsIgnoreCase("%health_percentage%")) {
                    bossBar.setProgress(healthPercent);
                } else {
                    bossBar.setProgress(Double.parseDouble(data.progress));

                }

                String raw = data.originalTitle.replace("%health_percentage%", String.format("%.1f", healthPercent * 100));
                String parsed = TextUtil.setPapi(null, raw);
                bossBar.setTitle(parsed);

                data.setBossBar(bossBar);
            }
        });
    }

    public void show(@NonNull String id, @NonNull List<Player> targets) {
        datas.values().stream()
                .filter(data -> data.id.equalsIgnoreCase(id))
                .forEach(data -> {
                    BossBar bar = data.bossBar;
                    if (bar == null) return;

                    Set<Player> viewers = data.players;
                    for (Player player : targets) {
                        bar.addPlayer(player);
                        viewers.add(player);
                    }
                });
    }

    public void show(@NonNull String id, @NonNull Entity entity, int radius) {
        UUID entityId = entity.getUniqueId();
        Data data = datas.get(entityId);
        if (data == null || data.bossBar == null || !data.id.equals(id)) return;

        if (data.nearTask != 0) {
            Bukkit.getScheduler().cancelTask(data.nearTask);
        }

        int taskId = Bukkit.getScheduler().runTaskTimer(EvilMobs.getInstance(), () -> {
            Set<Player> viewers = data.players;
            BossBar bar = data.bossBar;
            Location location = entity.getLocation();

            for (Player player : location.getWorld().getPlayers()) {
                boolean inRange = player.getLocation().distance(location) <= radius;

                if (inRange && !viewers.contains(player)) {
                    bar.addPlayer(player);
                    viewers.add(player);
                } else if (!inRange && viewers.contains(player)) {
                    bar.removePlayer(player);
                    viewers.remove(player);
                }
            }
            viewers.removeIf(player -> !player.isOnline());


        }, 0L, 5L).getTaskId();

        data.setNearTask(taskId);
    }

    public void remove(@NonNull String id, @NonNull Location location, int radius) {
        datas.values().stream()
                .filter(data -> data.id.equals(id))
                .forEach(data -> {
                    if (data.bossBar == null) return;

                    if (data.nearTask != 0) {
                        Bukkit.getScheduler().cancelTask(data.nearTask);
                    }

                    Set<Player> viewers = data.players;
                    BossBar bar = data.bossBar;

                    for (Player player : location.getWorld().getPlayers()) {
                        boolean inRange = player.getLocation().distance(location) <= radius;

                        if (inRange && viewers.contains(player)) {
                            bar.removePlayer(player);
                            viewers.remove(player);
                        }
                    }

                });
    }

    public void remove(@NonNull String id, @NonNull Player target) {
        datas.values().stream()
                .filter(data -> data.id.equals(id))
                .forEach(data -> {
                    BossBar bar = data.bossBar;
                    if (bar == null) return;

                    Set<Player> viewers = data.players;
                    if (viewers == null) return;

                    bar.removePlayer(target);
                    viewers.remove(target);
                });
    }

    public void remove(@NonNull String id, @NonNull List<Player> targets) {
        datas.values().stream()
                .filter(data -> data.id.equals(id))
                .forEach(data -> {
                    BossBar bar = data.bossBar;
                    if (bar == null) return;

                    Set<Player> viewers = data.players;
                    if (viewers == null) return;

                    for (Player player : targets) {
                        bar.removePlayer(player);
                        viewers.remove(player);
                    }
                });
    }

    public List<Player> getPlayers(@NonNull String id) {
        Set<Player> allViewers = new HashSet<>();
        datas.values().stream()
                .filter(data -> data.id.equals(id))
                .forEach(data -> {
                    Set<Player> viewers = data.players;
                    if (viewers != null) {
                        allViewers.addAll(viewers);
                    }
                });
        return new ArrayList<>(allViewers);
    }

    public void deleteBossBar(@NonNull UUID entityId) {
        Data data = datas.get(entityId);
        if (data == null) return;

        remove(data.id, new ArrayList<>(data.players));
        if (data.nearTask != 0) {
            Bukkit.getScheduler().cancelTask(data.nearTask);
        }
        if (data.durationTask != 0) {
            Bukkit.getScheduler().cancelTask(data.durationTask);
        }
        data.bossBar.removeAll();
        datas.remove(entityId);
    }

    public void deleteBossBar(@NonNull String id, @Nullable Entity entity) {
        if (entity==null) {
            deleteBossBar(id);
            return;
        }
        UUID entityId = entity.getUniqueId();
        Data data = datas.get(entityId);
        if (data == null || !data.id.equals(id)) return;

        remove(data.id, new ArrayList<>(data.players));
        if (data.nearTask != 0) {
            Bukkit.getScheduler().cancelTask(data.nearTask);
        }
        if (data.durationTask != 0) {
            Bukkit.getScheduler().cancelTask(data.durationTask);
        }
        data.bossBar.removeAll();
        datas.remove(entityId);
    }

    public void deleteBossBar(@NonNull String id) {
        List<UUID> toRemove = new ArrayList<>();
        datas.forEach((entityId, data) -> {
            if (data.id.equals(id)) {
                remove(id, new ArrayList<>(data.players));
                if (data.nearTask != 0) {
                    Bukkit.getScheduler().cancelTask(data.nearTask);
                }
                if (data.durationTask != 0) {
                    Bukkit.getScheduler().cancelTask(data.durationTask);
                }
                data.bossBar.removeAll();
                toRemove.add(entityId);
            }
        });
        toRemove.forEach(datas::remove);
    }
}