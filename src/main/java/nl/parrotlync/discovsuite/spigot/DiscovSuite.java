package nl.parrotlync.discovsuite.spigot;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
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
import java.util.Objects;
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
    private final VanishManager vanishManager = new VanishManager();
    private final AuthUtil authUtil = new AuthUtil();

    public DiscovSuite() {
        instance = this;
    }

    @Override
    public void onEnable() {
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
            assert scoreboardContent != null;
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
            new LuckPermsListener();
        }

        // Commands & Listeners
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        Objects.requireNonNull(getCommand("fly")).setExecutor(new FlyCommand());
        Objects.requireNonNull(getCommand("speed")).setExecutor(new SpeedCommand());
        Objects.requireNonNull(getCommand("clearinventory")).setExecutor(new ClearInventoryCommand());
        Objects.requireNonNull(getCommand("staffchat")).setExecutor(new StaffChatCommand());
        Objects.requireNonNull(getCommand("managementchat")).setExecutor(new ManagementChatCommand());
        Objects.requireNonNull(getCommand("staffalert")).setExecutor(new StaffAlertCommand());
        Objects.requireNonNull(getCommand("nick")).setExecutor(new NicknameCommand());
        Objects.requireNonNull(getCommand("localchat")).setExecutor(new LocalChatCommand());
        Objects.requireNonNull(getCommand("localbroadcast")).setExecutor(new LocalBroadcastCommand());
        Objects.requireNonNull(getCommand("warp")).setExecutor(new WarpCommand());
        Objects.requireNonNull(getCommand("setwarp")).setExecutor(new SetWarpCommand());
        Objects.requireNonNull(getCommand("delwarp")).setExecutor(new DelWarpCommand());
        Objects.requireNonNull(getCommand("nearby")).setExecutor(new NearbyCommand());
        Objects.requireNonNull(getCommand("warps")).setExecutor(new WarpsCommand());
        Objects.requireNonNull(getCommand("randomwarp")).setExecutor(new RandomWarpCommand());
        Objects.requireNonNull(getCommand("wake")).setExecutor(new WakeCommand());
        Objects.requireNonNull(getCommand("playertime")).setExecutor(new PlayerTimeCommand());
        Objects.requireNonNull(getCommand("build")).setExecutor(new BuildCommand());
        Objects.requireNonNull(getCommand("vanish")).setExecutor(new VanishCommand());
        getLogger().info("DiscovSuite is now enabled!");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        for (Player player : Bukkit.getOnlinePlayers()) {
            boardManager.remove(player);
            vanishManager.showPlayer(player);
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

    public VanishManager getVanishManager() {
        return vanishManager;
    }

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
            assert in != null;
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
