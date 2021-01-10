package nl.parrotlync.discovsuite.spigot.util;

import nl.parrotlync.discovsuite.common.MySQLDatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseUtil extends MySQLDatabaseConnector {

    public DatabaseUtil(String host, String username, String password, String database) {
        super(host, username, password, database);
    }

    public void createTables() throws SQLException, ClassNotFoundException {
        connect();
        Statement bannedStatement = connection.createStatement();
        bannedStatement.execute("CREATE TABLE IF NOT EXISTS dchat_banned\n" +
                "(\n" +
                "    ID      int auto_increment\n" +
                "        primary key,\n" +
                "    `match` varchar(50) null\n" +
                ");");
        Statement exclusionStatement = connection.createStatement();
        exclusionStatement.execute("CREATE TABLE IF NOT EXISTS dchat_exclusions\n" +
                "(\n" +
                "    ID      int auto_increment\n" +
                "        primary key,\n" +
                "    `match` varchar(50) null\n" +
                ");");
        Statement nicknameStatement = connection.createStatement();
        nicknameStatement.execute("CREATE TABLE IF NOT EXISTS dchat_nicknames\n" +
                "(\n" +
                "    player   varchar(36) default '' not null\n" +
                "        primary key,\n" +
                "    nickname varchar(36)            null\n" +
                ");");
    }

    public List<String> getBannedWords() throws SQLException, ClassNotFoundException {
        connect();
        List<String> bannedWords = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM dchat_banned");
        while (result.next()) {
            bannedWords.add(result.getString("match"));
        }
        return bannedWords;
    }

    public List<String> getExcludedWords() throws SQLException, ClassNotFoundException {
        connect();
        List<String> excludedWords = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM dchat_exclusions");
        while (result.next()) {
            excludedWords.add(result.getString("match"));
        }
        return excludedWords;
    }

    public void addBannedWord(String match) throws SQLException, ClassNotFoundException {
        connect();
        Statement statement = connection.createStatement();
        statement.execute("REPLACE INTO dchat_banned (`match`) VALUES ('" + match + "')");
    }

    public void addExcludedWord(String match) throws SQLException, ClassNotFoundException {
        connect();
        Statement statement = connection.createStatement();
        statement.execute("REPLACE INTO dchat_exclusions (`match`) VALUES ('" + match + "')");
    }

    public String getNickname(UUID uuid) throws SQLException, ClassNotFoundException {
        connect();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM dchat_nicknames WHERE player = '" + uuid.toString() + "'");
        if (result.next()) {
            return result.getString("nickname");
        }
        return null;
    }

    public void setNickname(UUID uuid, String nickname) throws SQLException, ClassNotFoundException {
        connect();
        Statement statement = connection.createStatement();
        statement.execute("REPLACE INTO dchat_nicknames (player, nickname) VALUES ('" + uuid.toString() + "', '" + nickname + "')");
    }
}
