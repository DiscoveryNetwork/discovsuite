package nl.parrotlync.discovsuite.listener;

import nl.parrotlync.discovsuite.DiscovSuite;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        DiscovSuite.getInstance().getBoardManager().init(event.getPlayer());
        if (event.getPlayer().hasPermission("discovsuite.fly.onjoin")) {
            event.getPlayer().setAllowFlight(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        DiscovSuite.getInstance().getBoardManager().remove(event.getPlayer());
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        String[] lines = event.getLines();
        for (int i = 0; i < lines.length; i++) {
            event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
        }
    }
}
