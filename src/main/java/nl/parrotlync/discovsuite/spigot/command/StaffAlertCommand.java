package nl.parrotlync.discovsuite.spigot.command;

import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import nl.parrotlync.discovsuite.spigot.util.PluginMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StaffAlertCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("discovsuite.command.staffalert")) {
            ChatUtil.sendConfigMessage(sender, "no-permission");
            return true;
        }

        if (args.length < 1) {
            ChatUtil.sendMissingArguments(sender, new String[] {"message"});
            return true;
        }

        PluginMessage.sendStaffAlert(sender, String.join(" ", args));
        return true;
    }
}
