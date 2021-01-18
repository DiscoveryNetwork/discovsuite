package nl.parrotlync.discovsuite.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;
import nl.parrotlync.discovsuite.bungeecord.util.ChatUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

public class OnlineTimeCommand extends Command implements TabExecutor {

    public OnlineTimeCommand() {
        super("onlinetime", "discovsuite.command.onlinetime");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String target;
        if (args.length == 1) {
            if (!sender.hasPermission("discovsuite.command.onlinetime.others")) {
                ChatUtil.sendConfigMessage(sender, "no-permission");
                return;
            }
            target = args[0];
        } else {
            target = sender.getName();
        }

        ProxyServer.getInstance().getScheduler().runAsync(DiscovSuite.getInstance(), () -> {
            try {
                int seconds = DiscovSuite.getInstance().getDatabase().getSeconds(target);
                int[] time = getTimes(seconds + getCurrentSeconds(target));
                String msg = String.format("§aTotal online time: §7%d hours, %d minutes, %d seconds", time[0], time[1], time[2]);
                sender.sendMessage(TextComponent.fromLegacyText(msg));
            } catch (Exception e) {
                sender.sendMessage(TextComponent.fromLegacyText("§cSomething went wrong while attempting to fetch data..."));
                e.printStackTrace();
            }
        });
    }

    @Override
    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args)
    {
        if (args.length != 2) {
            return Collections.emptyList();
        } else {
            return DiscovSuite.getInstance().getPlayerCache().getPlayers().stream().filter(input -> input.toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT))).collect(Collectors.toCollection(ArrayList::new));
        }
    }

    private int[] getTimes(int total) {
        int hours = total / 3600;
        int minutes = (total % 3600) / 60;
        int seconds = total % 60;
        return new int[] {hours, minutes, seconds};
    }

    private int getCurrentSeconds(String playerName) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
        if (player == null) { return 0; }
        Date login = DiscovSuite.getInstance().getSessions().get(player.getUniqueId());
        if (login == null) { return 0; }
        Date now = new Date();
        return (int) (now.toInstant().getEpochSecond() - login.toInstant().getEpochSecond());
    }
}
