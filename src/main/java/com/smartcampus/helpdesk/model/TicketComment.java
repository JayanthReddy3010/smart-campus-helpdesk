package com.smartcampus.helpdesk.model;

import java.time.LocalDateTime;

public class TicketComment {

    private Integer commentId;
    private Integer ticketId;
    private Integer userId;
    private String comment;
    private LocalDateTime createdAt;

    public TicketComment() {
    }

    public TicketComment(Integer commentId, Integer ticketId, Integer userId,
                          String comment, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.ticketId = ticketId;
        this.userId = userId;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
