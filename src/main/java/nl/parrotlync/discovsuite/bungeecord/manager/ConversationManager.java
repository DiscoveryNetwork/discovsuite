package nl.parrotlync.discovsuite.bungeecord.manager;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.UUID;

public class ConversationManager {
    private final HashMap<UUID, UUID> conversations = new HashMap<>();

    public void setReceiver(ProxiedPlayer sender, ProxiedPlayer receiver) {
        conversations.put(sender.getUniqueId(), receiver.getUniqueId());
    }

    public ProxiedPlayer getReceiver(ProxiedPlayer player) {
        return ProxyServer.getInstance().getPlayer(conversations.get(player.getUniqueId()));
    }
}
