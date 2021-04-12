package nl.parrotlync.discovsuite.spigot.util;

import com.google.common.collect.Iterables;
import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.model.Warp;
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

    public static void sendStaffAlert(CommandSender sender, String message) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        String format = PlaceholderUtil.parseForSender(sender, DiscovSuite.getInstance().getMessages().getString("formats.staff-alert"));

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

        String format = PlaceholderUtil.parse(player, DiscovSuite.getInstance().getMessages().getString("formats.chat-staff"));

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

        String format = PlaceholderUtil.parse(player, DiscovSuite.getInstance().getMessages().getString("formats.chat-management"));

        try {
            dataOutputStream.writeUTF(ChatColor.translateAlternateColorCodes('&', format + message));
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(DiscovSuite.getInstance(), "dsuite:mgchat", byteArrayOutputStream.toByteArray());
    }

    public static void sendDisplayName(Player player) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF(player.getUniqueId().toString());
            dataOutputStream.writeUTF(player.getDisplayName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(DiscovSuite.getInstance(), "dsuite:dpname", byteArrayOutputStream.toByteArray());
    }

    public static void auth(Player player) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF(player.getUniqueId().toString());
            dataOutputStream.writeUTF(player.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        getRandomPlayer().sendPluginMessage(DiscovSuite.getInstance(), "dsuite:auth", byteArrayOutputStream.toByteArray());
    }

    public static void connect(Player player, String server) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("Connect");
            dataOutputStream.writeUTF(server);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(DiscovSuite.getInstance(), "BungeeCord", byteArrayOutputStream.toByteArray());
    }

    public static void update() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("Forward");
            dataOutputStream.writeUTF("ALL");
            dataOutputStream.writeUTF("dsuite:update");

            byte[] data = "".getBytes();
            dataOutputStream.writeShort(data.length);
            dataOutputStream.write(data);

        } catch (IOException e) {
            e.printStackTrace();
        }

        getRandomPlayer().sendPluginMessage(DiscovSuite.getInstance(), "BungeeCord", byteArrayOutputStream.toByteArray());
    }

    public static void warp(Player player, Warp warp) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(msgBytes);

        try {
            dataOutputStream.writeUTF("Forward");
            dataOutputStream.writeUTF(warp.getServer());
            dataOutputStream.writeUTF("dsuite:warp");

            msgOut.writeUTF(player.getUniqueId().toString());
            msgOut.writeUTF(warp.getName());

            dataOutputStream.writeShort(msgBytes.toByteArray().length);
            dataOutputStream.write(msgBytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(DiscovSuite.getInstance(), "BungeeCord", byteArrayOutputStream.toByteArray());
        connect(player, warp.getServer());
    }

    private static Player getRandomPlayer() {
        return Iterables.get(Bukkit.getOnlinePlayers(), 0);
    }
}
