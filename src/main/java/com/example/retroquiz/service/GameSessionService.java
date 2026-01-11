package com.example.retroquiz.service;

import com.example.retroquiz.repository.CloudDatabaseConnection;

import java.sql.*;
import java.util.Random;

/**
 * Service for managing game sessions (creating and joining).
 */
public class GameSessionService {

    /**
     * Generate a unique 6-character session code.
     * Format: Letters and numbers (e.g., "A3B7C9")
     */
    public String generateSessionCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }

        return code.toString();
    }

    /**
     * Create a new game session with a unique code.
     * 
     * @param hostUsername  Username of the host
     * @param questionCount Number of questions (default: 10)
     * @param timerDuration Timer duration in seconds (default: 15)
     * @return The generated session code, or null if creation failed
     */
    public String createGameSession(String hostUsername, int questionCount, int timerDuration) {
        // Ensure Schema is up to date
        ensureSchema();

        // First, get or create player for the host
        PlayerService playerService = new PlayerService();
        com.example.retroquiz.model.Player hostPlayer = playerService.getPlayerByUsername(hostUsername);

        int hostId;
        if (hostPlayer == null) {
            // Create new player if doesn't exist
            hostPlayer = playerService.createPlayer(hostUsername);
            if (hostPlayer == null) {
                System.err.println("Failed to create host player");
                return null;
            }
        }
        hostId = hostPlayer.getId();

        // Generate unique session code
        String sessionCode = generateSessionCode();
        int attempts = 0;
        final int MAX_ATTEMPTS = 10;

        // Ensure code is unique
        while (sessionCodeExists(sessionCode) && attempts < MAX_ATTEMPTS) {
            sessionCode = generateSessionCode();
            attempts++;
        }

        if (attempts >= MAX_ATTEMPTS) {
            System.err.println("Failed to generate unique session code");
            return null;
        }

        // Create session in database
        String sql = "INSERT INTO game_sessions (session_code, host_id, status, question_count, timer_duration, created_at) "
                +
                "VALUES (?, ?, 'WAITING', ?, ?, CURRENT_TIMESTAMP) RETURNING id";

        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionCode);
            stmt.setInt(2, hostId);
            stmt.setInt(3, questionCount);
            stmt.setInt(4, timerDuration);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int sessionId = rs.getInt(1);
                    System.out.println("✅ Game session created: Code=" + sessionCode + ", ID=" + sessionId);

                    // Add host to session_players
                    addPlayerToSession(sessionId, hostId);

                    return sessionCode;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating game session: " + e.getMessage());
            e.printStackTrace();
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }

        return null;
    }

    /**
     * Check if a session code already exists.
     */
    private boolean sessionCodeExists(String code) {
        String sql = "SELECT COUNT(*) as count FROM game_sessions WHERE session_code = ?";
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking session code: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }

        return false;
    }

    /**
     * Join an existing game session using a session code.
     * 
     * @param sessionCode The session code to join
     * @param username    Username of the player joining
     * @return Session ID if successful, -1 if failed
     */
    public int joinGameSession(String sessionCode, String username) {
        if (sessionCode == null || sessionCode.trim().isEmpty()) {
            return -1;
        }

        // Get or create player
        PlayerService playerService = new PlayerService();
        com.example.retroquiz.model.Player player = playerService.getPlayerByUsername(username);

        int playerId;
        if (player == null) {
            player = playerService.createPlayer(username);
            if (player == null) {
                System.err.println("Failed to create/retrieve player");
                return -1;
            }
        }
        playerId = player.getId();

        // Get session ID from code
        String sql = "SELECT id, status FROM game_sessions WHERE session_code = ?";
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null) {
            return -1;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionCode.toUpperCase().trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int sessionId = rs.getInt("id");
                    String status = rs.getString("status");

                    // Check if session is joinable
                    if (!"WAITING".equals(status)) {
                        System.err.println("Session is not in WAITING status: " + status);
                        return -1;
                    }

                    // Add player to session
                    if (addPlayerToSession(sessionId, playerId)) {
                        System.out.println("✅ Player '" + username + "' joined session: " + sessionCode);
                        return sessionId;
                    } else {
                        System.err.println("Failed to add player to session");
                        return -1;
                    }
                } else {
                    System.err.println("Session code not found: " + sessionCode);
                    return -1;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error joining game session: " + e.getMessage());
            e.printStackTrace();
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }

        return -1;
    }

    /**
     * Add a player to a game session.
     * 
     * @param sessionId Session ID
     * @param playerId  Player ID
     * @return true if successful, false otherwise
     */
    private boolean addPlayerToSession(int sessionId, int playerId) {
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            // Check if player already in session
            String checkSql = "SELECT COUNT(*) as count FROM session_players WHERE session_id = ? AND player_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, sessionId);
                checkStmt.setInt(2, playerId);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt("count") > 0) {
                        // Player already in session
                        System.out.println("Player already in session");
                        return true;
                    }
                }
            }

            // Add player to session (UNIQUE constraint prevents duplicates)
            String insertSql = "INSERT INTO session_players (session_id, player_id, score, joined_at) " +
                    "VALUES (?, ?, 0, CURRENT_TIMESTAMP) " +
                    "ON CONFLICT (session_id, player_id) DO NOTHING";

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, sessionId);
                insertStmt.setInt(2, playerId);

                int rowsAffected = insertStmt.executeUpdate();
                // Returns true if inserted (rowsAffected > 0) or if already exists (we checked
                // earlier)
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error adding player to session: " + e.getMessage());
            return false;
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Get session ID from session code.
     * 
     * @param sessionCode Session code
     * @return Session ID, or -1 if not found
     */
    public int getSessionId(String sessionCode) {
        String sql = "SELECT id FROM game_sessions WHERE session_code = ?";
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null) {
            return -1;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionCode.toUpperCase().trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting session ID: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }

        return -1;
    }

    /**
     * Get list of usernames in a session.
     */
    public java.util.List<String> getPlayersInSession(int sessionId) {
        java.util.List<String> players = new java.util.ArrayList<>();
        String sql = "SELECT u.username FROM session_players sp " +
                "JOIN players u ON sp.player_id = u.id " +
                "WHERE sp.session_id = ?";

        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return players;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    players.add(rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting players in session: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
        return players;
    }

    /**
     * Get the current status of a session.
     */
    public String getSessionStatus(int sessionId) {
        String sql = "SELECT status FROM game_sessions WHERE id = ?";
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return "ERROR";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting session status: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
        return "UNKNOWN";
    }

    /**
     * Update the status of a session.
     */
    public void updateSessionStatus(int sessionId, String newStatus) {
        String sql = "UPDATE game_sessions SET status = ? WHERE id = ?";
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, sessionId);
            stmt.executeUpdate();
            System.out.println("Session " + sessionId + " status updated to: " + newStatus);
        } catch (SQLException e) {
            System.err.println("Error updating session status: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Update a player's score in the session_players table.
     */
    public void updatePlayerSessionScore(int sessionId, int playerId, int score) {
        String sql = "UPDATE session_players SET score = ? WHERE session_id = ? AND player_id = ?";
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, score);
            stmt.setInt(2, sessionId);
            stmt.setInt(3, playerId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating session score: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Get the leaderboard for a session (players with scores).
     */
    public java.util.List<com.example.retroquiz.model.Player> getSessionLeaderboard(int sessionId) {
        java.util.List<com.example.retroquiz.model.Player> players = new java.util.ArrayList<>();
        String sql = "SELECT p.id, p.username, sp.score " +
                "FROM session_players sp " +
                "JOIN players p ON sp.player_id = p.id " +
                "WHERE sp.session_id = ? " +
                "ORDER BY sp.score DESC";

        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return players;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Create Player object with ID and Username using the simple constructor
                    com.example.retroquiz.model.Player player = new com.example.retroquiz.model.Player(
                            rs.getInt("id"),
                            rs.getString("username"));
                    // Set the session score manually
                    player.setCurrentScore(rs.getInt("score"));
                    players.add(player);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting session leaderboard: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
        return players;
    }

    /**
     * Get the number of questions configured for a session.
     */
    public int getSessionQuestionCount(int sessionId) {
        String sql = "SELECT question_count FROM game_sessions WHERE id = ?";
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return 10; // Default fallback

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("question_count");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting question count: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
        return 10;
    }

    /**
     * Check if all players in the session have finished answering all questions.
     * Uses the `is_finished` flag in session_players table.
     */
    public boolean areAllPlayersFinished(int sessionId) {
        String sql = "SELECT COUNT(*) as active_count FROM session_players WHERE session_id = ? AND is_finished = FALSE";

        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return false; // Fail safe, don't block if DB down but safer to say not finished? Or true?
                          // False to keep trying.

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int active = rs.getInt("active_count");
                    return active == 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if all finished: " + e.getMessage());
            // Fallback to legacy check if column missing?
            // For now, let's assume our migration works.
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
        return false;
    }

    /**
     * Mark a player as finished in the session.
     */
    public void markPlayerFinished(int sessionId, int playerId) {
        String sql = "UPDATE session_players SET is_finished = TRUE WHERE session_id = ? AND player_id = ?";
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            stmt.setInt(2, playerId);
            stmt.executeUpdate();
            System.out.println("Player " + playerId + " marked as finished in session " + sessionId);
        } catch (SQLException e) {
            System.err.println("Error marking player finished: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Ensure the database schema has the necessary columns.
     * Specifically checks for 'is_finished' in 'session_players'.
     */
    public void ensureSchema() {
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null) return;

        try (Statement stmt = conn.createStatement()) {
            // Attempt to add column, ignore if exists
            // PostgreSQL specific syntax: ADD COLUMN IF NOT EXISTS
            String sql = "ALTER TABLE session_players ADD COLUMN IF NOT EXISTS is_finished BOOLEAN DEFAULT FALSE";
            stmt.execute(sql);
            System.out.println("Schema verification: 'is_finished' column ensured in 'session_players'.");
        } catch (SQLException e) {
            System.err.println("Error ensuring schema: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Check if a player is the host of the session.
     */
    public boolean isSessionHost(int sessionId, int playerId) {
        String sql = "SELECT host_id FROM game_sessions WHERE id = ?";
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("host_id") == playerId;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking session host: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
        return false;
    }
}
