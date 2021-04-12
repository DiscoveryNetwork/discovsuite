package nl.parrotlync.discovsuite.spigot.manager;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.model.Warp;
import nl.parrotlync.discovsuite.spigot.model.WarpGroup;
import nl.parrotlync.discovsuite.spigot.util.PluginMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class WarpManager {
    private final HashMap<String, Warp> warps = new HashMap<>();

    public boolean createWarp(String name, Location location, WarpGroup group) {
        String serverName = DiscovSuite.getInstance().getConfig().getString("server-name");
        location.setPitch((float) 0);
        Warp warp = new Warp(name, serverName, group, location);
        if (!warps.containsKey(name.toLowerCase())) {
            warps.put(name.toLowerCase(), warp);
            saveWarp(warp);
            PluginMessage.update();
            return true;
        }
        return false;
    }

    public void removeWarp(Warp warp) {
        warps.remove(warp.getName().toLowerCase());
        removeWarpDB(warp);
        PluginMessage.update();
    }

    public boolean hasWarp(String name) {
        return warps.containsKey(name.toLowerCase());
    }

    public Warp getWarp(String name) {
        return warps.get(name.toLowerCase());
    }

    public List<Warp> getWarps() {
        return new ArrayList<>(this.warps.values());
    }

    public List<Warp> getAccessibleWarps(Player player) {
        List<Warp> accessibleWarps = new ArrayList<>();
        for (Warp warp : warps.values()) {
            if (warp.canAccess(player)) {
                accessibleWarps.add(warp);
            }
        }
        return accessibleWarps;
    }

    public void load() {
        warps.clear();
        Bukkit.getScheduler().runTaskAsynchronously(DiscovSuite.getInstance(), () -> {
            try {
                List<Warp> warpList = DiscovSuite.getInstance().getDatabase().getWarps();
                for (Warp warp : warpList) {
                    if (!warps.containsKey(warp.getName().toLowerCase())) {
                        warps.put(warp.getName().toLowerCase(), warp);
                    }
                }
                DiscovSuite.getInstance().getLogger().info("Loaded " + warps.size() + " warps from the database!");
            } catch (Exception e) {
                DiscovSuite.getInstance().getLogger().warning("Something went wrong while fetching warps from the database.");
                e.printStackTrace();
            }
        });
    }

    private void saveWarp(Warp warp) {
        Bukkit.getScheduler().runTaskAsynchronously(DiscovSuite.getInstance(), () -> {
            try {
                DiscovSuite.getInstance().getDatabase().addWarp(warp);
            } catch (Exception e) {
                DiscovSuite.getInstance().getLogger().warning("Something went wrong while fetching warps from the database.");
                e.printStackTrace();
            }
        });
    }

    private void removeWarpDB(Warp warp) {
        Bukkit.getScheduler().runTaskAsynchronously(DiscovSuite.getInstance(), () -> {
            try {
                DiscovSuite.getInstance().getDatabase().removeWarp(warp.getName());
            } catch (Exception e) {
                DiscovSuite.getInstance().getLogger().warning("Something went wrong while fetching warps from the database.");
                e.printStackTrace();
            }
        });
    }

    public TreeMap<Double, Warp> getNearbyWarps(Location location) {
       TreeMap<Double, Warp> nearbyWarps = new TreeMap<>();

        for (Warp warp : warps.values()) {
            if (warp.isLocal() && warp.getLocation().getWorld() == location.getWorld() && location.distance(warp.getLocation()) <= 200) {
                nearbyWarps.put(location.distance(warp.getLocation()), warp);
            }
        }

        return nearbyWarps;
    }

    public Warp getNearestWarp(Player player) {
        Warp nearest = null;
        double distance = Double.MAX_VALUE;
        for (Warp warp : warps.values()) {
            if (warp.isLocal() && warp.getLocation().getWorld() == player.getLocation().getWorld() && warp.canAccess(player) && warp.getLocation().distance(player.getLocation()) < distance) {
                distance = warp.getLocation().distance(player.getLocation());
                nearest = warp;
            }
        }
        return nearest;
    }
}
