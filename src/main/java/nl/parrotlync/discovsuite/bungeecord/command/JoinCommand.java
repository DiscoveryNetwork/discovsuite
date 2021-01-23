package nl.parrotlync.discovsuite.bungeecord.command;

import com.google.common.collect.Iterables;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;
import nl.parrotlync.discovsuite.bungeecord.util.ChatUtil;
import org.bukkit.ChatColor;

import java.util.Collections;
import java.util.Locale;
import java.util.stream.Collectors;

public class JoinCommand extends Command implements TabExecutor {

    public JoinCommand() {
        super("join");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            ChatUtil.sendConfigMessage(sender, "player-only");
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (args.length < 1) {
            showMenu(player);
            return;
        }

        ServerInfo server = ProxyServer.getInstance().getServerInfo(args[0]);
        if (server == null) {
            showMenu(player);
            return;
        }

        if (server.canAccess(player)) {
            String message = ChatColor.translateAlternateColorCodes('&', DiscovSuite.getInstance().getConfig().getString("messages.server-connecting"));
            player.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        } else {
            ChatUtil.sendConfigMessage(player, "server-restricted");
        }
    }

    @Override
    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
        if (args.length == 1) {
            return Iterables.transform(ProxyServer.getInstance().getServers().values().stream().filter(server -> server.getName().toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT)) && server.canAccess(sender)).collect(Collectors.toList()), ServerInfo::getName);
        } else {
            return Collections.emptyList();
        }
    }

    private void showMenu(ProxiedPlayer player) {
        ChatUtil.sendConfigMessage(player, "server-list-title");
        for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
            if (server.canAccess(player)) {
                String message = DiscovSuite.getInstance().getConfig().getString("messages.server-list-item");
                TextComponent main = new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
                main.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join " + server.getName()));
                player.sendMessage(main);
            }
        }
    }
}
