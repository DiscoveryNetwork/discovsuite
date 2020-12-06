package nl.parrotlync.discovsuite.scoreboard;

import nl.parrotlync.discovsuite.DiscovSuite;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BoardManager {
    private final String title;
    private final HashMap<Integer, List<String>> lines;
    private final HashMap<UUID, PlayerBoard> boards = new HashMap<>();

    public BoardManager(String title, HashMap<Integer, List<String>> lines) {
        this.title = title;
        this.lines = lines;
    }

    public void init(Player player) {
        PlayerBoard board = new PlayerBoard(player);
        Bukkit.getScheduler().runTaskTimerAsynchronously(DiscovSuite.getInstance(), board, 0, DiscovSuite.getInstance().getConfig().getInt("scoreboard-settings.interval"));
        boards.put(player.getUniqueId(), board);
    }

    public void remove(Player player) {
        boards.remove(player.getUniqueId());
    }

    public String getTitle() {
        return title;
    }

    public HashMap<Integer, List<String>> getLines() {
        return lines;
    }
}
