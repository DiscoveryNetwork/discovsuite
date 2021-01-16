package nl.parrotlync.discovsuite.bungeecord.command;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import nl.parrotlync.discovsuite.bungeecord.util.ChatUtil;

import java.util.Collections;
import java.util.Locale;

public class TeleportCommand extends Command implements TabExecutor {

    public TeleportCommand() {
        super("teleport", "discovsuite.command.teleport", "tp");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            ChatUtil.sendConfigMessage(sender, "player-only");
            return;
        }

        if (args.length < 1) {
            ChatUtil.sendMissingArguments(sender, new String[] {"player"});
            return;
        }

        ProxiedPlayer player, target;
        if (args.length == 2) {
            player = ProxyServer.getInstance().getPlayer(args[0]);
            target = ProxyServer.getInstance().getPlayer(args[1]);
        } else {
            player = (ProxiedPlayer) sender;
            target = ProxyServer.getInstance().getPlayer(args[0]);
        }

        if (player == null || target == null) {
            ChatUtil.sendConfigMessage(sender, "player-not-found");
            return;
        }

        if (player.getServer().getInfo() != target.getServer().getInfo()) {
            player.connect(target.getServer().getInfo());
        }

        ChatUtil.sendConfigMessage(player, "player-teleport", target.getName());
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF(target.getUniqueId().toString());
        player.getServer().sendData("dsuite:teleport", byteArrayDataOutput.toByteArray());
    }

    @Override
    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
        return (args.length == 0 || args.length > 2) ? Collections.EMPTY_LIST : Iterables.transform(Iterables.filter(ProxyServer.getInstance().getPlayers(), new Predicate<ProxiedPlayer>() {
            private final String lower = args[args.length - 1].toLowerCase(Locale.ROOT);

            @Override
            public boolean apply(ProxiedPlayer proxiedPlayer) {
                return proxiedPlayer.getName().toLowerCase(Locale.ROOT).startsWith(lower);
            }
        }), CommandSender::getName);
    }
}
