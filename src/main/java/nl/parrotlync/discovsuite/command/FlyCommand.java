package nl.parrotlync.discovsuite.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FlyCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("discovsuite.fly")) {
                if (args.length == 1) {
                    if (player.hasPermission("discovsuite.fly.others")) {
                        Player target = Bukkit.getPlayer(args[0]);
                        if (target != null) {
                            target.setAllowFlight(!target.getAllowFlight());
                            if (!target.getAllowFlight()) {
                                target.setFlying(false);
                                target.sendMessage("§7Flight disabled.");
                                player.sendMessage("§7Disabled flight for " + target.getDisplayName());
                            } else {
                                target.sendMessage("§7Flight enabled.");
                                player.sendMessage("§7Enabled flight for " + target.getDisplayName());
                            }
                        } else {
                            sender.sendMessage("§cThat player doesn't exist!");
                        }
                    } else {
                        sender.sendMessage("§cYou don't have permission to do that!");
                    }
                    return true;
                }

                player.setAllowFlight(!player.getAllowFlight());
                if (!player.getAllowFlight()) {
                    player.setFlying(false);
                    player.sendMessage("§7Flight disabled.");
                } else {
                    player.sendMessage("§7Flight enabled.");
                }
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

        if (args.length == 1 && sender.hasPermission("discovsuite.fly.others")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                suggestions.add(player.getName());
            }
            return StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<>());
        }

        return suggestions;
    }
}
