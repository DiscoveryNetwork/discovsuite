package nl.parrotlync.discovsuite.bungeecord.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;

import java.util.Arrays;

public class ChatUtil {

    public static void sendConfigMessage(CommandSender sender, String path) {
        String message = DiscovSuite.getInstance().getMessages().getString("messages." + path);
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
    }

    public static void sendConfigMessage(CommandSender sender, String path, String argument) {
        String message = DiscovSuite.getInstance().getMessages().getString("messages." + path);
        message = String.format(message, argument);
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
    }

    public static void sendConfigMessage(CommandSender sender, String path, String[] arguments) {
        String message = DiscovSuite.getInstance().getMessages().getString("messages." + path);
        message = String.format(message, Arrays.stream(arguments).toArray());
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
    }

    public static void sendMissingArguments(CommandSender sender, String[] arguments) {
        String message = DiscovSuite.getInstance().getMessages().getString("messages.missing-arguments");
        message = ChatColor.translateAlternateColorCodes('&', String.format(message, Arrays.toString(arguments)));
        sender.sendMessage(TextComponent.fromLegacyText(message));
    }
}
