package nl.parrotlync.discovsuite.spigot.listener;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ModuleListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("discovsuite.override.watertp")) { return; }
        Location location = player.getLocation();
        if (location.getBlock().getType() == Material.STATIONARY_WATER || location.getBlock().getType() == Material.WATER) {
            World world = player.getWorld();
            if (isEnabledWorld(world, "water-teleport-enabled-worlds")) {
                player.teleport(world.getSpawnLocation());
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.CAULDRON) {
            World world = event.getClickedBlock().getWorld();
            if (isEnabledWorld(world, "cauldron-bins-enabled-worlds")) {
                if (world.getBlockAt(event.getClickedBlock().getLocation().add(0, 1, 0)).getType() == Material.WOOD_STEP) {
                    event.getPlayer().openInventory(Bukkit.createInventory(null, 27, "Disposal"));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (DiscovSuite.getInstance().getConfig().getBoolean("disable-join-quit-messages")) {
            event.setJoinMessage(null);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (DiscovSuite.getInstance().getConfig().getBoolean("disable-join-quit-messages")) {
            event.setQuitMessage(null);
        }
    }

    private boolean isEnabledWorld(World world, String path) {
        for (String enabledWorld : DiscovSuite.getInstance().getConfig().getStringList(path)) {
            if (Bukkit.getWorld(enabledWorld) != null) {
                if (world.getName().equalsIgnoreCase(enabledWorld)) {
                    return true;
                }
            }
        }
        return false;
    }
}
