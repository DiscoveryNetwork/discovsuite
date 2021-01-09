package nl.parrotlync.discovsuite.bungeecord.event;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class PlayerProtocolAcceptEvent extends Event {
    private final ProxiedPlayer player;

    public PlayerProtocolAcceptEvent(ProxiedPlayer player) {
        this.player = player;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }
}
