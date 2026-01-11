// src/main/java/com/example/retroquiz/QuizLobbyController.java

package com.example.retroquiz.controller;

import com.example.retroquiz.HelloApplication;
import com.example.retroquiz.model.User;
import com.example.retroquiz.model.Player;
import com.example.retroquiz.service.GameSessionService;
import com.example.retroquiz.service.PlayerService;
import com.example.retroquiz.util.UserDataInitializer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class QuizLobbyController implements UserDataInitializer {

    @FXML
    private Label usernameLabel;
    @FXML
    private Label codeLabel;
    @FXML
    private ListView<String> playerListView;
    @FXML
    private Button startGameButton;
    @FXML
    private Label statusLabel;
    @FXML
    private Label playerCountLabel;

    private User currentUser;
    private GameSessionService gameSessionService;
    private PlayerService playerService;
    private Timeline pollingTimeline;
    private String sessionCode;
    private int sessionId = -1;

    public QuizLobbyController() {
        this.gameSessionService = new GameSessionService();
        this.playerService = new PlayerService();
    }

    @Override
    public void initData(User user) {
        this.currentUser = user;
        this.sessionCode = user.getTournamentCode();

        usernameLabel.setText(user.isGuest() ? "Guest: " + user.getUsername() : "Player: " + user.getUsername());

        if (sessionCode != null) {
            codeLabel.setText("CODE: " + sessionCode);
            sessionId = gameSessionService.getSessionId(sessionCode);

            // Check if user is host
            Player player = playerService.getPlayerByUsername(user.getUsername());
            boolean isHost = false;

            if (player != null) {
                isHost = gameSessionService.isSessionHost(sessionId, player.getId());
            }

            startGameButton.setVisible(isHost);
            startGameButton.setManaged(isHost); // Remove from layout if hidden

            if (!isHost) {
                if (statusLabel != null)
                    statusLabel.setText("Waiting for host to start...");
            } else {
                if (statusLabel != null)
                    statusLabel.setText("You are the Host! Waiting for players...");
            }

            startPolling();
        } else {
            codeLabel.setText("CODE: ???");
            if (statusLabel != null)
                statusLabel.setText("Error: No Session Code");
        }
    }

    private void startPolling() {
        pollingTimeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            updateLobbyState();
        }));
        pollingTimeline.setCycleCount(Timeline.INDEFINITE);
        pollingTimeline.play();
    }

    private List<String> previousPlayers = new java.util.ArrayList<>();

    private void updateLobbyState() {
        if (sessionId == -1)
            return;

        // Run database queries in background thread to prevent UI lag
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            // 1. Check Game Status
            String status = gameSessionService.getSessionStatus(sessionId);
            if ("PLAYING".equals(status)) {
                Platform.runLater(() -> {
                    stopPolling();
                    proceedToGame();
                });
                return;
            }

            // 2. Update Player List
            List<String> players = gameSessionService.getPlayersInSession(sessionId);

            // Only update UI if player list changed
            if (!players.equals(previousPlayers)) {
                previousPlayers = new java.util.ArrayList<>(players);
                Platform.runLater(() -> {
                    if (playerListView != null) {
                        ObservableList<String> items = FXCollections.observableArrayList(players);
                        playerListView.setItems(items);
                    }
                    if (playerCountLabel != null) {
                        playerCountLabel.setText("Players: " + players.size());
                    }
                });
            }
        });
    }

    @FXML
    private void handleCopyCode(ActionEvent event) {
        if (sessionCode != null && !sessionCode.isEmpty()) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(sessionCode);
            clipboard.setContent(content);

            if (statusLabel != null) {
                statusLabel.setText("✅ Code copied to clipboard!");
                // Reset message after 2 seconds
                new Timeline(new KeyFrame(Duration.seconds(2), e -> {
                    if ("PLAYING".equals(gameSessionService.getSessionStatus(sessionId))) {
                        // Don't overwrite if playing status update happened
                    } else {
                        statusLabel.setText("Waiting for players...");
                    }
                })).play();
            }
        }
    }

    @FXML
    private void handleStartGame(ActionEvent event) {
        if (sessionId != -1) {
            gameSessionService.updateSessionStatus(sessionId, "PLAYING");
            // The polling will catch this change and redirect, or we can redirect
            // immediately
            stopPolling();
            proceedToGame();
        }
    }

    private void proceedToGame() {
        Platform.runLater(() -> {
            try {
                HelloApplication.setScene("quiz-game.fxml", currentUser);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void stopPolling() {
        if (pollingTimeline != null) {
            pollingTimeline.stop();
        }
    }

    @FXML
    private void handleExit(ActionEvent event) {
        stopPolling();
        try {
            HelloApplication.setScene("retroquiz.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
