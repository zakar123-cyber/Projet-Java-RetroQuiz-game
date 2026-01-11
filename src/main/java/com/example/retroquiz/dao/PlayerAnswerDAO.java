package com.example.retroquiz.dao;

import com.example.retroquiz.repository.CloudDatabaseConnection;

import java.sql.*;

public class PlayerAnswerDAO {

    public void create(int sessionId, int playerId, int questionId,
            Character selectedOption, boolean isCorrect,
            int timeTaken, int pointsEarned) {
        String sql = "INSERT INTO player_answers (session_id, player_id, question_id, " +
                "selected_option, is_correct, time_taken, points_earned) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            stmt.setInt(2, playerId);
            stmt.setInt(3, questionId);

            if (selectedOption != null) {
                stmt.setString(4, String.valueOf(selectedOption));
            } else {
                stmt.setNull(4, Types.CHAR);
            }

            stmt.setBoolean(5, isCorrect);
            stmt.setInt(6, timeTaken);
            stmt.setInt(7, pointsEarned);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error recording answer: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
    }

    public void deleteByQuestionId(int questionId, Connection conn) throws SQLException {
        String sql = "DELETE FROM player_answers WHERE question_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            stmt.executeUpdate();
        }
    }
}
