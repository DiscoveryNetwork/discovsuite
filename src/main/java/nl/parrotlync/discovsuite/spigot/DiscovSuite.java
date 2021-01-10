package nl.parrotlync.discovsuite.spigot;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ProxyServer;
import nl.parrotlync.discovsuite.spigot.command.*;
import nl.parrotlync.discovsuite.spigot.listener.*;
import nl.parrotlync.discovsuite.spigot.manager.ChannelManager;
import nl.parrotlync.discovsuite.spigot.manager.NicknameManager;
import nl.parrotlync.discovsuite.spigot.scoreboard.BoardManager;
import nl.parrotlync.discovsuite.spigot.util.ChatFilter;
import nl.parrotlync.discovsuite.spigot.util.DatabaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiscovSuite extends JavaPlugin {
    private static DiscovSuite instance;
    private DatabaseUtil database;
    private BoardManager boardManager;
    private final ChatFilter chatFilter;
    private final NicknameManager nicknameManager;
    private final ChannelManager channelManager;

    public DiscovSuite() {
        instance = this;
        chatFilter = new ChatFilter();
        nicknameManager = new NicknameManager();
        channelManager = new ChannelManager();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Channels
        getServer().getMessenger().registerOutgoingPluginChannel(this, "dsuite:chat");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "dsuite:staff");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "dsuite:mgchat");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "dsuite:mute");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "dsuite:clear");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "dsuite:broadcast");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "dsuite:notice");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "dsuite:filter");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "dsuite:dpname");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "dsuite:filter", new MessageListener());
        getServer().getMessenger().registerIncomingPluginChannel(this, "dsuite:mention", new MessageListener());

        // Database
        this.database = new DatabaseUtil(getConfig().getString("database.host"), getConfig().getString("database.username"),
                getConfig().getString("database.password"), getConfig().getString("database.database"));
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try {
                database.connect();
                database.createTables();
            } catch (Exception e) {
                getLogger().severe("Something went wrong while trying to establish a database connection!");
                e.printStackTrace();
            }
        });

        // Chat filter
        chatFilter.fetchBannedWords();
        chatFilter.fetchExcludedWords();

        // Scoreboard
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            HashMap<Integer, List<String>> lines = new HashMap<>();
            ConfigurationSection scoreboardContent = getConfig().getConfigurationSection("scoreboard-content");
            for (String key : scoreboardContent.getKeys(false)) {
                lines.put(Integer.parseInt(key), scoreboardContent.getStringList(key));
            }
            boardManager = new BoardManager(getConfig().getString("scoreboard-settings.title"), lines);
            for (Player player : Bukkit.getOnlinePlayers()) {
                boardManager.init(player);
            }
            getLogger().info("Dependency PlaceholderAPI was found. Scoreboard enabled.");
        } else {
            getLogger().info("Dependency PlaceholderAPI was not found. Scoreboard disabled.");
        }

        // LuckPerms listener
        if (getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            LuckPermsListener luckPermsListener = new LuckPermsListener();
        }

        // Commands & Listeners
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getPluginManager().registerEvents(new ModuleListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("speed").setExecutor(new SpeedCommand());
        getCommand("clearinventory").setExecutor(new ClearInventoryCommand());
        getCommand("staffchat").setExecutor(new StaffChatCommand());
        getCommand("managementchat").setExecutor(new ManagementChatCommand());
        getCommand("broadcast").setExecutor(new BroadcastCommand());
        getCommand("staffalert").setExecutor(new StaffAlertCommand());
        getCommand("clearchat").setExecutor(new ClearChatCommand());
        getCommand("mutechat").setExecutor(new MuteChatCommand());
        getCommand("nick").setExecutor(new NicknameCommand());
        getCommand("chatfilter").setExecutor(new FilterCommand());
        getCommand("localchat").setExecutor(new LocalChatCommand());
        getCommand("localbroadcast").setExecutor(new LocalBroadcastCommand());
        getLogger().info("DiscovSuite is now enabled!");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        for (Player player : Bukkit.getOnlinePlayers()) {
            boardManager.remove(player);
        }
        getLogger().info("DiscovSuite is now disabled!");
    }

    public BoardManager getBoardManager() {
        return boardManager;
    }

    public List<String> getDisabledWorlds() {
        if (getConfig().getStringList("punch-disabled-worlds") != null) {
            return getConfig().getStringList("punch-disabled-worlds");
        } else {
            return new ArrayList<>();
        }
    }

    public DatabaseUtil getDatabase() {
        return database;
    }

    public ChatFilter getChatFilter() {
        return chatFilter;
    }

    public NicknameManager getNicknameManager() {
        return nicknameManager;
    }

    public ChannelManager getChannelManager() {
        return channelManager;
    }

    public User getLuckPermsUser(Player player) {
        if (getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            return LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
        }
        return null;
    }

    public boolean getPlaceholderSupport() {
        return getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    public static DiscovSuite getInstance() {
        return instance;
    }
}
