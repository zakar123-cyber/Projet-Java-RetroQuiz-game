package com.example.retroquiz.controller;

import com.example.retroquiz.HelloApplication;
import com.example.retroquiz.model.Player;
import com.example.retroquiz.service.GameSessionService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class WaitingForResultsController implements Initializable {

    @FXML
    private Label statusLabel;

    private int sessionId;
    private GameSessionService gameSessionService;
    private Timeline pollingTimeline;
    private List<Player> currentPlayers; // We hold this to pass it forward

    public WaitingForResultsController() {
        this.gameSessionService = new GameSessionService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (statusLabel != null) {
            statusLabel.setText("Initializing...");
        }
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
        startPolling();
    }

    // Keep reference to players to pass to result screen if needed (though we'll
    // fetch fresh leaderboard)
    public void setPlayers(List<Player> players) {
        this.currentPlayers = players;
    }

    private void startPolling() {
        if (pollingTimeline != null) {
            pollingTimeline.stop();
        }

        pollingTimeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            checkIfFinished();
        }));
        pollingTimeline.setCycleCount(Timeline.INDEFINITE);
        pollingTimeline.play();

        statusLabel.setText("Syncing with other players...");
    }

    private void checkIfFinished() {
        if (gameSessionService.areAllPlayersFinished(sessionId)) {
            pollingTimeline.stop();
            proceedToResults();
        } else {
            // Optional: Update text to show progress?
            // statusLabel.setText("Waiting...");
        }
    }

    private void proceedToResults() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/result-screen.fxml"));
            Scene scene = new Scene(loader.load());

            ResultController resultController = loader.getController();

            // Get final leaderboard
            List<Player> finalComponentList = gameSessionService.getSessionLeaderboard(sessionId);
            resultController.setPlayers(finalComponentList);

            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Quiz Results");

        } catch (IOException e) {
            System.err.println("Failed to load result screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopPolling() {
        if (pollingTimeline != null) {
            pollingTimeline.stop();
        }
    }
}
