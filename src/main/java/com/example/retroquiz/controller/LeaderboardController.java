package com.example.retroquiz.controller;

import com.example.retroquiz.service.*;
import com.example.retroquiz.HelloApplication;
import com.example.retroquiz.model.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Leaderboard screen.
 * 
 * FEATURES:
 * - Displays top 10 players from database
 * - Shows username and total points
 * - Refreshable leaderboard
 * - Navigation back to main menu
 */
public class LeaderboardController implements Initializable {

    @FXML
    private Label titleLabel;
    @FXML
    private TableView<Player> leaderboardTable;
    @FXML
    private TableColumn<Player, Integer> rankColumn;
    @FXML
    private TableColumn<Player, String> usernameColumn;
    @FXML
    private TableColumn<Player, Integer> totalPointsColumn;
    @FXML
    private TableColumn<Player, Integer> gamesPlayedColumn;
    @FXML
    private TableColumn<Player, Double> accuracyColumn;
    @FXML
    private Button refreshButton;
    @FXML
    private Button backButton;

    private final PlayerService playerService = new PlayerService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Ensure database schema has is_guest column
        playerService.ensureSchema();

        // Setup table columns
        rankColumn.setCellValueFactory(cellData -> {
            int index = leaderboardTable.getItems().indexOf(cellData.getValue()) + 1;
            return new javafx.beans.property.SimpleIntegerProperty(index).asObject();
        });

        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        totalPointsColumn.setCellValueFactory(new PropertyValueFactory<>("totalPoints"));
        gamesPlayedColumn.setCellValueFactory(new PropertyValueFactory<>("gamesPlayed"));

        accuracyColumn.setCellValueFactory(cellData -> {
            double accuracy = cellData.getValue().getAccuracy();
            return new javafx.beans.property.SimpleDoubleProperty(accuracy).asObject();
        });

        // Format accuracy column to show percentage
        accuracyColumn.setCellFactory(column -> new javafx.scene.control.TableCell<Player, Double>() {
            @Override
            protected void updateItem(Double accuracy, boolean empty) {
                super.updateItem(accuracy, empty);
                if (empty || accuracy == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f%%", accuracy));
                }
            }
        });

        // Load leaderboard data
        loadLeaderboard();

        System.out.println("LeaderboardController initialized.");
    }

    /**
     * Load top players from database and display in table.
     */
    private void loadLeaderboard() {
        List<Player> topPlayers = playerService.getTopPlayers(10);

        if (topPlayers.isEmpty()) {
            System.out.println("No players found in database.");
            titleLabel.setText("Leaderboard (No Data)");
            return;
        }

        ObservableList<Player> playerData = FXCollections.observableArrayList(topPlayers);
        leaderboardTable.setItems(playerData);

        // Apply styling to top 3 players
        leaderboardTable.setRowFactory(tv -> new javafx.scene.control.TableRow<Player>() {
            @Override
            protected void updateItem(Player player, boolean empty) {
                super.updateItem(player, empty);

                if (empty || player == null) {
                    setStyle("");
                } else {
                    int rank = leaderboardTable.getItems().indexOf(player) + 1;
                    switch (rank) {
                        case 1 -> setStyle(
                                "-fx-background-color: rgba(255, 215, 0, 0.4); -fx-font-weight: bold; -fx-text-fill: white;"); // Gold
                        case 2 -> setStyle("-fx-background-color: rgba(192, 192, 192, 0.4); -fx-text-fill: white;"); // Silver
                        case 3 -> setStyle("-fx-background-color: rgba(205, 127, 50, 0.4); -fx-text-fill: white;"); // Bronze
                        default -> setStyle("");
                    }
                }
            }
        });

        System.out.println("Leaderboard loaded with " + topPlayers.size() + " players.");
    }

    /**
     * Handle "Refresh" button click.
     * Reload leaderboard data from database.
     */
    @FXML
    private void handleRefresh() {
        System.out.println("Refreshing leaderboard...");
        loadLeaderboard();
    }

    /**
     * Handle "Back" button click.
     * Return to main menu.
     */
    @FXML
    private void handleBack() {
        try {
            HelloApplication.setScene("retroquiz.fxml");
        } catch (IOException e) {
            System.err.println("Failed to return to main menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get the current top player.
     * 
     * @return The player with the highest total points, or null if no data
     */
    public Player getTopPlayer() {
        ObservableList<Player> players = leaderboardTable.getItems();
        return players.isEmpty() ? null : players.get(0);
    }

    /**
     * Search for a specific player in the leaderboard.
     * 
     * @param username Username to search for
     * @return The player if found, null otherwise
     */
    public Player findPlayer(String username) {
        return leaderboardTable.getItems().stream()
                .filter(p -> p.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }
}
