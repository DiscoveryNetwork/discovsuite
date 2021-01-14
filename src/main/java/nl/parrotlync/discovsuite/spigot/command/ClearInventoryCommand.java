package nl.parrotlync.discovsuite.spigot.command;

import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearInventoryCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtil.sendConfigMessage(sender, "player-only");
            return true;
        }
        
        Player player = (Player) sender;
        if (!player.hasPermission("discovsuite.command.clearinventory")) {
            ChatUtil.sendConfigMessage(sender, "no-permission");
            return true;
        }

        player.getInventory().clear();
        ChatUtil.sendConfigMessage(sender, "inventory-clear");
        return true;
    }
}
