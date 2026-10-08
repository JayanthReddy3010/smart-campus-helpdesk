package com.smartcampus.helpdesk.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Provides JDBC connections using externalized database configuration. */
public final class DatabaseConnection {

    private static final String CONFIGURATION_FILE = "db.properties";
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";

    private DatabaseConnection() {
        // Utility class.
    }

    /**
     * Opens a new JDBC connection for the caller. The caller owns the connection
     * and must close it, preferably with try-with-resources.
     *
     * @return an open JDBC connection
     * @throws SQLException if the JDBC driver cannot establish the connection
     * @throws IllegalStateException if required configuration is missing
     */
    public static Connection getConnection() throws SQLException {
        Properties configuration = loadConfiguration();
        String url = requiredValue(configuration, URL_KEY);
        String username = requiredValue(configuration, USERNAME_KEY);
        String password = configuration.getProperty(PASSWORD_KEY, "");

        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Indicates whether a usable URL and username are configured without opening
     * a database connection.
     *
     * @return true when the connection settings are present
     */
    public static boolean isConfigured() {
        try {
            Properties configuration = loadConfiguration();
            return hasValue(configuration, URL_KEY) && hasValue(configuration, USERNAME_KEY);
        } catch (IllegalStateException exception) {
            return false;
        }
    }

    private static Properties loadConfiguration() {
        Properties configuration = new Properties();

        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream(CONFIGURATION_FILE)) {
            if (input != null) {
                configuration.load(input);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read database configuration.", exception);
        }

        applyOverride(configuration, URL_KEY, "DB_URL");
        applyOverride(configuration, USERNAME_KEY, "DB_USERNAME");
        applyOverride(configuration, PASSWORD_KEY, "DB_PASSWORD");

        return configuration;
    }

    private static void applyOverride(Properties configuration, String propertyKey, String environmentKey) {
        String systemValue = System.getProperty(propertyKey);
        if (hasText(systemValue)) {
            configuration.setProperty(propertyKey, systemValue.trim());
            return;
        }

        String environmentValue = System.getenv(environmentKey);
        if (hasText(environmentValue)) {
            configuration.setProperty(propertyKey, environmentValue.trim());
        }
    }

    private static String requiredValue(Properties configuration, String key) {
        String value = configuration.getProperty(key);
        if (!hasText(value)) {
            throw new IllegalStateException(
                    "Missing database configuration: " + key + ". Provide db.properties or the matching environment variable.");
        }
        return value.trim();
    }

    private static boolean hasValue(Properties configuration, String key) {
        return hasText(configuration.getProperty(key));
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
