package nl.parrotlync.discovsuite.spigot.command;

import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import nl.parrotlync.discovsuite.spigot.util.PluginMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtil.sendConfigMessage(sender, "player-only");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("discovsuite.command.clearchat")) {
            ChatUtil.sendConfigMessage(sender, "no-permission");
            return true;
        }

        PluginMessage.clearChat(player);
        return true;

    }
}