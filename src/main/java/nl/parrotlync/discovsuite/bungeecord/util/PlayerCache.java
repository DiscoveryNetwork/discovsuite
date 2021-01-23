package nl.parrotlync.discovsuite.bungeecord.util;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerCache {
    private final HashMap<UUID, String> players = new HashMap<>();

    public void load() {
        players.clear();
        ProxyServer.getInstance().getScheduler().runAsync(DiscovSuite.getInstance(), () -> {
            try {
                HashMap<UUID, String> storedPlayers = DiscovSuite.getInstance().getDatabase().getPlayers();
                for (UUID player : storedPlayers.keySet()) {
                    players.put(player, storedPlayers.get(player));
                }
                DiscovSuite.getInstance().getLogger().warning("CACHE: Fetched " + players.size() + " players from database!");
            } catch (Exception e) {
                DiscovSuite.getInstance().getLogger().warning("CACHE: Something went wrong while fetching players");
                e.printStackTrace();
            }
        });
    }

    public void addPlayer(ProxiedPlayer player) {
        if (!players.containsKey(player.getUniqueId())) {
            players.put(player.getUniqueId(), player.getName());
        }
    }

    public boolean hasPlayer(ProxiedPlayer player) {
        return players.containsKey(player.getUniqueId());
    }

    public List<String> getPlayerNames() {
        return (List<String>) players.values();
    }
}
