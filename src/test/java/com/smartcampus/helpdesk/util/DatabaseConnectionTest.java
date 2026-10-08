package com.smartcampus.helpdesk.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

class DatabaseConnectionTest {

    @Test
    void connectsToMySqlAndExecutesQuery() throws SQLException {
        Assumptions.assumeTrue(
                DatabaseConnection.isConfigured(),
                "Database configuration is not available; set DB_URL and DB_USERNAME or add db.properties.");

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT 1");
             ResultSet resultSet = statement.executeQuery()) {
            assertFalse(connection.isClosed());
            assertEquals(1, resultSet.next() ? resultSet.getInt(1) : 0);
        }
    }
}
