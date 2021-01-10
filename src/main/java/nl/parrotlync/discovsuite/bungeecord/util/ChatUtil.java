package nl.parrotlync.discovsuite.bungeecord.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;

public class ChatUtil {

    public static void sendConfigMessage(CommandSender sender, String path) {
        String message = DiscovSuite.getInstance().getConfig().getString("messages." + path);
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
    }
}
