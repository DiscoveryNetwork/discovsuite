package nl.parrotlync.discovsuite.spigot.listener;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.model.Warp;
import org.apache.commons.lang.time.DateUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class EventListener implements Listener {
    private final HashMap<UUID, Date> cooldownMap = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (DiscovSuite.getInstance().getConfig().getBoolean("disable-join-quit-messages")) {
            event.setJoinMessage(null);
        }

        if (DiscovSuite.getInstance().getTeleportManager().isQueued(event.getPlayer())) {
            DiscovSuite.getInstance().getTeleportManager().teleport(event.getPlayer());
        } else {
            if (!isMatchingWorld(event.getPlayer().getWorld(), "nearest-warp-teleport-disabled-worlds")) {
                Warp warp = DiscovSuite.getInstance().getWarpManager().getNearestWarp(event.getPlayer());
                if (warp != null) {
                    event.getPlayer().teleport(warp.getLocation());
                }
            }
        }

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
        if (DiscovSuite.getInstance().getConfig().getBoolean("disable-join-quit-messages")) {
            event.setQuitMessage(null);
        }

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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("discovsuite.watertp.bypass")) { return; }
        Location location = player.getLocation();
        if (location.getBlock().getType() == Material.STATIONARY_WATER || location.getBlock().getType() == Material.WATER) {
            World world = player.getWorld();
            if (isMatchingWorld(world, "water-teleport-enabled-worlds")) {
                player.teleport(world.getSpawnLocation());
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.CAULDRON) {
            World world = event.getClickedBlock().getWorld();
            if (isMatchingWorld(world, "cauldron-bins-enabled-worlds")) {
                if (world.getBlockAt(event.getClickedBlock().getLocation().add(0, 1, 0)).getType() == Material.WOOD_STEP) {
                    event.getPlayer().openInventory(Bukkit.createInventory(null, 27, "Disposal"));
                }
            }
        }
    }

    private void launch(Player target) {
        if (!isMatchingWorld(target.getWorld(), "punch-disabled-worlds")) {
            if (target.hasPermission("discovsuite.punchable")) {
                Date now = new Date();
                if (cooldownMap.get(target.getUniqueId()) == null || now.compareTo(cooldownMap.get(target.getUniqueId())) > 0) {
                    cooldownMap.put(target.getUniqueId(), DateUtils.addSeconds(now, 5));
                    target.setVelocity(new Vector(0, 1, 0));
                }
            }
        }
    }

    private boolean isMatchingWorld(World world, String path) {
        for (String configWorld : DiscovSuite.getInstance().getConfig().getStringList(path)) {
            if (Bukkit.getWorld(configWorld) != null && world.getName().equalsIgnoreCase(configWorld)) {
                return true;
            }
        }
        return false;
    }
}
