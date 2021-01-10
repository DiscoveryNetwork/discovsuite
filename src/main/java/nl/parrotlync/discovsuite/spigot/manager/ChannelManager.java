package nl.parrotlync.discovsuite.spigot.manager;

import nl.parrotlync.discovsuite.spigot.model.ChannelType;
import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ChannelManager {
    private final HashMap<UUID, ChannelType> channels = new HashMap<>();

    public void setChannel(Player player, ChannelType type) {
        channels.put(player.getUniqueId(), type);
        ChatUtil.sendConfigMessage(player, "chat-channel-changed", type.name());
    }

    public ChannelType getChannel(Player player) {
        if (channels.get(player.getUniqueId()) != null) {
            return channels.get(player.getUniqueId());
        } else {
            return ChannelType.GLOBAL;
        }
    }
}
