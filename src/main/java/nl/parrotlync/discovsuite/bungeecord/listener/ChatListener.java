package nl.parrotlync.discovsuite.bungeecord.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;
import nl.parrotlync.discovsuite.bungeecord.event.PrivateMessageEvent;
import nl.parrotlync.discovsuite.bungeecord.util.ChatUtil;
import nl.parrotlync.discovsuite.bungeecord.util.PlaceholderUtil;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer)) { return; }
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        // Protocol check
        if (DiscovSuite.getInstance().getBlockedPlayers().contains(player)) {
            if (!event.getMessage().equalsIgnoreCase("/accept")) {
                ChatUtil.sendConfigMessage(player, "protocol-accept-first");
                event.setCancelled(true);
                return;
            }
        }

        // Check if valid command
        if (event.isCommand() || event.isProxyCommand()) {
            return;
        }

        // Chat mute check
        if (DiscovSuite.chatMuted) {
            if (!player.hasPermission("discovsuite.chat.mute.bypass") && !event.isCommand() && !event.isProxyCommand()) {
                event.setCancelled(true);
                ChatUtil.sendConfigMessage(player, "chat-blocked");
                return;
            }
        }

        // Chat filter
        if (DiscovSuite.getInstance().getChatFilter().hasForbidden(event.getMessage()) && !player.hasPermission("discovsuite.chat.filter.bypass")) {
            event.setCancelled(true);
            ChatUtil.sendConfigMessage(player, "message-blocked");
            sendSwearNotice(player, event.getMessage());
            return;
        }
        event.setMessage(DiscovSuite.getInstance().getChatFilter().parseReplacements(event.getMessage()));

        // Mentioning
        if (!event.isCommand() && !event.isProxyCommand()) {
            for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
                if (event.getMessage().toLowerCase().contains(onlinePlayer.getName().toLowerCase())) {
                    String replacement = (PlaceholderUtil.parse(onlinePlayer, "&9%NAME%") + PlaceholderUtil.parse(player, "%SUFFIX%"));
                    replacement = ChatColor.translateAlternateColorCodes('&', replacement);
                    event.setMessage(event.getMessage().replaceAll("(?i)" + onlinePlayer.getName(), replacement));
                    mentionPlayer(onlinePlayer);
                }
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
        if (!event.getSender().hasPermission("discovsuite.chat.socialspy.bypass")) {
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

    private void sendSwearNotice(ProxiedPlayer player, String message) {
        String notice = PlaceholderUtil.parse(player, DiscovSuite.getInstance().getConfig().getString("formats.swear-notice"));
        notice = ChatColor.translateAlternateColorCodes('&', notice.replaceAll("%MSG%", message));
        String command = "/warn " + player.getName() + " " + DiscovSuite.getInstance().getConfig().getString("messages.default-swear-warning");
        TextComponent text = new TextComponent(notice);
        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        String hoverMsg = PlaceholderUtil.parse(player, DiscovSuite.getInstance().getConfig().getString("formats.swear-hover-warning"));
        hoverMsg = ChatColor.translateAlternateColorCodes('&', hoverMsg.replaceAll("%NAME%", player.getName()));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMsg).create()));

        for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
            if (onlinePlayer.hasPermission("discovsuite.chat.filter.notice")) {
                onlinePlayer.sendMessage(text);
            }
        }
    }
}
