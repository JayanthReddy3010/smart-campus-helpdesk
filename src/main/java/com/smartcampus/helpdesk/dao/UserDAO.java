package com.smartcampus.helpdesk.dao;

import com.smartcampus.helpdesk.model.User;
import com.smartcampus.helpdesk.model.UserRole;
import com.smartcampus.helpdesk.util.DatabaseConnection;
import com.smartcampus.helpdesk.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserDAO {

    private static final String BASE_SELECT = "SELECT user_id, name, email, password_hash, role, department, created_at FROM users";

    public User create(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password_hash, role, department) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole().name());
            statement.setString(5, user.getDepartment());
            statement.executeUpdate();
            user.setUserId(generatedId(statement));
            return user;
        }
    }

    public User findById(Integer userId) throws SQLException {
        String sql = BASE_SELECT + " WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRow(resultSet) : null;
            }
        }
    }

    public User findByEmail(String email) throws SQLException {
        String sql = BASE_SELECT + " WHERE email = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRow(resultSet) : null;
            }
        }
    }

    /**
     * Resolves an existing user by email or creates a new one deterministically.
     * Guarantees all demo logins and fallback flows map to a valid persisted user row.
     */
    public User findOrCreateDemoUser(String identity, UserRole requestedRole) throws SQLException {
        String clean = identity == null ? "" : identity.trim();
        if (clean.isBlank()) {
            clean = "demo.student@campus.local";
        }
        String email = clean.contains("@") ? clean.toLowerCase(Locale.ROOT) : clean.toLowerCase(Locale.ROOT) + "@campus.local";
        User existing = findByEmail(email);
        if (existing != null) {
            return existing;
        }

        String name = clean.contains("@") ? clean.substring(0, clean.indexOf('@')) : clean;
        name = name.replace('.', ' ').replace('_', ' ').trim();
        if (name.isEmpty()) {
            name = "Demo User";
        } else {
            name = Character.toUpperCase(name.charAt(0)) + (name.length() > 1 ? name.substring(1) : "");
        }

        UserRole role = requestedRole != null ? requestedRole : UserRole.STUDENT;
        String department = role == UserRole.STAFF ? "Campus Facilities & IT"
                : (role == UserRole.ADMIN ? "Helpdesk Administration" : "Computer Science");
        String passwordHash = PasswordUtil.hashPassword("DemoPassword123!");

        User newUser = new User(null, name, email, passwordHash, role, department, null);
        try {
            return create(newUser);
        } catch (SQLException exception) {
            User retry = findByEmail(email);
            if (retry != null) {
                return retry;
            }
            throw exception;
        }
    }

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(BASE_SELECT + " ORDER BY name");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(mapRow(resultSet));
            }
        }
        return users;
    }

    public List<User> findByRole(UserRole role) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = BASE_SELECT + " WHERE role = ? ORDER BY name";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, role.name());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(mapRow(resultSet));
                }
            }
        }
        return users;
    }

    public boolean update(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, password_hash = ?, role = ?, department = ? WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole().name());
            statement.setString(5, user.getDepartment());
            statement.setInt(6, user.getUserId());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(Integer userId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
            statement.setInt(1, userId);
            return statement.executeUpdate() == 1;
        }
    }

    private User mapRow(ResultSet resultSet) throws SQLException {
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        return new User(
                resultSet.getInt("user_id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getString("password_hash"),
                UserRole.valueOf(resultSet.getString("role")),
                resultSet.getString("department"),
                createdAt == null ? null : createdAt.toLocalDateTime());
    }

    private Integer generatedId(PreparedStatement statement) throws SQLException {
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (!keys.next()) {
                throw new SQLException("Insert did not return a generated user ID.");
            }
            return keys.getInt(1);
        }
    }
}
