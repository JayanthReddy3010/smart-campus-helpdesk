package com.smartcampus.helpdesk.util;

import com.smartcampus.helpdesk.model.TicketStatus;

import java.util.EnumSet;
import java.util.Set;

public final class TicketStatusWorkflow {

    private TicketStatusWorkflow() {
    }

    public static boolean isAllowed(TicketStatus current, TicketStatus next) {
        if (current == null || next == null) {
            return false;
        }
        return switch (current) {
            case OPEN -> next == TicketStatus.ASSIGNED;
            case ASSIGNED -> next == TicketStatus.IN_PROGRESS;
            case IN_PROGRESS -> next == TicketStatus.RESOLVED;
            case RESOLVED -> next == TicketStatus.CLOSED;
            case CLOSED -> false;
        };
    }

    public static Set<TicketStatus> allowedNextStatuses(TicketStatus current) {
        if (current == null || current == TicketStatus.CLOSED) {
            return EnumSet.noneOf(TicketStatus.class);
        }
        TicketStatus next = switch (current) {
            case OPEN -> TicketStatus.ASSIGNED;
            case ASSIGNED -> TicketStatus.IN_PROGRESS;
            case IN_PROGRESS -> TicketStatus.RESOLVED;
            case RESOLVED -> TicketStatus.CLOSED;
            case CLOSED -> null;
        };
        return EnumSet.of(next);
    }

    public static void requireAllowed(TicketStatus current, TicketStatus next) {
        if (!isAllowed(current, next)) {
            throw new IllegalStateException("Invalid ticket status transition.");
        }
    }
}
