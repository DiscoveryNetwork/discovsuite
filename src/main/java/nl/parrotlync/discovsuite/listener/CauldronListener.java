package nl.parrotlync.discovsuite.listener;

import nl.parrotlync.discovsuite.DiscovSuite;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class CauldronListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.CAULDRON) {
            World world = event.getClickedBlock().getWorld();
            if (isEnabledWorld(world)) {
                if (world.getBlockAt(event.getClickedBlock().getLocation().add(0, 1, 0)).getType() == Material.WOOD_STEP) {
                    event.getPlayer().openInventory(Bukkit.createInventory(null, 27, "Disposal"));
                }
            }
        }
    }

    private boolean isEnabledWorld(World world) {
        for (String enabledWorld : DiscovSuite.getInstance().getConfig().getStringList("cauldron-bins-enabled-worlds")) {
            if (Bukkit.getWorld(enabledWorld) != null) {
                if (world.getName().equalsIgnoreCase(enabledWorld)) {
                    return true;
                }
            }
        }
        return false;
    }
}
