package nl.parrotlync.discovsuite.bungeecord.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;

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

        if (event.getTag().equalsIgnoreCase("dsuite:dpname")) {
            UUID uuid = UUID.fromString(byteArrayDataInput.readUTF());
            String displayName = byteArrayDataInput.readUTF();

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
            player.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        }
    }
}
