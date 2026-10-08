package com.itsc;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Reads MySQL connection settings from a local "db.properties" file
 * (or from DB_HOST / DB_PORT / DB_USER / DB_PASSWORD environment variables),
 * so no credentials are hard-coded in source control.
 */
public final class DbConfig {

    private final String host;
    private final String port;
    private final String user;
    private final String password;

    private DbConfig(String host, String port, String user, String password) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public static DbConfig load() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("db.properties")) {
            props.load(in);
        } catch (IOException e) {
            // No file found - fall back to environment variables.
        }

        String host = pick(props, "db.host", "DB_HOST", "localhost");
        String port = pick(props, "db.port", "DB_PORT", "3306");
        String user = pick(props, "db.user", "DB_USER", null);
        String password = pick(props, "db.password", "DB_PASSWORD", "");

        if (user == null) {
            throw new IllegalStateException(
                "No database user configured. Copy db.properties.example to "
                + "db.properties and fill in your MySQL username and password.");
        }
        return new DbConfig(host, port, user, password);
    }

    private static String pick(Properties props, String key, String envName, String def) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            value = System.getenv(envName);
        }
        return (value == null || value.isBlank()) ? def : value.trim();
    }

    private String base() {
        return "jdbc:mysql://" + host + ":" + port + "/";
    }

    private static final String OPTIONS = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    /** URL for the MySQL server (no database selected) - used to CREATE DATABASE. */
    public String serverUrl() {
        return base() + OPTIONS;
    }

    /** URL for a specific database. */
    public String databaseUrl(String databaseName) {
        return base() + databaseName + OPTIONS;
    }

    public String user() {
        return user;
    }

    public String password() {
        return password;
    }
}
