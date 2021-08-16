package nl.parrotlync.discovsuite.spigot.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class DiscovSuiteExpansion extends PlaceholderExpansion {

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "dsuite";
    }

    @Override
    public @NotNull String getAuthor() {
        return "ParrotLync";
    }

    @Override
    public @NotNull String getVersion() {
        return DiscovSuite.getInstance().getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (identifier.equalsIgnoreCase("vanished")) {
            assert player.getPlayer() != null;
            return String.valueOf(DiscovSuite.getInstance().getVanishManager().isPlayerHidden(player.getPlayer()));
        }

        return null;
    }
}
