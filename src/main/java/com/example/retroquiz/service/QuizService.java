package com.example.retroquiz.service;

import com.example.retroquiz.dao.PlayerAnswerDAO;
import com.example.retroquiz.dao.QuestionDAO;
import com.example.retroquiz.model.Question;
import com.example.retroquiz.repository.CloudDatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing quiz questions and game logic.
 * 
 * This service handles:
 * - Loading questions from database
 * - Randomizing question order
 * - Validating answers
 * - Recording player answers
 */
public class QuizService {

    private final QuestionDAO questionDAO = new QuestionDAO();
    private final PlayerAnswerDAO playerAnswerDAO = new PlayerAnswerDAO();

    /**
     * Load a specified number of random questions from the database.
     * Questions will not repeat within the same quiz session.
     * 
     * FALLBACK: If database is unavailable, uses hardcoded questions.
     * 
     * @param count Number of questions to load
     * @return List of random questions
     */
    public List<Question> loadRandomQuestions(int count) {
        List<Question> questions = questionDAO.getRandom(count);

        // If no questions loaded from database (or DB unavailable), use hardcoded
        if (questions.isEmpty()) {
            System.out.println("⚠️ No questions in database or DB unavailable. Using hardcoded questions (OFFLINE MODE).");
            return QuestionBank.getHardcodedQuestions(count);
        }

        System.out.println("✅ Loaded " + questions.size() + " questions from database.");
        return questions;
    }

    /**
     * Load all questions from a specific category.
     * 
     * @param category The category name
     * @param count    Maximum number of questions to load
     * @return List of questions from the category
     */
    public List<Question> loadQuestionsByCategory(String category, int count) {
        return questionDAO.getByCategory(category, count);
    }

    /**
     * Calculate points based on answer correctness.
     * 
     * SCORING RULES:
     * - Correct answer: +10 points
     * - Wrong answer: -5 points
     * - No answer: -15 points
     * 
     * @param isCorrect   Whether the answer was correct
     * @param wasAnswered Whether the question was answered (false = timeout)
     * @return Points earned/lost
     */
    public int calculatePoints(boolean isCorrect, boolean wasAnswered) {
        if (!wasAnswered) {
            return -15; // No answer penalty
        }
        return isCorrect ? 10 : -5;
    }

    /**
     * Record a player's answer to a question in the database.
     * This is used for analytics and game history.
     * 
     * @param sessionId      Current game session ID
     * @param playerId       Player ID
     * @param questionId     Question ID
     * @param selectedOption Selected option ('A', 'B', 'C', 'D', or null)
     * @param isCorrect      Whether the answer was correct
     * @param timeTaken      Time taken to answer (seconds)
     * @param pointsEarned   Points earned for this answer
     */
    public void recordAnswer(int sessionId, int playerId, int questionId,
            Character selectedOption, boolean isCorrect,
            int timeTaken, int pointsEarned) {
        playerAnswerDAO.create(sessionId, playerId, questionId, selectedOption, isCorrect, timeTaken, pointsEarned);
    }

    /**
     * Get total number of questions in the database.
     * Useful for determining quiz length.
     * 
     * @return Total question count
     */
    public int getTotalQuestionCount() {
        return questionDAO.getCount();
    }

    /**
     * Get available categories from the database.
     * 
     * @return List of unique category names
     */
    public List<String> getCategories() {
        return questionDAO.getAllCategories();
    }

    /**
     * Add a new question to the database.
     * 
     * @param question      The question text
     * @param optionA       Option A text
     * @param optionB       Option B text
     * @param optionC       Option C text
     * @param optionD       Option D text
     * @param correctOption The correct option character ('A', 'B', 'C', 'D')
     * @param category      Question category
     * @param difficulty    Question difficulty ("EASY", "MEDIUM", "HARD")
     * @return true if successful, false otherwise
     */
    public boolean addQuestion(String question, String optionA, String optionB,
            String optionC, String optionD, char correctOption,
            String category, String difficulty) {
        return questionDAO.create(question, optionA, optionB, optionC, optionD, correctOption, category, difficulty);
    }

    // --- Admin CRUD Operations for Questions ---

    /**
     * Get all questions from the database.
     * 
     * @return List of all questions
     */
    public List<Question> getAllQuestions() {
        return questionDAO.getAll();
    }

    /**
     * Update an existing question in the database.
     * 
     * @param id            Question ID to update
     * @param question      Updated question text
     * @param optionA       Updated option A
     * @param optionB       Updated option B
     * @param optionC       Updated option C
     * @param optionD       Updated option D
     * @param correctOption Updated correct option
     * @param category      Updated category
     * @param difficulty    Updated difficulty
     * @return true if successful, false otherwise
     */
    public boolean updateQuestion(int id, String question, String optionA, String optionB,
            String optionC, String optionD, char correctOption,
            String category, String difficulty) {
        return questionDAO.update(id, question, optionA, optionB, optionC, optionD, correctOption, category, difficulty);
    }

    /**
     * Delete a question from the database.
     * Also deletes related player_answers to avoid foreign key constraint
     * violations.
     * 
     * @param id Question ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteQuestion(int id) {
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null)
            return false;

        try {
            // Start transaction
            conn.setAutoCommit(false);

            // First, delete related player_answers
            playerAnswerDAO.deleteByQuestionId(id, conn);

            // Then delete the question
            String deleteQuestionSql = "DELETE FROM questions WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteQuestionSql)) {
                stmt.setInt(1, id);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error deleting question: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
                CloudDatabaseConnection.closeConnection(conn);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }
}
