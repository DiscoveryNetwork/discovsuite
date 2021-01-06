package nl.parrotlync.discovsuite.listener;

import nl.parrotlync.discovsuite.DiscovSuite;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class WaterListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("discovsuite.wtp.override")) { return; }
        Location location = player.getLocation();
        if (location.getBlock().getType() == Material.STATIONARY_WATER || location.getBlock().getType() == Material.WATER) {
            World world = player.getWorld();
            if (isEnabledWorld(world)) {
                player.teleport(world.getSpawnLocation());
            }
        }
    }

    private boolean isEnabledWorld(World world) {
        for (String enabledWorld : DiscovSuite.getInstance().getConfig().getStringList("water-teleport-enabled-worlds")) {
            if (Bukkit.getWorld(enabledWorld) != null) {
                if (world.getName().equalsIgnoreCase(enabledWorld)) {
                    return true;
                }
            }
        }
        return false;
    }
}
