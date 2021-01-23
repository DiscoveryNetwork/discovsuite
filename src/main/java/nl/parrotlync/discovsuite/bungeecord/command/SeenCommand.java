package nl.parrotlync.discovsuite.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;
import nl.parrotlync.discovsuite.bungeecord.util.ChatUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.stream.Collectors;

public class SeenCommand extends Command implements TabExecutor {

    public SeenCommand() {
        super("seen", "discovsuite.command.seen");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            ChatUtil.sendMissingArguments(sender, new String[] {"player"});
            return;
        }

        if (ProxyServer.getInstance().getPlayer(args[0]) != null) {
            ChatUtil.sendConfigMessage(sender, "player-online", args[0]);
            return;
        }

        ProxyServer.getInstance().getScheduler().runAsync(DiscovSuite.getInstance(), () -> {
            try {
                String seen = DiscovSuite.getInstance().getDatabase().getSeen(args[0]);
                if (seen != null) {
                    sender.sendMessage(TextComponent.fromLegacyText("§6" + args[0] + " §7was last seen " + seen));
                } else {
                    ChatUtil.sendConfigMessage(sender, "player-not-found");
                }
            } catch (Exception e) {
                ChatUtil.sendConfigMessage(sender, "data-fetch-error");
                e.printStackTrace();
            }
        });
    }

    @Override
    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args)
    {
        if (args.length != 1) {
            return Collections.emptyList();
        } else {
            return DiscovSuite.getInstance().getPlayerCache().getPlayerNames().stream().filter(input -> input.toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT))).collect(Collectors.toCollection(ArrayList::new));
        }
    }
}
