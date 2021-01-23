package nl.parrotlync.discovsuite.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import nl.parrotlync.discovsuite.bungeecord.util.ChatUtil;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ChatFilterCommand extends Command implements TabExecutor {

    public ChatFilterCommand() {
        super("chatfilter", "discovsuite.command.chatfilter");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            ChatUtil.sendMissingArguments(sender, new String[] {"type", "word"});
            return;
        } else if (args.length < 2) {
            ChatUtil.sendMissingArguments(sender, new String[] {"word"});
            return;
        }

        if (args[0].equalsIgnoreCase("ban")) {
            DiscovSuite.getInstance().getChatFilter().addBannedWord(args[1]);
            ChatUtil.sendConfigMessage(sender, "add-banned-word", args[1]);
        }

        if (args[0].equalsIgnoreCase("exclude")) {
            DiscovSuite.getInstance().getChatFilter().addExcludedWord(args[1]);
            ChatUtil.sendConfigMessage(sender, "add-excluded-word", args[1]);
        }
    }

    @Override
    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            suggestions.add("ban");
            suggestions.add("exclude");
            return suggestions.stream().filter(input -> input.toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT))).collect(Collectors.toCollection(ArrayList::new));
        } else {
            return Collections.emptyList();
        }
    }
}
