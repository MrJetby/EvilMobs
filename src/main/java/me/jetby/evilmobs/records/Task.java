package me.jetby.evilmobs.records;

import java.util.List;

public record Task(
        int delay,
        int period,
        int amount,
        List<String> actions

) {
}
