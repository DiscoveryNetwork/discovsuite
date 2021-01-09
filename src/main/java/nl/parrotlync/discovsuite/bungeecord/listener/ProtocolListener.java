package nl.parrotlync.discovsuite.bungeecord.listener;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;
import nl.parrotlync.discovsuite.bungeecord.util.ChatUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProtocolListener implements Listener {
    private final List<UUID> blockedPlayers = new ArrayList<>();

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Integer version = player.getPendingConnection().getVersion();
        List<Integer> allowedVersions = DiscovSuite.getInstance().getConfig().getIntList("accepted-protocol-versions");
        if (!allowedVersions.contains(version)) { sendWarning(player); }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer)) { return; }
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        if (!blockedPlayers.contains(player.getUniqueId())) { return; }

        if (event.isProxyCommand() || !event.getMessage().equalsIgnoreCase("/accept")) {
            ChatUtil.sendConfigMessage(player, "protocol-accept-first");
            event.setCancelled(true);
        }
    }

    private void sendWarning(ProxiedPlayer player) {
        blockedPlayers.add(player.getUniqueId());
        for (String line : DiscovSuite.getInstance().getConfig().getStringList("protocol-warning")) {
            player.sendMessage(TextComponent.fromLegacyText(line));
        }
    }
}
