package nl.parrotlync.discovsuite.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class MySQLDatabaseConnector {
    private final String host, database, username, password;
    protected Connection connection;

    public MySQLDatabaseConnector(String host, String username, String password, String database) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public void connect() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) { return; }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) { return; }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d/%s?autoReconnect=true&useSSL=false", host, 3306, database), username, password);
        }
    }
}
