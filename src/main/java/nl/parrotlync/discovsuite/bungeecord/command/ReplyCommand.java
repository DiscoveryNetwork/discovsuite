package nl.parrotlync.discovsuite.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;
import nl.parrotlync.discovsuite.bungeecord.event.PrivateMessageEvent;
import nl.parrotlync.discovsuite.bungeecord.util.ChatUtil;

public class ReplyCommand extends Command {

    public ReplyCommand() {
        super("reply", "", "r", "re");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            ProxiedPlayer target = DiscovSuite.getInstance().getConversationManager().getReceiver(player);

            if (target != null) {
                String message = String.join(" ", args);
                ProxyServer.getInstance().getPluginManager().callEvent(new PrivateMessageEvent(player, target, message));
            } else {
                ChatUtil.sendConfigMessage(sender, "player-not-online");
            }
        }
    }
}
