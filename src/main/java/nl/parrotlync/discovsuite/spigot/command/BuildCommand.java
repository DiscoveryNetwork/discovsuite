package nl.parrotlync.discovsuite.spigot.command;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.event.BuildModeToggleEvent;
import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class BuildCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("discovsuite.command.build")) {
            ChatUtil.sendConfigMessage(sender, "no-permission");
            return true;
        }

        Player target = null;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                ChatUtil.sendConfigMessage(sender, "player-only");
                return true;
            }
            target = (Player) sender;
        } else if (args.length == 1) {
            if (Bukkit.getPlayer(args[0]) == null) {
                ChatUtil.sendConfigMessage(sender, "player-not-found");
                return true;
            }
            target = Bukkit.getPlayer(args[0]);
        }

        if (target != null) {
            boolean enabled = !DiscovSuite.getInstance().getInventoryManager().hasInventory(target);
            Bukkit.getPluginManager().callEvent(new BuildModeToggleEvent(target, enabled));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                suggestions.add(player.getName());
            }
            return StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<>());
        }

        return suggestions;
    }
}
