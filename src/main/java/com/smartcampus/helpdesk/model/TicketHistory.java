package com.smartcampus.helpdesk.model;

import java.time.LocalDateTime;

public class TicketHistory {

    private Integer historyId;
    private Integer ticketId;
    private Integer changedBy;
    private TicketStatus oldStatus;
    private TicketStatus newStatus;
    private String actionDescription;
    private LocalDateTime changedAt;

    public TicketHistory() {
    }

    public TicketHistory(Integer historyId, Integer ticketId, Integer changedBy,
                         TicketStatus oldStatus, TicketStatus newStatus,
                         String actionDescription, LocalDateTime changedAt) {
        this.historyId = historyId;
        this.ticketId = ticketId;
        this.changedBy = changedBy;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.actionDescription = actionDescription;
        this.changedAt = changedAt;
    }

    public Integer getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Integer historyId) {
        this.historyId = historyId;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public Integer getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(Integer changedBy) {
        this.changedBy = changedBy;
    }

    public TicketStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(TicketStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public TicketStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(TicketStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}
