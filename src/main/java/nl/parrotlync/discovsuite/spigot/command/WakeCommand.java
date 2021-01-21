package nl.parrotlync.discovsuite.spigot.command;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class WakeCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("discovsuite.command.wake")) {
            ChatUtil.sendConfigMessage(sender, "no-permission");
            return true;
        }

        if (args.length < 1) {
            ChatUtil.sendMissingArguments(sender, new String[] {"player"});
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            ChatUtil.sendConfigMessage(sender, "player-not-found");
            return true;
        }

        int task = Bukkit.getScheduler().runTaskTimer(DiscovSuite.getInstance(), () -> {
            target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
        }, 0, 5).getTaskId();

        Bukkit.getScheduler().runTaskLater(DiscovSuite.getInstance(), () -> {
            Bukkit.getScheduler().cancelTask(task);
        }, 30);
        target.sendMessage("§a§lWAKEY, WAKEY!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (sender.hasPermission("discovsuite.command.wake") && args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                suggestions.add(player.getName());
            }
            StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<>());
        }

        return suggestions;
    }
}
