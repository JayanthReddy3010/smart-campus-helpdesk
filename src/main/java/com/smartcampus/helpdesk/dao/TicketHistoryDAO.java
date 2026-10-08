package com.smartcampus.helpdesk.dao;

import com.smartcampus.helpdesk.model.TicketHistory;
import com.smartcampus.helpdesk.model.TicketStatus;
import com.smartcampus.helpdesk.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TicketHistoryDAO {

    private static final String BASE_SELECT = "SELECT history_id, ticket_id, changed_by, old_status, new_status, action_description, changed_at FROM ticket_history";

    public TicketHistory create(TicketHistory history) throws SQLException {
        String sql = "INSERT INTO ticket_history (ticket_id, changed_by, old_status, new_status, action_description) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, history.getTicketId());
            statement.setInt(2, history.getChangedBy());
            if (history.getOldStatus() == null) {
                statement.setNull(3, java.sql.Types.VARCHAR);
            } else {
                statement.setString(3, history.getOldStatus().name());
            }
            statement.setString(4, history.getNewStatus().name());
            statement.setString(5, history.getActionDescription());
            statement.executeUpdate();
            history.setHistoryId(generatedId(statement));
            return history;
        }
    }

    public TicketHistory findById(Integer historyId) throws SQLException {
        String sql = BASE_SELECT + " WHERE history_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, historyId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRow(resultSet) : null;
            }
        }
    }

    public List<TicketHistory> findByTicketId(Integer ticketId) throws SQLException {
        List<TicketHistory> historyEntries = new ArrayList<>();
        String sql = BASE_SELECT + " WHERE ticket_id = ? ORDER BY changed_at ASC, history_id ASC";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ticketId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    historyEntries.add(mapRow(resultSet));
                }
            }
        }
        return historyEntries;
    }

    private TicketHistory mapRow(ResultSet resultSet) throws SQLException {
        Timestamp changedAt = resultSet.getTimestamp("changed_at");
        String oldStatus = resultSet.getString("old_status");
        return new TicketHistory(
                resultSet.getInt("history_id"),
                resultSet.getInt("ticket_id"),
                resultSet.getInt("changed_by"),
                oldStatus == null ? null : TicketStatus.valueOf(oldStatus),
                TicketStatus.valueOf(resultSet.getString("new_status")),
                resultSet.getString("action_description"),
                changedAt == null ? null : changedAt.toLocalDateTime());
    }

    private Integer generatedId(PreparedStatement statement) throws SQLException {
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (!keys.next()) {
                throw new SQLException("Insert did not return a generated history ID.");
            }
            return keys.getInt(1);
        }
    }
}
