package me.jetby.evilmobs.tools;


import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import lombok.Getter;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.Maps;
import me.jetby.evilmobs.MobCreator;
import me.jetby.evilmobs.records.Mob;
import me.jetby.treex.tools.Randomizer;
import org.bukkit.Bukkit;

import java.time.DayOfWeek;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

import static me.jetby.evilmobs.EvilMobs.LOGGER;

public class AutoStart {

    private final EvilMobs plugin;

    private final int minOnline;
    private final boolean isFreeze;
    private final int zone;

    public AutoStart(EvilMobs plugin, int minOnline,
                     boolean isFreeze,
                     int zone,
                     Object time) {
        this.plugin = plugin;
        this.minOnline = minOnline;
        this.isFreeze = isFreeze;
        this.zone = zone;

        if (time instanceof Integer i) {
            run(i);
        } else if (time instanceof List<?> l) {
            List<String> list = l.stream()
                    .filter(item -> item instanceof String)
                    .map(item -> (String) item)
                    .toList();
            run(list);
        }
    }

    @Getter
    private int timeToStart;

    private int taskId;

    private void run(int time) {
        timeToStart = time;
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (timeToStart <= 0) {
                timeToStart = time;
            } else {
                if (Bukkit.getOnlinePlayers().size() < minOnline) if (isFreeze) return;

                timeToStart--;
            }
        }, 0L, 20L).getTaskId();
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    private void run(List<String> list) {

        Set<Times> times = new HashSet<>();
        Object2BooleanMap<Times> executes = new Object2BooleanOpenHashMap<>();
        for (String s : list) {
            try {
                String[] globalArgs = s.split(";");
                if (globalArgs.length >= 1) {
                    String[] timeArgs = globalArgs[0].split(":");
                    String id = null;
                    if (globalArgs.length == 2) {
                        id = globalArgs[1];
                    }

                    int hour = -1, min = -1;
                    DayOfWeek day = null;
                    if (timeArgs.length >= 2) {
                        hour = Integer.parseInt(timeArgs[0]);
                        min = Integer.parseInt(timeArgs[1]);
                        if (timeArgs.length == 3) {
                            day = DayOfWeek.valueOf(timeArgs[2].toUpperCase());
                        }
                    }
                    times.add(new Times(hour, min, day, id));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        timeToStart = calculateSecondsToNextEvent(times);

        taskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            if (Bukkit.getOnlinePlayers().size() < minOnline) {
                return;
            }

            for (Times time : times) {
                boolean alreadyExecuted = executes.getOrDefault(time, false);
                ZonedDateTime now = ZonedDateTime.now(ZoneOffset.ofHours(zone));
                boolean isTargetTime;
                if (time.min() == -1 || time.hour() == -1) {
                    continue;
                }
                if (time.day() == null) {
                    isTargetTime = now.getHour() == time.hour()
                            && now.getMinute() == time.min();
                } else {
                    isTargetTime = now.getDayOfWeek() == time.day()
                            && now.getHour() == time.hour()
                            && now.getMinute() == time.min();
                }

                if (isTargetTime && !alreadyExecuted) {
                    executes.put(time, true);
                    if (Bukkit.getOnlinePlayers().size() < minOnline) {
                        return;
                    }
                    String id;
                    if (time.id() != null) {
                        id = time.id();
                    } else {
                        id = getRandomMob();
                    }
                    if (id == null) {
                        LOGGER.error("No mobs available to spawn.");
                        return;
                    }
                    if (Maps.mobCreators.containsKey(id)) {
                        LOGGER.warn("Mob with ID " + id + " already exists.");
                        return;
                    }
                    MobCreator mobCreator = new MobCreator(Maps.mobs.get(id));
                    mobCreator.spawn();

                    Maps.mobCreators.put(id, mobCreator);
                }

                if (!isTargetTime) {
                    executes.put(time, false);
                }
                timeToStart = calculateSecondsToNextEvent(times);
            }
        }, 0L, 20L).getTaskId();

    }

    private int calculateSecondsToNextEvent(Set<Times> times) {
        if (times.isEmpty()) return -1;

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.ofHours(zone));
        long minSeconds = Long.MAX_VALUE;

        for (Times targetTime : times) {
            if (targetTime.min() == -1 || targetTime.hour() == -1) {
                continue;
            }
            ZonedDateTime nextEvent;
            if (targetTime.day() == null) {
                nextEvent = now
                        .withHour(targetTime.hour())
                        .withMinute(targetTime.min())
                        .withSecond(0)
                        .withNano(0);
                if (now.isAfter(nextEvent) || now.equals(nextEvent)) {
                    nextEvent = nextEvent.plusDays(1);
                }
            } else {
                DayOfWeek currentDay = now.getDayOfWeek();
                int daysDiff = (targetTime.day().getValue() - currentDay.getValue() + 7) % 7;
                if (daysDiff == 0 && (now.getHour() > targetTime.hour() ||
                        (now.getHour() == targetTime.hour() && now.getMinute() >= targetTime.min()))) {
                    daysDiff = 7;
                }
                nextEvent = now.plusDays(daysDiff)
                        .withHour(targetTime.hour())
                        .withMinute(targetTime.min())
                        .withSecond(0)
                        .withNano(0);
            }

            long seconds = java.time.Duration.between(now, nextEvent).getSeconds();
            if (seconds > 0 && seconds < minSeconds) {
                minSeconds = seconds;
            }
        }

        return minSeconds == Long.MAX_VALUE ? -1 : (int) minSeconds;
    }

    private record Times(int hour, int min, DayOfWeek day, String id) {
    }

    private String getRandomMob() {
        Collection<Mob> mobs = Maps.mobs.values();
        if (mobs.isEmpty()) return null;
        List<Mob> mobsList = new ArrayList<>(mobs);
        return mobsList.get(Randomizer.rand(mobs.size())).id();
    }
}
