package nl.parrotlync.discovsuite.bungeecord.util;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;

import java.util.ArrayList;
import java.util.List;

public class PlayerCache {
    private final List<String> players = new ArrayList<>();

    public void load() {
        ProxyServer.getInstance().getScheduler().runAsync(DiscovSuite.getInstance(), () -> {
            try {
                List<String> playerList = DiscovSuite.getInstance().getDatabase().getPlayers();
                for (String player : playerList) {
                    addPlayer(player);
                }
                DiscovSuite.getInstance().getLogger().warning("CACHE: Fetched " + players.size() + " players from database!");
            } catch (Exception e) {
                DiscovSuite.getInstance().getLogger().warning("CACHE: Something went wrong while fetching players");
                e.printStackTrace();
            }
        });
    }

    public void addPlayer(String player) {
        if (!players.contains(player)) {
            players.add(player);
        }
    }

    public List<String> getPlayers() {
        return players;
    }
}
