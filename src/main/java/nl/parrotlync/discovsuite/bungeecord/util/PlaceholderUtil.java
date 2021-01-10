package nl.parrotlync.discovsuite.bungeecord.util;

import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;

public class PlaceholderUtil {

    public static String parse(ProxiedPlayer player, String format) {
        format = format.replaceAll("%NAME%", player.getName());
        format = format.replaceAll("%DISPLAYNAME%", player.getDisplayName());
        return format.replaceAll("%SUFFIX%", getSuffix(player));
    }

    private static String getSuffix(ProxiedPlayer player) {
        User user = DiscovSuite.getInstance().getLuckPermsUser(player);
        if (user != null) {
            return user.getCachedData().getMetaData().getSuffix();
        }
        return "";
    }
}
