package nl.parrotlync.discovsuite.spigot.util;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class ChatUtil {

    public static void sendConfigMessage(CommandSender sender, String path) {
        String message = DiscovSuite.getInstance().getConfig().getString("messages." + path);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendConfigMessage(CommandSender sender, String path, String argument) {
        String message = DiscovSuite.getInstance().getConfig().getString("messages." + path);
        message = String.format(message, argument);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendMissingArguments(CommandSender sender, String[] arguments) {
        String message = DiscovSuite.getInstance().getConfig().getString("messages.missing-arguments");
        message = String.format(message, Arrays.toString(arguments));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
