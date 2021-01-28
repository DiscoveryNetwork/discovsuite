package nl.parrotlync.discovsuite.spigot.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.model.Warp;
import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class MessageListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(bytes);

        if (channel.equalsIgnoreCase("BungeeCord")) {
            String subChannel = byteArrayDataInput.readUTF();
            if (subChannel.startsWith("dsuite:")) { channel = subChannel; }
        }

        if (!channel.startsWith("dsuite:")) { return; }

        if (channel.equalsIgnoreCase("dsuite:mention")) {
            UUID playerId = UUID.fromString(byteArrayDataInput.readUTF());
            String message = byteArrayDataInput.readUTF();
            Player mentionedPlayer = Bukkit.getPlayer(playerId);
            if (mentionedPlayer != null) {
                if (!message.isEmpty()) {
                    mentionedPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                }
                mentionedPlayer.playSound(mentionedPlayer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
            }
        }

        if (channel.equalsIgnoreCase("dsuite:teleport")) {
            UUID teleportPlayer = UUID.fromString(byteArrayDataInput.readUTF());
            Player target = Bukkit.getPlayer(UUID.fromString(byteArrayDataInput.readUTF()));
            if (target != null) {
                if (Bukkit.getPlayer(teleportPlayer) != null) {
                    Bukkit.getPlayer(teleportPlayer).teleport(target);
                } else {
                    DiscovSuite.getInstance().getTeleportManager().queue(teleportPlayer, target);
                }
            }
        }

        if (channel.equalsIgnoreCase("dsuite:warp")) {
            byte[] data = new byte[byteArrayDataInput.readShort()];
            byteArrayDataInput.readFully(data);
            DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(data));

            try {
                UUID teleportPlayer = UUID.fromString(inputStream.readUTF());
                Warp warp = DiscovSuite.getInstance().getWarpManager().getWarp(inputStream.readUTF());
                if (warp != null) {
                    if (Bukkit.getPlayer(teleportPlayer) != null) {
                        Bukkit.getPlayer(teleportPlayer).teleport(warp.getLocation());
                        ChatUtil.sendConfigMessage(player, "warp-teleported", warp.getName());
                    } else {
                        DiscovSuite.getInstance().getTeleportManager().queue(teleportPlayer, warp);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (channel.equalsIgnoreCase("dsuite:update")) {
            DiscovSuite.getInstance().getLogger().info("Received update command through BungeeCord");
            DiscovSuite.getInstance().getWarpManager().load();
        }
    }
}
