package dev.math3w.playerstash.databases;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDatabase implements SQLDatabase {
    private final String hostname;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    private Connection connection;

    public MySQLDatabase(String hostname, int port, String database, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        connect();
        if (!isConnected()) {
            Bukkit.getLogger().severe("Cannot connect to mysql database! Please check the databases config");
        }
    }

    private void connect() {
        if (isConnected()) return;

        String url = "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?autoReconnect=true";

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public Type getType() {
        return Type.MYSQL;
    }
}
