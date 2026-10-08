package com.smartcampus.helpdesk.dao;

import com.smartcampus.helpdesk.model.User;
import com.smartcampus.helpdesk.model.UserRole;
import com.smartcampus.helpdesk.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
