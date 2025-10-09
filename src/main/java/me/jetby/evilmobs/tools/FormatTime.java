package me.jetby.evilmobs.tools;


import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.configurations.Config;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class FormatTime {

    private final Config config;
    private final Map<String, String[]> cachedFormats;

    public FormatTime(EvilMobs plugin) {
        this.config = plugin.getCfg();
        this.cachedFormats = new HashMap<>();

        FileConfiguration configuration = plugin.getLang().getConfig();

        cachedFormats.put("weeks", configuration.getStringList("formattedTime.weeks").toArray(new String[0]));
        cachedFormats.put("days", configuration.getStringList("formattedTime.days").toArray(new String[0]));
        cachedFormats.put("hours", configuration.getStringList("formattedTime.hours").toArray(new String[0]));
        cachedFormats.put("minutes", configuration.getStringList("formattedTime.minutes").toArray(new String[0]));
        cachedFormats.put("seconds", configuration.getStringList("formattedTime.seconds").toArray(new String[0]));
    }

    public String stringFormat(int totalSeconds) {
        int weeks = totalSeconds / (7 * 24 * 3600);
        int days = (totalSeconds % (7 * 24 * 3600)) / (24 * 3600);
        int hours = (totalSeconds % (24 * 3600)) / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        Map<String, String> timeUnits = new HashMap<>(8);

        timeUnits.put("%weeks%", formatUnit(weeks, cachedFormats.get("weeks")));
        timeUnits.put("%days%", formatUnit(days, cachedFormats.get("days")));
        timeUnits.put("%hours%", formatUnit(hours, cachedFormats.get("hours")));
        timeUnits.put("%minutes%", formatUnit(minutes, cachedFormats.get("minutes")));
        timeUnits.put("%seconds%", formatUnit(seconds, cachedFormats.get("seconds")));

        String format = config.getFormattedTimeFormat();

        for (Map.Entry<String, String> entry : timeUnits.entrySet()) {
            format = format.replace(entry.getKey(), entry.getValue());
        }
        format = format.trim();
        if (format.isEmpty()) {
            return "0";
        }

        return format;
    }

    private String formatUnit(int value, String[] forms) {
        if (value == 0 || forms == null || forms.length < 3) {
            return "";
        }

        value = Math.abs(value);
        int remainder10 = value % 10;
        int remainder100 = value % 100;

        if (remainder10 == 1 && remainder100 != 11) {
            return String.format("%d %s", value, forms[0]);
        } else if (remainder10 >= 2 && remainder10 <= 4 && (remainder100 < 10 || remainder100 >= 20)) {
            return String.format("%d %s", value, forms[1]);
        } else {
            return String.format("%d %s", value, forms[2]);
        }
    }
}