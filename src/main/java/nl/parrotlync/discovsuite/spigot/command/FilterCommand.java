package nl.parrotlync.discovsuite.spigot.command;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FilterCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("discovsuite.command.chatfilter")) {
            ChatUtil.sendConfigMessage(sender, "no-permission");
            return true;
        }

        if (args.length < 1) {
            ChatUtil.sendMissingArguments(sender, new String[] {"type", "word"});
            return true;
        } else if (args.length < 2) {
            ChatUtil.sendMissingArguments(sender, new String[] {"word"});
            return true;
        }

        if (args[0].equalsIgnoreCase("ban")) {
            DiscovSuite.getInstance().getChatFilter().addBannedWord(args[1]);
            ChatUtil.sendConfigMessage(sender, "add-banned-word", args[1]);
            return true;
        }

        if (args[0].equalsIgnoreCase("exclude")) {
            DiscovSuite.getInstance().getChatFilter().addExcludedWord(args[1]);
            ChatUtil.sendConfigMessage(sender, "add-excluded-word", args[1]);
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("ban");
            suggestions.add("exclude");
            StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<>());
        }

        return suggestions;
    }
}
