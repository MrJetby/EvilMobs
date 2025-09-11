package me.jetby.evilmobs.tools;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.records.Bar;
import me.jetby.evilmobs.records.Mob;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

@UtilityClass
public class MiniBar {

    @Getter
    private final Map<UUID, Data> datas = new HashMap<>();

    @RequiredArgsConstructor
    @Setter
    @Getter
    public class Data {
        net.kyori.adventure.bossbar.BossBar bossBar = null;
        Set<Audience> audiences = new HashSet<>();
        int durationTask;
        int nearTask = 0;
        String id;
    }

    public void createBossBar(@NonNull String id, @NonNull Mob mob, @NonNull Entity entity) {
        Bar bossBarConfig = mob.bossBars().get(id);
        Component title = Component.text(bossBarConfig.title());
        net.kyori.adventure.bossbar.BossBar bar = net.kyori.adventure.bossbar.BossBar.bossBar(
                title,
                1.0f,
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
        datas.put(entityId, data);
    }

    public void show(@NonNull String id, @NonNull Audience target) {
        datas.values().stream()
                .filter(data -> data.id.equals(id))
                .forEach(data -> {
                    net.kyori.adventure.bossbar.BossBar bar = data.bossBar;
                    if (bar == null) return;

                    Set<Audience> viewers = data.audiences;
                    target.showBossBar(bar);
                    viewers.add(target);
                });
    }

    public void show(@NonNull String id, @NonNull List<Audience> targets) {
        datas.values().stream()
                .filter(data -> data.id.equals(id))
                .forEach(data -> {
                    net.kyori.adventure.bossbar.BossBar bar = data.bossBar;
                    if (bar == null) return;

                    Set<Audience> viewers = data.audiences;
                    for (Audience audience : targets) {
                        audience.showBossBar(bar);
                        viewers.add(audience);
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
            Set<Audience> viewers = data.audiences;
            net.kyori.adventure.bossbar.BossBar bar = data.bossBar;
            Location location = entity.getLocation();

            for (Player player : location.getWorld().getPlayers()) {
                boolean inRange = player.getLocation().distance(location) <= radius;

                if (inRange && !viewers.contains(player)) {
                    player.showBossBar(bar);
                    viewers.add(player);
                } else if (!inRange && viewers.contains(player)) {
                    player.hideBossBar(bar);
                    viewers.remove(player);
                }
            }
            viewers.removeIf(audience -> audience instanceof Player p && !p.isOnline());

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

                    Set<Audience> viewers = data.audiences;
                    net.kyori.adventure.bossbar.BossBar bar = data.bossBar;

                    for (Player player : location.getWorld().getPlayers()) {
                        boolean inRange = player.getLocation().distance(location) <= radius;

                        if (inRange && viewers.contains(player)) {
                            player.hideBossBar(bar);
                            viewers.remove(player);
                        }
                    }
                });
    }

    public void remove(@NonNull String id, @NonNull Audience target) {
        datas.values().stream()
                .filter(data -> data.id.equals(id))
                .forEach(data -> {
                    net.kyori.adventure.bossbar.BossBar bar = data.bossBar;
                    if (bar == null) return;

                    Set<Audience> viewers = data.audiences;
                    if (viewers == null) return;

                    target.hideBossBar(bar);
                    viewers.remove(target);
                });
    }

    public void remove(@NonNull String id, @NonNull List<Audience> targets) {
        datas.values().stream()
                .filter(data -> data.id.equals(id))
                .forEach(data -> {
                    net.kyori.adventure.bossbar.BossBar bar = data.bossBar;
                    if (bar == null) return;

                    Set<Audience> viewers = data.audiences;
                    if (viewers == null) return;

                    for (Audience audience : targets) {
                        audience.hideBossBar(bar);
                        viewers.remove(audience);
                    }
                });
    }

    public List<Audience> getPlayers(@NonNull String id) {
        Set<Audience> allViewers = new HashSet<>();
        datas.values().stream()
                .filter(data -> data.id.equals(id))
                .forEach(data -> {
                    Set<Audience> viewers = data.audiences;
                    if (viewers != null) {
                        allViewers.addAll(viewers);
                    }
                });
        return new ArrayList<>(allViewers);
    }

    public void deleteBossBar(@NonNull UUID entityId) {
        Data data = datas.get(entityId);
        if (data == null) return;

        remove(data.id, new ArrayList<>(data.audiences));
        if (data.nearTask != 0) {
            Bukkit.getScheduler().cancelTask(data.nearTask);
        }
        if (data.durationTask != 0) {
            Bukkit.getScheduler().cancelTask(data.durationTask);
        }
        datas.remove(entityId);
    }

    public void deleteBossBar(@NonNull String id, @NonNull Entity entity) {
        UUID entityId = entity.getUniqueId();
        Data data = datas.get(entityId);
        if (data == null || !data.id.equals(id)) return;

        remove(data.id, new ArrayList<>(data.audiences));
        if (data.nearTask != 0) {
            Bukkit.getScheduler().cancelTask(data.nearTask);
        }
        if (data.durationTask != 0) {
            Bukkit.getScheduler().cancelTask(data.durationTask);
        }
        datas.remove(entityId);
    }

    public void deleteBossBar(@NonNull String id) {
        List<UUID> toRemove = new ArrayList<>();
        datas.forEach((entityId, data) -> {
            if (data.id.equals(id)) {
                remove(id, new ArrayList<>(data.audiences));
                if (data.nearTask != 0) {
                    Bukkit.getScheduler().cancelTask(data.nearTask);
                }
                if (data.durationTask != 0) {
                    Bukkit.getScheduler().cancelTask(data.durationTask);
                }
                toRemove.add(entityId);
            }
        });
        toRemove.forEach(datas::remove);
    }
}