package com.example.retroquiz.dao;

import com.example.retroquiz.model.Question;
import com.example.retroquiz.repository.CloudDatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    public List<Question> getRandom(int count) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions ORDER BY RANDOM() LIMIT " + count;

        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return questions;

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                questions.add(mapResultSetToQuestion(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error loading random questions: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }

        return questions;
    }

    public List<Question> getByCategory(String category, int count) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE category = ? ORDER BY RANDOM() LIMIT " + count;

        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return questions;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapResultSetToQuestion(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading questions by category: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }

        return questions;
    }

    public int getCount() {
        String sql = "SELECT COUNT(*) as total FROM questions";
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return 0;

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error counting questions: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }

        return 0;
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM questions WHERE category IS NOT NULL ORDER BY category";

        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return categories;

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }

        } catch (SQLException e) {
            System.err.println("Error loading categories: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }

        return categories;
    }

    public boolean create(String question, String optionA, String optionB,
            String optionC, String optionD, char correctOption,
            String category, String difficulty) {
        String sql = "INSERT INTO questions (question, optionA, optionB, optionC, optionD, correct_option, category, difficulty) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, question);
            stmt.setString(2, optionA);
            stmt.setString(3, optionB);
            stmt.setString(4, optionC);
            stmt.setString(5, optionD);
            stmt.setString(6, String.valueOf(Character.toUpperCase(correctOption)));
            stmt.setString(7, category);
            stmt.setString(8, difficulty);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding question: " + e.getMessage());
            return false;
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
    }

    public List<Question> getAll() {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions ORDER BY id ASC";

        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return questions;

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                questions.add(mapResultSetToQuestion(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error loading all questions: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }

        return questions;
    }

    public boolean update(int id, String question, String optionA, String optionB,
            String optionC, String optionD, char correctOption,
            String category, String difficulty) {
        String sql = "UPDATE questions SET question = ?, optionA = ?, optionB = ?, " +
                "optionC = ?, optionD = ?, correct_option = ?, category = ?, difficulty = ? " +
                "WHERE id = ?";

        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, question);
            stmt.setString(2, optionA);
            stmt.setString(3, optionB);
            stmt.setString(4, optionC);
            stmt.setString(5, optionD);
            stmt.setString(6, String.valueOf(Character.toUpperCase(correctOption)));
            stmt.setString(7, category);
            stmt.setString(8, difficulty);
            stmt.setInt(9, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating question: " + e.getMessage());
            return false;
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM questions WHERE id = ?";
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting question: " + e.getMessage());
            return false;
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
    }

    private Question mapResultSetToQuestion(ResultSet rs) throws SQLException {
        return new Question(
                rs.getInt("id"),
                rs.getString("question"),
                rs.getString("optionA"),
                rs.getString("optionB"),
                rs.getString("optionC"),
                rs.getString("optionD"),
                rs.getString("correct_option").charAt(0),
                rs.getString("category"),
                rs.getString("difficulty"));
    }
}
