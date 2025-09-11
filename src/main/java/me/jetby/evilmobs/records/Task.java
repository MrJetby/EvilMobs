package me.jetby.evilmobs.records;

import java.util.List;

public record Task(
        int delay,
        int period,
        List<String> actions

) {
}
