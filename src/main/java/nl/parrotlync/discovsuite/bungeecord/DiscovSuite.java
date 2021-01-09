package nl.parrotlync.discovsuite.bungeecord;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import nl.parrotlync.discovsuite.bungeecord.command.AcceptCommand;
import nl.parrotlync.discovsuite.bungeecord.listener.PlayerListener;
import nl.parrotlync.discovsuite.bungeecord.listener.ProtocolListener;
import nl.parrotlync.discovsuite.bungeecord.util.DatabaseUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class DiscovSuite extends Plugin {
    private static DiscovSuite instance;
    private final HashMap<UUID, Date> sessions;
    private DatabaseUtil database;

    public DiscovSuite() {
        instance = this;
        sessions = new HashMap<>();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Database
        this.database = new DatabaseUtil(getConfig().getString("database.host"), getConfig().getString("database.username"),
                getConfig().getString("database.password"), getConfig().getString("database.database"));
        getProxy().getScheduler().runAsync(this, () -> {
           try {
               database.connect();
               database.createTables();
           } catch (Exception e) {
               getLogger().severe("Something went wrong while trying to establish a database connection!");
               e.printStackTrace();
           }
        });

        // Listeners & Commands
        getProxy().getPluginManager().registerListener(this, new ProtocolListener());
        getProxy().getPluginManager().registerListener(this, new PlayerListener());
        getProxy().getPluginManager().registerCommand(this, new AcceptCommand());
    }

    @Override
    public void onDisable() {

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

    public static DiscovSuite getInstance() {
        return instance;
    }
}
