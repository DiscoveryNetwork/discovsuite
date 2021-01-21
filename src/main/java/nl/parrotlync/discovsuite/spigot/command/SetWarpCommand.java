package nl.parrotlync.discovsuite.spigot.command;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.model.WarpGroup;
import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class SetWarpCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtil.sendConfigMessage(sender, "player-only");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("discovsuite.command.setwarp")) {
            ChatUtil.sendConfigMessage(player, "no-permission");
            return true;
        }

        if (args.length < 1) {
            ChatUtil.sendMissingArguments(player, new String[] {"name"});
            return true;
        }

        WarpGroup group;
        if (args.length == 2) {
            try {
                group = WarpGroup.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                ChatUtil.sendConfigMessage(player, "invalid-group");
                return true;
            }
        } else {
            group = WarpGroup.VISITOR;
        }

        if (DiscovSuite.getInstance().getWarpManager().createWarp(args[0], player.getLocation(), group)) {
            ChatUtil.sendConfigMessage(player, "warp-created", new String[] {args[0], group.toString()});
        } else {
            ChatUtil.sendConfigMessage(player, "warp-already-exists");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (sender.hasPermission("discovsuite.command.setwarp")) {
            if (args.length == 1) {
                if (DiscovSuite.getInstance().getWarpManager().hasWarp(args[0])) {
                    suggestions.add("A warp with that name already exists");
                    return suggestions;
                }
            }

            if (args.length == 2) {
                for (WarpGroup group : WarpGroup.values()) {
                    suggestions.add(group.toString());
                }
                return StringUtil.copyPartialMatches(args[1], suggestions, new ArrayList<>());
            }
        }

        return suggestions;
    }
}
