package nl.parrotlync.discovsuite.bungeecord.command;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import nl.parrotlync.discovsuite.bungeecord.event.PrivateMessageEvent;

import java.util.Collections;
import java.util.Locale;

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
        return (args.length > 1) ? Collections.EMPTY_LIST : Iterables.transform(Iterables.filter(ProxyServer.getInstance().getPlayers(), new Predicate<ProxiedPlayer>() {
            private final String lower = (args.length == 0) ? "" : args[0].toLowerCase(Locale.ROOT);

            @Override
            public boolean apply(ProxiedPlayer proxiedPlayer) {
                return proxiedPlayer.getName().toLowerCase(Locale.ROOT).startsWith(lower);
            }
        }), CommandSender::getName);
    }
}
