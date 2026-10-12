package com.smartcampus.helpdesk.util;

import com.smartcampus.helpdesk.model.TicketStatus;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TicketStatusWorkflowTest {

    @Test
    void allowsExpectedForwardTransitions() {
        assertTrue(TicketStatusWorkflow.isAllowed(TicketStatus.OPEN, TicketStatus.ASSIGNED));
        assertTrue(TicketStatusWorkflow.isAllowed(TicketStatus.ASSIGNED, TicketStatus.IN_PROGRESS));
        assertTrue(TicketStatusWorkflow.isAllowed(TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED));
        assertTrue(TicketStatusWorkflow.isAllowed(TicketStatus.RESOLVED, TicketStatus.CLOSED));
    }

    @Test
    void rejectsDisallowedTransitions() {
        assertFalse(TicketStatusWorkflow.isAllowed(TicketStatus.OPEN, TicketStatus.RESOLVED));
        assertFalse(TicketStatusWorkflow.isAllowed(TicketStatus.OPEN, TicketStatus.CLOSED));
        assertFalse(TicketStatusWorkflow.isAllowed(TicketStatus.CLOSED, TicketStatus.OPEN));
        assertFalse(TicketStatusWorkflow.isAllowed(TicketStatus.RESOLVED, TicketStatus.OPEN));
        assertFalse(TicketStatusWorkflow.isAllowed(TicketStatus.IN_PROGRESS, TicketStatus.OPEN));
        assertFalse(TicketStatusWorkflow.isAllowed(null, TicketStatus.OPEN));
        assertFalse(TicketStatusWorkflow.isAllowed(TicketStatus.OPEN, null));
    }

    @Test
    void requireAllowedEnforcesWorkflowRules() {
        assertDoesNotThrow(() -> TicketStatusWorkflow.requireAllowed(TicketStatus.OPEN, TicketStatus.ASSIGNED));
        assertThrows(IllegalStateException.class,
                () -> TicketStatusWorkflow.requireAllowed(TicketStatus.OPEN, TicketStatus.CLOSED));
    }

    @Test
    void returnsAllowedNextStatusesSet() {
        Set<TicketStatus> openNext = TicketStatusWorkflow.allowedNextStatuses(TicketStatus.OPEN);
        assertEquals(1, openNext.size());
        assertTrue(openNext.contains(TicketStatus.ASSIGNED));

        Set<TicketStatus> closedNext = TicketStatusWorkflow.allowedNextStatuses(TicketStatus.CLOSED);
        assertTrue(closedNext.isEmpty());
    }
}
