package nl.parrotlync.discovsuite.bungeecord.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;
import nl.parrotlync.discovsuite.bungeecord.util.ChatUtil;

public class BroadcastCommand extends Command {

    public BroadcastCommand() {
        super("broadcast", "discovsuite.command.broadcast", "bc");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            ChatUtil.sendMissingArguments(sender, new String[] {"message"});
            return;
        }

        String message = DiscovSuite.getInstance().getMessages().getString("formats.broadcast") + String.join(" ", args);
        message = ChatColor.translateAlternateColorCodes('&', message);
        for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
            if (onlinePlayer.hasPermission("discovsuite.chat.broadcast.showsender")) {
                onlinePlayer.sendMessage(TextComponent.fromLegacyText(message.replace("%{}%", sender.getName())));
            } else {
                onlinePlayer.sendMessage(TextComponent.fromLegacyText(message.replace("%{}%", "Information")));
            }
        }
    }
}
