package nl.parrotlync.discovsuite.spigot.util;

import com.google.common.collect.Iterables;
import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PluginMessage {

    public static void sendChat(Player player, String message, String format) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF(player.getUniqueId().toString());
            dataOutputStream.writeUTF(format.replace("%2$s", message));
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(DiscovSuite.getInstance(), "dsuite:chat", byteArrayOutputStream.toByteArray());
    }

    public static void sendBroadcast(CommandSender sender, String message) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        String format = DiscovSuite.getInstance().getConfig().getString("formats.broadcast");

        try {
            dataOutputStream.writeUTF(ChatColor.translateAlternateColorCodes('&', format + message));
            dataOutputStream.writeUTF(sender.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        getRandomPlayer().sendPluginMessage(DiscovSuite.getInstance(), "dsuite:broadcast", byteArrayOutputStream.toByteArray());
    }

    public static void clearChat(Player player) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF(player.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(DiscovSuite.getInstance(), "dsuite:clear", byteArrayOutputStream.toByteArray());
    }

    public static void muteChat(Player player) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF(player.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(DiscovSuite.getInstance(), "dsuite:mute", byteArrayOutputStream.toByteArray());
    }

    public static void updateFilter() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        getRandomPlayer().sendPluginMessage(DiscovSuite.getInstance(), "dsuite:filter", byteArrayOutputStream.toByteArray());
    }

    public static void sendStaffAlert(CommandSender sender, String message) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        String format = PlaceholderUtil.parseForSender(sender, DiscovSuite.getInstance().getConfig().getString("formats.staff-alert"));

        try {
            dataOutputStream.writeUTF(ChatColor.translateAlternateColorCodes('&', format + message));
        } catch (IOException e) {
            e.printStackTrace();
        }

        getRandomPlayer().sendPluginMessage(DiscovSuite.getInstance(), "dsuite:staff", byteArrayOutputStream.toByteArray());
    }


    public static void sendStaffMessage(Player player, String message) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        String format = PlaceholderUtil.parse(player, DiscovSuite.getInstance().getConfig().getString("formats.chat-staff"));

        try {
            dataOutputStream.writeUTF(ChatColor.translateAlternateColorCodes('&', format + message));
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(DiscovSuite.getInstance(), "dsuite:staff", byteArrayOutputStream.toByteArray());
    }

    public static void sendManagementMessage(Player player, String message) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        String format = PlaceholderUtil.parse(player, DiscovSuite.getInstance().getConfig().getString("formats.chat-management"));

        try {
            dataOutputStream.writeUTF(ChatColor.translateAlternateColorCodes('&', format + message));
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(DiscovSuite.getInstance(), "dsuite:mgchat", byteArrayOutputStream.toByteArray());
    }

    public static void sendDisplayName(Player player, String displayName) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF(player.getUniqueId().toString());
            dataOutputStream.writeUTF(displayName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(DiscovSuite.getInstance(), "dsuite:dpname", byteArrayOutputStream.toByteArray());
    }

    public static void sendNotice(Player player, String message) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF(player.getUniqueId().toString());
            dataOutputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(DiscovSuite.getInstance(), "dsuite:notice", byteArrayOutputStream.toByteArray());
    }

    private static Player getRandomPlayer() {
        return Iterables.get(Bukkit.getOnlinePlayers(), 0);
    }
}
