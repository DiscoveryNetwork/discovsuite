package nl.parrotlync.discovsuite.spigot.util;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class AuthUtil {
    private final HashMap<UUID, Integer> tasks = new HashMap<>();

    public void startAuthProcess(final Player player) {
        Bukkit.getLogger().info("Starting authentication process for player " + player.getName());
        PluginMessage.auth(player);
        Integer taskId = Bukkit.getScheduler().runTaskLater(DiscovSuite.getInstance(), () -> player.kickPlayer("Unauthorized"), 50).getTaskId();
        tasks.put(player.getUniqueId(), taskId);
    }

    public void authSuccess(UUID uuid) {
        Integer taskId = tasks.get(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (taskId != null && player != null) {
            Bukkit.getLogger().info(player.getName() + " was successfully authenticated by BungeeCord!");
            Bukkit.getScheduler().cancelTask(taskId);
            tasks.remove(uuid);
        }
    }
}
