// src/main/java/com/example/retroquiz/Backend/UserService.java (Remains compatible)

package com.example.retroquiz.service;

import com.example.retroquiz.dao.PlayerDAO;
import com.example.retroquiz.dao.UserDAO;
import com.example.retroquiz.repository.*;
import java.sql.*;

public class UserService {

    private final UserDAO userDAO = new UserDAO();
    private final PlayerDAO playerDAO = new PlayerDAO();

    // ... (isValidTournamentCode remains the same)
    public boolean isValidTournamentCode(String code) {
        // Simple logic: Code must be 6 characters and contain 'Q'
        return code == null || code.trim().length() != 6 || !code.toUpperCase().contains("Q");
    }

    public boolean loginUser(String username, String password) {
        String storedPassword = userDAO.getPasswordHashByUsername(username);
        return storedPassword != null && storedPassword.equals(password);
    }

    public boolean registerUser(String username, String password) {
        // We use the DAOs to create both user and player entries.
        // Note: Currently DAOs manage their own connections, so this is not fully atomic
        // but it follows the requested DAO pattern and is consistent with the rest of the app.
        
        if (userDAO.create(username, password)) {
            // If user created, ensure player exists (ignoring failure if already exists)
            playerDAO.create(username, false);
            return true;
        }
        return false;
    }
    // --- Admin CRUD Operations ---

    public java.util.List<String> getAllUsers() {
        return userDAO.getAllUsernames();
    }

    public boolean deleteUser(String username) {
        return userDAO.delete(username);
    }

    public boolean updateUser(String currentUsername, String newUsername, String newPassword) {
        return userDAO.update(currentUsername, newUsername, newPassword);
    }
}
