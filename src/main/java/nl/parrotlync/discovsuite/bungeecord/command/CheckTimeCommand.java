package nl.parrotlync.discovsuite.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;
import nl.parrotlync.discovsuite.bungeecord.util.ChatUtil;

import java.util.Date;

public class CheckTimeCommand extends Command {

    public CheckTimeCommand() {
        super("checktime", "discovsuite.command.checktime");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String target;
        if (args.length == 1) {
            if (!sender.hasPermission("discovsuite.command.checktime.others")) {
                ChatUtil.sendConfigMessage(sender, "no-permission");
                return;
            }
            target = args[0];
        } else {
            target = sender.getName();
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