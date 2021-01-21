package nl.parrotlync.discovsuite.spigot.command;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.model.Warp;
import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class DelWarpCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("discovsuite.command.delwarp")) {
            ChatUtil.sendConfigMessage(sender, "no-permission");
            return true;
        }

        if (args.length < 1) {
            ChatUtil.sendMissingArguments(sender, new String[] {"name"});
            return true;
        }

        Warp warp = DiscovSuite.getInstance().getWarpManager().getWarp(args[0]);
        if (warp != null) {
            DiscovSuite.getInstance().getWarpManager().removeWarp(warp);
            ChatUtil.sendConfigMessage(sender, "warp-removed", warp.getName());
        } else {
            ChatUtil.sendConfigMessage(sender, "invalid-warp");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (sender.hasPermission("discovsuite.command.setwarp")) {
            if (args.length == 1) {
                for (Warp warp : DiscovSuite.getInstance().getWarpManager().getWarps()) {
                    suggestions.add(warp.getName());
                }
                StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<>());
            }
        }

        return suggestions;
    }
}
