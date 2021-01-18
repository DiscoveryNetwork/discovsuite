package nl.parrotlync.discovsuite.spigot.model;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Warp {
    private final String name;
    private final String server;
    private final Location location;

    public Warp(String name, String server, Location location) {
        this.name = name;
        this.server = server;
        this.location = location;
    }

    public Warp(String name, String server, String worldName, double x, double y, double z, float yaw, float pitch) {
        World world = Bukkit.getWorld(worldName);
        this.location = new Location(world, x, y, z, yaw, pitch);
        this.name = name;
        this.server = server;
    }

    public String getName() {
        return name;
    }

    public String getServer() {
        return server;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isLocal() {
        String serverName = DiscovSuite.getInstance().getConfig().getString("server-name");
        return serverName.equalsIgnoreCase(server);
    }
}
