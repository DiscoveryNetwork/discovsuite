package nl.parrotlync.discovsuite.bungeecord.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;
import nl.parrotlync.discovsuite.bungeecord.event.PrivateMessageEvent;
import nl.parrotlync.discovsuite.bungeecord.util.ChatUtil;
import nl.parrotlync.discovsuite.bungeecord.util.PlaceholderUtil;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(ChatEvent event) {
        if (DiscovSuite.chatMuted) {
            if (event.getSender() instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) event.getSender();
                if (!player.hasPermission("discovsuite.chat.mute.bypass") && !event.isCommand() && !event.isProxyCommand()) {
                    event.setCancelled(true);
                    ChatUtil.sendConfigMessage(player, "chat-blocked");
                }
            }
        }

        if (!event.isCommand() && !event.isProxyCommand()) {
            for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
                if (event.getMessage().toLowerCase().contains(onlinePlayer.getName().toLowerCase())) {
                    String replacement = PlaceholderUtil.parse(onlinePlayer, "&9%NAME%%SUFFIX%").replaceAll("&", "~");
                    event.setMessage(event.getMessage().replaceAll("(?i)" + onlinePlayer.getName(), replacement));
                    mentionPlayer(onlinePlayer);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPostLogin(PostLoginEvent event) {
        if (!event.getPlayer().hasPermission("discovsuite.chat.staff")) {
            String playerJoin = DiscovSuite.getInstance().getConfig().getString("formats.player-join");
            playerJoin = ChatColor.translateAlternateColorCodes('&', PlaceholderUtil.parse(event.getPlayer(), playerJoin));
            ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(playerJoin));
        } else {
            String staffJoin = DiscovSuite.getInstance().getConfig().getString("formats.staff-join");
            staffJoin = ChatColor.translateAlternateColorCodes('&', PlaceholderUtil.parse(event.getPlayer(), staffJoin));
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (player.hasPermission("discovsuite.chat.staff")) {
                    player.sendMessage(TextComponent.fromLegacyText(staffJoin));
                }
            }
        }

        ProxyServer.getInstance().getScheduler().runAsync(DiscovSuite.getInstance(), () -> {
            try {
                if (DiscovSuite.getInstance().getDatabase().getSeen(event.getPlayer().getName()) == null) {
                    String joinMessage = DiscovSuite.getInstance().getConfig().getString("formats.player-first-join");
                    ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(PlaceholderUtil.parse(event.getPlayer(), joinMessage)));
                }
            } catch (Exception ignored) {}
        });
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        String playerLeave = DiscovSuite.getInstance().getConfig().getString("formats.player-leave");
        playerLeave = ChatColor.translateAlternateColorCodes('&', PlaceholderUtil.parse(event.getPlayer(), playerLeave));
        ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(playerLeave));

        if (event.getPlayer().hasPermission("discovsuite.chat.staff") && !DiscovSuite.chatMuted) {
            boolean staffOnline = false;
            String staffLeave = DiscovSuite.getInstance().getConfig().getString("formats.staff-leave");
            staffLeave = ChatColor.translateAlternateColorCodes('&', PlaceholderUtil.parse(event.getPlayer(), staffLeave));
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (player.hasPermission("discovsuite.chat.staff")) {
                    staffOnline = true;
                    player.sendMessage(TextComponent.fromLegacyText(staffLeave));
                }
            }

            if (!staffOnline) {
                ProxyServer.getInstance().getLogger().info("No online staff detected. Muting chat...");
                String message = DiscovSuite.getInstance().getConfig().getString("messages.chat-muted");
                message = ChatColor.translateAlternateColorCodes('&', message);
                ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(message.replace("%{}%", "Chat")));
                DiscovSuite.chatMuted = true;
            }
        }
    }

    @EventHandler
    public void onPrivateMessage(PrivateMessageEvent event) {
        DiscovSuite.getInstance().getConversationManager().setReceiver(event.getReceiver(), event.getSender());
        String sendFormat = DiscovSuite.getInstance().getConfig().getString("formats.msg-send");
        String receiveFormat = DiscovSuite.getInstance().getConfig().getString("formats.msg-receive");
        String spyFormat = DiscovSuite.getInstance().getConfig().getString("formats.msg-spy");

        String senderMsg = PlaceholderUtil.parse(event.getReceiver(), sendFormat).replaceAll("%MSG%", event.getMessage());
        event.getSender().sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', senderMsg)));

        String receiverMsg = PlaceholderUtil.parse(event.getSender(), receiveFormat).replaceAll("%MSG%", event.getMessage());
        event.getReceiver().sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', receiverMsg)));

        String spyMsg = spyFormat.replaceAll("%SENDER%", event.getSender().getName());
        spyMsg = spyMsg.replaceAll("%RECEIVER%", event.getReceiver().getName());
        spyMsg = spyMsg.replaceAll("%MSG%", event.getMessage());
        if (!event.getSender().hasPermission("discovsuite.socialspy.bypass")) {
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (player != event.getReceiver() && player != event.getSender() && player.hasPermission("discovsuite.socialspy")) {
                    player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', spyMsg)));
                }
            }
        }
    }

    private void mentionPlayer(ProxiedPlayer player) {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF(player.getUniqueId().toString());
        byteArrayDataOutput.writeUTF(ChatColor.translateAlternateColorCodes('&', DiscovSuite.getInstance().getConfig().getString("messages.chat-mentioned")));
        player.getServer().sendData("dsuite:mention", byteArrayDataOutput.toByteArray());
    }
}
