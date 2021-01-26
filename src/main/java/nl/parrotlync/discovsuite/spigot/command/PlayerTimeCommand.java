package nl.parrotlync.discovsuite.spigot.command;

import nl.parrotlync.discovsuite.spigot.model.TimeValue;
import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PlayerTimeCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtil.sendConfigMessage(sender, "player-only");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("discovsuite.command.playertime")) {
            ChatUtil.sendConfigMessage(player, "no-permission");
            return true;
        }

        if (args.length < 1) {
            ChatUtil.sendMissingArguments(player, new String[] {"time"});
            return true;
        }

        if (args[0].equalsIgnoreCase("reset")) {
            player.resetPlayerTime();
            ChatUtil.sendConfigMessage(player, "player-time-reset");
            return true;
        }

        try {
            player.setPlayerTime(getTimeTicks(args[0]), false);
            ChatUtil.sendConfigMessage(player, "player-time-set", getTimeTicks(args[0]).toString());
        } catch (NumberFormatException e) {
            ChatUtil.sendConfigMessage(player, "player-time-invalid");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1 && sender.hasPermission("discovsuite.command.playertime")) {
            suggestions.add("reset");
            for (TimeValue value : TimeValue.values()) {
                suggestions.add(value.toString().toLowerCase());
            }
            StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<>());
        }

        return suggestions;
    }

    private Long getTimeTicks(String argument) throws NumberFormatException {
        try {
            return Long.parseLong(argument);
        } catch (NumberFormatException ignored) {}

        try {
            return TimeValue.valueOf(argument.toUpperCase()).getValue();
        } catch (IllegalArgumentException ignored) {}

        throw new NumberFormatException();
    }
}
