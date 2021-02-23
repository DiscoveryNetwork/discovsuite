package nl.parrotlync.discovsuite.spigot.command;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.model.Warp;
import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.TreeMap;

public class NearbyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtil.sendConfigMessage(sender, "player-only");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("discovsuite.command.nearby")) {
            ChatUtil.sendConfigMessage(player, "no-permission");
            return true;
        }

        TreeMap<Double, Warp> warps = DiscovSuite.getInstance().getWarpManager().getNearbyWarps(player.getLocation());
        if (warps.size() != 0) {
            ChatUtil.sendConfigMessage(player, "nearby-warps-title");
            String item = ChatColor.translateAlternateColorCodes('&', DiscovSuite.getInstance().getMessages().getString("messages.nearby-warps-item"));
            for (Warp warp : warps.values()) {
                if (warp.canAccess(player)) {
                    TextComponent main = new TextComponent(String.format(item, warp.getName()));
                    main.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warp " + warp.getName()));
                    player.spigot().sendMessage(main);
                }
            }
        } else {
            ChatUtil.sendConfigMessage(player, "no-warps-found");
        }
        return true;
    }
}
