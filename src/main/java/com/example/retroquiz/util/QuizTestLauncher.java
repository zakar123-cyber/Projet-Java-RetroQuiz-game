package com.example.retroquiz.util;

import com.example.retroquiz.controller.QuizController;
import com.example.retroquiz.controller.ResultController;
import com.example.retroquiz.model.Player;
import com.example.retroquiz.service.PlayerService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Standalone launcher for testing the Quiz module.
 * 
 * This class demonstrates how to launch the quiz without the lobby.
 * Use this for testing and as a reference for lobby integration.
 * 
 * USAGE:
 * 1. Run this class directly to test the quiz
 * 2. It creates a test player and starts a 10-question quiz
 * 3. Use this code as a template for your lobby integration
 */
public class QuizTestLauncher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // STEP 1: Create or load test players
        PlayerService playerService = new PlayerService();
        Player testPlayer = null;

        try {
            // Try to load existing player from database
            testPlayer = playerService.getPlayerByUsername("TestPlayer");

            // Create new player if doesn't exist
            if (testPlayer == null) {
                System.out.println("Creating new test player in database...");
                testPlayer = playerService.createPlayer("TestPlayer");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Database connection failed. Running in OFFLINE MODE.");
            System.out.println("Creating temporary test player (data won't be saved)...");
        }

        // If database failed, create a temporary player for testing
        if (testPlayer == null) {
            testPlayer = new Player(1, "TestPlayer (Offline)");
            System.out.println("✅ Temporary player created: " + testPlayer.getUsername());
        }

        // For multiplayer testing, add more players:
        List<Player> players = new ArrayList<>();
        players.add(testPlayer);

        // Uncomment to test multiplayer:
        // Player player2 = new Player(2, "Player2");
        // players.add(player2);

        // STEP 2: Load quiz scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/quiz-game.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 700);

        // STEP 3: Get controller and configure quiz
        QuizController quizController = loader.getController();

        // IMPORTANT: Set players before starting quiz
        quizController.setPlayers(players);

        // Optional: Set session ID (for database tracking)
        // quizController.setSessionId(1);

        // STEP 4: Start quiz with desired number of questions
        quizController.startQuiz(10); // 10 questions

        // STEP 5: Show window
        primaryStage.setTitle("RetroQuiz - Test Mode");
        primaryStage.setScene(scene);
        primaryStage.show();

        System.out.println("Quiz test launcher started successfully!");
        System.out.println("Player: " + testPlayer.getUsername());
        System.out.println("Press any answer button to test the quiz.");
    }

    /**
     * Alternative: Test the leaderboard directly
     */
    public void testLeaderboard(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/leaderboard.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 700);

        stage.setTitle("RetroQuiz - Leaderboard Test");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Alternative: Test the results screen directly
     */
    public void testResults(Stage stage) throws Exception {
        // Create test players with scores
        Player player1 = new Player(1, "Winner");
        player1.setCurrentScore(100);
        player1.setRank(1);

        Player player2 = new Player(2, "Runner-Up");
        player2.setCurrentScore(80);
        player2.setRank(2);

        Player player3 = new Player(3, "ThirdPlace");
        player3.setCurrentScore(60);
        player3.setRank(3);

        List<Player> players = List.of(player1, player2, player3);

        // Load results screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/result-screen.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 700);

        ResultController resultController = loader.getController();
        resultController.setPlayers(players);

        stage.setTitle("RetroQuiz - Results Test");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
