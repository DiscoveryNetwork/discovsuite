package nl.parrotlync.discovsuite.spigot.manager;

import nl.parrotlync.discovsuite.spigot.model.Warp;
import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class TeleportManager {
    private final HashMap<UUID, UUID> player_queue = new HashMap<>();
    private final HashMap<UUID, Warp> warp_queue = new HashMap<>();

    public void queue(UUID player, Player target) {
        player_queue.put(player, target.getUniqueId());
    }

    public void queue(UUID player, Warp warp) {
        warp_queue.put(player, warp);
    }

    public boolean isQueued(Player player) {
        return player_queue.containsKey(player.getUniqueId()) || warp_queue.containsKey(player.getUniqueId());
    }

    public void teleport(Player player) {
        if (player_queue.containsKey(player.getUniqueId())) {
            Player target = Bukkit.getPlayer(player_queue.get(player.getUniqueId()));
            if (target != null) {
                player.teleport(target);
            }
            player_queue.remove(player.getUniqueId());
        } else if (warp_queue.containsKey(player.getUniqueId())) {
            Warp warp = warp_queue.get(player.getUniqueId());
            player.teleport(warp.getLocation());
            ChatUtil.sendConfigMessage(player, "warp-teleported", warp.getName());
            warp_queue.remove(player.getUniqueId());
        }
    }
}
