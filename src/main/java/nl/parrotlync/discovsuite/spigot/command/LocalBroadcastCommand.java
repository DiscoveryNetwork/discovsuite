package nl.parrotlync.discovsuite.spigot.command;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LocalBroadcastCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("discovsuite.command.localbroadcast")) {
            ChatUtil.sendConfigMessage(sender, "no-permission");
            return true;
        }

        if (args.length < 1) {
            ChatUtil.sendMissingArguments(sender, new String[] {"message"});
            return true;
        }

        String message = DiscovSuite.getInstance().getMessages().getString("formats.broadcast") + String.join(" ", args);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("discovsuite.chat.broadcast.showsender")) {
                onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("%{}%", sender.getName())));
            } else {
                onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("%{}%", "Information")));
            }
        }
        return true;
    }
}