package nl.parrotlync.discovsuite.spigot.listener;

import me.clip.placeholderapi.PlaceholderAPI;
import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import nl.parrotlync.discovsuite.spigot.model.ChannelType;
import nl.parrotlync.discovsuite.spigot.util.ChatUtil;
import nl.parrotlync.discovsuite.spigot.util.PlaceholderUtil;
import nl.parrotlync.discovsuite.spigot.util.PluginMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    
    @EventHandler(priority = EventPriority.LOW) 
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (DiscovSuite.getInstance().getPlaceholderSupport() && player.hasPermission("discovsuite.chat.placeholders")) {
            event.setMessage(PlaceholderAPI.setPlaceholders(player, event.getMessage()));
        }

        event.setMessage(ChatColor.translateAlternateColorCodes('~', event.getMessage()));
        if (player.hasPermission("discovsuite.chat.color")) {
            event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
        }

        ChannelType channel = DiscovSuite.getInstance().getChannelManager().getChannel(player);

        if (channel == ChannelType.GLOBAL) {
            String format = PlaceholderUtil.parse(player, DiscovSuite.getInstance().getConfig().getString("formats.chat-global"));
            event.setFormat(ChatColor.translateAlternateColorCodes('&', format + "%2$s"));
            PluginMessage.sendChat(player, event.getMessage(), event.getFormat());
        } else if (channel == ChannelType.LOCAL) {
            String format = PlaceholderUtil.parse(player, DiscovSuite.getInstance().getConfig().getString("formats.chat-local"));
            Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', format + event.getMessage()), "discovsuite.chat.local");
            event.setCancelled(true);
        } else if (channel == ChannelType.MANAGEMENT_CHAT) {
            PluginMessage.sendManagementMessage(player, event.getMessage());
            event.setCancelled(true);
        } else if (channel == ChannelType.STAFF_CHAT) {
            PluginMessage.sendStaffMessage(player, event.getMessage());
            event.setCancelled(true);
        }
    }
}
