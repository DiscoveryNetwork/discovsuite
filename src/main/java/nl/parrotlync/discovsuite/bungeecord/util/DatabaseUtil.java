package nl.parrotlync.discovsuite.bungeecord.util;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import nl.parrotlync.discovsuite.common.MySQLDatabaseConnector;
import org.ocpsoft.prettytime.PrettyTime;

import java.sql.*;
import java.util.Calendar;
import java.util.Date;

public class DatabaseUtil extends MySQLDatabaseConnector {

    public DatabaseUtil(String host, String username, String password, String database) {
        super(host, username, password, database);
    }

    public void createTables() throws SQLException, ClassNotFoundException {
        connect();
        Statement playerStatement = connection.createStatement();
        playerStatement.execute("CREATE TABLE IF NOT EXISTS playermonitor_players (\n" +
                "    UUID        varchar(36) default '' not null\n" +
                "        primary key,\n" +
                "    name        varchar(16)            null,\n" +
                "    first_login timestamp              null,\n" +
                "    last_seen   timestamp              null,\n" +
                "    ip_address  varchar(16)            null\n" +
                ");");
        Statement sessionStatement = connection.createStatement();
        sessionStatement.execute("CREATE TABLE IF NOT EXISTS playermonitor_sessions\n" +
                "(\n" +
                "    ID          int auto_increment\n" +
                "        primary key,\n" +
                "    player      varchar(36) null,\n" +
                "    time_login  timestamp   null,\n" +
                "    time_logout timestamp   null,\n" +
                "    seconds     int         null\n" +
                ");");
    }

    public void savePlayer(ProxiedPlayer player, Date login) throws SQLException, ClassNotFoundException {
        connect();
        Statement statement = connection.createStatement();
        String address = player.getSocketAddress().toString().replace("/", "").split(":")[0];
        statement.execute("INSERT INTO playermonitor_players (UUID, `name`, first_login, ip_address) VALUES ('" + player.getUniqueId().toString() + "', '" + player.getName() + "', '" + new Timestamp(login.getTime()) + "', '" + address + "') ON DUPLICATE KEY UPDATE `name` = '" + player.getName() + "', ip_address = '" + address + "'");
    }

    public void saveSession(ProxiedPlayer player, Date login, Date logout) throws SQLException, ClassNotFoundException {
        connect();
        Statement statement = connection.createStatement();
        long seconds = logout.toInstant().getEpochSecond() - login.toInstant().getEpochSecond();
        statement.execute("INSERT INTO playermonitor_sessions (player, time_login, time_logout, seconds) VALUES ('" + player.getUniqueId().toString() + "', '" + new Timestamp(login.getTime()) + "', '" + new Timestamp(logout.getTime()) + "', '" + seconds + "')");
        saveLastLogout(player, logout);
    }

    private void saveLastLogout(ProxiedPlayer player, Date logout) throws SQLException, ClassNotFoundException {
        connect();
        Statement statement = connection.createStatement();
        statement.execute("UPDATE playermonitor_players SET last_seen = '" + new Timestamp(logout.getTime()) + "' WHERE UUID = '" + player.getUniqueId().toString() + "'");
    }

    public String getSeen(String player) throws SQLException, ClassNotFoundException {
        connect();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT last_seen FROM playermonitor_players WHERE playermonitor_players.name = '" + player + "'");
        if (result.next()) {
            return new PrettyTime().format(result.getTimestamp("last_seen"));
        }
        return null;
    }

    public int getSecondsThisWeek(String player) throws SQLException, ClassNotFoundException {
        connect();
        Statement statement = connection.createStatement();
        int seconds = 0;
        ResultSet result = statement.executeQuery("SELECT seconds FROM playermonitor_sessions, playermonitor_players WHERE player = UUID AND `name` = '" + player + "' AND time_login > '" + getFirstDayOfWeek() + "'");
        while (result.next()) {
            seconds += result.getInt("seconds");
        }
        return seconds;
    }

    public int getSeconds(String player) throws SQLException, ClassNotFoundException {
        connect();
        Statement statement = connection.createStatement();
        int seconds = 0;
        ResultSet result = statement.executeQuery("SELECT seconds FROM playermonitor_sessions, playermonitor_players WHERE player = UUID AND `name` = '" + player + "'");
        while (result.next()) {
            seconds += result.getInt("seconds");
        }
        return seconds;
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
