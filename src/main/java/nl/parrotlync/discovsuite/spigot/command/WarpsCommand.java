package nl.parrotlync.discovsuite.spigot.command;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.model.Warp;
import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtil.sendConfigMessage(sender, "player-only");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("discovsuite.command.warps")) {
            ChatUtil.sendConfigMessage(player, "no-permission");
            return true;
        }

        ChatUtil.sendConfigMessage(player, "warps-title");
        String item = ChatColor.translateAlternateColorCodes('&', DiscovSuite.getInstance().getConfig().getString("messages.warps-item"));
        List<String> warpNames = new ArrayList<>();
        for (Warp warp: DiscovSuite.getInstance().getWarpManager().getAccessibleWarps(player)) {
            warpNames.add(String.format(item, warp.getName()));
        }
        player.sendMessage(String.join("§f, ", warpNames));
        return true;
    }
}
