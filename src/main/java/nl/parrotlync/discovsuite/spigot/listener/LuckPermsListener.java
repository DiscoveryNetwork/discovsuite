package nl.parrotlync.discovsuite.spigot.listener;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.log.LogPublishEvent;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.util.PluginMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LuckPermsListener {

    public LuckPermsListener() {
        EventBus eventBus = LuckPermsProvider.get().getEventBus();
        eventBus.subscribe(DiscovSuite.getInstance(), LogPublishEvent.class, e -> e.setCancelled(true));
        eventBus.subscribe(DiscovSuite.getInstance(), UserDataRecalculateEvent.class, this::onUserDataRecalculate);
    }

    public void onUserDataRecalculate(UserDataRecalculateEvent event) {
        Player player = Bukkit.getPlayer(event.getUser().getUniqueId());
        if (player != null) {
            String prefix = event.getUser().getCachedData().getMetaData().getPrefix();
            String displayName = prefix + " " + DiscovSuite.getInstance().getNicknameManager().getNickname(player);
            player.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            PluginMessage.sendDisplayName(player);
        }
    }
}
