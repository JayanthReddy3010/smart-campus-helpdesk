package com.smartcampus.helpdesk.dao;

import com.smartcampus.helpdesk.model.Category;
import com.smartcampus.helpdesk.util.DatabaseConnection;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryDAOTest {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @BeforeEach
    void verifyDatabaseAvailable() {
        Assumptions.assumeTrue(DatabaseConnection.isConfigured(),
                "Database configuration is not available; skipping DB test.");
    }

    @Test
    void findsActiveCategoriesSuccessfully() throws SQLException {
        List<Category> active = categoryDAO.findActive();
        assertNotNull(active);
        assertFalse(active.isEmpty(), "Active categories should be seeded and retrieved");

        for (Category category : active) {
            assertTrue(category.isActive());
            assertNotNull(category.getCategoryId());
            assertNotNull(category.getCategoryName());
        }
    }

    @Test
    void findsCategoryById() throws SQLException {
        List<Category> active = categoryDAO.findActive();
        assertFalse(active.isEmpty());

        Category first = active.get(0);
        Category found = categoryDAO.findById(first.getCategoryId());
        assertNotNull(found);
        org.junit.jupiter.api.Assertions.assertEquals(first.getCategoryName(), found.getCategoryName());
    }
}
