package nl.parrotlync.discovsuite.bungeecord.command;

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
import java.util.stream.Collectors;

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
        byteArrayDataOutput.writeUTF(player.getUniqueId().toString());
        byteArrayDataOutput.writeUTF(target.getUniqueId().toString());
        player.getServer().getInfo().sendData("dsuite:teleport", byteArrayDataOutput.toByteArray());
    }

    @Override
    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
        if (args.length == 0 || args.length > 2) {
            return Collections.emptyList();
        } else {
            return Iterables.transform(ProxyServer.getInstance().getPlayers().stream().filter(proxiedPlayer -> proxiedPlayer.getName().toLowerCase(Locale.ROOT).startsWith(args[args.length - 1].toLowerCase(Locale.ROOT))).collect(Collectors.toList()), CommandSender::getName);
        }
    }
}
