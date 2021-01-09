package nl.parrotlync.discovsuite.spigot.command;

import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
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
        if (!(sender instanceof Player)) {
            ChatUtil.sendConfigMessage(sender, "player-only");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("discovsuite.command.fly")) {
            ChatUtil.sendConfigMessage(sender, "no-permission");
            return true;
        }

        Player target = null;
        if (args.length == 1) {
            if (!player.hasPermission("discovsuite.command.fly.others")) {
                ChatUtil.sendConfigMessage(sender, "no-permission");
                return true;
            }
            target = Bukkit.getPlayer(args[0]);
        }

        if (target == null) {
            target = player;
        }

        target.setAllowFlight(!target.getAllowFlight());
        if (target.getAllowFlight()) {
            ChatUtil.sendConfigMessage(sender, "flight-enabled");
        } else {
            target.setFlying(false);
            ChatUtil.sendConfigMessage(sender, "flight-disabled");
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
