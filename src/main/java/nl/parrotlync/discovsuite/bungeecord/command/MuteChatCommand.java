package nl.parrotlync.discovsuite.bungeecord.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;
import nl.parrotlync.discovsuite.bungeecord.util.ChatUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MuteChatCommand extends Command implements TabExecutor {

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
            message = ChatColor.translateAlternateColorCodes('&', DiscovSuite.getInstance().getMessages().getString("messages.chat-muted"));
        } else {
            message = ChatColor.translateAlternateColorCodes('&', DiscovSuite.getInstance().getMessages().getString("messages.chat-unmuted"));
        }

        for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
            if (onlinePlayer.hasPermission("discovsuite.chat.mute.bypass")) {
                onlinePlayer.sendMessage(TextComponent.fromLegacyText(message.replace("%{}%", sender.getName())));
            } else {
                onlinePlayer.sendMessage(TextComponent.fromLegacyText(message.replace("%{}%", "Chat")));
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            suggestions.add("check");
            return suggestions.stream().filter(input -> input.toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT))).collect(Collectors.toCollection(ArrayList::new));
        } else {
            return Collections.emptyList();
        }
    }
}
