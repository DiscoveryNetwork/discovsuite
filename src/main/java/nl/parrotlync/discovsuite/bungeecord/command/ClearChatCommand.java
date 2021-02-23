package nl.parrotlync.discovsuite.bungeecord.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;

public class ClearChatCommand extends Command {

    public ClearChatCommand() {
        super("clearchat", "discovsuite.command.clearchat", "cc", "chatclear");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
            if (!onlinePlayer.hasPermission("discovsuite.chat.clear.bypass")) {
                for (int i = 0; i < 151; i++) {
                    onlinePlayer.sendMessage(TextComponent.fromLegacyText(""));
                }
            }

            String message = ChatColor.translateAlternateColorCodes('&', DiscovSuite.getInstance().getMessages().getString("messages.chat-cleared"));
            if (onlinePlayer.hasPermission("discovsuite.chat.clear.bypass")) {
                onlinePlayer.sendMessage(TextComponent.fromLegacyText(message.replace("%{}%", sender.getName())));
            } else {
                onlinePlayer.sendMessage(TextComponent.fromLegacyText(message.replace("%{}%", "Chat")));
            }
        }
    }
}
