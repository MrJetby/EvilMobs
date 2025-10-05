package me.jetby.evilmobs.tools;

import me.jetby.evilmobs.EvilMobs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Version implements Listener {
    private final EvilMobs plugin;
    private String LAST_VERSION = "error";

    private final String VERSION_LINK = "https://raw.githubusercontent.com/MrJetby/EvilMobs/refs/heads/master/VERSION";
    private String DOWNLOAD_LINK = "https://raw.githubusercontent.com/MrJetby/EvilMobs/refs/heads/master/UPDATE_LINK";
    private final String NAME = "EvilMobs";
    private final String COLOR = "§6§l";

    public Version(EvilMobs plugin) {
        this.plugin = plugin;

        LAST_VERSION = getLastVersion();
        DOWNLOAD_LINK = getDownloadLink();
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission(NAME.toLowerCase() + ".version")) {
            if (!isLastVersion()) {
                for (String string : getAlert()) {
                    player.sendMessage(string);
                }
            }
        }
    }


    public List<String> getAlert() {
        List<String> oldVersion = new ArrayList<>(List.of(
                "",
                "§7-------- " + COLOR + NAME + " §7--------",
                COLOR + "● §fAttention, update available, please update the plugin.",
                COLOR + "● §7Your version: §c" + getVersion() + " §7а latest §a" + LAST_VERSION,
                "",
                COLOR + "● §fDownload here: §b" + DOWNLOAD_LINK,
                "§7------------------------",
                ""
        ));
        List<String> lastVersion = new ArrayList<>(List.of(
                "",
                "§7-------- " + COLOR + NAME + " §7--------",
                COLOR + "● §7Plugin version: §a" + getVersion(),
                "",
                COLOR + "● §aYou are using the latest version ✔",
                "",
                "§7------------------------",
                ""
        ));

        if (!isLastVersion()) {
            return oldVersion;
        }
        return lastVersion;
    }

    private String getRaw(String link) {
        try {
            URL url = new URL(link);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder builder = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                builder.append(inputLine);
            }
            in.close();
            return builder.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    public boolean isLastVersion() {
        return getVersion().equalsIgnoreCase(LAST_VERSION);
    }


    private String getLastVersion() {
        String result = getRaw(VERSION_LINK);
        assert result != null;
        return result;
    }

    private String getDownloadLink() {
        String result = getRaw(DOWNLOAD_LINK);
        assert result != null;
        return result;
    }
}
