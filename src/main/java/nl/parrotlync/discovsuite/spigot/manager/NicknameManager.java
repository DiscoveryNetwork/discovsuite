package nl.parrotlync.discovsuite.spigot.manager;

import net.luckperms.api.model.user.User;
import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.util.PluginMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class NicknameManager {
    private final HashMap<UUID, String> nicknames = new HashMap<>();

    public String getNickname(Player player) {
        if (!nicknames.containsKey(player.getUniqueId())) {
            return player.getName();
        } else {
            return nicknames.get(player.getUniqueId());
        }
    }

    public void setNickname(Player player, String nickname) {
        nicknames.put(player.getUniqueId(), nickname);
        updateDisplayName(player);
        Bukkit.getScheduler().runTaskAsynchronously(DiscovSuite.getInstance(), () -> {
            try {
                DiscovSuite.getInstance().getDatabase().setNickname(player.getUniqueId(), nickname);
            } catch (Exception e) {
                DiscovSuite.getInstance().getLogger().warning("Something went wrong while updating a nickname.");
                e.printStackTrace();
            }
        });
    }

    public void load(Player player) {
        Bukkit.getScheduler().runTask(DiscovSuite.getInstance(), () -> {
            try {
                String nickname = DiscovSuite.getInstance().getDatabase().getNickname(player.getUniqueId());
                if (nickname != null) {
                    nicknames.put(player.getUniqueId(), nickname);
                }
                Bukkit.getScheduler().runTask(DiscovSuite.getInstance(), () -> updateDisplayName(player));
            } catch (Exception e) {
                DiscovSuite.getInstance().getLogger().warning("Something went wrong while fetching nickname for player " + player.getUniqueId());
                updateDisplayName(player);
                e.printStackTrace();
            }
        });
    }

    private void updateDisplayName(Player player) {
        User user = DiscovSuite.getInstance().getLuckPermsUser(player);
        if (user != null) {
            String prefix = user.getCachedData().getMetaData().getPrefix();
            String displayName = prefix + " " + getNickname(player);
            player.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            PluginMessage.sendDisplayName(player);
        }
    }
}
