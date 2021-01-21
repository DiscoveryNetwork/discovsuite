package nl.parrotlync.discovsuite.bungeecord.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;
import nl.parrotlync.discovsuite.bungeecord.event.PlayerProtocolAcceptEvent;
import nl.parrotlync.discovsuite.bungeecord.util.ChatUtil;
import nl.parrotlync.discovsuite.bungeecord.util.PlaceholderUtil;

import java.util.Date;
import java.util.List;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPostLogin(PostLoginEvent event) {
        // First join
        if (!DiscovSuite.getInstance().getPlayerCache().hasPlayer(event.getPlayer())) {
            String joinMessage = DiscovSuite.getInstance().getConfig().getString("formats.player-first-join");
            joinMessage = ChatColor.translateAlternateColorCodes('&', PlaceholderUtil.parse(event.getPlayer(), joinMessage));
            ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(joinMessage));
        }

        // Join message
        DiscovSuite.getInstance().getPlayerCache().addPlayer(event.getPlayer().getName());
        String playerJoin = DiscovSuite.getInstance().getConfig().getString("formats.player-join");
        playerJoin = ChatColor.translateAlternateColorCodes('&', PlaceholderUtil.parse(event.getPlayer(), playerJoin));
        ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(playerJoin));

        // Staff join message
        if (event.getPlayer().hasPermission("discovsuite.chat.staff")) {
            String staffJoin = DiscovSuite.getInstance().getConfig().getString("formats.staff-join");
            staffJoin = ChatColor.translateAlternateColorCodes('&', PlaceholderUtil.parse(event.getPlayer(), staffJoin));
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (player.hasPermission("discovsuite.chat.staff")) {
                    player.sendMessage(TextComponent.fromLegacyText(staffJoin));
                }
            }
        }

        // Protocol check
        Integer version = event.getPlayer().getPendingConnection().getVersion();
        List<Integer> allowedVersions = DiscovSuite.getInstance().getConfig().getIntList("accepted-protocol-versions");
        if (!allowedVersions.contains(version)) {
            DiscovSuite.getInstance().getBlockedPlayers().add(event.getPlayer());
            sendWarning(event.getPlayer());
        }

        // Player monitoring
        final Date now = new Date();
        ProxiedPlayer player = event.getPlayer();
        DiscovSuite.getInstance().getSessions().put(player.getUniqueId(), now);
        ProxyServer.getInstance().getScheduler().runAsync(DiscovSuite.getInstance(), () -> {
            try {
                DiscovSuite.getInstance().getDatabase().savePlayer(player, now);
            } catch (Exception e) {
                DiscovSuite.getInstance().getLogger().warning("Something went wrong while saving a player");
                e.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        // Leave message
        String playerLeave = DiscovSuite.getInstance().getConfig().getString("formats.player-leave");
        playerLeave = ChatColor.translateAlternateColorCodes('&', PlaceholderUtil.parse(event.getPlayer(), playerLeave));
        ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(playerLeave));

        // Staff leave message & auto mute
        if (event.getPlayer().hasPermission("discovsuite.chat.staff")) {
            boolean staffOnline = false;
            String staffLeave = DiscovSuite.getInstance().getConfig().getString("formats.staff-leave");
            staffLeave = ChatColor.translateAlternateColorCodes('&', PlaceholderUtil.parse(event.getPlayer(), staffLeave));
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (player.hasPermission("discovsuite.chat.staff")) {
                    staffOnline = true;
                    player.sendMessage(TextComponent.fromLegacyText(staffLeave));
                }
            }

            if (!staffOnline && !DiscovSuite.chatMuted) {
                ProxyServer.getInstance().getLogger().info("No online staff detected. Muting chat...");
                String message = DiscovSuite.getInstance().getConfig().getString("messages.chat-muted");
                message = ChatColor.translateAlternateColorCodes('&', message);
                ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(message.replace("%{}%", "Chat")));
                DiscovSuite.chatMuted = true;
            }
        }

        // Player monitoring
        ProxiedPlayer player = event.getPlayer();
        final Date logout = new Date();
        ProxyServer.getInstance().getScheduler().runAsync(DiscovSuite.getInstance(), () -> {
            try {
                DiscovSuite.getInstance().getDatabase().saveSession(player, DiscovSuite.getInstance().getSessions().get(player.getUniqueId()), logout);
            } catch (Exception e) {
                DiscovSuite.getInstance().getLogger().warning("Something went wrong while saving a player");
                e.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
        ServerInfo defaultServer = ProxyServer.getInstance().getServerInfo(DiscovSuite.getInstance().getConfig().getString("default-server"));
        if (defaultServer != null && event.getKickedFrom() != defaultServer) {
            event.setCancelServer(defaultServer);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerProtocolAccept(PlayerProtocolAcceptEvent event) {
        if (DiscovSuite.getInstance().getBlockedPlayers().contains(event.getPlayer())) {
            DiscovSuite.getInstance().getBlockedPlayers().remove(event.getPlayer());
            ChatUtil.sendConfigMessage(event.getPlayer(), "protocol-accepted");
        }
    }

    private void sendWarning(ProxiedPlayer player) {
        for (String line : DiscovSuite.getInstance().getConfig().getStringList("protocol-warning")) {
            player.sendMessage(TextComponent.fromLegacyText(line));
        }
    }
}
