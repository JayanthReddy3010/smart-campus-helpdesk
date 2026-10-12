package com.smartcampus.helpdesk.dao;

import com.smartcampus.helpdesk.model.User;
import com.smartcampus.helpdesk.model.UserRole;
import com.smartcampus.helpdesk.util.DatabaseConnection;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDAOTest {

    private final UserDAO userDAO = new UserDAO();

    @BeforeEach
    void verifyDatabaseAvailable() {
        Assumptions.assumeTrue(DatabaseConnection.isConfigured(),
                "Database configuration is not available; skipping DB test.");
    }

    @Test
    void findOrCreateDemoUserProvisionsPersistedUserWithValidId() throws SQLException {
        User user = userDAO.findOrCreateDemoUser("unit.test.student@campus.local", UserRole.STUDENT);

        assertNotNull(user);
        assertNotNull(user.getUserId(), "Persisted user ID must not be null");
        assertTrue(user.getUserId() > 0, "Persisted user ID must be positive and non-zero");
        assertEquals("unit.test.student@campus.local", user.getEmail());
        assertEquals(UserRole.STUDENT, user.getRole());

        // Repeated lookup should return the existing record without duplicate insertion
        User repeated = userDAO.findOrCreateDemoUser("unit.test.student@campus.local", UserRole.STUDENT);
        assertNotNull(repeated);
        assertEquals(user.getUserId(), repeated.getUserId(), "Repeated lookup must return same user ID");
    }

    @Test
    void findByEmailReturnsPersistedUser() throws SQLException {
        User user = userDAO.findOrCreateDemoUser("lookup.test@campus.local", UserRole.STUDENT);
        assertNotNull(user);

        User found = userDAO.findByEmail("lookup.test@campus.local");
        assertNotNull(found);
        assertEquals(user.getUserId(), found.getUserId());
    }
}
