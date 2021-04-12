package nl.parrotlync.discovsuite.spigot.command;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.model.Warp;
import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import nl.parrotlync.discovsuite.spigot.util.PluginMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class RandomWarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtil.sendConfigMessage(sender, "player-only");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("discovsuite.command.randomwarp")) {
            ChatUtil.sendConfigMessage(player, "no-permission");
            return true;
        }

        List<Warp> warps = DiscovSuite.getInstance().getWarpManager().getAccessibleWarps(player);
        Warp warp = warps.get(new Random().nextInt(warps.size()));
        if (warp != null) {
            if (warp.isLocal()) {
                player.teleport(warp.getLocation());
                ChatUtil.sendConfigMessage(player, "warp-teleported", warp.getName());
            } else {
                PluginMessage.warp(player, warp);
            }
        } else {
            ChatUtil.sendConfigMessage(player, "no-warps-found");
        }
        return true;
    }
}
