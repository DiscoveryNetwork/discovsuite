package nl.parrotlync.discovsuite.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class SpeedCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("discovsuite.speed")) {
                if (args.length == 2) {
                    float value = Math.max(0, Math.min(10, Float.parseFloat(args[1])));
                    if (args[0].equalsIgnoreCase("walk")) {
                        player.setWalkSpeed(value / 10);
                        player.sendMessage("§7Set walking speed to §3" + value);
                    } else if (args[0].equalsIgnoreCase("fly")) {
                        player.setFlySpeed(value / 10);
                        player.sendMessage("§7Set flying speed to §3" + value);
                    }
                    return true;
                }
                return false;
            } else {
                sender.sendMessage("§cYou don't have permission to do that!");
            }
        } else {
            sender.sendMessage("§cYou need to be a player to execute this command!");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();


        if (args.length == 1 && sender.hasPermission("discovsuite.speed")) {
            suggestions.add("walk");
            suggestions.add("fly");
            return StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<>());
        }

        return suggestions;
    }
}
