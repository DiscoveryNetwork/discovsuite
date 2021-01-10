package nl.parrotlync.discovsuite.spigot.command;

import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import nl.parrotlync.discovsuite.spigot.util.PluginMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BroadcastCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("discovsuite.command.broadcast")) {
            ChatUtil.sendConfigMessage(sender, "no-permission");
            return true;
        }

        if (args.length < 1) {
            ChatUtil.sendMissingArguments(sender, new String[] {"message"});
            return true;
        }

        PluginMessage.sendBroadcast(sender, String.join(" ", args));
        return true;
    }
}
