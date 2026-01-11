package com.example.retroquiz.service;

import com.example.retroquiz.dao.PlayerDAO;
import com.example.retroquiz.model.*;
import com.example.retroquiz.repository.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing player data and statistics.
 * 
 * LOBBY INTEGRATION POINT:
 * The lobby module should use this service to:
 * 1. Create/load Player objects before starting a quiz
 * 2. Update player statistics after quiz completion
 */
public class PlayerService {

    private final PlayerDAO playerDAO = new PlayerDAO();

    /**
     * Load a player from the database by ID.
     * 
     * LOBBY INTEGRATION: Call this when a user joins from the lobby.
     * 
     * @param playerId Player ID
     * @return Player object, or null if not found
     */
    public Player getPlayerById(int playerId) {
        return playerDAO.getById(playerId);
    }

    /**
     * Load a player from the database by username.
     * 
     * @param username Player username
     * @return Player object, or null if not found
     */
    public Player getPlayerByUsername(String username) {
        return playerDAO.getByUsername(username);
    }

    /**
     * Create a new player in the database.
     * 
     * @param username Player username
     * @param isGuest  Whether the player is a guest
     * @return The created Player object with database ID, or null if failed
     */
    public Player createPlayer(String username, boolean isGuest) {
        // Ensure schema
        ensureSchema();
        Player player = playerDAO.create(username, isGuest);
        if (player != null) {
            System.out.println("Created new player: " + username + " (ID: " + player.getId() + ", Guest: " + isGuest + ")");
        }
        return player;
    }

    public Player createPlayer(String username) {
        return createPlayer(username, false);
    }

    /**
     * Update player statistics after a game.
     * 
     * LOBBY INTEGRATION: Call this after quiz completion to persist results.
     * 
     * @param player Player object with updated stats
     */
    public void updatePlayerStats(Player player) {
        playerDAO.updateStats(player);
        System.out.println("Updated stats for player: " + player.getUsername());
    }

    /**
     * Add points to a player's total score.
     * 
     * @param playerId Player ID
     * @param points   Points to add (can be negative)
     */
    public void addPoints(int playerId, int points) {
        playerDAO.addPoints(playerId, points);
    }

    /**
     * Get top players for leaderboard.
     * 
     * @param limit Number of top players to retrieve
     * @return List of top players ordered by total points
     */
    public List<Player> getTopPlayers(int limit) {
        List<Player> players = playerDAO.getTopPlayers(limit);
        System.out.println("Loaded " + players.size() + " top players for leaderboard.");
        return players;
    }

    /**
     * Ensure the database schema has the necessary columns.
     */
    public void ensureSchema() {
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return;

        try (Statement stmt = conn.createStatement()) {
            // Attempt to add column, ignore if exists
            String sql = "ALTER TABLE players ADD COLUMN IF NOT EXISTS is_guest BOOLEAN DEFAULT FALSE";
            stmt.execute(sql);

            // Update existing players to have is_guest = FALSE if NULL
            String updateSql = "UPDATE players SET is_guest = FALSE WHERE is_guest IS NULL";
            int updated = stmt.executeUpdate(updateSql);
            if (updated > 0) {
                System.out.println("Updated " + updated + " existing players to set is_guest = FALSE");
            }
        } catch (SQLException e) {
            System.err.println("Error ensuring schema: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Check if a username already exists.
     * 
     * @param username Username to check
     * @return true if exists, false otherwise
     */
    public boolean usernameExists(String username) {
        return playerDAO.existsByUsername(username);
    }

    /**
     * Get player rank based on total points.
     * 
     * @param playerId Player ID
     * @return Player's rank (1 = highest), or -1 if not found
     */
    public int getPlayerRank(int playerId) {
        return playerDAO.getRank(playerId);
    }
}
