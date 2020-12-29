package nl.parrotlync.discovsuite.command;

import nl.parrotlync.discovsuite.DiscovSuite;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (DiscovSuite.getInstance().getResourcePackURL() != null) {
                player.setResourcePack(DiscovSuite.getInstance().getResourcePackURL());
            }
        } else {
            sender.sendMessage("§cYou need to be a player to execute this command!");
        }
        return true;
    }
}
