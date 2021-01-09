package nl.parrotlync.discovsuite.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import nl.parrotlync.discovsuite.bungeecord.event.PlayerProtocolAcceptEvent;
import nl.parrotlync.discovsuite.bungeecord.util.ChatUtil;

public class AcceptCommand extends Command {

    public AcceptCommand() {
        super("accept");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            ChatUtil.sendConfigMessage(sender, "player-only");
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        ProxyServer.getInstance().getPluginManager().callEvent(new PlayerProtocolAcceptEvent(player));
    }
}
