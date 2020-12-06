package nl.parrotlync.discovsuite;

import nl.parrotlync.discovsuite.command.FlyCommand;
import nl.parrotlync.discovsuite.command.SpeedCommand;
import nl.parrotlync.discovsuite.listener.CauldronListener;
import nl.parrotlync.discovsuite.listener.EventListener;
import nl.parrotlync.discovsuite.listener.JoinQuitListener;
import nl.parrotlync.discovsuite.listener.WaterListener;
import nl.parrotlync.discovsuite.scoreboard.BoardManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public class DiscovSuite extends JavaPlugin {
    private static DiscovSuite instance;
    private BoardManager boardManager;

    public DiscovSuite() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Join & Quit messages
        if (getConfig().getBoolean("disable-join-quit-messages")) {
            getLogger().info("Activating module: DISABLE_JOIN_QUIT");
            getServer().getPluginManager().registerEvents(new JoinQuitListener(), this);
        }

        // Water teleport
        if (getConfig().getBoolean("enable-water-teleport")) {
            getLogger().info("Activating module: WATER_TELEPORT");
            getServer().getPluginManager().registerEvents(new WaterListener(), this);
        }

        // Cauldron bins
        if (getConfig().getBoolean("enable-cauldron-bins")) {
            getLogger().info("Activating module: CAULDRON_BINS");
            getServer().getPluginManager().registerEvents(new CauldronListener(), this);
        }

        // Scoreboard
        HashMap<Integer, List<String>> lines = new HashMap<>();
        ConfigurationSection scoreboardContent = getConfig().getConfigurationSection("scoreboard-content");
        for (String key : scoreboardContent.getKeys(false)) {
            lines.put(Integer.parseInt(key), scoreboardContent.getStringList(key));
        }
        boardManager = new BoardManager(getConfig().getString("scoreboard-settings.title"), lines);
        for (Player player : Bukkit.getOnlinePlayers()) {
            boardManager.init(player);
        }

        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("speed").setExecutor(new SpeedCommand());
        getLogger().info("DiscovSuite is now enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("DiscovSuite is now disabled!");
    }

    public BoardManager getBoardManager() {
        return boardManager;
    }

    public static DiscovSuite getInstance() {
        return instance;
    }
}
