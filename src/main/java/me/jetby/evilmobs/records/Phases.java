package me.jetby.evilmobs.records;

import java.util.List;
import java.util.Map;

public record Phases(
        String type,
        Map<String, List<String>> actions

) {
}
