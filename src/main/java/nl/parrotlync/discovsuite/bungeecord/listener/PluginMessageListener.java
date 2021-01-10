package nl.parrotlync.discovsuite.bungeecord.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;
import nl.parrotlync.discovsuite.bungeecord.util.PlaceholderUtil;

import java.util.UUID;

public class PluginMessageListener implements Listener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().startsWith("dsuite:")) { return; }
        ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(event.getData());

        if (event.getTag().equalsIgnoreCase("dsuite:chat")) {
            UUID playerId = UUID.fromString(byteArrayDataInput.readUTF());
            String message = byteArrayDataInput.readUTF();

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerId);

            if (!DiscovSuite.chatMuted || player.hasPermission("discovsuite.chat.mute.bypass")) {
                Server server = (Server) event.getSender();
                ProxyServer.getInstance().getLogger().info("CHAT > " + message);

                for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
                    if (!onlinePlayer.getServer().getInfo().getName().equals(server.getInfo().getName())) {
                        onlinePlayer.sendMessage(TextComponent.fromLegacyText(message));
                    }
                }
            }
        }

        if (event.getTag().equalsIgnoreCase("dsuite:broadcast")) {
            String message = byteArrayDataInput.readUTF();
            String player = byteArrayDataInput.readUTF();
            ProxyServer.getInstance().getLogger().info("BROADCAST (" + player +") > " + message);

            for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
                if (onlinePlayer.hasPermission("discovsuite.broadcast.showsender")) {
                    onlinePlayer.sendMessage(TextComponent.fromLegacyText(message.replace("%{}%", player)));
                } else {
                    onlinePlayer.sendMessage(TextComponent.fromLegacyText(message.replace("%{}%", "Information")));
                }
            }
        }

        if (event.getTag().equalsIgnoreCase("dsuite:staff")) {
            String message = byteArrayDataInput.readUTF();
            ProxyServer.getInstance().getLogger().info("CHAT (Staff) > " + message);

            for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
                if (onlinePlayer.hasPermission("discovsuite.chat.staff")) {
                    onlinePlayer.sendMessage(TextComponent.fromLegacyText(message));
                }
            }
        }

        if (event.getTag().equalsIgnoreCase("dsuite:mgchat")) {
            String message = byteArrayDataInput.readUTF();
            ProxyServer.getInstance().getLogger().info("CHAT (Management) > " + message);

            for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
                if (onlinePlayer.hasPermission("discovsuite.chat.management")) {
                    onlinePlayer.sendMessage(TextComponent.fromLegacyText(message));
                }
            }
        }

        if (event.getTag().equalsIgnoreCase("dsuite:mute")) {
            String player = byteArrayDataInput.readUTF();
            DiscovSuite.chatMuted = !DiscovSuite.chatMuted;
            ProxyServer.getInstance().getLogger().info("Toggled mute state to" + DiscovSuite.chatMuted);
            String message;
            if (DiscovSuite.chatMuted) {
                message = ChatColor.translateAlternateColorCodes('&', DiscovSuite.getInstance().getConfig().getString("messages.chat-muted"));
            } else {
                message = ChatColor.translateAlternateColorCodes('&', DiscovSuite.getInstance().getConfig().getString("messages.chat-unmuted"));
            }

            for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
                if (onlinePlayer.hasPermission("discovsuite.chat.mute.bypass")) {
                    onlinePlayer.sendMessage(TextComponent.fromLegacyText(message.replace("%{}%", player)));
                } else {
                    onlinePlayer.sendMessage(TextComponent.fromLegacyText(message.replace("%{}%", "Chat")));
                }
            }
        }

        if (event.getTag().equalsIgnoreCase("dsuite:clear")) {
            String player = byteArrayDataInput.readUTF();
            ProxyServer.getInstance().getLogger().info("Clearing chat...");
            for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
                if (!onlinePlayer.hasPermission("discovsuite.chat.clear.bypass")) {
                    for (int i = 0; i < 151; i++) {
                        onlinePlayer.sendMessage(TextComponent.fromLegacyText(""));
                    }
                }
                String message = ChatColor.translateAlternateColorCodes('&', DiscovSuite.getInstance().getConfig().getString("messages.chat-cleared"));
                if (onlinePlayer.hasPermission("discovsuite.chat.clear.bypass")) {
                    onlinePlayer.sendMessage(TextComponent.fromLegacyText(message.replace("%{}%", player)));
                } else {
                    onlinePlayer.sendMessage(TextComponent.fromLegacyText(message.replace("%{}%", "Chat")));
                }
            }
        }

        if (event.getTag().equalsIgnoreCase("dsuite:notice")) {
            String uuid = byteArrayDataInput.readUTF();
            String message = byteArrayDataInput.readUTF();

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));
            ProxyServer.getInstance().getLogger().info("CHATFILTER (" + player.getName() + ") > " + message);
            String notice = PlaceholderUtil.parse(player, DiscovSuite.getInstance().getConfig().getString("formats.swear-notice"));
            String command = "/warn " + player.getName() + " " + DiscovSuite.getInstance().getConfig().getString("messages.default-swear-warning");
            TextComponent text = new TextComponent(notice);
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
            String hoverMsg = PlaceholderUtil.parse(player, DiscovSuite.getInstance().getConfig().getString("formats.swear-hover-warning"));
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMsg).create()));

            for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
                if (onlinePlayer.hasPermission("discovsuite.chat.filter.notice")) {
                    onlinePlayer.sendMessage(text);
                }
            }
        }

        if (event.getTag().equalsIgnoreCase("dsuite:filter")) {
            ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
            for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
                if (server.getPlayers() != null && !server.getPlayers().isEmpty()) {
                    server.sendData("dsuite:filter", byteArrayDataOutput.toByteArray());
                }
            }
        }

        if (event.getTag().equalsIgnoreCase("dsuite:dpname")) {
            UUID uuid = UUID.fromString(byteArrayDataInput.readUTF());
            String displayName = byteArrayDataInput.readUTF();

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
            player.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        }
    }
}
