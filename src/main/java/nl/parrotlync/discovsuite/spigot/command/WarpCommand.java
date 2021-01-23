package nl.parrotlync.discovsuite.spigot.command;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.model.Warp;
import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import nl.parrotlync.discovsuite.spigot.util.PluginMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class WarpCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("discovsuite.command.warp")) {
            ChatUtil.sendConfigMessage(sender, "no-permission");
            return true;
        }

        if (args.length < 1) {
            ChatUtil.sendMissingArguments(sender, new String[] {"warp"});
            return true;
        }

        Player target;
        if (args.length == 2) {
            if (Bukkit.getPlayer(args[1]) != null) {
                target = Bukkit.getPlayer(args[1]);
            } else {
                ChatUtil.sendConfigMessage(sender, "player-not-found");
                return true;
            }
        } else {
            target = (Player) sender;
        }

        Warp warp = DiscovSuite.getInstance().getWarpManager().getWarp(args[0]);
        if (warp != null) {
            if (warp.canAccess(target)) {
                if (warp.isLocal()) {
                    target.teleport(warp.getLocation());
                    ChatUtil.sendConfigMessage(target, "warp-teleported", warp.getName());
                } else {
                    PluginMessage.warp(target, warp);
                }
            } else {
                ChatUtil.sendConfigMessage(sender, "warp-no-permission", warp.getGroup().toString());
            }
        } else {
            ChatUtil.sendConfigMessage(sender, "invalid-warp");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1 && sender.hasPermission("discovsuite.command.warp")) {
            for (Warp warp : DiscovSuite.getInstance().getWarpManager().getWarps()) {
                if (sender instanceof Player && warp.canAccess((Player) sender)) {
                    suggestions.add(warp.getName());
                }
            }
            return StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<>());
        }

        if (args.length == 2 && sender.hasPermission("discovsuite.command.warp.others")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                suggestions.add(player.getName());
            }
            return StringUtil.copyPartialMatches(args[1], suggestions, new ArrayList<>());
        }

        return suggestions;
    }
}
