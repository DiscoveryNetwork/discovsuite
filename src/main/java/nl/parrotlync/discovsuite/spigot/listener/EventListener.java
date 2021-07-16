package nl.parrotlync.discovsuite.spigot.listener;

import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import nl.parrotlync.discovoutlines.DiscovOutlines;
import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.event.BuildModeToggleEvent;
import nl.parrotlync.discovsuite.spigot.model.Warp;
import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import org.apache.commons.lang.time.DateUtils;
import org.bukkit.*;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class EventListener implements Listener {
    private final HashMap<UUID, Date> cooldownMap = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (DiscovSuite.getInstance().getConfig().getBoolean("disable-join-quit-messages")) {
            event.setJoinMessage(null);
        }

        DiscovSuite.getInstance().getVanishManager().handleNewPlayer(event.getPlayer());
        if (event.getPlayer().hasPermission("discovsuite.vanish.onjoin")) {
            DiscovSuite.getInstance().getVanishManager().hidePlayer(event.getPlayer());
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

        Bukkit.getScheduler().runTaskLater(DiscovSuite.getInstance(), () -> DiscovSuite.getInstance().getAuthUtil().startAuthProcess(event.getPlayer()), 20);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (DiscovSuite.getInstance().getConfig().getBoolean("disable-join-quit-messages")) {
            event.setQuitMessage(null);
        }

        DiscovSuite.getInstance().getVanishManager().showPlayer(event.getPlayer());
        DiscovSuite.getInstance().getBoardManager().remove(event.getPlayer());

        // Disable BuildMode
        DiscovSuite.getInstance().getInventoryManager().returnInventory(event.getPlayer());
        disableOutlineHolograms(event.getPlayer());
        User user = DiscovSuite.getInstance().getLuckPermsUser(event.getPlayer());
        if (user != null) {
            user.data().remove(Node.builder("group.buildmode").build());
        }
    }

    @EventHandler
    public void onBuildModeToggle(BuildModeToggleEvent event) {
        if (event.isEnabled()) {
            DiscovSuite.getInstance().getInventoryManager().giveBuildInventory(event.getPlayer());
            event.getPlayer().setGameMode(GameMode.CREATIVE);
            enableOutlineHolograms(event.getPlayer());
            User user = DiscovSuite.getInstance().getLuckPermsUser(event.getPlayer());
            DiscovSuite.getInstance().getLogger().info(user.toString());
            user.data().add(Node.builder("group.buildmode").build());
            ChatUtil.sendConfigMessage(event.getPlayer(), "buildmode-enabled");
        } else {
            DiscovSuite.getInstance().getInventoryManager().returnInventory(event.getPlayer());
            event.getPlayer().setGameMode(GameMode.ADVENTURE);
            disableOutlineHolograms(event.getPlayer());
            User user = DiscovSuite.getInstance().getLuckPermsUser(event.getPlayer());
            if (user != null) {
                user.data().remove(Node.builder("group.buildmode").build());
            }
            ChatUtil.sendConfigMessage(event.getPlayer(), "buildmode-disabled");
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        String[] lines = event.getLines();
        for (int i = 0; i < lines.length; i++) {
            event.setLine(i, ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(event.getLine(i))));
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

        if (event.getRightClicked() instanceof ArmorStand) {
            ArmorStand stand = (ArmorStand) event.getRightClicked();
            String bin = DiscovSuite.getInstance().getConfig().getString("armor-stand-bin");
            if (Objects.requireNonNull(stand.getHelmet().getData()).toString().equals(bin)) {
                event.getPlayer().openInventory(Bukkit.createInventory(null, 27, "Disposal"));
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("discovsuite.watertp.bypass")) { return; }
        Location location = player.getLocation();
        if (location.getBlock().getType() == Material.WATER) {
            World world = player.getWorld();
            if (isMatchingWorld(world, "water-teleport-enabled-worlds")) {
                player.teleport(world.getSpawnLocation());
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && Objects.requireNonNull(event.getClickedBlock()).getType() == Material.CAULDRON) {
            World world = event.getClickedBlock().getWorld();
            if (isMatchingWorld(world, "cauldron-bins-enabled-worlds")) {
                if (world.getBlockAt(event.getClickedBlock().getLocation().add(0, 1, 0)).getBlockData() instanceof Slab) {
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

    private void enableOutlineHolograms(Player player) {
        if (Bukkit.getPluginManager().isPluginEnabled("DiscovOutlines")) {
            DiscovOutlines.getInstance().getReferenceManager().showHolograms(player);
        }
    }

    private void disableOutlineHolograms(Player player) {
        if (Bukkit.getPluginManager().isPluginEnabled("DiscovOutlines")) {
            DiscovOutlines.getInstance().getReferenceManager().hideHolograms(player);
        }
    }
}
