package nl.parrotlync.discovsuite.bungeecord.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;
import nl.parrotlync.discovsuite.bungeecord.util.ChatUtil;

public class MuteChatCommand extends Command {

    public MuteChatCommand() {
        super("mutechat", "discovsuite.command.mutechat", "chatmute");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("check")) {
            ChatUtil.sendConfigMessage(sender, "chat-mute-check", String.valueOf(DiscovSuite.chatMuted));
            return;
        }

        DiscovSuite.chatMuted = !DiscovSuite.chatMuted;
        String message;
        if (DiscovSuite.chatMuted) {
            message = ChatColor.translateAlternateColorCodes('&', DiscovSuite.getInstance().getConfig().getString("messages.chat-muted"));
        } else {
            message = ChatColor.translateAlternateColorCodes('&', DiscovSuite.getInstance().getConfig().getString("messages.chat-unmuted"));
        }

        for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
            if (onlinePlayer.hasPermission("discovsuite.chat.mute.bypass")) {
                onlinePlayer.sendMessage(TextComponent.fromLegacyText(message.replace("%{}%", sender.getName())));
            } else {
                onlinePlayer.sendMessage(TextComponent.fromLegacyText(message.replace("%{}%", "Chat")));
            }
        }
    }
}
