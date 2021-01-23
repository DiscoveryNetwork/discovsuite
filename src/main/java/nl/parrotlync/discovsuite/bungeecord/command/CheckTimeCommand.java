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

public class CheckTimeCommand extends Command implements TabExecutor {

    public CheckTimeCommand() {
        super("checktime", "discovsuite.command.checktime");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String target;

        if (args.length == 0) {
            target = sender.getName();
        } else if (args.length == 1 && sender.hasPermission("discovsuite.command.checktime.others")) {
            target = args[0];
        } else {
            ChatUtil.sendConfigMessage(sender, "no-permission");
            return;
        }

        ProxyServer.getInstance().getScheduler().runAsync(DiscovSuite.getInstance(), () -> {
            try {
                int seconds = DiscovSuite.getInstance().getDatabase().getSecondsThisWeek(target);
                int[] time = getTimes(seconds + getCurrentSeconds(target));
                if (time[0] >= 2) {
                    sender.sendMessage(TextComponent.fromLegacyText("§7-- §aPlaytime this week §7--"));
                } else {
                    sender.sendMessage(TextComponent.fromLegacyText("§7-- §cPlaytime this week §7--"));
                }
                String msg = String.format("%d hours, %d minutes, %d seconds", time[0], time[1], time[2]);
                sender.sendMessage(TextComponent.fromLegacyText(msg));
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
