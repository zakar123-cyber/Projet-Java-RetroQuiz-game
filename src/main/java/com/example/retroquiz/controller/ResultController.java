package com.example.retroquiz.controller;

import com.example.retroquiz.HelloApplication;
import com.example.retroquiz.model.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Quiz Results screen.
 * 
 * FEATURES:
 * - Displays final rankings
 * - Shows player scores
 * - Highlights top 3 players
 * - Provides navigation to leaderboard or main menu
 * 
 * LOBBY INTEGRATION POINT:
 * Receives List<Player> from QuizController with final scores.
 * Can send results back to lobby for session management.
 */
public class ResultController implements Initializable {

    @FXML
    private Label titleLabel;
    @FXML
    private Label winnerLabel;
    @FXML
    private TableView<Player> resultsTable;
    @FXML
    private TableColumn<Player, Integer> rankColumn;
    @FXML
    private TableColumn<Player, String> usernameColumn;
    @FXML
    private TableColumn<Player, Integer> scoreColumn;
    @FXML
    private Button viewLeaderboardButton;
    @FXML
    private Button playAgainButton;
    @FXML
    private Button mainMenuButton;

    private List<Player> players;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup table columns
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("currentScore"));

        System.out.println("ResultController initialized.");
    }

    /**
     * LOBBY INTEGRATION METHOD
     * 
     * Set the players with their final scores.
     * This method will calculate rankings and display results.
     * 
     * @param players List of players who participated in the quiz
     */
    public void setPlayers(List<Player> players) {
        if (players == null || players.isEmpty()) {
            System.err.println("ERROR: No players provided to results screen!");
            return;
        }

        this.players = players;
        calculateRankings();
        displayResults();
    }

    /**
     * Calculate player rankings based on scores.
     * Higher score = better rank.
     * Ties are broken by answer time (earlier = better).
     */
    private void calculateRankings() {
        // Sort players by score (descending), then by answer time (ascending)
        players.sort((p1, p2) -> p1.compareByScore(p2));

        // Assign ranks
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setRank(i + 1);
        }

        System.out.println("Rankings calculated:");
        for (Player p : players) {
            System.out.println("  " + p.getRank() + ". " + p.getUsername() + " - " + p.getCurrentScore() + " points");
        }
    }

    /**
     * Display the results on screen.
     */
    private void displayResults() {
        // Display winner
        Player winner = players.get(0);
        winnerLabel.setText("🏆 Winner: " + winner.getUsername() + " with " + winner.getCurrentScore() + " points!");

        // Apply winner styling
        if (winner.getCurrentScore() > 0) {
            winnerLabel.setStyle("-fx-text-fill: #ffff00; -fx-font-size: 24px; -fx-font-weight: bold;");
        } else {
            winnerLabel.setStyle("-fx-text-fill: #ff00ff; -fx-font-size: 20px;");
        }

        // Populate table
        ObservableList<Player> playerData = FXCollections.observableArrayList(players);
        resultsTable.setItems(playerData);

        // Apply row styling based on rank
        resultsTable.setRowFactory(tv -> new javafx.scene.control.TableRow<Player>() {
            @Override
            protected void updateItem(Player player, boolean empty) {
                super.updateItem(player, empty);

                if (empty || player == null) {
                    setStyle("");
                } else {
                    switch (player.getRank()) {
                        case 1 -> setStyle("-fx-background-color: rgba(255, 215, 0, 0.3);"); // Gold
                        case 2 -> setStyle("-fx-background-color: rgba(192, 192, 192, 0.3);"); // Silver
                        case 3 -> setStyle("-fx-background-color: rgba(205, 127, 50, 0.3);"); // Bronze
                        default -> setStyle("");
                    }
                }
            }
        });
    }

    /**
     * Handle "View Leaderboard" button click.
     */
    @FXML
    private void handleViewLeaderboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/leaderboard.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) resultsTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Leaderboard");

        } catch (IOException e) {
            System.err.println("Failed to load leaderboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle "Play Again" button click.
     * 
     * LOBBY INTEGRATION: This should return to the lobby or restart the quiz.
     */
    @FXML
    private void handlePlayAgain() {
        try {
            // Option 1: Return to lobby (if lobby exists)
            // HelloApplication.setScene("quiz-lobby.fxml");

            // Option 2: Start a new quiz directly
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/quiz-game.fxml"));
            Scene scene = new Scene(loader.load());

            QuizController quizController = loader.getController();

            // Reset player scores
            for (Player p : players) {
                p.resetSession();
            }

            quizController.setPlayers(players);
            quizController.startQuiz(10); // Start with 10 questions

            Stage stage = (Stage) resultsTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("RetroQuiz - Game");

        } catch (IOException e) {
            System.err.println("Failed to restart quiz: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle "Main Menu" button click.
     */
    @FXML
    private void handleMainMenu() {
        try {
            HelloApplication.setScene("retroquiz.fxml");
        } catch (IOException e) {
            System.err.println("Failed to return to main menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get the winner of the quiz.
     * 
     * @return The player with rank 1
     */
    public Player getWinner() {
        return players != null && !players.isEmpty() ? players.get(0) : null;
    }

    /**
     * Get all players sorted by rank.
     * 
     * @return List of players in rank order
     */
    public List<Player> getRankedPlayers() {
        return players;
    }
}
