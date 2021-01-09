package nl.parrotlync.discovsuite.spigot;

import nl.parrotlync.discovsuite.spigot.command.ClearInventoryCommand;
import nl.parrotlync.discovsuite.spigot.command.FlyCommand;
import nl.parrotlync.discovsuite.spigot.command.SpeedCommand;
import nl.parrotlync.discovsuite.spigot.listener.EventListener;
import nl.parrotlync.discovsuite.spigot.listener.ModuleListener;
import nl.parrotlync.discovsuite.spigot.scoreboard.BoardManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
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

        // Commands & Listeners
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getPluginManager().registerEvents(new ModuleListener(), this);
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("speed").setExecutor(new SpeedCommand());
        getCommand("clearinventory").setExecutor(new ClearInventoryCommand());
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

    public static DiscovSuite getInstance() {
        return instance;
    }
}
