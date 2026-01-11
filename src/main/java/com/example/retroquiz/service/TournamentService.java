// src/main/java/com/example/retroquiz/Backend/TournamentService.java (MODIFIED for SQL Server)

package com.example.retroquiz.service;

import com.example.retroquiz.repository.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
// Removed unused imports for HashMap and Map

/**
 * Service for handling Host/Admin data and Tournament Codes using SQL Server.
 */
public class TournamentService {

    private static final String HARDCODED_TOURNAMENT_CODE = "QZ1984";

    /**
     * Authenticates the admin user against the database.
     * Checks the 'users' table where is_admin is TRUE (BIT = 1).
     * * @return true if credentials are valid AND the user is an admin.
     */
    public boolean authenticateAdmin(String username, String password) {
        if (username == null || password == null)
            return false;

        // Query checks for the username, password_hash, AND admin status (is_admin = 1 or TRUE)
        // CHANGED: Table 'players' -> 'users' (Admin flag is on users table)
        // Postgres uses BOOLEAN for is_admin, so TRUE is safer than 1, but most drivers handle 1 as true.
        String sql = "SELECT password_hash FROM users WHERE username = ? AND is_admin = TRUE";
        Connection conn = CloudDatabaseConnection.getConnection();

        if (conn == null)
            return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password_hash");
                    // Compare the password (assuming plain text for now, should be a secure hash)
                    return storedPassword != null && storedPassword.equals(password);
                }
            }
            return false; // User not found or is not an admin

        } catch (SQLException e) {
            System.err.println("SQL Error during admin authentication: " + e.getMessage());
            return false;
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Gets the active tournament code.
     * NOTE: This is still hardcoded but is ready for future database integration.
     * * @return The active tournament code for users to join.
     */
    public String getActiveTournamentCode() {
        // Future: Query a dedicated 'Tournaments' table
        return HARDCODED_TOURNAMENT_CODE;
    }
}
