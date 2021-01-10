package nl.parrotlync.discovsuite.spigot.command;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.model.ChannelType;
import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import nl.parrotlync.discovsuite.spigot.util.PluginMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtil.sendConfigMessage(sender, "player-only");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("discovsuite.chat.staff")) {
            ChatUtil.sendConfigMessage(sender, "no-permission");
            return true;
        }

        if (args.length == 0) {
            if (DiscovSuite.getInstance().getChannelManager().getChannel(player) != ChannelType.STAFF_CHAT) {
                DiscovSuite.getInstance().getChannelManager().setChannel(player, ChannelType.STAFF_CHAT);
            } else {
                DiscovSuite.getInstance().getChannelManager().setChannel(player, ChannelType.GLOBAL);
            }
        } else {
            if (args[0].isEmpty()) {
                ChatUtil.sendMissingArguments(sender, new String[] {"message"});
                return true;
            }

            PluginMessage.sendStaffMessage(player, String.join(" ", args));
        }
        return true;
    }
}
