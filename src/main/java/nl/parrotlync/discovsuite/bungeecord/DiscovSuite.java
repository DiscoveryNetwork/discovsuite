package nl.parrotlync.discovsuite.bungeecord;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import nl.parrotlync.discovsuite.bungeecord.command.*;
import nl.parrotlync.discovsuite.bungeecord.listener.ChatListener;
import nl.parrotlync.discovsuite.bungeecord.listener.PlayerListener;
import nl.parrotlync.discovsuite.bungeecord.listener.PluginMessageListener;
import nl.parrotlync.discovsuite.bungeecord.manager.ConversationManager;
import nl.parrotlync.discovsuite.bungeecord.util.ChatFilter;
import nl.parrotlync.discovsuite.bungeecord.util.DatabaseUtil;
import nl.parrotlync.discovsuite.bungeecord.util.PlayerCache;
import nl.parrotlync.discovsuite.common.Beacon;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

public class DiscovSuite extends Plugin {
    private static DiscovSuite instance;
    public static boolean chatMuted = true;
    private final HashMap<UUID, Date> sessions = new HashMap<>();
    private final ConversationManager conversationManager = new ConversationManager();
    private final List<ProxiedPlayer> blockedPlayers = new ArrayList<>();
    private final PlayerCache playerCache = new PlayerCache();
    private final ChatFilter chatFilter = new ChatFilter();
    private DatabaseUtil database;

    public DiscovSuite() {
        instance = this;
    }

    @Override
    public void onEnable() {
        if (!Beacon.authenticate()) { return; }
        saveDefaultConfig();

        // Channels
        getProxy().registerChannel("dsuite:chat");
        getProxy().registerChannel("dsuite:staff");
        getProxy().registerChannel("dsuite:mgchat");
        getProxy().registerChannel("dsuite:broadcast");
        getProxy().registerChannel("dsuite:mention");
        getProxy().registerChannel("dsuite:dpname");
        getProxy().registerChannel("dsuite:teleport");

        // Database
        this.database = new DatabaseUtil(getConfig().getString("database.host"), getConfig().getString("database.username"),
                getConfig().getString("database.password"), getConfig().getString("database.database"));
        getProxy().getScheduler().runAsync(this, () -> {
           try {
               database.connect();
               database.createTables();
               getLogger().info("Database connection established!");
           } catch (Exception e) {
               getLogger().severe("Something went wrong while trying to establish a database connection!");
               e.printStackTrace();
           }
        });

        // Managers
        playerCache.load();
        chatFilter.fetchBannedWords();
        chatFilter.fetchExcludedWords();

        // Listeners & Commands
        getProxy().getPluginManager().registerListener(this, new PlayerListener());
        getProxy().getPluginManager().registerListener(this, new ChatListener());
        getProxy().getPluginManager().registerListener(this, new PluginMessageListener());
        getProxy().getPluginManager().registerCommand(this, new JoinCommand());
        getProxy().getPluginManager().registerCommand(this, new AcceptCommand());
        getProxy().getPluginManager().registerCommand(this, new MessageCommand());
        getProxy().getPluginManager().registerCommand(this, new ReplyCommand());
        getProxy().getPluginManager().registerCommand(this, new OnlineTimeCommand());
        getProxy().getPluginManager().registerCommand(this, new CheckTimeCommand());
        getProxy().getPluginManager().registerCommand(this, new SeenCommand());
        getProxy().getPluginManager().registerCommand(this, new TeleportCommand());
        getProxy().getPluginManager().registerCommand(this, new BroadcastCommand());
        getProxy().getPluginManager().registerCommand(this, new ChatFilterCommand());
        getProxy().getPluginManager().registerCommand(this, new ClearChatCommand());
        getProxy().getPluginManager().registerCommand(this, new MuteChatCommand());
        getLogger().info("DiscovSuite has started.");
    }

    public DatabaseUtil getDatabase() {
        return database;
    }

    public HashMap<UUID, Date> getSessions() {
        return sessions;
    }

    private void saveDefaultConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config-bungee.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Configuration getConfig() {
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Configuration();
    }

    public User getLuckPermsUser(ProxiedPlayer player) {
        if (ProxyServer.getInstance().getPluginManager().getPlugin("LuckPerms") != null) {
            return LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
        }
        return null;
    }

    public ConversationManager getConversationManager() {
        return conversationManager;
    }

    public PlayerCache getPlayerCache() { return playerCache; }

    public ChatFilter getChatFilter() { return chatFilter; }

    public List<ProxiedPlayer> getBlockedPlayers() {
        return blockedPlayers;
    }

    public static DiscovSuite getInstance() {
        return instance;
    }
}
