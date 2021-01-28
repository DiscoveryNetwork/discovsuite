package nl.parrotlync.discovsuite.bungeecord.util;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import nl.parrotlync.discovsuite.common.MySQLDatabaseConnector;
import org.ocpsoft.prettytime.PrettyTime;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class DatabaseUtil extends MySQLDatabaseConnector {

    public DatabaseUtil(String host, String username, String password, String database) {
        super(host, username, password, database);
    }

    public void createTables() throws SQLException, ClassNotFoundException {
        connect();
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS dsuite_players (\n" +
                "    UUID        varchar(36) default '' not null\n" +
                "        primary key,\n" +
                "    name        varchar(16)            null,\n" +
                "    first_login timestamp              null,\n" +
                "    last_seen   timestamp              null,\n" +
                "    ip_address  varchar(16)            null\n" +
                ");");
        statement.execute("CREATE TABLE IF NOT EXISTS dsuite_sessions\n" +
                "(\n" +
                "    ID          int auto_increment\n" +
                "        primary key,\n" +
                "    player      varchar(36) null,\n" +
                "    time_login  timestamp   null,\n" +
                "    time_logout timestamp   null,\n" +
                "    seconds     int         null\n" +
                ");");
        statement.execute("CREATE TABLE IF NOT EXISTS dsuite_chat_banned\n" +
                "(\n" +
                "    ID      int auto_increment\n" +
                "        primary key,\n" +
                "    `match` varchar(50) null\n" +
                ");");
        statement.execute("CREATE TABLE IF NOT EXISTS dsuite_chat_exclusions\n" +
                "(\n" +
                "    ID      int auto_increment\n" +
                "        primary key,\n" +
                "    `match` varchar(50) null\n" +
                ");");
        statement.execute("create table dsuite_chat_replacements\n" +
                "(\n" +
                "    ID          int auto_increment\n" +
                "        primary key,\n" +
                "    `match`     varchar(50) null,\n" +
                "    replacement varchar(50) null\n" +
                ");");
    }

    public void savePlayer(ProxiedPlayer player, Date login) throws SQLException, ClassNotFoundException {
        connect();
        String address = player.getSocketAddress().toString().replace("/", "").split(":")[0];
        PreparedStatement statement = connection.prepareStatement("INSERT INTO dsuite_players (UUID, `name`, first_login, ip_address) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE `name` = ?, ip_address = ?");
        statement.setString(1, player.getUniqueId().toString());
        statement.setString(2, player.getName());
        statement.setTimestamp(3, new Timestamp(login.getTime()));
        statement.setString(4, address);
        statement.setString(5, player.getName());
        statement.setString(6, address);
        statement.execute();
    }

    public void saveSession(ProxiedPlayer player, Date login, Date logout) throws SQLException, ClassNotFoundException {
        connect();
        long seconds = logout.toInstant().getEpochSecond() - login.toInstant().getEpochSecond();
        PreparedStatement statement = connection.prepareStatement("INSERT INTO dsuite_sessions (player, time_login, time_logout, seconds) VALUES (?, ?, ?, ?)");
        statement.setString(1, player.getUniqueId().toString());
        statement.setTimestamp(2, new Timestamp(login.getTime()));
        statement.setTimestamp(3, new Timestamp(logout.getTime()));
        statement.setString(4, String.valueOf(seconds));
        statement.execute();
        saveLastLogout(player, logout);
    }

    private void saveLastLogout(ProxiedPlayer player, Date logout) throws SQLException, ClassNotFoundException {
        connect();
        PreparedStatement statement = connection.prepareStatement("UPDATE dsuite_players SET last_seen = ? WHERE UUID = ?");
        statement.setTimestamp(1, new Timestamp(logout.getTime()));
        statement.setString(2, player.getUniqueId().toString());
        statement.execute();
    }

    public HashMap<UUID, String> getPlayers() throws SQLException, ClassNotFoundException {
        connect();
        HashMap<UUID, String> players = new HashMap<>();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT UUID, `name` FROM dsuite_players");
        while (result.next()) {
            players.put(UUID.fromString(result.getString("UUID")), result.getString("name"));
        }
        return players;
    }

    public String getSeen(String player) throws SQLException, ClassNotFoundException {
        connect();
        PreparedStatement statement = connection.prepareStatement("SELECT last_seen FROM dsuite_players WHERE dsuite_players.name = ?");
        statement.setString(1, player);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            return new PrettyTime().format(result.getTimestamp("last_seen"));
        }
        return null;
    }

    public int getSecondsThisWeek(String player) throws SQLException, ClassNotFoundException {
        connect();
        int seconds = 0;
        PreparedStatement statement = connection.prepareStatement("SELECT seconds FROM dsuite_sessions, dsuite_players WHERE player = UUID AND `name` = ? AND time_login > ?");
        statement.setString(1, player);
        statement.setTimestamp(2, getFirstDayOfWeek());
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            seconds += result.getInt("seconds");
        }
        return seconds;
    }

    public int getSeconds(String player) throws SQLException, ClassNotFoundException {
        connect();
        int seconds = 0;
        PreparedStatement statement = connection.prepareStatement("SELECT seconds FROM dsuite_sessions, dsuite_players WHERE player = UUID AND `name` = ?");
        statement.setString(1, player);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            seconds += result.getInt("seconds");
        }
        return seconds;
    }
    public List<String> getBannedWords() throws SQLException, ClassNotFoundException {
        connect();
        List<String> bannedWords = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM dsuite_chat_banned");
        while (result.next()) {
            bannedWords.add(result.getString("match"));
        }
        return bannedWords;
    }

    public List<String> getExcludedWords() throws SQLException, ClassNotFoundException {
        connect();
        List<String> excludedWords = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM dsuite_chat_exclusions");
        while (result.next()) {
            excludedWords.add(result.getString("match"));
        }
        return excludedWords;
    }

    public HashMap<String, String> getReplacements() throws SQLException, ClassNotFoundException {
        connect();
        HashMap<String, String> replacements = new HashMap<>();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM dsuite_chat_replacements");
        while (result.next()) {
            replacements.put(result.getString("match"), result.getString("replacement"));
        }
        return replacements;
    }

    public void addBannedWord(String match) throws SQLException, ClassNotFoundException {
        connect();
        PreparedStatement statement = connection.prepareStatement("REPLACE INTO dsuite_chat_banned (`match`) VALUES (?)");
        statement.setString(1, match);
        statement.execute();
    }

    public void addExcludedWord(String match) throws SQLException, ClassNotFoundException {
        connect();
        PreparedStatement statement = connection.prepareStatement("REPLACE INTO dsuite_chat_exclusions (`match`) VALUES (?)");
        statement.setString(1, match);
        statement.execute();
    }

    public void addReplacement(String match, String replacement) throws SQLException, ClassNotFoundException {
        connect();
        PreparedStatement statement = connection.prepareStatement("REPLACE INTO dsuite_chat_replacements (`match`, replacement) VALUES (?, ?)");
        statement.setString(1, match);
        statement.setString(2, replacement);
        statement.execute();
    }

    private Timestamp getFirstDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY,  0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);

        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return new Timestamp(calendar.getTime().getTime());
    }
}
