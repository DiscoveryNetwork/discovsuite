package nl.parrotlync.discovsuite.listener;

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
            player.teleport(world.getSpawnLocation());
        }
    }
}
