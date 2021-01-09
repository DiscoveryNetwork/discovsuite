package nl.parrotlync.discovsuite.bungeecord.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;

import java.util.Date;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        final Date now = new Date();
        ProxiedPlayer player = event.getPlayer();
        DiscovSuite.getInstance().getSessions().put(player.getUniqueId(), now);
        ProxyServer.getInstance().getScheduler().runAsync(DiscovSuite.getInstance(), () -> {
            try {
                DiscovSuite.getInstance().getDatabase().savePlayer(player, now);
            } catch (Exception e) {
                DiscovSuite.getInstance().getLogger().warning("Something went wrong while saving a player");
                e.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        final Date logout = new Date();
        ProxyServer.getInstance().getScheduler().runAsync(DiscovSuite.getInstance(), () -> {
            try {
                DiscovSuite.getInstance().getDatabase().saveSession(player, DiscovSuite.getInstance().getSessions().get(player.getUniqueId()), logout);
            } catch (Exception e) {
                DiscovSuite.getInstance().getLogger().warning("Something went wrong while saving a player");
                e.printStackTrace();
            }
        });
    }
}
