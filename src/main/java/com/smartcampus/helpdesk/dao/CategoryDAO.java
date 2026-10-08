package com.smartcampus.helpdesk.dao;

import com.smartcampus.helpdesk.model.Category;
import com.smartcampus.helpdesk.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    private static final String BASE_SELECT = "SELECT category_id, category_name, description, active FROM categories";

    public Category create(Category category) throws SQLException {
        String sql = "INSERT INTO categories (category_name, description, active) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, category.getCategoryName());
            statement.setString(2, category.getDescription());
            statement.setBoolean(3, category.isActive());
            statement.executeUpdate();
            category.setCategoryId(generatedId(statement));
            return category;
        }
    }

    public Category findById(Integer categoryId) throws SQLException {
        String sql = BASE_SELECT + " WHERE category_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, categoryId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRow(resultSet) : null;
            }
        }
    }

    public List<Category> findAll() throws SQLException {
        return findByActive(null);
    }

    public List<Category> findActive() throws SQLException {
        return findByActive(true);
    }

    public boolean update(Category category) throws SQLException {
        String sql = "UPDATE categories SET category_name = ?, description = ?, active = ? WHERE category_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, category.getCategoryName());
            statement.setString(2, category.getDescription());
            statement.setBoolean(3, category.isActive());
            statement.setInt(4, category.getCategoryId());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(Integer categoryId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM categories WHERE category_id = ?")) {
            statement.setInt(1, categoryId);
            return statement.executeUpdate() == 1;
        }
    }

    public int importConfiguration(List<Category> categories) throws SQLException {
        String sql = "INSERT INTO categories (category_name, description, active) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE description = VALUES(description), active = VALUES(active)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            try {
                for (Category category : categories) {
                    statement.setString(1, category.getCategoryName());
                    statement.setString(2, category.getDescription());
                    statement.setBoolean(3, category.isActive());
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
                return categories.size();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private List<Category> findByActive(Boolean active) throws SQLException {
        String sql = BASE_SELECT;
        if (active != null) {
            sql += " WHERE active = ?";
        }
        sql += " ORDER BY category_name";

        List<Category> categories = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (active != null) {
                statement.setBoolean(1, active);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    categories.add(mapRow(resultSet));
                }
            }
        }
        return categories;
    }

    private Category mapRow(ResultSet resultSet) throws SQLException {
        return new Category(
                resultSet.getInt("category_id"),
                resultSet.getString("category_name"),
                resultSet.getString("description"),
                resultSet.getBoolean("active"));
    }

    private Integer generatedId(PreparedStatement statement) throws SQLException {
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (!keys.next()) {
                throw new SQLException("Insert did not return a generated category ID.");
            }
            return keys.getInt(1);
        }
    }
}
