package nl.parrotlync.discovsuite.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;
import nl.parrotlync.discovsuite.bungeecord.util.ChatUtil;

public class SeenCommand extends Command {

    public SeenCommand() {
        super("seen", "discovsuite.command.seen");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (ProxyServer.getInstance().getPlayer(args[0]) != null) {
                sender.sendMessage(TextComponent.fromLegacyText("§6" + args[0] + " §7is currently §aonline"));
                return;
            }
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
}
