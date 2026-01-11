package com.example.retroquiz.dao;

import com.example.retroquiz.model.Player;
import com.example.retroquiz.repository.CloudDatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAO {

    public Player getById(int playerId) {
        String sql = "SELECT * FROM players WHERE id = ?";
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPlayer(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading player by ID: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }

        return null;
    }

    public Player getByUsername(String username) {
        String sql = "SELECT * FROM players WHERE username = ?";
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPlayer(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading player by username: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }

        return null;
    }

    public Player create(String username, boolean isGuest) {
        String sql = "INSERT INTO players (username, total_points, games_played, is_guest) VALUES (?, 0, 0, ?)";
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return null;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setBoolean(2, isGuest);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int playerId = generatedKeys.getInt(1);
                        return new Player(playerId, username);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating player: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }

        return null;
    }

    public void updateStats(Player player) {
        String sql = "UPDATE players SET " +
                "total_points = ?, " +
                "games_played = ?, " +
                "correct_answers = ?, " +
                "wrong_answers = ?, " +
                "no_answers = ?, " +
                "last_played = CURRENT_TIMESTAMP " +
                "WHERE id = ?";

        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, player.getTotalPoints());
            stmt.setInt(2, player.getGamesPlayed());
            stmt.setInt(3, player.getCorrectAnswers());
            stmt.setInt(4, player.getWrongAnswers());
            stmt.setInt(5, player.getNoAnswers());
            stmt.setInt(6, player.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating player stats: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
    }

    public void addPoints(int playerId, int points) {
        String sql = "UPDATE players SET total_points = total_points + ? WHERE id = ?";
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, points);
            stmt.setInt(2, playerId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error adding points: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
    }

    public List<Player> getTopPlayers(int limit) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players WHERE is_guest = FALSE ORDER BY total_points DESC LIMIT " + limit;

        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return players;

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                players.add(mapResultSetToPlayer(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error loading top players: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }

        return players;
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) as count FROM players WHERE username = ?";
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }

        return false;
    }

    public int getRank(int playerId) {
        String sql = "SELECT COUNT(*) + 1 as rank FROM players " +
                "WHERE total_points > (SELECT total_points FROM players WHERE id = ?)";

        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return -1;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("rank");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting player rank: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }

        return -1;
    }

    private Player mapResultSetToPlayer(ResultSet rs) throws SQLException {
        return new Player(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getInt("total_points"),
                rs.getInt("games_played"),
                rs.getInt("correct_answers"),
                rs.getInt("wrong_answers"),
                rs.getInt("no_answers"));
    }
}
