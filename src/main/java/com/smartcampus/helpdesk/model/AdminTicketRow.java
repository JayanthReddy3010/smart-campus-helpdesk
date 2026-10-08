package com.smartcampus.helpdesk.model;

import java.time.LocalDateTime;

public class AdminTicketRow {

    private final Integer ticketId;
    private final String subject;
    private final String categoryName;
    private final TicketPriority priority;
    private final TicketStatus status;
    private final LocalDateTime createdAt;
    private final String assignedStaffName;

    public AdminTicketRow(Integer ticketId, String subject, String categoryName,
                          TicketPriority priority, TicketStatus status,
                          LocalDateTime createdAt, String assignedStaffName) {
        this.ticketId = ticketId;
        this.subject = subject;
        this.categoryName = categoryName;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
        this.assignedStaffName = assignedStaffName;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public String getSubject() {
        return subject;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getAssignedStaffName() {
        return assignedStaffName;
    }
}
