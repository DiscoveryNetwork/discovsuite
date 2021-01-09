package nl.parrotlync.discovsuite.spigot.command;

import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class SpeedCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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

        float value = Math.max(0, Math.min(10, Float.parseFloat(args[1])));
        if (player.isFlying()) {
            player.setFlySpeed(value / 10);
            ChatUtil.sendConfigMessage(sender, "fly-speed-change", String.valueOf(value));
        } else {
            player.setWalkSpeed(value / 10);
            ChatUtil.sendConfigMessage(sender, "walk-speed-change", String.valueOf(value));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();


        if (args.length == 1 && sender.hasPermission("discovsuite.speed")) {
            suggestions.add("walk");
            suggestions.add("fly");
            return StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<>());
        }

        return suggestions;
    }
}
