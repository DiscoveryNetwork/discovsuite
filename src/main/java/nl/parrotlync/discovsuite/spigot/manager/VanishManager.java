package nl.parrotlync.discovsuite.spigot.manager;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.util.PluginMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VanishManager {
    private final List<UUID> vanishedPlayers = new ArrayList<>();

    public void hidePlayer(Player player) {
        if (!vanishedPlayers.contains(player.getUniqueId())) {
            vanishedPlayers.add(player.getUniqueId());
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer == player) { continue; }
            if (onlinePlayer.hasPermission("discovsuite.vanish.bypass")) { continue; }
            onlinePlayer.hidePlayer(DiscovSuite.getInstance(), player);
        }
        PluginMessage.sendStaffMessage(player, DiscovSuite.getInstance().getMessages().getString("messages.staff-vanish-announce"), false);
    }

    public void showPlayer(Player player) {
        if (vanishedPlayers.contains(player.getUniqueId())) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer == player) { continue; }
                onlinePlayer.showPlayer(DiscovSuite.getInstance(), player);
            }
            vanishedPlayers.remove(player.getUniqueId());
            PluginMessage.sendStaffMessage(player, DiscovSuite.getInstance().getMessages().getString("messages.staff-unvanish-announce"), false);
        }
    }

    public boolean isPlayerHidden(Player player) {
        return vanishedPlayers.contains(player.getUniqueId());
    }

    public void handleNewPlayer(Player joinedPlayer) {
        if (joinedPlayer.hasPermission("discovsuite.vanish.bypass")) { return; }
        for (UUID uuid : vanishedPlayers) {
            Player hiddenPlayer = Bukkit.getPlayer(uuid);
            assert hiddenPlayer != null;
            joinedPlayer.hidePlayer(DiscovSuite.getInstance(), hiddenPlayer);
        }
    }
}
