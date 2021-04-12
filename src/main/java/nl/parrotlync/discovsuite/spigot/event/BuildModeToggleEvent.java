package nl.parrotlync.discovsuite.spigot.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BuildModeToggleEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final boolean enabled;

    public BuildModeToggleEvent(Player player, boolean enabled) {
        this.player = player;
        this.enabled = enabled;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
