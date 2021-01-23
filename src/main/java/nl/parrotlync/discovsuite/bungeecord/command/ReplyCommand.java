package nl.parrotlync.discovsuite.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
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
        if (!(sender instanceof ProxiedPlayer)) {
            ChatUtil.sendConfigMessage(sender, "player-only");
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (args.length < 1) {
            ChatUtil.sendMissingArguments(sender, new String[] {"message"});
            return;
        }

        ProxiedPlayer target = DiscovSuite.getInstance().getConversationManager().getReceiver(player);
        if (target == null) {
            ChatUtil.sendConfigMessage(sender, "player-not-online");
            return;
        }

        String message = String.join(" ", args);
        ProxyServer.getInstance().getPluginManager().callEvent(new PrivateMessageEvent(player, target, message));
    }
}
