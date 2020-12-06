package nl.parrotlync.discovsuite.listener;

import nl.parrotlync.discovsuite.DiscovSuite;
import org.apache.commons.lang.time.DateUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class EventListener implements Listener {
    private final HashMap<UUID, Date> cooldownMap = new HashMap<>();

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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getDamager().hasPermission("discovsuite.punch") && event.getDamager() instanceof Player) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (player.hasPermission("discovsuite.punchable")) {
                    Date now = new Date();
                    if (cooldownMap.get(event.getDamager().getUniqueId()) == null || now.compareTo(cooldownMap.get(event.getDamager().getUniqueId())) > 0) {
                        cooldownMap.put(event.getDamager().getUniqueId(), DateUtils.addSeconds(now, 5));
                        player.setVelocity(new Vector(0, 1, 0));
                    }
                }
            }
        }
    }
}
