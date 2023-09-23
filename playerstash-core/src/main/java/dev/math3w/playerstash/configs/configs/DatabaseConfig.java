package dev.math3w.playerstash.configs.configs;

import dev.math3w.playerstash.configs.Config;
import dev.math3w.playerstash.configs.annotations.ConfigField;
import dev.math3w.playerstash.configs.annotations.ConfigInfo;
import dev.math3w.playerstash.databases.SQLDatabase;

@ConfigInfo(fileName = "database.yml")
public class DatabaseConfig implements Config {
    @ConfigField(path = "type")
    private String type = "file";
    @ConfigField(path = "mysql.hostname")
    private String hostname = "";
    @ConfigField(path = "mysql.port")
    private int port = 3306;
    @ConfigField(path = "mysql.database")
    private String database = "";
    @ConfigField(path = "mysql.username")
    private String username = "";
    @ConfigField(path = "mysql.password")
    private String password = "";

    public SQLDatabase.Type getSqlType() {
        for (SQLDatabase.Type type : SQLDatabase.Type.values()) {
            if (!this.type.equalsIgnoreCase(type.getConfigName())) continue;
            return type;
        }
        return SQLDatabase.Type.SQLITE;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}