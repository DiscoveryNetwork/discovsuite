package nl.parrotlync.discovsuite.spigot.listener;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import org.apache.commons.lang.time.DateUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
        DiscovSuite.getInstance().getNicknameManager().load(event.getPlayer());
        DiscovSuite.getInstance().getBoardManager().init(event.getPlayer());
        if (event.getPlayer().hasPermission("discovsuite.fly.onjoin")) {
            event.getPlayer().setAllowFlight(true);
        }

        if (event.getPlayer().hasPermission("discovsuite.op") && !event.getPlayer().isOp()) {
            event.getPlayer().setOp(true);
        } else if (!event.getPlayer().hasPermission("discovsuite.op") && event.getPlayer().isOp()) {
            event.getPlayer().setOp(false);
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

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getPlayer().hasPermission("discovsuite.punch")) {
            if (event.getRightClicked() instanceof Player) {
                Player target = (Player) event.getRightClicked();
                launch(target);
            }
        }
    }

    private void launch(Player target) {
        if (!DiscovSuite.getInstance().getDisabledWorlds().contains(target.getWorld().getName().toLowerCase())) {
            if (target.hasPermission("discovsuite.punchable")) {
                Date now = new Date();
                if (cooldownMap.get(target.getUniqueId()) == null || now.compareTo(cooldownMap.get(target.getUniqueId())) > 0) {
                    cooldownMap.put(target.getUniqueId(), DateUtils.addSeconds(now, 5));
                    target.setVelocity(new Vector(0, 1, 0));
                }
            }
        }
    }
}
