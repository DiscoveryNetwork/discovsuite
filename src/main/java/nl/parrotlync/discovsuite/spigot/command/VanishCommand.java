package nl.parrotlync.discovsuite.spigot.command;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.manager.VanishManager;
import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VanishCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        VanishManager vanishManager = DiscovSuite.getInstance().getVanishManager();

        if (!sender.hasPermission("discovsuite.command.vanish")) {
            ChatUtil.sendConfigMessage(sender, "no-permission");
            return true;
        }

        if (!(sender instanceof Player)) {
            ChatUtil.sendConfigMessage(sender, "player-only");
            return true;
        }

        Player player = (Player) sender;
        if (vanishManager.isPlayerHidden(player)) {
            vanishManager.showPlayer(player);
            ChatUtil.sendConfigMessage(sender, "player-unvanish");
        } else {
            vanishManager.hidePlayer(player);
            ChatUtil.sendConfigMessage(sender, "player-vanish");
        }
        return true;
    }
}
