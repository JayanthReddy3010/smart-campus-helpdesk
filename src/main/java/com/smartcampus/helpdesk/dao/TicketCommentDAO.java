package com.smartcampus.helpdesk.dao;

import com.smartcampus.helpdesk.model.TicketComment;
import com.smartcampus.helpdesk.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TicketCommentDAO {

    private static final String BASE_SELECT = "SELECT comment_id, ticket_id, user_id, comment, created_at FROM ticket_comments";

    public TicketComment create(TicketComment comment) throws SQLException {
        String sql = "INSERT INTO ticket_comments (ticket_id, user_id, comment) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, comment.getTicketId());
            statement.setInt(2, comment.getUserId());
            statement.setString(3, comment.getComment());
            statement.executeUpdate();
            comment.setCommentId(generatedId(statement));
            return comment;
        }
    }

    public TicketComment findById(Integer commentId) throws SQLException {
        String sql = BASE_SELECT + " WHERE comment_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, commentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRow(resultSet) : null;
            }
        }
    }

    public List<TicketComment> findByTicketId(Integer ticketId) throws SQLException {
        List<TicketComment> comments = new ArrayList<>();
        String sql = BASE_SELECT + " WHERE ticket_id = ? ORDER BY created_at ASC, comment_id ASC";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ticketId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    comments.add(mapRow(resultSet));
                }
            }
        }
        return comments;
    }

    public boolean delete(Integer commentId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM ticket_comments WHERE comment_id = ?")) {
            statement.setInt(1, commentId);
            return statement.executeUpdate() == 1;
        }
    }

    private TicketComment mapRow(ResultSet resultSet) throws SQLException {
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        return new TicketComment(
                resultSet.getInt("comment_id"),
                resultSet.getInt("ticket_id"),
                resultSet.getInt("user_id"),
                resultSet.getString("comment"),
                createdAt == null ? null : createdAt.toLocalDateTime());
    }

    private Integer generatedId(PreparedStatement statement) throws SQLException {
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (!keys.next()) {
                throw new SQLException("Insert did not return a generated comment ID.");
            }
            return keys.getInt(1);
        }
    }
}
