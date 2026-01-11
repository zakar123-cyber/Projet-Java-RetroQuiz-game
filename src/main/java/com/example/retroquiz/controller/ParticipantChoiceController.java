package com.example.retroquiz.controller;

import com.example.retroquiz.HelloApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import java.io.IOException;

public class ParticipantChoiceController {

    @FXML
    private Button userLoginButton;

    @FXML
    private Button guestJoinButton;

    @FXML
    private Button backButton;

    // --- Event Handlers ---

    @FXML
    private void handleUserLogin(ActionEvent event) {
        System.out.println("Navigating to dedicated User Login Screen...");
        // TODO: Implement scene switching to user_login.fxml
    }

    @FXML
    private void handleGuestJoin(ActionEvent event) {
        System.out.println("Navigating to Guest Registration Screen (Code Entry)...");
        // TODO: Implement scene switching to registration.fxml
    }

    @FXML
    private void handleViewLeaderboard(ActionEvent event) {
        try {
            HelloApplication.setScene("leaderboard.fxml");
        } catch (IOException e) {
            System.err.println("Failed to load leaderboard.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToMenu(ActionEvent event) {
        try {
            // Logic to switch back to the main retroquiz.fxml scene
            HelloApplication.setScene("retroquiz.fxml");
        } catch (IOException e) {
            System.err.println("Failed to load retroquiz.fxml (Back to Menu).");
            e.printStackTrace();
        }
    }
}
