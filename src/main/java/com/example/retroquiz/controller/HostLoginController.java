package com.example.retroquiz.controller;

import com.example.retroquiz.model.*;
import com.example.retroquiz.service.*;
import com.example.retroquiz.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label; // <-- Added Label import
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HostLoginController implements Initializable {

    // --- FXML ELEMENTS ---
    @FXML
    private ImageView backgroundView;
    @FXML
    private VBox neonBox;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label feedbackLabel; // <-- New Feedback Label

    private static final String GIF_PATH = "/retro-neon.gif";

    // --- BACKEND INSTANCE ---
    private final TournamentService tournamentService = new TournamentService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadAnimatedBackground();
        applyStyle("cyan");
        feedbackLabel.setText("Active Tournament Code for Users: " + tournamentService.getActiveTournamentCode());
    }

    // --- Dynamic Styling Utility ---
    // (Existing applyStyle code is retained)
    private void applyStyle(String color) {
        String neonColor = "#00ffff"; // Fixed to Cyan for Admin Login
        String actionStyleClass = "neon-button cyan"; // Fixed to Cyan for Admin Login

        neonBox.setStyle(
                "-fx-neon-color: " + neonColor + ";" +
                        "-fx-input-color: " + neonColor + ";" +
                        "-fx-secondary-text-color: " + neonColor + ";" +
                        "-fx-effect: dropshadow(gaussian, " + neonColor + ", 20, 0.8, 0, 0);");

        loginButton.getStyleClass().setAll(actionStyleClass.split(" "));
    }

    // --- Handlers ---

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            feedbackLabel.setText("⚠️ Please enter both username and password.");
            return;
        }

        // Use the TournamentService for authentication
        if (tournamentService.authenticateAdmin(username, password)) {
            feedbackLabel.setText("✅ Login Successful! Redirecting...");

            // In a real application, you might pass the Host object here
            try {
                // Assuming admin_dashboard.fxml is the next scene
                HelloApplication.setScene("admin_dashboard.fxml");
            } catch (IOException e) {
                System.err.println("Failed to load admin_dashboard.fxml");
                e.printStackTrace();
            }
        } else {
            feedbackLabel.setText("❌ Login Failed: Invalid Admin credentials.");
        }
    }

    @FXML
    private void handleBackToMenu(ActionEvent event) {
        try {
            HelloApplication.setScene("retroquiz.fxml");
        } catch (IOException e) {
            System.err.println("Failed to load retroquiz.fxml");
            e.printStackTrace();
        }
    }

    // --- Load Background ---
    private void loadAnimatedBackground() {
        // ... (rest of the background loading logic)
        try {
            URL imageUrl = getClass().getResource(GIF_PATH);
            if (imageUrl == null) {
                System.err.println("CRITICAL FILE ERROR: Could not find GIF at: " + GIF_PATH);
                return;
            }
            Image image = new Image(imageUrl.toExternalForm(), true);
            backgroundView.setImage(image);

            backgroundView.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    backgroundView.fitWidthProperty().bind(newScene.widthProperty());
                    backgroundView.fitHeightProperty().bind(newScene.heightProperty());
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to load local background image file.");
            e.printStackTrace();
        }
    }
}
