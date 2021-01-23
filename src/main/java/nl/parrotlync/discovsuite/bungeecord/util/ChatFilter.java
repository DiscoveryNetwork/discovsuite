package nl.parrotlync.discovsuite.bungeecord.util;

import net.md_5.bungee.api.ProxyServer;
import nl.parrotlync.discovsuite.bungeecord.DiscovSuite;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatFilter {
    List<String> bannedWords = new ArrayList<>();
    List<String> excludedWords = new ArrayList<>();
    HashMap<String, String> replacements = new HashMap<>();

    public boolean hasForbidden(String message) {
        String[] words = message.split(" ");
        List<String> matches = new ArrayList<>();
        List<String> finalMatches = new ArrayList<>();

        for (String word : words) {
            if (bannedWords.stream().anyMatch(word.toLowerCase()::contains)) {
                matches.add(word);
            }
        }

        for (String match : matches) {
            if (excludedWords.stream().noneMatch(match.toLowerCase()::contains)) {
                finalMatches.add(match);
            }
        }

        return !finalMatches.isEmpty();
    }

    public String parseReplacements(String message) {
        for (String match : replacements.keySet()) {
            if (message.toLowerCase().contains(match)) {
                message = message.replaceAll("(?i)" + match, replacements.get(match));
            }
        }
        return message;
    }

    public void addBannedWord(String match) {
        bannedWords.add(match);
        ProxyServer.getInstance().getScheduler().runAsync(DiscovSuite.getInstance(), () -> {
            try {
                DiscovSuite.getInstance().getDatabase().addBannedWord(match);
            } catch (Exception e) {
                DiscovSuite.getInstance().getLogger().warning("Something went wrong while updating the ChatFilter.");
                e.printStackTrace();
            }
        });
    }

    public void addExcludedWord(String match) {
        excludedWords.add(match);
        ProxyServer.getInstance().getScheduler().runAsync(DiscovSuite.getInstance(), () -> {
            try {
                DiscovSuite.getInstance().getDatabase().addExcludedWord(match);
            } catch (Exception e) {
                DiscovSuite.getInstance().getLogger().warning("Something went wrong while updating the ChatFilter.");
                e.printStackTrace();
            }
        });
    }

    public void fetchBannedWords() {
        ProxyServer.getInstance().getScheduler().runAsync(DiscovSuite.getInstance(), () -> {
            try {
                List<String> bannedWords = DiscovSuite.getInstance().getDatabase().getBannedWords();
                if (bannedWords != null && !bannedWords.isEmpty()) {
                    this.bannedWords = bannedWords;
                    DiscovSuite.getInstance().getLogger().info("Succesfully retrieved " + bannedWords.size() + " banned words from the database!");
                }
            } catch (Exception e) {
                DiscovSuite.getInstance().getLogger().warning("Something went wrong while fetching banned words from the database.");
                e.printStackTrace();
            }
        });
    }

    public void fetchExcludedWords() {
        ProxyServer.getInstance().getScheduler().runAsync(DiscovSuite.getInstance(), () -> {
            try {
                List<String> excludedWords = DiscovSuite.getInstance().getDatabase().getExcludedWords();
                if (excludedWords != null && !excludedWords.isEmpty()) {
                    this.excludedWords = excludedWords;
                    DiscovSuite.getInstance().getLogger().info("Succesfully retrieved " + excludedWords.size() + " excluded words from the database!");
                }
            } catch (Exception e) {
                DiscovSuite.getInstance().getLogger().warning("Something went wrong while fetching excluded words from the database.");
                e.printStackTrace();
            }
        });
    }
}
