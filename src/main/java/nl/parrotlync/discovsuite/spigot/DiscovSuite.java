package nl.parrotlync.discovsuite.spigot;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import nl.parrotlync.discovsuite.common.Beacon;
import nl.parrotlync.discovsuite.spigot.command.*;
import nl.parrotlync.discovsuite.spigot.listener.ChatListener;
import nl.parrotlync.discovsuite.spigot.listener.EventListener;
import nl.parrotlync.discovsuite.spigot.listener.LuckPermsListener;
import nl.parrotlync.discovsuite.spigot.listener.MessageListener;
import nl.parrotlync.discovsuite.spigot.manager.*;
import nl.parrotlync.discovsuite.spigot.scoreboard.BoardManager;
import nl.parrotlync.discovsuite.spigot.util.AuthUtil;
import nl.parrotlync.discovsuite.spigot.util.DatabaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class DiscovSuite extends JavaPlugin {
    private static DiscovSuite instance;
    private DatabaseUtil database;
    private BoardManager boardManager;
    private final NicknameManager nicknameManager = new NicknameManager();
    private final ChannelManager channelManager = new ChannelManager();
    private final WarpManager warpManager = new WarpManager();
    private final TeleportManager teleportManager = new TeleportManager();
    private final InventoryManager inventoryManager = new InventoryManager();
    private final AuthUtil authUtil = new AuthUtil();

    public DiscovSuite() {
        instance = this;
    }

    @Override
    public void onEnable() {
        if (!Beacon.authenticate()) { this.setEnabled(false); return; }
        saveDefaultConfig();
        updateConfig();
        reloadConfig();
        saveMessages();

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
        getServer().getMessenger().registerOutgoingPluginChannel(this, "dsuite:auth");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener());
        getServer().getMessenger().registerIncomingPluginChannel(this, "dsuite:auth", new MessageListener());
        getServer().getMessenger().registerIncomingPluginChannel(this, "dsuite:filter", new MessageListener());
        getServer().getMessenger().registerIncomingPluginChannel(this, "dsuite:mention", new MessageListener());
        getServer().getMessenger().registerIncomingPluginChannel(this, "dsuite:teleport", new MessageListener());

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

        // Managers
        warpManager.load();

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
        if (getServer().getPluginManager().isPluginEnabled("LuckPerms")) {
            LuckPermsListener luckPermsListener = new LuckPermsListener();
        }

        // Commands & Listeners
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("speed").setExecutor(new SpeedCommand());
        getCommand("clearinventory").setExecutor(new ClearInventoryCommand());
        getCommand("staffchat").setExecutor(new StaffChatCommand());
        getCommand("managementchat").setExecutor(new ManagementChatCommand());
        getCommand("staffalert").setExecutor(new StaffAlertCommand());
        getCommand("nick").setExecutor(new NicknameCommand());
        getCommand("localchat").setExecutor(new LocalChatCommand());
        getCommand("localbroadcast").setExecutor(new LocalBroadcastCommand());
        getCommand("warp").setExecutor(new WarpCommand());
        getCommand("setwarp").setExecutor(new SetWarpCommand());
        getCommand("delwarp").setExecutor(new DelWarpCommand());
        getCommand("nearby").setExecutor(new NearbyCommand());
        getCommand("warps").setExecutor(new WarpsCommand());
        getCommand("randomwarp").setExecutor(new RandomWarpCommand());
        getCommand("wake").setExecutor(new WakeCommand());
        getCommand("playertime").setExecutor(new PlayerTimeCommand());
        getCommand("build").setExecutor(new BuildCommand());
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

    public DatabaseUtil getDatabase() {
        return database;
    }

    public NicknameManager getNicknameManager() {
        return nicknameManager;
    }

    public ChannelManager getChannelManager() {
        return channelManager;
    }

    public WarpManager getWarpManager() { return warpManager; }

    public TeleportManager getTeleportManager() { return teleportManager; }

    public InventoryManager getInventoryManager() { return inventoryManager; }

    public AuthUtil getAuthUtil() { return authUtil; }

    public User getLuckPermsUser(Player player) {
        if (getServer().getPluginManager().isPluginEnabled("LuckPerms")) {
            return LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
        }
        return null;
    }

    public boolean getPlaceholderSupport() {
        return getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public YamlConfiguration getMessages() {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(new File(getDataFolder(), "messages.yml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return config;
    }

    private void saveMessages() {
        File file = new File(getDataFolder(), "messages.yml");
        try (InputStream in = getResource("messages-spigot.yml")) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateConfig() {
        YamlConfiguration resource = new YamlConfiguration();
        try (InputStream in = getResource("config.yml")) {
            resource.loadFromString(stringFromInputStream(in));
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String path : getConfig().getKeys(false)) {
            if (!resource.isSet(path)) {
                getConfig().set(path, null);
            }
        }

        for (String path : resource.getKeys(false)) {
            if (!getConfig().isSet((path))) {
                getConfig().set(path, resource.get(path));
            }
        }
        saveConfig();
    }

    private String stringFromInputStream(InputStream in) {
        return new Scanner(in).useDelimiter("\\A").next();
    }

    public static DiscovSuite getInstance() {
        return instance;
    }
}
