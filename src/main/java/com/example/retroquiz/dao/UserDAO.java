package com.example.retroquiz.dao;

import com.example.retroquiz.repository.CloudDatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public String getPasswordHashByUsername(String username) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        Connection conn = CloudDatabaseConnection.getConnection();

        if (conn == null)
            return null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password_hash");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error getting password hash: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
        return null;
    }

    public boolean create(String username, String passwordHash) {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        Connection conn = CloudDatabaseConnection.getConnection();

        if (conn == null)
            return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error creating user: " + e.getMessage());
            return false;
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
    }

    public List<String> getAllUsernames() {
        List<String> users = new ArrayList<>();
        String sql = "SELECT username FROM users ORDER BY username ASC";

        try (Connection conn = CloudDatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(rs.getString("username"));
            }

        } catch (SQLException e) {
            System.err.println("SQL Error getting user list: " + e.getMessage());
        }
        return users;
    }

    public boolean delete(String username) {
        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection conn = CloudDatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("SQL Error deleting user: " + e.getMessage());
            return false;
        }
    }

    public boolean update(String currentUsername, String newUsername, String passwordHash) {
        String sql;
        boolean updatePassword = (passwordHash != null && !passwordHash.isEmpty());

        if (updatePassword) {
            sql = "UPDATE users SET username = ?, password_hash = ? WHERE username = ?";
        } else {
            sql = "UPDATE users SET username = ? WHERE username = ?";
        }

        try (Connection conn = CloudDatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newUsername);

            if (updatePassword) {
                stmt.setString(2, passwordHash);
                stmt.setString(3, currentUsername);
            } else {
                stmt.setString(2, currentUsername);
            }

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("SQL Error updating user: " + e.getMessage());
            return false;
        }
    }
}
