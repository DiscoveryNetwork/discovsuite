package nl.parrotlync.discovsuite.spigot.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class MessageListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(bytes);

        if (channel.equalsIgnoreCase("discovchat:filter")) {
            DiscovSuite.getInstance().getChatFilter().fetchBannedWords();
            DiscovSuite.getInstance().getChatFilter().fetchExcludedWords();
        }

        if (channel.equalsIgnoreCase("discovchat:mention")) {
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
    }
}
