package nl.parrotlync.discovsuite.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearInventoryCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("discovsuite.clearinventory")) {
                player.getInventory().clear();
                player.sendMessage("§aYour inventory has been cleared!");
            } else {
                sender.sendMessage("§cYou don't have permission to do that!");
            }
        } else {
            sender.sendMessage("§cYou need to be a player to execute this command!");
        }
        return true;
    }
}
