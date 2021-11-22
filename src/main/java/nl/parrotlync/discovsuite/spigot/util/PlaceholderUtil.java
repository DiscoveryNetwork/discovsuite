package nl.parrotlync.discovsuite.spigot.util;

import net.luckperms.api.model.user.User;
import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class PlaceholderUtil {

    public static String parse(Player player, String format) {
        format = format.replaceAll("%SERVER%", Objects.requireNonNull(DiscovSuite.getInstance().getConfig().getString("server-name")));
        format = format.replaceAll("%DISPLAYNAME%", player.getDisplayName());
        format = format.replaceAll("%NAME%", player.getName());
        String suffix = getSuffix(player);
        if (suffix == null) { suffix = ""; }
        return format.replaceAll("%SUFFIX%", suffix);
    }

    public static String parseForSender(CommandSender sender, String format) {
        format = format.replaceAll("%SERVER%", Objects.requireNonNull(DiscovSuite.getInstance().getConfig().getString("server-name")));
        format = format.replaceAll("%NAME%", sender.getName());
        return format;
    }

    private static String getSuffix(Player player) {
        User user = DiscovSuite.getInstance().getLuckPermsUser(player);
        if (user != null) {
            return user.getCachedData().getMetaData().getSuffix();
        }
        return "";
    }
}
