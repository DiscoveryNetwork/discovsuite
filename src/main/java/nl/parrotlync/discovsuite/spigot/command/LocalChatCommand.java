package nl.parrotlync.discovsuite.spigot.command;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.model.ChannelType;
import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import nl.parrotlync.discovsuite.spigot.util.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LocalChatCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("discovsuite.chat.local")) {
            ChatUtil.sendConfigMessage(sender, "no-permission");
            return true;
        }
        
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                ChatUtil.sendConfigMessage(sender, "player-only");
                return true;
            }

            Player player = (Player) sender;
            if (DiscovSuite.getInstance().getChannelManager().getChannel(player) != ChannelType.LOCAL) {
                DiscovSuite.getInstance().getChannelManager().setChannel(player, ChannelType.LOCAL);
            } else {
                DiscovSuite.getInstance().getChannelManager().setChannel(player, ChannelType.GLOBAL);
            }
        } else {
            if (args[0].isEmpty()) {
                ChatUtil.sendMissingArguments(sender, new String[] {"message"});
                return true;
            }

            String format = PlaceholderUtil.parseForSender(sender, DiscovSuite.getInstance().getConfig().getString("formats.chat-local"));
            Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', format + String.join(" ", args)), "discovsuite.chat.local");
        }
        return true;
    }
}
