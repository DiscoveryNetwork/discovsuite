package nl.parrotlync.discovsuite.spigot.model;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Warp {
    private final String name;
    private final String server;
    private final Location location;
    private final WarpGroup group;

    public Warp(String name, String server, WarpGroup group, Location location) {
        this.name = name;
        this.server = server;
        this.group = group;
        this.location = location;
    }

    public Warp(String name, String server, WarpGroup group, String worldName, double x, double y, double z, float yaw, float pitch) {
        World world = Bukkit.getWorld(worldName);
        this.location = new Location(world, x, y, z, yaw, pitch);
        this.name = name;
        this.server = server;
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public String getServer() {
        return server;
    }

    public WarpGroup getGroup() { return group; }

    public Location getLocation() {
        return location;
    }

    public boolean isLocal() {
        String serverName = DiscovSuite.getInstance().getConfig().getString("server-name");
        assert serverName != null;
        return serverName.equalsIgnoreCase(server);
    }

    public boolean canAccess(Player player) {
        return player.hasPermission("discovsuite.warps.group." + group.toString().toLowerCase());
    }
}
