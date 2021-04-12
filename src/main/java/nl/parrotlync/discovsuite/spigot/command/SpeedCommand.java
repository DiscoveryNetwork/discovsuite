package nl.parrotlync.discovsuite.spigot.command;

import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtil.sendConfigMessage(sender, "player-only");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("discovsuite.command.speed")) {
            ChatUtil.sendConfigMessage(sender, "no-permission");
            return true;
        }

        if (args.length < 1) {
            ChatUtil.sendMissingArguments(sender, new String[] {"speed"});
            return true;
        }

        float value = Math.max(0, Math.min(10, Float.parseFloat(args[0])));
        if (player.isFlying()) {
            player.setFlySpeed(value / 10);
            ChatUtil.sendConfigMessage(sender, "fly-speed-change", String.valueOf(value));
        } else {
            player.setWalkSpeed(value / 10);
            ChatUtil.sendConfigMessage(sender, "walk-speed-change", String.valueOf(value));
        }
        return true;
    }
}
