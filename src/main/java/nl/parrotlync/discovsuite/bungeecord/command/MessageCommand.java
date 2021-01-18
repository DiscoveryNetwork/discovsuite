package nl.parrotlync.discovsuite.bungeecord.command;

import com.google.common.collect.Iterables;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import nl.parrotlync.discovsuite.bungeecord.event.PrivateMessageEvent;

import java.util.Collections;
import java.util.Locale;
import java.util.stream.Collectors;

public class MessageCommand extends Command implements TabExecutor {

    public MessageCommand() {
        super("message", "", "msg", "tell", "whisper", "m", "w");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 1) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            String message = String.join(" ", args).replaceAll(args[0] + " ", "");
            ProxyServer.getInstance().getPluginManager().callEvent(new PrivateMessageEvent(player, target, message));
        }
    }

    @Override
    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
        if (args.length == 2) {
            return Collections.emptyList();
        } else {
            return Iterables.transform(ProxyServer.getInstance().getPlayers().stream().filter(proxiedPlayer -> proxiedPlayer.getName().toLowerCase(Locale.ROOT).startsWith(args[args.length - 1].toLowerCase(Locale.ROOT))).collect(Collectors.toList()), CommandSender::getName);
        }
    }
}
