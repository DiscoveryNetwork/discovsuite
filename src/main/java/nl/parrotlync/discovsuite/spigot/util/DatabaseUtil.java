package nl.parrotlync.discovsuite.spigot.util;

import nl.parrotlync.discovsuite.common.MySQLDatabaseConnector;
import nl.parrotlync.discovsuite.spigot.model.Warp;
import nl.parrotlync.discovsuite.spigot.model.WarpGroup;
import org.bukkit.Location;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DatabaseUtil extends MySQLDatabaseConnector {

    public DatabaseUtil(String host, String username, String password, String database) {
        super(host, username, password, database);
    }

    public void createTables() throws SQLException, ClassNotFoundException {
        connect();
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS dsuite_nicknames\n" +
                "(\n" +
                "    player   varchar(36) default '' not null\n" +
                "        primary key,\n" +
                "    nickname varchar(36)            null\n" +
                ");");
        statement.execute("CREATE TABLE IF NOT EXISTS dsuite_warps\n" +
                "(\n" +
                "    ID     int auto_increment\n" +
                "        primary key,\n" +
                "    name   varchar(48) null,\n" +
                "    server varchar(48) null,\n" +
                "    `group`  varchar(48) null,\n" +
                "    world  varchar(48) null,\n" +
                "    x      double      null,\n" +
                "    y      double      null,\n" +
                "    z      double      null,\n" +
                "    yaw    float       null,\n" +
                "    pitch  float       null\n" +
                ");");
    }

    public String getNickname(UUID uuid) throws SQLException, ClassNotFoundException {
        connect();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM dsuite_nicknames WHERE player = ?");
        statement.setString(1, uuid.toString());
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            return result.getString("nickname");
        }
        return null;
    }

    public void setNickname(UUID uuid, String nickname) throws SQLException, ClassNotFoundException {
        connect();
        PreparedStatement statement = connection.prepareStatement("REPLACE INTO dsuite_nicknames (player, nickname) VALUES (?, ?)");
        statement.setString(1, uuid.toString());
        statement.setString(2, nickname);
        statement.execute();
    }

    public List<Warp> getWarps() throws SQLException, ClassNotFoundException {
        connect();
        List<Warp> warps = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM dsuite_warps");
        while (result.next()) {
            WarpGroup group = WarpGroup.valueOf(result.getString("group"));
            Warp warp = new Warp(result.getString("name"), result.getString("server"), group,
                    result.getString("world"), result.getDouble("x"), result.getDouble("y"),
                    result.getDouble("z"), result.getFloat("yaw"), result.getFloat("pitch"));
            warps.add(warp);
        }
        return warps;
    }

    public void addWarp(Warp warp) throws SQLException, ClassNotFoundException {
        connect();
        Location location = warp.getLocation();
        PreparedStatement statement = connection.prepareStatement("INSERT INTO dsuite_warps (name, server, `group`, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, warp.getName());
        statement.setString(2, warp.getServer());
        statement.setString(3, warp.getGroup().toString());
        statement.setString(4, Objects.requireNonNull(location.getWorld()).getName());
        statement.setDouble(5, location.getX());
        statement.setDouble(6, location.getY());
        statement.setDouble(7, location.getZ());
        statement.setFloat(8, location.getYaw());
        statement.setFloat(9, location.getPitch());
        statement.execute();
    }

    public void removeWarp(String name) throws SQLException, ClassNotFoundException {
        connect();
        PreparedStatement statement = connection.prepareStatement("DELETE FROM dsuite_warps WHERE name = ?");
        statement.setString(1, name);
        statement.execute();
    }
}
