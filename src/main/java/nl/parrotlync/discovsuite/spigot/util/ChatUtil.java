package nl.parrotlync.discovsuite.spigot.util;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class ChatUtil {

    public static void sendConfigMessage(CommandSender sender, String path) {
        String message = DiscovSuite.getInstance().getMessages().getString("messages." + path);
        assert message != null;
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendConfigMessage(CommandSender sender, String path, String argument) {
        String message = DiscovSuite.getInstance().getMessages().getString("messages." + path);
        assert message != null;
        message = String.format(message, argument);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendConfigMessage(CommandSender sender, String path, String[] arguments) {
        String message = DiscovSuite.getInstance().getMessages().getString("messages." + path);
        assert message != null;
        message = String.format(message, Arrays.stream(arguments).toArray());
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendMissingArguments(CommandSender sender, String[] arguments) {
        String message = DiscovSuite.getInstance().getMessages().getString("messages.missing-arguments");
        assert message != null;
        message = String.format(message, Arrays.toString(arguments));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
