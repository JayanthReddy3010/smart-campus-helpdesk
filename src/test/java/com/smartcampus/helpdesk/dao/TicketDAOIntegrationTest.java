package com.smartcampus.helpdesk.dao;

import com.smartcampus.helpdesk.model.Category;
import com.smartcampus.helpdesk.model.Ticket;
import com.smartcampus.helpdesk.model.TicketComment;
import com.smartcampus.helpdesk.model.TicketHistory;
import com.smartcampus.helpdesk.model.TicketPriority;
import com.smartcampus.helpdesk.model.TicketStatus;
import com.smartcampus.helpdesk.model.User;
import com.smartcampus.helpdesk.model.UserRole;
import com.smartcampus.helpdesk.util.DatabaseConnection;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TicketDAOIntegrationTest {

    private final TicketDAO ticketDAO = new TicketDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final UserDAO userDAO = new UserDAO();
    private final TicketCommentDAO commentDAO = new TicketCommentDAO();
    private final TicketHistoryDAO historyDAO = new TicketHistoryDAO();

    @BeforeEach
    void verifyDatabaseAvailable() {
        Assumptions.assumeTrue(DatabaseConnection.isConfigured(),
                "Database configuration is not available; skipping DB test.");
    }

    @Test
    void createsTicketWithInitialHistoryAndPersistsInDatabase() throws SQLException {
        // 1. Ensure active categories exist
        List<Category> categories = categoryDAO.findActive();
        assertFalse(categories.isEmpty(), "Active categories must exist in database");
        Category category = categories.get(0);

        // 2. Provision test user
        User user = userDAO.findOrCreateDemoUser("ticket.test.student@campus.local", UserRole.STUDENT);
        assertNotNull(user.getUserId());
        assertTrue(user.getUserId() > 0);

        // 3. Create ticket with initial history
        Ticket newTicket = new Ticket(null, user.getUserId(), category.getCategoryId(),
                "Integration Test Subject", "Detailed integration test description for campus support.",
                "Lab Room 101", TicketPriority.HIGH, null, null, null, null, null, null);

        Ticket created = ticketDAO.createWithInitialHistory(newTicket, user.getUserId());
        assertNotNull(created.getTicketId(), "Generated ticket ID must not be null");
        assertTrue(created.getTicketId() > 0, "Generated ticket ID must be positive");
        assertEquals(TicketStatus.OPEN, created.getStatus());

        Integer ticketId = created.getTicketId();

        try {
            // 4. Verify ticket can be found by ID
            Ticket found = ticketDAO.findById(ticketId);
            assertNotNull(found);
            assertEquals("Integration Test Subject", found.getSubject());
            assertEquals(user.getUserId(), found.getUserId());
            assertEquals(category.getCategoryId(), found.getCategoryId());

            // 5. Verify initial history was recorded
            List<TicketHistory> history = historyDAO.findByTicketId(ticketId);
            assertFalse(history.isEmpty(), "Initial history must be recorded");
            TicketHistory initialRecord = history.get(0);
            assertEquals(TicketStatus.OPEN, initialRecord.getNewStatus());
            assertEquals(user.getUserId(), initialRecord.getChangedBy());

            // 6. Test adding a comment
            TicketComment comment = new TicketComment(null, ticketId, user.getUserId(), "Test comment", null);
            TicketComment savedComment = commentDAO.create(comment);
            assertNotNull(savedComment.getCommentId());
            List<TicketComment> comments = commentDAO.findByTicketId(ticketId);
            assertFalse(comments.isEmpty());
            assertEquals("Test comment", comments.get(0).getComment());

            // 7. Test staff assignment and status workflow
            User staffUser = userDAO.findOrCreateDemoUser("support.staff@campus.local", UserRole.STAFF);
            boolean assigned = ticketDAO.assignToStaff(ticketId, staffUser.getUserId(), user.getUserId());
            assertTrue(assigned, "Ticket assignment must succeed");

            Ticket assignedTicket = ticketDAO.findById(ticketId);
            assertEquals(TicketStatus.ASSIGNED, assignedTicket.getStatus());
            assertEquals(staffUser.getUserId(), assignedTicket.getAssignedTo());

            // 8. Test transition to IN_PROGRESS
            boolean inProgress = ticketDAO.updateStatusWithHistory(ticketId, TicketStatus.IN_PROGRESS,
                    staffUser.getUserId(), "Work commenced");
            assertTrue(inProgress);
            Ticket inProgressTicket = ticketDAO.findById(ticketId);
            assertEquals(TicketStatus.IN_PROGRESS, inProgressTicket.getStatus());
        } finally {
            // Clean up test ticket to leave database tidy
            ticketDAO.delete(ticketId);
        }
    }
}
