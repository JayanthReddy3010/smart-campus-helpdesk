package com.smartcampus.helpdesk.dao;

import com.smartcampus.helpdesk.model.Ticket;
import com.smartcampus.helpdesk.model.AdminTicketRow;
import com.smartcampus.helpdesk.model.TicketPriority;
import com.smartcampus.helpdesk.model.TicketStatus;
import com.smartcampus.helpdesk.util.DatabaseConnection;
import com.smartcampus.helpdesk.util.TicketStatusWorkflow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    private static final String BASE_SELECT = "SELECT ticket_id, user_id, category_id, subject, description, location, priority, status, assigned_to, resolution, created_at, updated_at, resolved_at FROM tickets";

    public Ticket create(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets (user_id, category_id, subject, description, location, priority, status, assigned_to, resolution) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setTicketFields(statement, ticket);
            statement.executeUpdate();
            ticket.setTicketId(generatedId(statement));
            return ticket;
        }
    }

    /** Creates an open ticket and its initial audit record atomically. */
    public Ticket createWithInitialHistory(Ticket ticket, Integer changedBy) throws SQLException {
        String ticketSql = "INSERT INTO tickets (user_id, category_id, subject, description, location, priority, status, assigned_to, resolution) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String historySql = "INSERT INTO ticket_history (ticket_id, changed_by, old_status, new_status, action_description) VALUES (?, ?, ?, ?, ?)";
        ticket.setStatus(TicketStatus.OPEN);

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ticketStatement = connection.prepareStatement(ticketSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement historyStatement = connection.prepareStatement(historySql)) {
            connection.setAutoCommit(false);
            try {
                setTicketFields(ticketStatement, ticket);
                ticketStatement.executeUpdate();
                ticket.setTicketId(generatedId(ticketStatement));

                historyStatement.setInt(1, ticket.getTicketId());
                historyStatement.setInt(2, changedBy);
                historyStatement.setNull(3, java.sql.Types.VARCHAR);
                historyStatement.setString(4, TicketStatus.OPEN.name());
                historyStatement.setString(5, "Ticket created");
                historyStatement.executeUpdate();
                connection.commit();
                return ticket;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public Ticket findById(Integer ticketId) throws SQLException {
        String sql = BASE_SELECT + " WHERE ticket_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ticketId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRow(resultSet) : null;
            }
        }
    }

    public Ticket findByIdForUser(Integer ticketId, Integer userId) throws SQLException {
        String sql = BASE_SELECT + " WHERE ticket_id = ? AND user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ticketId);
            statement.setInt(2, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRow(resultSet) : null;
            }
        }
    }

    public List<Ticket> findAll() throws SQLException {
        return findMany(BASE_SELECT + " ORDER BY created_at DESC", null);
    }

    public List<Ticket> findByUserId(Integer userId) throws SQLException {
        return findMany(BASE_SELECT + " WHERE user_id = ? ORDER BY created_at DESC", userId);
    }

    public List<Ticket> findByUserIdFiltered(Integer userId, TicketStatus status,
                                              TicketPriority priority, Integer categoryId,
                                              String search) throws SQLException {
        StringBuilder sql = new StringBuilder(BASE_SELECT + " WHERE user_id = ?");
        List<Object> parameters = new ArrayList<>();
        parameters.add(userId);
        appendTicketFilters(sql, parameters, status, priority, categoryId, search, null);
        sql.append(" ORDER BY created_at DESC, ticket_id DESC");
        return findTickets(sql.toString(), parameters);
    }

    public List<Ticket> findRecentByUserId(Integer userId, int limit) throws SQLException {
        int safeLimit = Math.max(1, Math.min(limit, 20));
        List<Ticket> tickets = new ArrayList<>();
        String sql = BASE_SELECT + " WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, safeLimit);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tickets.add(mapRow(resultSet));
                }
            }
        }
        return tickets;
    }

    public int countByUserId(Integer userId) throws SQLException {
        return countByUserId(userId, null);
    }

    public int countByUserId(Integer userId, TicketStatus status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tickets WHERE user_id = ?";
        if (status != null) {
            sql += " AND status = ?";
        }
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            if (status != null) {
                statement.setString(2, status.name());
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }

    public int countAdminTickets() throws SQLException {
        return countAdminTickets(null, null, false);
    }

    public int countAdminTickets(TicketStatus status) throws SQLException {
        return countAdminTickets(status, null, false);
    }

    public int countAssignedAdminTickets() throws SQLException {
        return countAdminTickets(null, true, false);
    }

    public int countHighPriorityAdminTickets() throws SQLException {
        return countAdminTickets(null, null, true);
    }

    public List<AdminTicketRow> findAdminTickets(TicketStatus status, TicketPriority priority,
                                                  Integer categoryId, String search,
                                                  Integer assignedStaffId) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT t.ticket_id, t.subject, c.category_name, t.priority, t.status, "
                        + "t.created_at, assigned.name AS assigned_staff_name "
                        + "FROM tickets t JOIN categories c ON c.category_id = t.category_id "
                        + "LEFT JOIN users assigned ON assigned.user_id = t.assigned_to WHERE 1 = 1");
        List<Object> parameters = new ArrayList<>();
        appendTicketFilters(sql, parameters, status, priority, categoryId, search, assignedStaffId);
        sql.append(" ORDER BY t.created_at DESC, t.ticket_id DESC");

        List<AdminTicketRow> tickets = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int index = 0; index < parameters.size(); index++) {
                Object parameter = parameters.get(index);
                if (parameter instanceof Integer integer) {
                    statement.setInt(index + 1, integer);
                } else {
                    statement.setString(index + 1, parameter.toString());
                }
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Timestamp createdAt = resultSet.getTimestamp("created_at");
                    tickets.add(new AdminTicketRow(
                            resultSet.getInt("ticket_id"),
                            resultSet.getString("subject"),
                            resultSet.getString("category_name"),
                            TicketPriority.valueOf(resultSet.getString("priority")),
                            TicketStatus.valueOf(resultSet.getString("status")),
                            createdAt == null ? null : createdAt.toLocalDateTime(),
                            resultSet.getString("assigned_staff_name")));
                }
            }
        }
        return tickets;
    }

    private void appendTicketFilters(StringBuilder sql, List<Object> parameters,
                                     TicketStatus status, TicketPriority priority,
                                     Integer categoryId, String search, Integer assignedStaffId) {
        String prefix = sql.indexOf(" t.") >= 0 ? "t." : "";
        if (status != null) {
            sql.append(" AND ").append(prefix).append("status = ?");
            parameters.add(status.name());
        }
        if (priority != null) {
            sql.append(" AND ").append(prefix).append("priority = ?");
            parameters.add(priority.name());
        }
        if (categoryId != null) {
            sql.append(" AND ").append(prefix).append("category_id = ?");
            parameters.add(categoryId);
        }
        if (search != null && !search.isBlank()) {
            sql.append(" AND (CAST(").append(prefix).append("ticket_id AS CHAR) LIKE ? OR ")
                    .append(prefix).append("subject LIKE ?)");
            String searchPattern = "%" + search.trim() + "%";
            parameters.add(searchPattern);
            parameters.add(searchPattern);
        }
        if (assignedStaffId != null) {
            sql.append(" AND t.assigned_to = ?");
            parameters.add(assignedStaffId);
        }
    }

    private List<Ticket> findTickets(String sql, List<Object> parameters) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindParameters(statement, parameters);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tickets.add(mapRow(resultSet));
                }
            }
        }
        return tickets;
    }

    private void bindParameters(PreparedStatement statement, List<Object> parameters) throws SQLException {
        for (int index = 0; index < parameters.size(); index++) {
            Object parameter = parameters.get(index);
            if (parameter instanceof Integer integer) {
                statement.setInt(index + 1, integer);
            } else {
                statement.setString(index + 1, parameter.toString());
            }
        }
    }

    private int countAdminTickets(TicketStatus status, Boolean assigned, boolean highPriority) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM tickets WHERE 1 = 1");
        List<String> parameters = new ArrayList<>();
        if (status != null) {
            sql.append(" AND status = ?");
            parameters.add(status.name());
        }
        if (assigned != null) {
            sql.append(assigned ? " AND assigned_to IS NOT NULL" : " AND assigned_to IS NULL");
        }
        if (highPriority) {
            sql.append(" AND priority IN (?, ?)");
            parameters.add(TicketPriority.HIGH.name());
            parameters.add(TicketPriority.URGENT.name());
        }
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int index = 0; index < parameters.size(); index++) {
                statement.setString(index + 1, parameters.get(index));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }

    public List<Ticket> findByAssignedTo(Integer assignedTo) throws SQLException {
        return findMany(BASE_SELECT + " WHERE assigned_to = ? ORDER BY created_at DESC", assignedTo);
    }

    public List<Ticket> findByStatus(TicketStatus status) throws SQLException {
        return findMany(BASE_SELECT + " WHERE status = ? ORDER BY created_at DESC", status.name());
    }

    public boolean update(Ticket ticket) throws SQLException {
        String sql = "UPDATE tickets SET user_id = ?, category_id = ?, subject = ?, description = ?, location = ?, priority = ?, status = ?, assigned_to = ?, resolution = ?, resolved_at = ? WHERE ticket_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setTicketFields(statement, ticket);
            statement.setTimestamp(10, timestamp(ticket.getResolvedAt()));
            statement.setInt(11, ticket.getTicketId());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(Integer ticketId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM tickets WHERE ticket_id = ?")) {
            statement.setInt(1, ticketId);
            return statement.executeUpdate() == 1;
        }
    }

    /** Assigns a ticket to staff and records the change atomically. */
    public boolean assignToStaff(Integer ticketId, Integer staffId, Integer changedBy) throws SQLException {
        String findStatusSql = "SELECT status FROM tickets WHERE ticket_id = ? FOR UPDATE";
        String updateSql = "UPDATE tickets SET assigned_to = ?, status = ? WHERE ticket_id = ?";
        String historySql = "INSERT INTO ticket_history (ticket_id, changed_by, old_status, new_status, action_description) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement findStatus = connection.prepareStatement(findStatusSql);
                 PreparedStatement update = connection.prepareStatement(updateSql);
                 PreparedStatement history = connection.prepareStatement(historySql)) {
                findStatus.setInt(1, ticketId);
                TicketStatus oldStatus;
                try (ResultSet resultSet = findStatus.executeQuery()) {
                    if (!resultSet.next()) {
                        connection.rollback();
                        return false;
                    }
                    oldStatus = TicketStatus.valueOf(resultSet.getString("status"));
                }
                TicketStatusWorkflow.requireAllowed(oldStatus, TicketStatus.ASSIGNED);

                update.setInt(1, staffId);
                update.setString(2, TicketStatus.ASSIGNED.name());
                update.setInt(3, ticketId);
                if (update.executeUpdate() != 1) {
                    connection.rollback();
                    return false;
                }

                history.setInt(1, ticketId);
                history.setInt(2, changedBy);
                history.setString(3, oldStatus.name());
                history.setString(4, TicketStatus.ASSIGNED.name());
                history.setString(5, "Ticket assigned to staff");
                history.executeUpdate();
                connection.commit();
                return true;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    /** Updates a ticket status and records its audit entry as one transaction. */
    public boolean updateStatusWithHistory(Integer ticketId, TicketStatus newStatus,
                                            Integer changedBy, String actionDescription) throws SQLException {
        String findStatusSql = "SELECT status FROM tickets WHERE ticket_id = ? FOR UPDATE";
        String updateSql = "UPDATE tickets SET status = ?, resolved_at = CASE WHEN ? = 'RESOLVED' THEN CURRENT_TIMESTAMP ELSE resolved_at END WHERE ticket_id = ?";
        String historySql = "INSERT INTO ticket_history (ticket_id, changed_by, old_status, new_status, action_description) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement findStatus = connection.prepareStatement(findStatusSql);
                 PreparedStatement update = connection.prepareStatement(updateSql);
                 PreparedStatement history = connection.prepareStatement(historySql)) {
                findStatus.setInt(1, ticketId);
                TicketStatus oldStatus;
                try (ResultSet resultSet = findStatus.executeQuery()) {
                    if (!resultSet.next()) {
                        connection.rollback();
                        return false;
                    }
                    oldStatus = TicketStatus.valueOf(resultSet.getString("status"));
                }
                TicketStatusWorkflow.requireAllowed(oldStatus, newStatus);

                update.setString(1, newStatus.name());
                update.setString(2, newStatus.name());
                update.setInt(3, ticketId);
                if (update.executeUpdate() != 1) {
                    connection.rollback();
                    return false;
                }

                history.setInt(1, ticketId);
                history.setInt(2, changedBy);
                history.setString(3, oldStatus.name());
                history.setString(4, newStatus.name());
                history.setString(5, actionDescription);
                history.executeUpdate();
                connection.commit();
                return true;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private List<Ticket> findMany(String sql, Object parameter) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (parameter instanceof Integer integer) {
                statement.setInt(1, integer);
            } else if (parameter instanceof String string) {
                statement.setString(1, string);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tickets.add(mapRow(resultSet));
                }
            }
        }
        return tickets;
    }

    private void setTicketFields(PreparedStatement statement, Ticket ticket) throws SQLException {
        statement.setInt(1, ticket.getUserId());
        statement.setInt(2, ticket.getCategoryId());
        statement.setString(3, ticket.getSubject());
        statement.setString(4, ticket.getDescription());
        statement.setString(5, ticket.getLocation());
        statement.setString(6, ticket.getPriority().name());
        statement.setString(7, ticket.getStatus().name());
        setNullableInt(statement, 8, ticket.getAssignedTo());
        statement.setString(9, ticket.getResolution());
    }

    private Ticket mapRow(ResultSet resultSet) throws SQLException {
        return new Ticket(
                resultSet.getInt("ticket_id"),
                resultSet.getInt("user_id"),
                resultSet.getInt("category_id"),
                resultSet.getString("subject"),
                resultSet.getString("description"),
                resultSet.getString("location"),
                TicketPriority.valueOf(resultSet.getString("priority")),
                TicketStatus.valueOf(resultSet.getString("status")),
                nullableInt(resultSet, "assigned_to"),
                resultSet.getString("resolution"),
                localDateTime(resultSet, "created_at"),
                localDateTime(resultSet, "updated_at"),
                localDateTime(resultSet, "resolved_at"));
    }

    private Integer generatedId(PreparedStatement statement) throws SQLException {
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (!keys.next()) {
                throw new SQLException("Insert did not return a generated ticket ID.");
            }
            return keys.getInt(1);
        }
    }

    private void setNullableInt(PreparedStatement statement, int index, Integer value) throws SQLException {
        if (value == null) {
            statement.setNull(index, java.sql.Types.INTEGER);
        } else {
            statement.setInt(index, value);
        }
    }

    private Integer nullableInt(ResultSet resultSet, String column) throws SQLException {
        int value = resultSet.getInt(column);
        return resultSet.wasNull() ? null : value;
    }

    private Timestamp timestamp(java.time.LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private java.time.LocalDateTime localDateTime(ResultSet resultSet, String column) throws SQLException {
        Timestamp value = resultSet.getTimestamp(column);
        return value == null ? null : value.toLocalDateTime();
    }
}
